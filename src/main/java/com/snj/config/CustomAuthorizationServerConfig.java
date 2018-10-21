package com.snj.config;

import com.snj.services.CustomUserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

/*
 *  CustomAuthorizationServerConfig generates tokens specific to a client(it can be other spring boot micro service)
 */

@Configuration
@EnableAuthorizationServer
public class CustomAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private static final String CLIENT_ID = "client";
    private static final String CLIENT_SECRET = "secret";
    private static final String GRANT_TYPE_PASSWORD = "password";
    private static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
    private static final String GRANT_TYPE_AUTH_CODE = "authorization_code";

    private static final String SCOPE_READ = "read";
    private static final String SCOPE_WRITE = "write";
    private static final String SCOPE_TRUST = "trust";


    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Qualifier("authenticationManagerBean")
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Value("${config.oauth2.tokenTimeout}")
    private int ACCESS_TOKEN_VALIDITY_SECONDS;

    @Value("${config.oauth2.tokenValidity}")
    private int REFRESH_TOKEN_VALIDITY_SECONDS;

    @Value("${config.oauth2.resource.jwt.key-pair.store-password}")
    private String keyStorePass;

    @Value("${config.oauth2.resource.jwt.key-pair.alias}")
    private String keyPairAlias;

    @Bean("userDetailService")
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailService();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                .inMemory()
                .withClient(CLIENT_ID)
                .authorizedGrantTypes(GRANT_TYPE_CLIENT_CREDENTIALS, GRANT_TYPE_PASSWORD, GRANT_TYPE_REFRESH_TOKEN, GRANT_TYPE_AUTH_CODE)
                .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
                .scopes(SCOPE_READ, SCOPE_WRITE, SCOPE_TRUST)
                .resourceIds("oauth2-resource")
                .accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS)
                .refreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY_SECONDS)
                .secret(passwordEncoder.encode(CLIENT_SECRET));
    }


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
                .tokenStore(tokenStore())
                .userDetailsService(userDetailsService)
                .tokenServices(tokenServices())
                .accessTokenConverter(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {

        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), keyStorePass.toCharArray());
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair(keyPairAlias));
        return converter;
    }

    @Bean
    public JwtTokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setTokenEnhancer(accessTokenConverter());
        return defaultTokenServices;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
    }


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.checkTokenAccess("isAuthenticated()")
                .tokenKeyAccess("permitAll()");
    }
}

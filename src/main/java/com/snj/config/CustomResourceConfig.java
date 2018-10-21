package com.snj.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;



/*
 *
 *
 */

@Configuration
@EnableResourceServer
public class CustomResourceConfig extends ResourceServerConfigurerAdapter {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Value("${config.oauth2.resource.id}")
    private String resourceId;


    @Value("${config.oauth2.resource.jwt.key-pair.store-password}")
    private String keyStorePass;

    @Value("${config.oauth2.resource.jwt.key-pair.alias}")
    private String keyPairAlias;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
//                .anonymous().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).authenticated()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers("/", "/home", "/register", "/login").permitAll()
                .antMatchers("/oauth/**").authenticated();

    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources
                .resourceId(resourceId)
                .tokenServices(tokenServices())
                .tokenStore(tokenStore());
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
}

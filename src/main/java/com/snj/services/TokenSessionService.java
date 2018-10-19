package com.snj.services;

import com.snj.entities.TokenSession;
import com.snj.entities.ValidTokenResponse;
import com.snj.repositories.TokenSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

@Service
public class TokenSessionService {
    @Autowired
    private TokenSessionRepository tokenSessionRepository;

    public ValidTokenResponse isValidTokenSessionMapping(TokenSession TokenSession) throws UsernameNotFoundException {

        String username = TokenSession.getUsername();
        TokenSession tokenSessionFromDB = tokenSessionRepository.findOneByUsername(username);

        if (Objects.isNull(tokenSessionFromDB)) {

//            LOGGER.error("User " + username + " mapping with token is not found in the database.");
            throw new UsernameNotFoundException("User " + username + "  mapping with token is not found in the database.");
        }

        /**
         * TODO Time zone of data base and client may be different.
         */
        LocalDateTime currentSystemTime = LocalDateTime.now();
        ZonedDateTime currentZonedDateTime = currentSystemTime.atZone(ZoneId.systemDefault());
        long currentTimeInMillis = currentZonedDateTime.toInstant().toEpochMilli();

        ZonedDateTime dataBaseZonedDateTime = tokenSessionFromDB.getCreatedTime().atZone(ZoneId.systemDefault());

        /**
         * tokenTimeInMillis = created_time in millis + expiry time (seconds) * 1000.
         */
        long tokenTimeInMillis = dataBaseZonedDateTime.toInstant().toEpochMilli() + (tokenSessionFromDB.getExpiryTime() * 1000);

        if (currentTimeInMillis >= tokenTimeInMillis) {

//            LOGGER.info("User " + username + " token has expired. Please generate new token. Deleting the expired token mapping.");
            tokenSessionRepository.delete(tokenSessionFromDB);
            throw new UsernameNotFoundException("User " + username + " token has expired. Please generate new token.");

        } else if (!TokenSession.equals(tokenSessionFromDB)) {

            if (!tokenSessionFromDB.getToken().equals(TokenSession.getToken())) {
//                LOGGER.info("User "+TokenSession.getUsername()+ " has invalid user and token mapping. Please generate new token.");

            } else {
//                LOGGER.info("User "+TokenSession.getUsername()+ " has invalid user and session-id mapping. Please generate new token.");
            }

//            LOGGER.info("So, Deleting the invalid mapping.");
            tokenSessionRepository.delete(tokenSessionFromDB);
            throw new UsernameNotFoundException("User " + username + " has invalid user, session-id and token mapping. Please generate new token.");

        } else {

//            LOGGER.info("User " + username + " has valid token.");
            ValidTokenResponse validMappingResponse = new ValidTokenResponse(true, tokenSessionFromDB);
            return validMappingResponse;
        }

    }

    public TokenSession saveUserTokenSessionMapping(TokenSession userTokenSession) {

        TokenSession userTokenSessionFromDB = tokenSessionRepository.findOneByUsername(userTokenSession.getUsername());

        /**
         * 1. If User is making the login call again with the same session-id and token. Then delete the old mapping and return the new inserted mapping.
         * 2. If same user is making login call with the new token or session-id. Then delete the old mapping and return the new inserted mapping
         */
        if (Objects.nonNull(userTokenSessionFromDB)) {

            if (userTokenSessionFromDB.equals(userTokenSession)) {
//                LOGGER.info("User "+userTokenSession.getUsername()+ " making login call again with same token and session-id.");

            } else if (!userTokenSessionFromDB.getToken().equals(userTokenSession.getToken())) {
//                LOGGER.info("User "+userTokenSession.getUsername()+ " making login call with new token");

            } else {
//                LOGGER.info("User "+userTokenSession.getUsername()+ " making login call with different session-id");

            }
//            LOGGER.info("So, Deleting older mapping from tbl_user_token_session."+userTokenSessionFromDB);
            tokenSessionRepository.delete(userTokenSessionFromDB);

        }

        return tokenSessionRepository.save(userTokenSession);
    }


}

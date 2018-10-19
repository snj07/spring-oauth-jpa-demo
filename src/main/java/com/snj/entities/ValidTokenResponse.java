package com.snj.entities;

public class ValidTokenResponse {

    private boolean valid;
    private TokenSession tokenSession;

    public ValidTokenResponse() {
    }

    public ValidTokenResponse(boolean valid, TokenSession TokenSession) {
        this.valid = valid;
        this.tokenSession = TokenSession;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }


    public TokenSession getTokenSession() {
        return tokenSession;
    }

    public void setTokenSession(TokenSession tokenSession) {
        this.tokenSession = tokenSession;
    }
}

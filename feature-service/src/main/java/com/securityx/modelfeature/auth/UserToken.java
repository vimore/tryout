package com.securityx.modelfeature.auth;



import javax.ws.rs.core.MultivaluedMap;

/**
 * UserToken class is a POJO used to hold custom security token to be used to link a request to a specific User
 */
class UserToken {
    private final String token;
    private final String accessKey;
    private final MultivaluedMap<String, String> payload;
    private final User fetchedUser;

    UserToken(String token, String accessKey, MultivaluedMap<String, String> payload, User fetchedUser) {
        this.token = token;
        this.accessKey = accessKey;
        this.payload = payload;
        this.fetchedUser = fetchedUser;
    }
    String getToken() {
        return token;
    }

    String getAccessKey() {
        return accessKey;
    }

    MultivaluedMap<String, String> getPayload() {
        return payload;
    }

    public User getFetchedUser() {
        return fetchedUser;
    }

    @Override
    public String toString(){
        return String.format("token: %s, accessKey: %s, payload: %s", token, accessKey, payload.toString());
    }
}

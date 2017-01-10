package com.securityx.modelfeature.auth;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.nio.charset.Charset;

/**
 * This is an authenticator that takes the security token extracted from the request by the SecurityProvider
 * and authenticates the User
 */
public class UserAuthenticator implements Authenticator<UserToken, User> {

    private static Logger LOGGER = LoggerFactory.getLogger(UserAuthenticator.class);

    @Override
    public Optional<User> authenticate(UserToken userSecToken) throws AuthenticationException {

        String[] tokens = userSecToken.getToken().split("::");
        String secJwtToken = tokens[0];
        String secret = tokens[1];

        try{
            Jws<Claims> jws = Jwts.parser().setSigningKey(secret.getBytes(Charset.forName("US-ASCII"))).parseClaimsJws(secJwtToken);
            User user = new User();
            Claims claims = jws.getBody();
            if(!validateClaims(claims, userSecToken.getPayload())){
                LOGGER.error("The payload is not identical to the query params and request body. The request may have been tampered! Payload: {}, Request: {}", userSecToken.getPayload(), claims.toString());
                throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\": \"Invalid Token. Payload does not match the data encoded in the token\"}").build());
            }
            String subject = claims.getSubject();
            if (subject != null && !subject.isEmpty()) {
                user.setDisplayName(subject);
                user.setUsername(subject);
            } else {
                subject = userSecToken.getAccessKey();
                if (subject != null && !subject.isEmpty()) {
                    user.setDisplayName(subject);
                    user.setUsername(subject);
                } else {
                    LOGGER.error("Access key is empty in the token {}", userSecToken.toString());
                    throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Invalid Token. No access key in the token\"}").build());
                }
            }
            return Optional.of(userSecToken.getFetchedUser());
        }catch (Exception e){
            LOGGER.error("Failed to parse the JWT Token: {}", e.getMessage(), e);
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(String.format("{\"error\": \"%s\"}", e.getMessage())).build());
        }
    }
    private static final String EXPIRY="exp";
    private static final String AUDIENCE="aud";
    private static final String SUBJECT="sub";
    private boolean validateClaims(Claims claims, MultivaluedMap<String, String> map) {
        //ignore comparing the payload user login and UI server data
        if (claims.containsKey(SUBJECT) && claims.containsKey(AUDIENCE) && claims.containsKey(EXPIRY)) {
            return true;
        }
        for(String k1 : claims.keySet()){
            if(k1.equals(EXPIRY)){
                continue;
            }
            String v1 = claims.get(k1).toString();
            String v2 =  map.getFirst(k1);
            if(!(v1!=null && v2!=null && v1.equals(v2))){
                return false;
            }

        }
        return true;
    }
}

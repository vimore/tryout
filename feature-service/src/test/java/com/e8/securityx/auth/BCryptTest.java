package com.e8.securityx.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.securityx.modelfeature.config.SecurityTokenConfiguration;
import com.securityx.modelfeature.utils.BCrypt;
import io.jsonwebtoken.*;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class BCryptTest {
    @Test
    public void testBcyrpt() throws Exception{
        String password = BCrypt.hashpw("e8sec", BCrypt.gensalt(12));
        System.out.println(password);
        assertTrue(password.length()!=0);

        HashMap<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("blahKey", "blahVal");
        requestBody.put("blehKey", "blehVal");

        HashMap<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("queryKey1", "queryVal1");
        queryParams.put("queryKey2", "queryVal2");

        HashMap<String, String> payload = new HashMap<String, String>();
        payload.putAll(queryParams);
        payload.putAll(requestBody);
        ObjectMapper mapper = new ObjectMapper();
        String payloadJson =mapper.writeValueAsString(payload);
        System.out.println(payloadJson);

        String jwtToken = getJwtToken(
                "$2a$12$1D4jjWwu8IrE0URnLIz8uOpvwKOD0OsCTHKiWEX/CEmWQiWGhWNRC",
                "523BAD96BCDE4CE1BCBD3DED49BA89B8",
                "viewer",
                System.currentTimeMillis(),
                System.currentTimeMillis()+86400,
                null);
        System.out.println(jwtToken);
    }
    /*
    {
      "sub": "523BAD96BCDE4CE1BCBD3DED49BA89B8",
      "aud": "viewer",
      "exp": 1466875525,
      "iat": 1466789125
    }

    NTIzQkFEOTZCQ0RFNENFMUJDQkQzREVENDlCQTg5Qjg6ZXlKaGJHY2lPaUpJVXpVeE1pSjkuZXlKemRXSWlPaUkxTWpOQ1FVUTVOa0pEUkVVMFEwVXhRa05DUkRORVJVUTBPVUpCT0RsQ09DSXNJbUYxWkNJNkluWnBaWGRsY2lJc0ltVjRjQ0k2SWpFME5qWTRPVFF4TVRJeE9EQWlMQ0pwWVhRaU9pSXhORFkyT0RrME1ESTFOemd3SW4wLnI0Z1FINDl6c1l3M1hCMHY1d1YxTDJ6eXVEcUZud1R0ZENUTUlHaXRQd0NLRGU0dnBqdjU3eThWdjZUYnU2OGV0WW1xOTNXUDFuSWZZRWZjUFBpVTZR
     */
    @Test
    public void testToken() throws Exception{
        String accessKey = "523BAD96BCDE4CE1BCBD3DED49BA89B8";
        String accessSecret = "$2a$12$hkmxSHppSAgKsMgwceWLv.yO6s.K8ChFjEEjvRkZ1A/47PQoMG72K";

        long millis = System.currentTimeMillis();

        String jwtToken = getJwtToken(accessSecret, accessKey, "viewer", millis, millis+86400, null);
        System.out.println("JWT TOKEN: "+ jwtToken);
        String authToken = accessKey+":"+jwtToken;
        String base64AuthToken = Base64.encodeBase64String(authToken.getBytes(Charset.forName("US-ASCII")));
        System.out.println(String.format("-H 'Authorization: %s'", base64AuthToken));

    }
    private String getJwtToken(String secret, String subject, String audience, long iat, long exp,  String payload ){
        SecurityTokenConfiguration secTokenConf = new SecurityTokenConfiguration();
        secTokenConf.setSignatureAlgorithm(SignatureAlgorithm.HS512);
        JwtBuilder jwtBuilder = Jwts.builder();
        if(payload!=null && !payload.isEmpty()) {
            jwtBuilder.setPayload(payload);
        }
        jwtBuilder.setSubject(subject);
        jwtBuilder.setAudience(audience);
        jwtBuilder.setIssuedAt(new Date(iat));
        jwtBuilder.setExpiration(new Date(exp));

        jwtBuilder.signWith(secTokenConf.getSignatureAlgorithm(), secret.getBytes(Charset.forName("US-ASCII")));
        String secJwtToken = jwtBuilder.compact();
        Jws<Claims> jws = Jwts.parser()
                .setSigningKey(secret.getBytes(Charset.forName("US-ASCII")))
                .parseClaimsJws(secJwtToken);
        return secJwtToken;
    }
}

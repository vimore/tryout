package com.securityx.modelfeature.utils;

import com.google.common.base.Optional;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.config.SecurityTokenConfiguration;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

/**
 * TokenUtils is a utility class for generating and validating the security tokens.
 */
public class TokenUtils {

    private static Logger LOGGER = LoggerFactory.getLogger(TokenUtils.class);

    public static String generateSecurityToken(String userName, String userRole, final String secretKey,
                                               final FeatureServiceConfiguration conf) {

        SecurityTokenConfiguration secTokenConf = conf.getSecurityToken();

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, secTokenConf.getExpirationTime());
        Date expiryDateTime = c.getTime();

        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.setSubject(userName);
        jwtBuilder.setAudience(userRole);
        jwtBuilder.setExpiration(expiryDateTime);
        jwtBuilder.signWith(secTokenConf.getSignatureAlgorithm(), secretKey.getBytes());


        return jwtBuilder.compact();
    }

    public static Boolean isTokenValid(String compactJwt, final String secretKey) throws Exception{
        try {
            Jwts.parser().setSigningKey(secretKey.getBytes("US-ASCII")).parseClaimsJws(compactJwt);
            //OK, we can trust this JWT
            return true;
        } catch (Exception e) {
            //don't trust the JWT!
            if (e instanceof ExpiredJwtException) {
                LOGGER.error("INVALID TOKEN - Token is Expired", e);
            } else if (e instanceof SignatureException) {
                LOGGER.error("INVALID TOKEN - Token Signature is Invalid", e);
            } else {
                LOGGER.error("INVALID TOKEN", e);
            }
            throw e;
        }
    }

    public static Optional<Jws<Claims>> getJwtsClaimFromToken(String compactJwt, final String secretKey) {
        try {
            return Optional.fromNullable(Jwts.parser().setSigningKey(secretKey.getBytes("US-ASCII")).parseClaimsJws(compactJwt));
            //OK, we can trust this JWT
        } catch (Exception e) {
            //don't trust the JWT!
            if (e instanceof ExpiredJwtException) {
                LOGGER.error("INVALID TOKEN - Token is Expired", e);
            } else if (e instanceof SignatureException) {
                LOGGER.error("INVALID TOKEN - Token Signature is Invalid", e);
            } else {
                LOGGER.error("INVALID TOKEN", e);
            }
        }
        return Optional.absent();
    }
}

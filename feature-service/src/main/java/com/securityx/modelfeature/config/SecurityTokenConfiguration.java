package com.securityx.modelfeature.config;

import com.google.common.base.Objects;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Created by sachinkapse on 31/05/16.
 */
public class SecurityTokenConfiguration {

    private int expirationTime;

    private SignatureAlgorithm signatureAlgorithm;

    public int getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(int expirationTime) {
        this.expirationTime = expirationTime;
    }

    public SignatureAlgorithm getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(SignatureAlgorithm signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("expirationTime", expirationTime)
                .add("signatureAlgorithm", signatureAlgorithm)
                .toString();
    }
}

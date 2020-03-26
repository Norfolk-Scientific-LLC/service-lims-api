package com.nfsci.servicelimsapi.authorization;

import com.auth0.jwk.JwkException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nfsci.servicelimsapi.config.WebSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class RsaTokenDecryptor {

    private final WebSecurityConfig webSecurityConfig;

    @Autowired
    public RsaTokenDecryptor(WebSecurityConfig webSecurityConfig) {
        this.webSecurityConfig = webSecurityConfig;
    }

    public Map<String, Claim> decrypt(String token) throws JwkException {
        DecodedJWT jwt = decode(token);

        return jwt.getClaims();
    }

    public DecodedJWT decode(String token) {
        return JWT.decode(token);
    }
}

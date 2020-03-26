package com.nfsci.servicelimsapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

/**
 * service-ngs-api
 */

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final String audience;
    private final String issuer;
    private final SecretKey key;

    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    @Autowired
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, String secret, String audience, String issuer) {
        super(authenticationManager);
        this.audience = audience;
        this.issuer = issuer;
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public void doFilterInternal(HttpServletRequest httpServletRequest,
                                 HttpServletResponse httpServletResponse,
                                 FilterChain filterChain) throws IOException, ServletException {

        LOG.debug(String.format("Filtering request: \n%s", httpServletRequest.getPathInfo()));
        String header = httpServletRequest.getHeader("authorization");

        if (header == null || !header.startsWith("Bearer")) {
            LOG.info(String.format("Rejecting request due to no authorization token %s",
                    StringUtils.join(httpServletRequest.getHeaderNames(), ",")));
            rejectRequest(filterChain, httpServletResponse, httpServletRequest);
        } else {
            try {
                String jwt = header.replace("Bearer ", "");
                Jws<Claims> jwsClaims = getClaims(jwt);
                Claims claims = jwsClaims.getBody();
                Instant timestamp = new Date().toInstant();

                if (!claims.getAudience().contains(audience)) {
                    LOG.info("Rejecting request due to bad audience");
                    rejectRequest(filterChain, httpServletResponse, httpServletRequest);
                }

                if (!claims.getIssuer().equals(issuer)) {
                    LOG.info("Rejecting request due to bad issuer");
                    rejectRequest(filterChain, httpServletResponse, httpServletRequest);
                }

                if (claims.getExpiration().toInstant().isBefore(timestamp)) {
                    LOG.info("Rejecting request due to token expired");
                    rejectRequest(filterChain, httpServletResponse, httpServletRequest);
                }

                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(claims.getSubject(),
                        null, new ArrayList<>()));
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            } catch (JwtException ex) {
                LOG.info(String.format("Rejecting request due to bad signature \n%s", ex.getMessage()));
                rejectRequest(filterChain, httpServletResponse, httpServletRequest);
            }
        }
    }

    private void rejectRequest(FilterChain filterChain, HttpServletResponse httpServletResponse,
                               HttpServletRequest httpServletRequest) throws IOException, ServletException {
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private Jws<Claims> getClaims(String jwtString) throws JwtException {

        return Jwts.parser().setSigningKey(key).parseClaimsJws(jwtString);
    }
}

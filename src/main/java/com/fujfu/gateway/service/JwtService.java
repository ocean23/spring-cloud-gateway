package com.fujfu.gateway.service;

import com.fujfu.gateway.exception.InvalidTokenException;
import com.fujfu.gateway.service.bo.JwtBO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.micrometer.core.instrument.util.StringUtils;
import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author Ocean
 * @date 2021/3/8 6:03 PM
 */
@Service
@AllArgsConstructor
@Slf4j
public class JwtService {
    private static final String MOBILE = "mobile";
    private static final String secret = "abcdeB5WeYoceanjc9qbj78AaMdex888";
    // 60*60*24*7 一周的时间
    private final static int expiration = 604800;

    public String generateToken(String name, String mobile) {
        return Jwts.builder()
                .setSubject(name)
                .claim(MOBILE, mobile)
                .setExpiration(generateExpirationDate(expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public JwtBO extractToken(String token) throws InvalidTokenException {
        if (StringUtils.isEmpty(token)) {
            throw new InvalidTokenException("empty token");
        }
        JwtBO jwtBO = new JwtBO();
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        jwtBO.setName(claims.getSubject());
        jwtBO.setMobile(String.valueOf(claims.get(MOBILE)));
        return jwtBO;
    }

    private Date generateExpirationDate(long seconds) {
        LocalDateTime localDateTime = LocalDateTime.now().plusSeconds(seconds);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}

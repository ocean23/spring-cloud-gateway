package com.fujfu.gateway.filter;

import com.fujfu.gateway.exception.InvalidTokenException;
import com.fujfu.gateway.service.JwtService;
import com.fujfu.gateway.service.bo.JwtBO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Jun Luo
 * @date 2021/3/9 2:41 PM
 */
@Slf4j
@Component
@AllArgsConstructor
public class AuthorizationFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final String LOGIN_URL = "/spring-cloud-frontend/auth/login";
    private static final String JWT_HEADER_NAME = "X-User-Token";
    private static final String MOBILE_KEY = "mobile";
    private static final String USER_ID_KEY = "userId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String urlPath = request.getURI().getPath();
        String token = request.getHeaders().getFirst(JWT_HEADER_NAME);
        boolean jwtValid = true;
        // 如果是登录的请求，直接通过
        if (checkLoginRequest(request.getMethod(), urlPath)) {
            return chain.filter(exchange);
        }
        JwtBO jwtBO = null;
        try {
            jwtBO = jwtService.extractToken(token);

        } catch (InvalidTokenException e) {
            jwtValid = false;
            log.debug("获取到了无效的 JWT，token = {}", token);
            if (Strings.isNotEmpty(token)) {
                // 如果用户传入了 token，且 token 已经失效的话，直接返回 401，无论这个接口是否需要鉴权
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }
        if (jwtValid) {
            JwtBO fJwtBO = jwtBO;
            request.mutate().headers(httpHeaders -> {
                httpHeaders.remove(USER_ID_KEY);
                httpHeaders.remove(MOBILE_KEY);
                httpHeaders.add(MOBILE_KEY, fJwtBO.getMobile());
                httpHeaders.add(USER_ID_KEY, fJwtBO.getUserId());
            }).build();
            exchange.mutate().request(request).build();
            return chain.filter(exchange);
        }
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private boolean isAllowAccess(ServerHttpRequest request) {
        return true;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }

    private boolean checkLoginRequest(HttpMethod httpMethod, String path) {
        if (HttpMethod.POST.equals(httpMethod) && antPathMatcher.match(LOGIN_URL, path)) {
            return true;
        }
        return false;
    }
}

package com.fujfu.gateway.filter;

import com.fujfu.gateway.config.properties.RouteProperties;
import com.fujfu.gateway.config.properties.vo.RequestUrlVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Jun Luo
 * @date 2021/4/1 9:01 AM
 */
@Component
@Slf4j
@AllArgsConstructor
public class SpecificUrlBlockFilter implements GlobalFilter, Ordered {
    private final RouteProperties routeProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String urlPath = request.getURI().getPath();
        for (RequestUrlVO requestUrl : routeProperties.getBlockUrl()) {
            if (requestUrl.getMethod().equals(request.getMethod()) && antPathMatcher.match(requestUrl.getPattern(), urlPath)) {
                exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                return exchange.getResponse().setComplete();
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}

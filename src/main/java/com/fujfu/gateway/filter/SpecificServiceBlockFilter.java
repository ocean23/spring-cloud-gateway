package com.fujfu.gateway.filter;

import com.fujfu.gateway.config.properties.RouteProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Jun Luo
 * @date 2021/3/9 2:06 PM
 */
@Component
@Slf4j
@AllArgsConstructor
public class SpecificServiceBlockFilter implements GlobalFilter, Ordered {

    private final RouteProperties routeProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String urlPath = request.getURI().getPath();
        for (String service : routeProperties.getAllowedService()) {
            if (urlPath.startsWith(service)) {
                return chain.filter(exchange);
            }
        }
        // 如果不在允许的访问列表中，直接返回 404
        exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

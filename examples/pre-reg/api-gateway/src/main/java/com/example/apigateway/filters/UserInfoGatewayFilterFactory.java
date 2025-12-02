package com.example.apigateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class UserInfoGatewayFilterFactory 
    extends AbstractGatewayFilterFactory<UserInfoGatewayFilterFactory.Config> {
    
    private static final Logger log = LoggerFactory.getLogger(UserInfoGatewayFilterFactory.class);
    
    public UserInfoGatewayFilterFactory() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            return ReactiveSecurityContextHolder.getContext()
                .doOnNext(ctx -> {
                    log.debug("Security Context: {}", ctx);
                    if (ctx.getAuthentication() != null) {
                        log.debug("Authenticated: {}", ctx.getAuthentication().isAuthenticated());
                        log.debug("Principal: {}", ctx.getAuthentication().getPrincipal());
                    } else {
                        log.warn("No authentication found!");
                    }
                })
                .map(ctx -> {
                    if (ctx.getAuthentication() != null && ctx.getAuthentication().getPrincipal() instanceof Jwt) {
                        Jwt jwt = (Jwt) ctx.getAuthentication().getPrincipal();
                        org.springframework.http.server.reactive.ServerHttpRequest request = exchange.getRequest().mutate()
                            .header("X-User-Id", jwt.getSubject())
                            .header("X-User-Email", jwt.getClaimAsString("email"))
                            .header("X-User-Name", jwt.getClaimAsString("name"))
                            .build();
                        log.debug("Added headers: X-User-Id={}", jwt.getSubject());
                        return exchange.mutate().request(request).build();
                    }
                    return exchange;
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
        };
    }
    
    public static class Config {
        // Configuration properties if needed
    }
}

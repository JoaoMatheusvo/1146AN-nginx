package com.seuprojeto.gateway.filter;
import com.seuprojeto.authservice.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.stream.Collectors;
@Component
public class JwtRoleFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        List<String> authHeaders = exchange.getRequest().getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION);
        if (!authHeaders.isEmpty()) {
            String header = authHeaders.get(0);
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                try {
                    Jws<Claims> claimsJws = com.seuprojeto.authservice.security.JwtUtilHolder.getInstance().parseToken(token);
                    Claims claims = claimsJws.getBody();
                    @SuppressWarnings("unchecked") var roles = (List<String>) claims.get("roles");
                    String rolesHeader = roles.stream().collect(Collectors.joining(","));
                    ServerHttpRequest mutated = exchange.getRequest().mutate()
                            .header("X-User-Roles", rolesHeader)
                            .header("X-User-Name", claims.getSubject())
                            .build();
                    ServerWebExchange mutatedExchange = exchange.mutate().request(mutated).build();
                    return chain.filter(mutatedExchange);
                } catch (Exception e) {
                    exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            }
        }
        return chain.filter(exchange);
    }
    @Override
    public int getOrder() { return -1; }
}

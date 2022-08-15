package ru.vk.competition.minbenchmark;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Component
public class Filter implements WebFilter {

    Logger logger = Logger.getLogger("filter");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        logger.info(exchange.getRequest().getPath().toString());
        return chain.filter(exchange);
    }
}

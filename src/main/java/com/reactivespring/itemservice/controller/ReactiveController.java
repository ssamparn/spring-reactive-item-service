package com.reactivespring.itemservice.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class ReactiveController {

    @GetMapping("/flux")
    public Flux<Integer> getFlux() {
        return Flux.just(1, 2, 3, 4, 5, 6)
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

    @GetMapping(value = "/fluxstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Long> getStreamFlux() {
        return Flux.interval(Duration.ofSeconds(1))
                .log();
    }

    @GetMapping(value = "/mono")
    public Mono<Integer> getMono() {
        return Mono.just(1)
                .log();

    }

}

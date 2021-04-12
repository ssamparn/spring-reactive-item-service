package com.reactivespring.itemservice.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "10000") //By default, the WebTestClient is timedout after 5 seconds. We can configure the timeout with @AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
public class ReactiveControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    final void getFluxTest() {
        Flux<Integer> integerFluxResponse = webTestClient.get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(integerFluxResponse)
                .expectSubscription()
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .expectNext(5)
                .expectNext(6)
                .verifyComplete();
    }

    @Test
    final void getFluxTest_DiffApproach() {
        webTestClient.get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBodyList(Integer.class)
                .hasSize(6);
    }

    @Test
    final void getFluxTest_AnotherApproach() {

        EntityExchangeResult<List<Integer>> entityExchangeResult = webTestClient.get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .returnResult();
        Assertions.assertEquals(entityExchangeResult.getResponseBody(), Arrays.asList(1, 2, 3, 4, 5, 6));
    }

    @Test
    final void getFluxTest_OneMoreApproach() {

        webTestClient.get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith(response -> Assertions.assertEquals(response.getResponseBody(), Arrays.asList(1, 2, 3, 4, 5, 6)));
    }

    @Test
    final void fluxStreamTest() {
        Flux<Long> longFluxResponse = webTestClient.get()
                .uri("/fluxstream")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(longFluxResponse)
                .expectSubscription()
                .expectNext(0L, 1L, 2L, 3L)
                .thenCancel()
                .verify();
    }

    @Test
    final void monoTest() {
        webTestClient.get()
                .uri("/mono")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith((response) -> {
                    Assertions.assertEquals(response.getResponseBody(), Integer.valueOf(1));
                });
    }
}
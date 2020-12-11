package com.reactivespring.itemservice.reactivetests.flux;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxTest {

    @Test
    public void flux_Test() {

        Flux<String> springFlux = Flux.just("Spring Framework", "Spring Boot", "Spring Reactive")
                .concatWith(Flux.error(new RuntimeException("Exception occured")))
                .concatWith(Flux.just("Test to see if Flux emits")) // Note: After an error is emitted from Flux, it will not emit anymore data. So this line will not be included in the onComplete() event.
                .log();

        springFlux.subscribe(System.out::println, (e) -> System.err.println(e), () -> System.out.println("Completed")); // The last Runnable will not run as well because of above reason.
    }

    @Test
    public void fluxElementsTest_WithoutError() {

        Flux<String> stringElementsFlux = Flux.just("Spring", "Spring Boot", "Spring Framework", "Spring Reactive")
                .log();

        StepVerifier.create(stringElementsFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Spring Framework")
                .expectNext("Spring Reactive")
        .verifyComplete();
    }

    @Test
    public void testElements_WithError() {

        Flux<String> stringElementsFlux = Flux.just("Spring Boot", "Spring Framework", "Spring Reactive")
                .concatWith(Flux.error(new RuntimeException("Exception occured")))
                .log();

        StepVerifier.create(stringElementsFlux)
                .expectNext("Spring Boot")
                .expectNext("Spring Framework")
                .expectNext("Spring Reactive")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void testElements_WithError_AnotherWay() {

        Flux<String> stringElementsFlux = Flux.just("Spring Boot", "Spring Framework", "Spring Reactive")
                .concatWith(Flux.error(new RuntimeException("Exception occured")))
                .log();

        StepVerifier.create(stringElementsFlux)
                .expectNext("Spring Boot", "Spring Framework", "Spring Reactive")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void testElements_WithErrorMessage() {

        Flux<String> stringElementsFlux = Flux.just("Spring Boot", "Spring Framework", "Spring Reactive")
                .concatWith(Flux.error(new RuntimeException("Exception occured")))
                .log();

        StepVerifier.create(stringElementsFlux)
                .expectNext("Spring Boot")
                .expectNext("Spring Framework")
                .expectNext("Spring Reactive")
                .expectErrorMessage("Exception occured")
                .verify();
    }

    @Test
    public void testNumberOfFluxElements() {

        Flux<String> stringElementsFlux = Flux.just("Spring Boot", "Spring Framework", "Spring Reactive")
                .log();

        StepVerifier.create(stringElementsFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void testNumberOfFluxElementsCount_WithError() {

        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Spring Reactive")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .expectErrorMessage("Exception Occurred")
                .verify();
    }

}
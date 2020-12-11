package com.reactivespring.itemservice.reactivetests.timetest;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxMonoTimeTest {

    @Test
    public void infiniteSequenceTest_WithThread() throws InterruptedException {

        Flux<Long> intervalLongFlux = Flux.interval(Duration.ofMillis(100))
                .log();

        intervalLongFlux
                .subscribe((element) -> System.out.println(element));

        Thread.sleep(3000);
    }

    @Test
    public void infiniteSequenceTest_WithTake() {
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceTest_WithMap() {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .map(element -> element.intValue())
                .take(4)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2, 3)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceTest_WithMapDelay() {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .delayElements(Duration.ofMillis(100))
                .map(element -> element.intValue())
                .take(4)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2, 3)
                .verifyComplete();
    }


}

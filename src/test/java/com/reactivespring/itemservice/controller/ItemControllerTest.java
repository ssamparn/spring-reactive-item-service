package com.reactivespring.itemservice.controller;

import com.reactivespring.itemservice.document.Item;
import com.reactivespring.itemservice.repository.ItemReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureWebTestClient
@ActiveProfiles("integration-test")
public class ItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @Before
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Inserted item is: " + item))
                .blockLast();
    }

    @Test
    public void getAllItemsTest() {
        webTestClient.get()
                .uri("/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5);
    }

    @Test
    public void getAllItemsTestInsertion() {
        webTestClient.get()
                .uri("/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5)
        .consumeWith((response) -> {
            List<Item> responseBody = response.getResponseBody();
            responseBody.forEach((item -> assertTrue(item.getId() != null)));
        });
    }

    @Test
    public void getAllItemsTestInsertion_AnotherApproach() {
        Flux<Item> responseFlux = webTestClient.get()
                .uri("/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(responseFlux.log())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getAnItemTest() {
        webTestClient.get()
                .uri("/items".concat("/{itemId}"), "SomeExistingId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price").isEqualTo( 149.99);
    }

    @Test
    public void getAnItem_NotFound_Test() {
        webTestClient.get()
                .uri("/items".concat("/{itemId}"), "SomeNonExistingId")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void createItemTest() {
        Item newItem = new Item(null, "IPhone X", 999.99);

        webTestClient.post()
                .uri("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(newItem), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("IPhone X")
                .jsonPath("$.price").isEqualTo(999.99);
    }

    @Test
    public void deleteAnItemTest() {
        webTestClient.delete()
                .uri("/items".concat("/{itemId}"), "SomeExistingId")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void updateItemTest_Ok() {
        double newPrice = 100.00;
        String newId = "UpdatedId";
        Item newItem = new Item(newId, "Senheiser Headphone", newPrice);

        webTestClient.put()
                .uri("/items".concat("/{itemId}"), "SomeExistingId")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(newItem), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price").isEqualTo(newPrice)
                .jsonPath("$.id").isEqualTo(newId);
    }

    @Test
    public void updateItemTest_NotFound() {
        double newPrice = 100.00;
        String nonExistenceId = "nonExistenceId";
        Item newItem = new Item(nonExistenceId, "Senheiser Headphone", newPrice);

        webTestClient.put()
                .uri("/items".concat("/{itemId}"), nonExistenceId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(newItem), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    public List<Item> data() {
        return Arrays.asList(new Item(null, "Samsung TV", 400.00),
                new Item(null, "LG TV", 330.00),
                new Item(null, "Apple Watch", 349.99),
                new Item(null, "Beats Headphone", 299.99),
                new Item("SomeExistingId", "Senheiser Headphone", 149.99)
        );
    }

}

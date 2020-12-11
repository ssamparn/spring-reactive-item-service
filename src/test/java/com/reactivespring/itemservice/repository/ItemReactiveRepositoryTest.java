package com.reactivespring.itemservice.repository;

import com.reactivespring.itemservice.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@DirtiesContext
@DataMongoTest
@RunWith(SpringRunner.class)
@ActiveProfiles("integration-test")
public class ItemReactiveRepositoryTest {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    private List<Item> getItemsList() {
        return Arrays.asList(new Item("id1", "Description1", 200.0),
                new Item("id2", "Description2", 300.0),
                new Item("id3", "Description3", 400.0),
                new Item("id4", "Description4", 500.0),
                new Item("id5", "Description5", 600.0)
        );
    }

    @Before
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(getItemsList()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Inserted item is from Test: " + item))
                .blockLast();
    }

    @Test
    public void getAllItemsTest() {
        Flux<Item> itemsFlux = itemReactiveRepository.findAll();

        StepVerifier.create(itemsFlux.log())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getItemByIdTest() {
        Mono<Item> itemReactiveRepositoryById = itemReactiveRepository.findById("id5");

        StepVerifier.create(itemReactiveRepositoryById.log())
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Description5"))
                .verifyComplete();

    }

    @Test
    public void getItemByDescription_Test() {
        Flux<Item> descriptionMono = itemReactiveRepository.findByDescription("Description4");

        StepVerifier.create(descriptionMono.log())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItemTest() {
        Item itemToBeSaved = new Item(null, "Google Home Mini", 30.00);
        Mono<Item> itemMonoSaved = itemReactiveRepository.save(itemToBeSaved);

        StepVerifier.create(itemMonoSaved.log())
                .expectSubscription()
                .expectNextMatches(getItemSaved())
                .verifyComplete();

    }

    private Predicate<Item> getItemSaved() {
        return item -> item.getId() != null && item.getDescription().equals("Google Home Mini");
    }

    @Test
    public void updateItemPriceTest() {

        double newPrice = 520.00;

        Flux<Item> updatedItem = itemReactiveRepository.findByDescription("Description2")
                .map(item -> {
                    item.setPrice(newPrice);
                    return item;
                })
                .flatMap((item) -> itemReactiveRepository.save(item));

        StepVerifier.create(updatedItem.log())
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == 520.00)
                .verifyComplete();
    }

    @Test
    public void deleteItem_Test1() {
        Mono<Void> deletedItem = itemReactiveRepository
                .findById("id1")
                .map(Item::getId)
                .flatMap(itemReactiveRepository::deleteById);

        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void deleteItem_Test2() {
        Flux<Void> deletedItem = itemReactiveRepository.findByDescription("Description4")
                .flatMap(itemReactiveRepository::delete);

        StepVerifier.create(deletedItem)
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }
}

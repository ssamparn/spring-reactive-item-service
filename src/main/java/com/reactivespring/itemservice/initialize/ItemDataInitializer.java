package com.reactivespring.itemservice.initialize;

import com.reactivespring.itemservice.document.Item;
import com.reactivespring.itemservice.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Profile("!integration-test")
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @Override
    public void run(String... args) throws Exception {
        initialDataSetUp();
    }

    private void initialDataSetUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(item -> {
                    log.info("Item inserteed from Command Line Runner :" + item);
                });
    }

    public List<Item> data() {
        return Arrays.asList(new Item(null, "Samsung TV", 400.00),
                new Item(null, "LG TV", 330.00),
                new Item(null, "Apple Watch", 349.99),
                new Item(null, "Beats Headphone", 299.99),
                new Item("ExistingId", "Beats Headphone", 299.99));
    }
}

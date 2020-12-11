package com.reactivespring.itemservice.service;

import com.reactivespring.itemservice.document.Item;
import com.reactivespring.itemservice.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
public class ItemService {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    public Flux<Item> getAllItems() {
        return itemReactiveRepository.findAll();
    }

    public Mono<Item> getAnItemById(String itemId) {
        return itemReactiveRepository.findById(itemId);
    }

    public Mono<Item> createAnItem(Item item) {
        return itemReactiveRepository.save(item);
    }

    public Mono<Item> updateItem(String itemId, Item item) {
        return itemReactiveRepository.findById(itemId)
                .flatMap(getItemMonoFunction(item));
    }

    private Function<Item, Mono<? extends Item>> getItemMonoFunction(Item item) {
        return currentItem -> {
            currentItem.setId(item.getId());
            currentItem.setDescription(item.getDescription());
            currentItem.setPrice(item.getPrice());
            return itemReactiveRepository.save(currentItem);
        };
    }

    public Mono<Void> deleteAnItem(String itemId) {
        return itemReactiveRepository.deleteById(itemId);
    }

}

package com.reactivespring.itemservice.controller;

import com.reactivespring.itemservice.document.Item;
import com.reactivespring.itemservice.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item newItem) {
        return itemService.createAnItem(newItem);
    }

    @GetMapping("/items")
    public Flux<Item> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/items/{itemId}")
    public Mono<ResponseEntity<Item>> getAnItem(@PathVariable String itemId) {

        Mono<Item> anItemById = itemService.getAnItemById(itemId);

        return anItemById
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/items/{itemId}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable String itemId, @RequestBody Item newItem) {

        Mono<Item> updatedItem = itemService.updateItem(itemId, newItem);

        return updatedItem
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/items/{itemId}")
    public Mono<Void> deleteItem(@PathVariable String itemId) {
        return itemService.deleteAnItem(itemId);
    }
}

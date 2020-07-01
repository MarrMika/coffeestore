package com.coffeecraft.coffeestore.controller;

import com.coffeecraft.coffeestore.model.Coffee;
import com.coffeecraft.coffeestore.model.CoffeeEvent;
import com.coffeecraft.coffeestore.repository.CoffeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/coffees")
public class CoffeeController {
    private CoffeeRepository repository;

    @Autowired
    public CoffeeController(CoffeeRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Flux<Coffee> getAllProducts(){
        return repository.findAll();
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<Coffee>> getCoffee(@PathVariable String id){
        return repository.findById(id)
                .map(coffee -> ResponseEntity.ok(coffee))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Coffee> saveCoffee(@RequestBody Coffee coffee){
        return repository.save(coffee);
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<Coffee>> updateCoffee(@PathVariable String id,
                                                      @RequestBody Coffee coffee){
        return repository.findById(id)
                .flatMap(existingCoffee->{
                    existingCoffee.setName(coffee.getName());
                    existingCoffee.setPrice(coffee.getPrice());
                    return repository.save(existingCoffee);
                })
                .map(updatedCoffee-> ResponseEntity.ok(updatedCoffee))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> deleteCoffee(@PathVariable String id){
        return repository.findById(id)
                .flatMap(existingCoffee ->
                    repository.delete(existingCoffee)
                            .then(Mono.just(ResponseEntity.ok().<Void>build()))
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());

    }

    @DeleteMapping
    public Mono<Void> deleteAllCoffees(){
        return repository.deleteAll();
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CoffeeEvent> getCoffeeEvents(){
        return Flux.interval(Duration.ofSeconds(1))
                .map(coffeeId->
                        new CoffeeEvent(coffeeId, "Coffee event")
                );
    }



}

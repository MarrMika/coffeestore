package com.coffeecraft.coffeestore.repository;

import com.coffeecraft.coffeestore.model.Coffee;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CoffeeRepository extends ReactiveMongoRepository<Coffee, String> {
  //Flux<Coffee> findByNameAndOrderByPrice(Publisher<String> name);
}

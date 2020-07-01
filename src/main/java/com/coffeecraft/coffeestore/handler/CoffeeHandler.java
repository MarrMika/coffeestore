//package com.coffeecraft.coffeestore.handler;
//
//import com.coffeecraft.coffeestore.model.Coffee;
//import com.coffeecraft.coffeestore.model.CoffeeEvent;
//import com.coffeecraft.coffeestore.repository.CoffeeRepository;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.server.ServerRequest;
//import org.springframework.web.reactive.function.server.ServerResponse;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.time.Duration;
//
//import static org.springframework.http.MediaType.APPLICATION_JSON;
//import static org.springframework.web.reactive.function.BodyInserters.fromObject;
//
//@Component
//public class CoffeeHandler {
//    private CoffeeRepository repository;
//
//    public CoffeeHandler(CoffeeRepository repository) {
//        this.repository = repository;
//    }
//
//    public Mono<ServerResponse> getAllCoffees(ServerRequest request){
//        Flux<Coffee> coffees = repository.findAll();
//        return ServerResponse.ok()
//                .contentType(APPLICATION_JSON)
//                .body(coffees, Coffee.class);
//    }
//
//    public Mono<ServerResponse> getCoffee(ServerRequest request){
//        String id = request.pathVariable("id");
//        Mono<Coffee> coffeeMono = repository.findById(id);
//        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
//
//        return coffeeMono
//                .flatMap(coffee ->
//                    ServerResponse.ok()
//                        .contentType(APPLICATION_JSON)
//                        .body(fromObject(coffee)))
//                .switchIfEmpty(notFound);
//    }
//
//    public Mono<ServerResponse> saveCoffee(ServerRequest request) {
//        Mono<Coffee> coffeeMono = request.bodyToMono(Coffee.class);
//
//        return coffeeMono.flatMap(Coffee ->
//                ServerResponse.status(HttpStatus.CREATED)
//                        .contentType(APPLICATION_JSON)
//                        .body(repository.save(Coffee), Coffee.class)
//        );
//    }
//
//    public Mono<ServerResponse> updateCoffee(ServerRequest request){
//        String id = request.pathVariable("id");
//        Mono<Coffee> existingCoffeeMono = repository.findById(id);
//        Mono<Coffee> coffeeMono = request.bodyToMono(Coffee.class);
//        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
//
//        return coffeeMono.zipWith(existingCoffeeMono,
//                (coffee, existingCoffee) ->
//                new Coffee(existingCoffee.getId(), coffee.getName(), coffee.getPrice())
//        ).flatMap(coffee ->
//                ServerResponse.ok()
//                        .contentType(APPLICATION_JSON)
//                        .body(repository.save(coffee), Coffee.class)
//        ).switchIfEmpty(notFound);
//    }
//
//    public Mono<ServerResponse> deleteCoffee(ServerRequest request){
//        String id = request.pathVariable("id");
//        Mono<Coffee> coffeeMono = repository.findById(id);
//        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
//
//        return coffeeMono
//                .flatMap(
//                        existingCoffee->
//                                ServerResponse.ok()
//                                    .build(repository.delete(existingCoffee))
//                ).switchIfEmpty(notFound);
//    }
//
//    public Mono<ServerResponse> deleteAllCoffees(ServerRequest request){
//        return ServerResponse.ok()
//                .build(repository.deleteAll());
//    }
//
//    public Mono<ServerResponse> getCoffeeEvents(ServerRequest request){
//        Flux<CoffeeEvent> eventsFlux = Flux.interval(Duration.ofSeconds(1)).map(coffeeId->
//                        new CoffeeEvent(coffeeId, "Coffee event")
//        );
//
//        return ServerResponse.ok()
//            .contentType(MediaType.TEXT_EVENT_STREAM)
//            .body(eventsFlux, CoffeeEvent.class);
//    }
//}

package com.coffeecraft.coffeestore;

import com.coffeecraft.coffeestore.model.Coffee;
import com.coffeecraft.coffeestore.repository.CoffeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static java.lang.invoke.VarHandle.AccessMode.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RequestPredicates.method;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class CoffeeStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoffeeStoreApplication.class, args);
	}

	@Bean
	CommandLineRunner init(CoffeeRepository repository){
		return args -> {
			Flux<Coffee> coffeeFlux = Flux.just(
					new Coffee(null, "Latte", 33.34),
					new Coffee(null, "Espresso", 29.45),
					new Coffee(null,"Cappuccino", 55.54)
			).flatMap(repository::save);

			coffeeFlux.thenMany(repository.findAll())
					.subscribe(System.out::println);
		};
	}
//	@Bean
//	RouterFunction<ServerResponse> routes(CoffeeHandler handler) {
////		return route(GET("/coffees").and(accept(APPLICATION_JSON)), handler::getAllCoffees)
////                .andRoute(POST("/coffees").and(accept(APPLICATION_JSON)), handler::saveCoffee)
////                .andRoute(DELETE("/coffees").and(accept(APPLICATION_JSON)), handler::deleteAllCoffees)
////                .andRoute(GET("/coffees/events").and(accept(TEXT_EVENT_STREAM)), handler::getCoffeeEvents)
////                .andRoute(GET("/coffees/{id}").and(accept(APPLICATION_JSON)), handler::getCoffee)
////                .andRoute(PUT("/coffees/{id}"), handler::updateCoffee)
////                .andRoute(DELETE("/coffees/{id}").and(accept(APPLICATION_JSON)), handler::deleteCoffee);
//
//		return nest(path("/coffees"),
//                nest(accept(APPLICATION_JSON).or(contentType(APPLICATION_JSON)).or(contentType(TEXT_EVENT_STREAM)),
//                        route(GET("/"), handler::getAllCoffees)
//                            .andRoute(method(HttpMethod.POST), handler::saveCoffee)
//                            .andRoute(DELETE("/"), handler::deleteAllCoffees)
//                            .andRoute(GET("/events"), handler::getCoffeeEvents)
//                            .andNest(path("/{id}"),
//                                    route(method(HttpMethod.GET), handler::getCoffee)
//                                    .andRoute(method(HttpMethod.PUT), handler::updateCoffee)
//                                    .andRoute(method(HttpMethod.DELETE), handler::deleteCoffee)
//                            )
//                )
//        );
//	}
}

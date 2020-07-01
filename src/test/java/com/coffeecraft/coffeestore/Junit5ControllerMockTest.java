package com.coffeecraft.coffeestore;

import com.coffeecraft.coffeestore.controller.CoffeeController;
import com.coffeecraft.coffeestore.model.Coffee;
import com.coffeecraft.coffeestore.model.CoffeeEvent;
import com.coffeecraft.coffeestore.repository.CoffeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

public class Junit5ControllerMockTest {

    private WebTestClient testClient;

    private List<Coffee> expectedList;

    @Mock
    private CoffeeRepository repository;

    @InjectMocks
    private CoffeeController controller;

    @BeforeEach
    void beforeEach() {
        this.testClient =
                WebTestClient.bindToController(controller)
                        .configureClient()
                        .baseUrl("/coffees")
                        .build();

        this.expectedList = Arrays.asList(new Coffee("1", "Latte", 33.34));
    }

    @Test
    void testGetAllCoffees() {
        given(repository.findAll()).willAnswer(answer -> Flux.fromIterable(this.expectedList));

        List<Coffee> actual = testClient.get()
                .uri("/").exchange()
                .expectStatus().isOk()
                .expectBodyList(Coffee.class).returnResult().getResponseBody();
        assertEquals(actual.toString(), expectedList.toString());
    }

    @Test
    void testGetCoffeeInvalidIdNotFound() {
        given(repository.findById("fake")).willAnswer(answer -> Mono.empty());

        testClient.get().uri("/fake").exchange().expectStatus().isNotFound();
    }

    @Test
    void testCoffeeIdFound() {
        given(repository.findById(expectedList.get(0).getId())).willAnswer(answer -> Mono.just(expectedList.get(0)));

        Coffee expectedCoffee = expectedList.get(0);
        Coffee coffee = testClient.get().uri("/{id}", expectedCoffee.getId())
                .exchange().expectStatus().isOk().expectBody(Coffee.class)
                .returnResult().getResponseBody();

        assertEquals(coffee.getId(), expectedCoffee.getId());
        assertEquals(coffee.getName(), expectedCoffee.getName());
        assertEquals(coffee.getPrice(), expectedCoffee.getPrice());
    }

    @Test
    void testCoffeeEvents() {
        CoffeeEvent expectedEvent = new CoffeeEvent(0L, "Coffee Event");

        FluxExchangeResult<CoffeeEvent> result =
                testClient.get()
                        .uri("/events")
                        .accept(MediaType.TEXT_EVENT_STREAM).exchange()
                        .expectStatus().isOk()
                        .returnResult(CoffeeEvent.class);

        StepVerifier.create(result.getResponseBody())
                .expectNextMatches(exp -> exp.getEventId().equals(expectedEvent.getEventId()))
                .expectNextCount(2)
                .consumeNextWith(event ->
                        assertEquals(Long.valueOf(3), event.getEventId())
                ).thenCancel().verify();
    }
}

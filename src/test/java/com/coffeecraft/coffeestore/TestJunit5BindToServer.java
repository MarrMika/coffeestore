package com.coffeecraft.coffeestore;

import com.coffeecraft.coffeestore.model.Coffee;
import com.coffeecraft.coffeestore.model.CoffeeEvent;
import com.coffeecraft.coffeestore.repository.CoffeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestJunit5BindToServer {

    private WebTestClient testClient;

    private List<Coffee> expectedList;

    @Autowired
    private CoffeeRepository repository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void beforeEach() {
        this.testClient =
                WebTestClient.bindToServer()
                        .baseUrl("http://localhost:" + port + "/coffees")
                        .build();

        this.expectedList =
                repository.findAll().collectList().block();
    }

    @Test
    void testGetAllCoffees() {
        List<Coffee> actual = testClient.get()
                .uri("/").exchange()
                .expectStatus().isOk()
                .expectBodyList(Coffee.class).returnResult().getResponseBody();
        assertEquals(actual.toString(), expectedList.toString());
    }

    @Test
    void testGetCoffeeInvalidIdNotFound() {
        testClient.get().uri("/fake").exchange().expectStatus().isNotFound();
    }

    @Test
    void testCoffeeIdFound() {
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

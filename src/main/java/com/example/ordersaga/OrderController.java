package com.example.ordersaga;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderSagaService orderSagaService;
    private final StockClient stockClient;

    @PostMapping
    public Order create(@RequestBody CreateOrderRequest request) {
        return orderSagaService.createOrder(request);
    }

    @GetMapping
    public Collection<Order> list() {
        return orderSagaService.findAll();
    }

    @GetMapping("/events")
    public List<SagaEventLog> events() {
        return orderSagaService.findAllEvents();
    }

    @GetMapping("/inventory/{productId}")
    public QuantityResponse getInventory(@PathVariable String productId) {
        try {
            return stockClient.getQuantity(productId);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Inventory endpoint not found in stock service. Check stock.saga.inventory-path.",
                    ex
            );
        } catch (HttpStatusCodeException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Stock service call failed: " + ex.getStatusCode(),
                    ex
            );
        }
    }
}
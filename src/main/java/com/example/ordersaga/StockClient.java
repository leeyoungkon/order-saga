package com.example.ordersaga;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface StockClient {

    @GetExchange("${stock.saga.inventory-path:/api/inventory/{productId}}")
    QuantityResponse getQuantity(@PathVariable String productId);
}

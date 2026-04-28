package com.example.ordersaga;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class StockClient {

    private final RestClient stockSagaRestClient;
    private final String inventoryPath;

    public StockClient(
            RestClient stockSagaRestClient,
            @Value("${stock.saga.inventory-path:/api/inventory/{productId}}") String inventoryPath
    ) {
        this.stockSagaRestClient = stockSagaRestClient;
        this.inventoryPath = inventoryPath;
    }

    public QuantityResponse getQuantity(String productId) {
        return stockSagaRestClient.get()
                .uri(uriBuilder -> uriBuilder.path(inventoryPath).build(productId))
                .retrieve()
                .body(QuantityResponse.class);
    }
}

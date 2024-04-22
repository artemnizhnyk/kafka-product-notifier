package com.artemnizhnyk.kafkaproductmicroservice.service;

import com.artemnizhnyk.kafkaproductmicroservice.web.dto.CreateProductDto;
import com.artemnizhnyk.kafkaproductmicroservice.web.event.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    @Override
    public String createProduct(final CreateProductDto createProductDto) throws ExecutionException, InterruptedException {
        //TODO save to DB
        String productId = UUID.randomUUID().toString();

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(
                productId,
                createProductDto.getTitle(),
                createProductDto.getPrice(),
                createProductDto.getQuantity()
        );

        SendResult<String, ProductCreatedEvent> result = kafkaTemplate
                .send("product-created-events-topic", productId, productCreatedEvent).get();

        log.info(String.valueOf(result.getRecordMetadata()));
        log.info(Instant.now().toString());
        log.info("Returned: {}", productId);
        return productId;
    }
}
package com.project.order_service.service;

import brave.Tracer;
import com.project.order_service.Event.OrderPlacedEvent;
import com.project.order_service.dto.InventoryResponse;
import com.project.order_service.dto.OrderLinesItemsDto;
import com.project.order_service.dto.OrderRequest;
import com.project.order_service.model.Order;
import com.project.order_service.model.OrderLinesItems;
import com.project.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClient;
    private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;
    public String placeOrder(OrderRequest orderRequest)
    {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        log.info("UUID:" + UUID.randomUUID().toString());
        List<OrderLinesItemsDto> orderLinesItemsList = orderRequest.getOrderLinesItemsDtoList();
        List<OrderLinesItems> requiredOrderItemsList = createOrderList(orderLinesItemsList);
        order.setOrderLinesItemsList(requiredOrderItemsList);

        List<String>skuCodeList = order.getOrderLinesItemsList().stream()
                .map(OrderLinesItems::getSkuCode)
                .toList();

        InventoryResponse [] inventoryResponsesArray = webClient.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCodes",skuCodeList).build())
                .retrieve().bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductInStock = Arrays.stream(inventoryResponsesArray).allMatch(InventoryResponse::isInStock);
        if(allProductInStock)
        {
            orderRepository.save(order);
            log.info("oder saved successfully");
            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
            return "order placed Successfully";
        }
        else {
            throw new IllegalArgumentException("Product is not in stock, please try again after some time");
        }
    }

    private List<OrderLinesItems> createOrderList(List<OrderLinesItemsDto> orderLinesItemsList) {
        List<OrderLinesItems> orderLinesItemsList1 = new ArrayList<>();
        for(int i=0;i< orderLinesItemsList.size(); i++)
        {
            OrderLinesItems orderLinesItems = new OrderLinesItems();
            orderLinesItems.setPrice(orderLinesItemsList.get(i).getPrice());
            orderLinesItems.setQuantity(orderLinesItemsList.get(i).getQuantity());
            orderLinesItems.setSkuCode(orderLinesItemsList.get(i).getSkuCode());
            orderLinesItemsList1.add(orderLinesItems);
        }
        return orderLinesItemsList1;
    }
}

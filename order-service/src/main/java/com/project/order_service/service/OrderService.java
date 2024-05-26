package com.project.order_service.service;

import com.project.order_service.dto.OrderLinesItemsDto;
import com.project.order_service.dto.OrderRequest;
import com.project.order_service.model.Order;
import com.project.order_service.model.OrderLinesItems;
import com.project.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    public void placeOrder(OrderRequest orderRequest)
    {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        log.info("UUID:" + UUID.randomUUID().toString());
        List<OrderLinesItemsDto> orderLinesItemsList = orderRequest.getOrderLinesItemsDtoList();
        List<OrderLinesItems> requiredOrderItemsList = createOrderList(orderLinesItemsList);
        order.setOrderLinesItemsList(requiredOrderItemsList);
        orderRepository.save(order);
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

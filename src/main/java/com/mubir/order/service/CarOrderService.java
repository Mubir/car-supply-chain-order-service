package com.mubir.order.service;

import com.mubir.order.web.model.CarOrderDto;
import com.mubir.order.web.model.CarOrderPagedList;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CarOrderService {

    CarOrderPagedList listOrder(UUID customerId, Pageable pageable);
    CarOrderDto placeOrder(UUID customerId,CarOrderDto carOrderDto);
    CarOrderDto getOrderById(UUID customerId,UUID orderId);
    void pickupOrder(UUID customerId,UUID orderId);
}

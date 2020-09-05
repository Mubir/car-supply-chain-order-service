package com.mubir.order.service;

import com.mubir.order.domain.CarOrder;
import com.mubir.order.web.model.CarOrderDto;

import java.util.UUID;

public interface CarOrderManager {
    CarOrder newCarOrder(CarOrder carOrder);
    void processValidationResult(UUID carOrderId,Boolean isValid);
    void carOrderAllocationPassed(CarOrderDto carOrderDto);
    void carOrderAllocationPendingInventory(CarOrderDto carOrderDto);
    void carOrderAllocationFailed(CarOrderDto carOrderDto);
    void carOrderPickUp(UUID id);
    void cancelOrder(UUID id);
}

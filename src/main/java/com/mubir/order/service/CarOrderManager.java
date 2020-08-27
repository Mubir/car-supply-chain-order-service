package com.mubir.order.service;

import com.mubir.order.domain.CarOrder;

import java.util.UUID;

public interface CarOrderManager {
    CarOrder newCarOrder(CarOrder carOrder);
    void processValidationResult(UUID carOrderId,Boolean isValid);
}

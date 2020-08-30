package com.mubir.order.service.listeners;

import com.mubir.common.events.AllocateOrderResult;
import com.mubir.order.config.JmsConfig;
import com.mubir.order.service.CarOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CarOrderAllocationResultListener {
    private final CarOrderManager carOrderManager;
    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void listen(AllocateOrderResult result)
    {
        if(!result.getAllocationError() && !result.getPendingInventory())
        {
            carOrderManager.carOrderAllocationPassed(result.getCarOrderDto());
        }else if(!result.getAllocationError() && result.getPendingInventory())
        {
            carOrderManager.carOrderAllocationPendingInventory(result.getCarOrderDto());
        }else if(result.getAllocationError())
        {
            carOrderManager.carOrderAllocationFailed(result.getCarOrderDto());
        }
    }
}

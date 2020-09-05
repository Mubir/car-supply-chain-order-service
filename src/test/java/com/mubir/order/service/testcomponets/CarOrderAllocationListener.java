package com.mubir.order.service.testcomponets;

import com.mubir.common.events.AllocateOrderRequest;
import com.mubir.common.events.AllocateOrderResult;
import com.mubir.order.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CarOrderAllocationListener {

    private final JmsTemplate jmsTemplate;
    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(Message msg)
    {
        AllocateOrderRequest request = (AllocateOrderRequest) msg.getPayload();
        boolean pendingInventory = false;
        boolean allocationError = false;

        // set pending inventory
        if(request.getCarOrderDto().getCustomerRef()!= null && request.getCarOrderDto().getCustomerRef()
                .equals("partial-allocation"))
        {
            pendingInventory=true;
        }
        boolean sendResponse =true;
        // set allocation error
        if(request.getCarOrderDto().getCustomerRef()!= null && request.getCarOrderDto().getCustomerRef()
                .equals("partial-allocation"))
        {
            allocationError=true;
        }

        if(request.getCarOrderDto().getCustomerRef() != null)
        {
            if(request.getCarOrderDto().getCustomerRef().equals("fail-allocation"))
            {
                allocationError = true;
            }else if(request.getCarOrderDto().getCustomerRef().equals("partial-allocation"))
            {
                pendingInventory = true;
            }else if(request.getCarOrderDto().getCustomerRef().equals("dont-allocate"))
            {
                sendResponse = true;
            }
        }
        boolean finalPendingInventory= pendingInventory;
        request.getCarOrderDto().getCarOrderLines().forEach(carOrderLineDto -> {
           // carOrderLineDto.setQuantityAllocated(carOrderLineDto.getOrderQuantity());

            if(finalPendingInventory)
            {
                carOrderLineDto.setQuantityAllocated(carOrderLineDto.getOrderQuantity()-1);
            }else
            {
                carOrderLineDto.setQuantityAllocated(carOrderLineDto.getOrderQuantity());
            }
        });



        if(sendResponse)
        {
            jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE, AllocateOrderResult.builder()
                    .carOrderDto(request.getCarOrderDto())
                    .pendingInventory(pendingInventory)
                    .allocationError(allocationError)
                    .build());
        }
    }
}

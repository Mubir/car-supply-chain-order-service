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
        request.getCarOrderDto().getCarOrderLines().forEach(carOrderLineDto -> {
            carOrderLineDto.setQuantityAllocated(carOrderLineDto.getOrderQuantity());
        });

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE, AllocateOrderResult.builder()
        .carOrderDto(request.getCarOrderDto())
        .pendingInventory(false)
        .allocationError(false)
        .build());
    }
}

package com.mubir.order.service.testcomponets;

import com.mubir.common.events.ValidateOrderRequest;
import com.mubir.common.events.ValidateOrderResult;
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
public class CarOrderValidationListener {
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void list(Message msg)
    {
        ValidateOrderRequest request = (ValidateOrderRequest) msg.getPayload();

        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE,
                ValidateOrderResult.builder()
        .isValid(true)
        .orderId(request.getCarOrderDto().getId())
        .build());
    }
}

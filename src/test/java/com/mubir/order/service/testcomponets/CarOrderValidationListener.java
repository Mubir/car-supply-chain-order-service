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
        boolean isValid = true;
        boolean sendResponse = true;
        ValidateOrderRequest request = (ValidateOrderRequest) msg.getPayload();
       /* if(request.getCarOrderDto().getCustomerRef() != null && request.getCarOrderDto().getCustomerRef()
        .equals("fail-validation"))
        {
            isValid =false;
        }

        */
        if(request.getCarOrderDto().getCustomerRef() != null)
        {
            if(request.getCarOrderDto().getCustomerRef().equals("fail-validation"))
            {
            isValid = false;
            }else if(request.getCarOrderDto().getCustomerRef().equals("dont-validate"))
            {
            sendResponse = false;
            }
        }
        if(sendResponse) {
            jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE,
                    ValidateOrderResult.builder()
                            .isValid(isValid)
                            .orderId(request.getCarOrderDto().getId())
                            .build());
        }
    }
}

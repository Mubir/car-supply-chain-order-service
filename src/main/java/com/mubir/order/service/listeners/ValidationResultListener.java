package com.mubir.order.service.listeners;

import com.mubir.common.events.ValidateOrderRequest;
import com.mubir.common.events.ValidateOrderResult;
import com.mubir.order.config.JmsConfig;
import com.mubir.order.service.CarOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ValidationResultListener {
    private final CarOrderManager carOrderManager;
    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE)
    public void listen(ValidateOrderResult result)
    {
        final UUID carOrderId = result.getOrderId();
        log.debug("validated order id :"+carOrderId);
        carOrderManager.processValidationResult(carOrderId,result.getIsValid());
    }
}

package com.mubir.order.statemachine.actions;

import com.mubir.common.events.AllocationFailureEvent;
import com.mubir.order.config.JmsConfig;
import com.mubir.order.domain.CarOrderEventEnum;
import com.mubir.order.domain.CarOrderStatusEnum;
import com.mubir.order.service.CarOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationFailureAction implements Action<CarOrderStatusEnum, CarOrderEventEnum> {

    private final JmsTemplate jmsTemplate;


    @Override
    public void execute(StateContext<CarOrderStatusEnum, CarOrderEventEnum> stateContext) {
        String carOrderId = (String) stateContext.getMessage().getHeaders()
                .get(CarOrderManagerImpl.ORDER_ID_HEADER);

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_FAILURE_QUEUE, AllocationFailureEvent.builder()
        .orderId(UUID.fromString(carOrderId))
        .build());

        log.debug("Sent allocaiton failure:"+carOrderId);
    }
}

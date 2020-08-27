package com.mubir.order.statemachine.actions;


import com.mubir.common.events.ValidateOrderRequest;
import com.mubir.order.config.JmsConfig;
import com.mubir.order.domain.CarOrder;
import com.mubir.order.domain.CarOrderEventEnum;
import com.mubir.order.domain.CarOrderStatusEnum;
import com.mubir.order.repositories.CarOrderRepository;
import com.mubir.order.service.CarOrderManager;
import com.mubir.order.service.CarOrderManagerImpl;
import com.mubir.order.web.mapper.CarOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateOrderAction implements Action<CarOrderStatusEnum, CarOrderEventEnum> {
    private final CarOrderRepository carOrderRepository;
    private final CarOrderMapper carOrderMapper;
    private final JmsTemplate jmsTemplate;
    @Override
    public void execute(StateContext<CarOrderStatusEnum, CarOrderEventEnum> stateContext) {
        String carOrderId = (String) stateContext.getMessage().getHeaders().get(CarOrderManagerImpl.ORDER_ID_HEADER);
        CarOrder carOrder = carOrderRepository.findOneById(UUID.fromString(carOrderId));

        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_QUEUE, ValidateOrderRequest.builder()
        .carOrderDto(carOrderMapper.carOrderToDto(carOrder)).build()
        );

        log.debug(" Validation request sent for id "+carOrderId);
    }
}

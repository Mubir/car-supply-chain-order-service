package com.mubir.order.statemachine.actions;

import com.mubir.common.events.AllocateOrderRequest;
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

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class AllocateOrderAction implements Action<CarOrderStatusEnum, CarOrderEventEnum> {
    private final JmsTemplate jmsTemplate;
    private final CarOrderRepository carOrderRepository;
    private final CarOrderMapper carOrderMapper;
    @Override
    public void execute(StateContext<CarOrderStatusEnum, CarOrderEventEnum> stateContext) {
        String carOrderId = (String) stateContext.getMessage().getHeaders().get(CarOrderManagerImpl.ORDER_ID_HEADER);
       // CarOrder  carOrder =carOrderRepository.findOneById(UUID.fromString(carOrderId));
        Optional<CarOrder> carOrderOptional = carOrderRepository.findById(UUID.fromString(carOrderId));


      //  jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_QUEUE,carOrderMapper.carOrderToDto(carOrder));

        carOrderOptional.ifPresentOrElse( carOrder -> {
            jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_QUEUE,
                   // carOrderMapper.carOrderToDto(carOrderOptional.get()));
                    AllocateOrderRequest.builder()
                    .carOrderDto(carOrderMapper.carOrderToDto(carOrder))
                    .build());
            log.debug(" order id "+carOrderId);
        },()->log.error("car not found"));
        log.debug("Sent allocation request order id"+carOrderId);
    }
}

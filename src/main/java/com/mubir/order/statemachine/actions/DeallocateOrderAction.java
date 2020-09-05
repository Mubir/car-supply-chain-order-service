package com.mubir.order.statemachine.actions;

import com.mubir.common.events.DeallocateOrderRequest;
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

@Slf4j
@RequiredArgsConstructor
@Component
public class DeallocateOrderAction implements Action<CarOrderStatusEnum, CarOrderEventEnum> {


    private final JmsTemplate jmsTemplate;
    private final CarOrderRepository carOrderRepository;
    private final CarOrderMapper carOrderMapper;

    @Override
    public void execute(StateContext<CarOrderStatusEnum, CarOrderEventEnum> stateContext) {
        String carOrderId =(String) stateContext.getMessage().getHeaders().get(CarOrderManagerImpl.ORDER_ID_HEADER);
        Optional<CarOrder> carOrderOptional = carOrderRepository.findById(UUID.fromString(carOrderId));

        carOrderOptional.ifPresentOrElse(
                carOrder -> {
                    jmsTemplate.convertAndSend(JmsConfig.DEALLOCATE_ORDER_QUEUE, DeallocateOrderRequest.builder()
                    .carOrderDto(carOrderMapper.carOrderToDto(carOrder)).build());
                },() -> log.error("Car order not found")
        );
    }
}

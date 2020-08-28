package com.mubir.order.service;

import com.mubir.order.domain.CarOrder;
import com.mubir.order.domain.CarOrderEventEnum;
import com.mubir.order.domain.CarOrderStatusEnum;
import com.mubir.order.repositories.CarOrderRepository;
import com.mubir.order.statemachine.CarOrderStateChangeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarOrderManagerImpl implements CarOrderManager{
    public static final String ORDER_ID_HEADER = "ORDER_ID_HEADER";
    private final StateMachineFactory<CarOrderStatusEnum, CarOrderEventEnum> stateMachineFactory;
    private final CarOrderRepository carOrderRepository;
    private final CarOrderStateChangeInterceptor carOrderStateChangeInterceptor;

    @Transactional
    @Override
    public CarOrder newCarOrder(CarOrder carOrder) {
        carOrder.setId(null);
        carOrder.setOrderStatus(CarOrderStatusEnum.NEW);
        CarOrder savedCarOrder = carOrderRepository.save(carOrder);
        sendCarOrderEvent(savedCarOrder,CarOrderEventEnum.VALIDATE_ORDER);
        return savedCarOrder;
    }

    @Override
    public void processValidationResult(UUID carOrderId, Boolean isValid) {
        CarOrder carOrder = carOrderRepository.getOne(carOrderId);

        if(isValid)
        {
            sendCarOrderEvent(carOrder,CarOrderEventEnum.VALIDATION_PASSED);
            CarOrder validatedOrder = carOrderRepository.findOneById(carOrderId);
            sendCarOrderEvent(validatedOrder,CarOrderEventEnum.ALLOCATE_ORDER);
        }else
        {
            sendCarOrderEvent(carOrder,CarOrderEventEnum.VALIDATION_FAILED);
        }
    }

    private void sendCarOrderEvent(CarOrder carOrder,CarOrderEventEnum eventEnum){
        StateMachine<CarOrderStatusEnum,CarOrderEventEnum>  sm = build(carOrder);
        Message msg = MessageBuilder.withPayload(eventEnum)
                .setHeader(ORDER_ID_HEADER,carOrder.getId().toString()).build();
        sm.sendEvent(msg);
    }

    private StateMachine<CarOrderStatusEnum,CarOrderEventEnum> build(CarOrder carOrder)
    {
        StateMachine<CarOrderStatusEnum,CarOrderEventEnum> sm = stateMachineFactory.getStateMachine(carOrder.getId());
        sm.stop();
        sm.getStateMachineAccessor().doWithAllRegions(
                sma->{

                    sma.addStateMachineInterceptor(carOrderStateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(carOrder.getOrderStatus(),null,null,null));
                }
        );

        sm.start();
        return sm;
    }
}

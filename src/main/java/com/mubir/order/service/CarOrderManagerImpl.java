package com.mubir.order.service;

import com.mubir.order.domain.CarOrder;
import com.mubir.order.domain.CarOrderEventEnum;
import com.mubir.order.domain.CarOrderStatusEnum;
import com.mubir.order.repositories.CarOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarOrderManagerImpl implements CarOrderManager{
    private final StateMachineFactory<CarOrderStatusEnum, CarOrderEventEnum> stateMachineFactory;
    private final CarOrderRepository carOrderRepository;

    @Transactional
    @Override
    public CarOrder newCarOrder(CarOrder carOrder) {
        carOrder.setId(null);
        carOrder.setOrderStatus(CarOrderStatusEnum.NEW);
        CarOrder savedCarOrder = carOrderRepository.save(carOrder);
        sendCarOrderEvent(savedCarOrder,CarOrderEventEnum.VALIDATE_ORDER);
        return savedCarOrder;
    }

    private void sendCarOrderEvent(CarOrder carOrder,CarOrderEventEnum eventEnum){
        StateMachine<CarOrderStatusEnum,CarOrderEventEnum>  sm = build(carOrder);
        Message msg = MessageBuilder.withPayload(eventEnum).build();
        sm.sendEvent(msg);
    }

    private StateMachine<CarOrderStatusEnum,CarOrderEventEnum> build(CarOrder carOrder)
    {
        StateMachine<CarOrderStatusEnum,CarOrderEventEnum> sm = stateMachineFactory.getStateMachine(carOrder.getId());
        sm.stop();
        sm.getStateMachineAccessor().doWithAllRegions(
                sma->{
                    sma.resetStateMachine(new DefaultStateMachineContext<>(carOrder.getOrderStatus(),null,null,null));
                }
        );

        sm.start();
        return sm;
    }
}

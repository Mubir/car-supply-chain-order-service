package com.mubir.order.statemachine;

import com.mubir.order.domain.CarOrder;
import com.mubir.order.domain.CarOrderEventEnum;
import com.mubir.order.domain.CarOrderStatusEnum;
import com.mubir.order.repositories.CarOrderRepository;
import com.mubir.order.service.CarOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CarOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<CarOrderStatusEnum, CarOrderEventEnum> {

    private final CarOrderRepository carOrderRepository;

    @Override
    public void preStateChange(State<CarOrderStatusEnum, CarOrderEventEnum> state, Message<CarOrderEventEnum> message, Transition<CarOrderStatusEnum, CarOrderEventEnum> transition,
                               StateMachine<CarOrderStatusEnum, CarOrderEventEnum> stateMachine, StateMachine<CarOrderStatusEnum, CarOrderEventEnum> rootStateMachine) {
        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable((String)msg.getHeaders().getOrDefault(CarOrderManagerImpl.ORDER_ID_HEADER,"")))
                .ifPresent(orderId ->{
                    log.debug("Saving state for order id:"+orderId+" Status: "+state.getId());

                    CarOrder carOrder = carOrderRepository.getOne(UUID.fromString(orderId));
                    carOrder.setOrderStatus(state.getId());
                    carOrderRepository.saveAndFlush(carOrder);
                });
    }
}

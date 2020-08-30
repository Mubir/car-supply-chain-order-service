package com.mubir.order.statemachine;

import com.mubir.order.domain.CarOrderEventEnum;
import com.mubir.order.domain.CarOrderStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;
@RequiredArgsConstructor
@Configuration
@EnableStateMachineFactory
public class CarOrderStateMachineConfig extends StateMachineConfigurerAdapter<CarOrderStatusEnum, CarOrderEventEnum> {
    private final Action<CarOrderStatusEnum,CarOrderEventEnum> validateOrderAction;
    private final Action<CarOrderStatusEnum,CarOrderEventEnum> allocateOrderAction;
    @Override
    public void configure(StateMachineStateConfigurer<CarOrderStatusEnum, CarOrderEventEnum>
                                      states) throws Exception {
        states.withStates()
                .initial(CarOrderStatusEnum.NEW)
                .states(EnumSet.allOf(CarOrderStatusEnum.class))
                .end(CarOrderStatusEnum.PICKED_UP)
                .end(CarOrderStatusEnum.DELIVERED)
                .end(CarOrderStatusEnum.DELIVERY_EXCEPTION)
                .end(CarOrderStatusEnum.VALIDATION_EXCEPTION)
                .end(CarOrderStatusEnum.ALLOCATION_EXCEPTION);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<CarOrderStatusEnum, CarOrderEventEnum> transitions) throws Exception {
        transitions.withExternal()
                .source(CarOrderStatusEnum.NEW).target(CarOrderStatusEnum.VALIDATION_PENDING)
                .event(CarOrderEventEnum.VALIDATE_ORDER)
                .action(validateOrderAction)
                .and().withExternal()
                .source(CarOrderStatusEnum.NEW).target(CarOrderStatusEnum.VALIDATED)
                .event(CarOrderEventEnum.VALIDATION_PASSED)
                .and().withExternal()
                .source(CarOrderStatusEnum.NEW).target(CarOrderStatusEnum.VALIDATION_EXCEPTION)
                .event(CarOrderEventEnum.VALIDATION_FAILED)
                .and().withExternal()
                .source(CarOrderStatusEnum.VALIDATED).target(CarOrderStatusEnum.ALLOCATION_PENDING)
                .event(CarOrderEventEnum.ALLOCATE_ORDER)
                .action(allocateOrderAction)
                .and().withExternal()
                .source(CarOrderStatusEnum.ALLOCATION_PENDING).target(CarOrderStatusEnum.ALLOCATED)
                .event(CarOrderEventEnum.ALLOCATION_SUCCESS)
                .and().withExternal()
                .source(CarOrderStatusEnum.ALLOCATION_PENDING).target(CarOrderStatusEnum.ALLOCATION_EXCEPTION)
                .event(CarOrderEventEnum.ALLOCATION_FAILED)
                .and().withExternal()
                .source(CarOrderStatusEnum.ALLOCATION_PENDING).target(CarOrderStatusEnum.PENDING_INVENTORY)
                .event(CarOrderEventEnum.ALLOCATION_ON_INVENTORY);
    }
}

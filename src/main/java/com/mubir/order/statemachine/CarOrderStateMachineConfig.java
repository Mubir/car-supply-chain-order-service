package com.mubir.order.statemachine;

import com.mubir.order.domain.CarOrderEventEnum;
import com.mubir.order.domain.CarOrderStatusEnum;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class CarOrderStateMachineConfig extends StateMachineConfigurerAdapter<CarOrderStatusEnum, CarOrderEventEnum> {

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
}

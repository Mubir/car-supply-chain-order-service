package com.mubir.order.statemachine.actions;

import com.mubir.order.domain.CarOrderEventEnum;
import com.mubir.order.domain.CarOrderStatusEnum;
import com.mubir.order.service.CarOrderManagerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ValidationFailureAction implements Action<CarOrderStatusEnum, CarOrderEventEnum> {

    @Override
    public void execute(StateContext<CarOrderStatusEnum, CarOrderEventEnum> stateContext) {
        String carOrderId = (String) stateContext.getMessage().getHeaders().get(CarOrderManagerImpl.ORDER_ID_HEADER);
        log.error("Compnesationg traansaction ...."+carOrderId);
    }
}

package com.mubir.order.service;

import com.mubir.order.domain.CarOrder;
import com.mubir.order.domain.CarOrderEventEnum;
import com.mubir.order.domain.CarOrderStatusEnum;
import com.mubir.order.repositories.CarOrderRepository;
import com.mubir.order.statemachine.CarOrderStateChangeInterceptor;
import com.mubir.order.web.model.CarOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class CarOrderManagerImpl implements CarOrderManager{
    public static final String ORDER_ID_HEADER = "ORDER_ID_HEADER";
    private final StateMachineFactory<CarOrderStatusEnum, CarOrderEventEnum> stateMachineFactory;
    private final CarOrderRepository carOrderRepository;
    private final CarOrderStateChangeInterceptor carOrderStateChangeInterceptor;
    private final EntityManager entityManager;

    @Transactional
    @Override
    public CarOrder newCarOrder(CarOrder carOrder) {
        carOrder.setId(null);
        carOrder.setOrderStatus(CarOrderStatusEnum.NEW);
       // CarOrder savedCarOrder = carOrderRepository.save(carOrder);
        CarOrder savedCarOrder = carOrderRepository.saveAndFlush(carOrder);
        sendCarOrderEvent(savedCarOrder,CarOrderEventEnum.VALIDATE_ORDER);
        return savedCarOrder;
    }

    @Transactional
    @Override
    public void processValidationResult(UUID carOrderId, Boolean isValid) {
       // CarOrder carOrder = carOrderRepository.getOne(carOrderId);
        log.debug(" validation id :"+carOrderId+" status: "+isValid);
        entityManager.flush();
        Optional<CarOrder> carOrderOptional = carOrderRepository.findById(carOrderId);
       /*
        if(isValid)
        {
            sendCarOrderEvent(carOrder,CarOrderEventEnum.VALIDATION_PASSED);
            CarOrder validatedOrder = carOrderRepository.findOneById(carOrderId);
            sendCarOrderEvent(validatedOrder,CarOrderEventEnum.ALLOCATE_ORDER);
        }else
        {
            sendCarOrderEvent(carOrder,CarOrderEventEnum.VALIDATION_FAILED);
        }
        */

        carOrderOptional.ifPresentOrElse(carOrder -> {
            if(isValid){
                sendCarOrderEvent(carOrder, CarOrderEventEnum.VALIDATION_PASSED);

                CarOrder validatedOrder = carOrderRepository.findById(carOrderId).get();

                sendCarOrderEvent(validatedOrder, CarOrderEventEnum.ALLOCATE_ORDER);

            } else {
                sendCarOrderEvent(carOrder, CarOrderEventEnum.VALIDATION_FAILED);
            }
        }, () -> log.error("Order Not Found. Id: " + carOrderId));
    }

    @Override
    public void carOrderAllocationPassed(CarOrderDto carOrderDto) {
        /*CarOrder carOrder = carOrderRepository.getOne(carOrderDto.getId());
        sendCarOrderEvent(carOrder,CarOrderEventEnum.ALLOCATION_SUCCESS);
        updateAllocatedQuantity(carOrderDto,carOrder);

         */
        Optional<CarOrder> beerOrderOptional = carOrderRepository.findById(carOrderDto.getId());

        beerOrderOptional.ifPresentOrElse(carOrder -> {
            sendCarOrderEvent(carOrder, CarOrderEventEnum.ALLOCATION_SUCCESS);
            updateAllocatedQuantity(carOrderDto);
        }, () -> log.error("Order Id Not Found: " + carOrderDto.getId() ));
    }
    //private void updateAllocatedQuantity(CarOrderDto carOrderDto,CarOrder carOrder)
    private void updateAllocatedQuantity(CarOrderDto carOrderDto)
    {
        /*
        CarOrder allocatedOrder = carOrderRepository.getOne(carOrderDto.getId());

        allocatedOrder.getCarOrderLines().forEach( carOrderLine -> {
            carOrderDto.getCarOrderLines().forEach(carOrderLineDto -> {
                if(carOrderLine.getId().equals(carOrderLineDto.getId()))
                {
                    carOrderLine.setQuantityAllocated(carOrderLineDto.getQuantityAllocated());
                }
            });
        });

         */
        Optional<CarOrder> allocatedOrderOptional = carOrderRepository.findById(carOrderDto.getId());

        allocatedOrderOptional.ifPresentOrElse(allocatedOrder -> {
            allocatedOrder.getCarOrderLines().forEach(carOrderLine -> {
                carOrderDto.getCarOrderLines().forEach(carOrderLineDto -> {
                    if(carOrderLine.getId() .equals(carOrderLineDto.getId())){
                        carOrderLine.setQuantityAllocated(carOrderLineDto.getQuantityAllocated());
                    }
                });
            });

            carOrderRepository.saveAndFlush(allocatedOrder);
        }, () -> log.error("Order Not Found. Id: " + carOrderDto.getId()));
    }
    @Override
    public void carOrderAllocationPendingInventory(CarOrderDto carOrderDto) {
       /* CarOrder carOrder = carOrderRepository.getOne(carOrderDto.getId());
        sendCarOrderEvent(carOrder,CarOrderEventEnum.ALLOCATION_ON_INVENTORY);
        updateAllocatedQuantity(carOrderDto,carOrder);
        */
        Optional<CarOrder> beerOrderOptional = carOrderRepository.findById(carOrderDto.getId());

        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            sendCarOrderEvent(beerOrder, CarOrderEventEnum.ALLOCATION_ON_INVENTORY);

            updateAllocatedQuantity(carOrderDto);
        }, () -> log.error("Order Id Not Found: " + carOrderDto.getId() ));
    }

    @Override
    public void carOrderAllocationFailed(CarOrderDto carOrderDto) {
        /*
        CarOrder carOrder = carOrderRepository.getOne(carOrderDto.getId());
        sendCarOrderEvent(carOrder,CarOrderEventEnum.ALLOCATION_FAILED);

         */
        Optional<CarOrder> beerOrderOptional = carOrderRepository.findById(carOrderDto.getId());

        beerOrderOptional.ifPresentOrElse(carOrder -> {
            sendCarOrderEvent(carOrder, CarOrderEventEnum.ALLOCATION_FAILED);
        }, () -> log.error("Order Not Found. Id: " + carOrderDto.getId()) );
    }

    @Override
    public void carOrderPickUp(UUID id) {
        Optional<CarOrder> carOrderOptional = carOrderRepository.findById(id);
        carOrderOptional.ifPresentOrElse(carOrder -> {
            sendCarOrderEvent(carOrder,CarOrderEventEnum.CARORDER_PICKED_UP);
        },()-> log.error("order not found "+id));
    }

    @Override
    public void cancelOrder(UUID id) {
        carOrderRepository.findById(id).ifPresentOrElse(
                carOrder -> {
                    sendCarOrderEvent(carOrder,CarOrderEventEnum.CANCEL_ORDER);
                },()->log.error("Order not found "+id)
        );
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

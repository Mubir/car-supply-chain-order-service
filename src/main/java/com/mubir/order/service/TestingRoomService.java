package com.mubir.order.service;

import com.mubir.order.bootstrap.CarOrderBootstrap;
import com.mubir.order.domain.Customer;
import com.mubir.order.repositories.CarOrderRepository;
import com.mubir.order.repositories.CustomerRepository;
import com.mubir.order.web.model.CarOrderDto;
import com.mubir.order.web.model.CarOrderLineDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class TestingRoomService {
    private final CustomerRepository customerRepository;
    private final CarOrderService carOrderService;
    private final CarOrderRepository carOrderRepository;
    private final List<String> carUpcs = new ArrayList<>(3);

    public TestingRoomService(CustomerRepository customerRepository, CarOrderService carOrderService, CarOrderRepository carOrderRepository) {
        this.customerRepository = customerRepository;
        this.carOrderService = carOrderService;
        this.carOrderRepository = carOrderRepository;

        carUpcs.add(CarOrderBootstrap.CAR_1_UPC);
        carUpcs.add(CarOrderBootstrap.CAR_2_UPC);
        carUpcs.add(CarOrderBootstrap.CAR_3_UPC);

    }
    @Transactional
    @Scheduled(fixedRate = 2000)
    public void placeTastingRoomOrder()
    {
        List<Customer> customerList = customerRepository.findAllByCustomerNameLike(CarOrderBootstrap.TASTING_ROOM);
        if(customerList.size() == 1)
        {
            doPlaceOrder(customerList.get(0));
        }else
        {
            log.error("too many/few customer");
        }
    }

    private void doPlaceOrder(Customer customer)
    {
        String carToOrder = getRandomCarUpc();
        CarOrderLineDto carOrderLine = CarOrderLineDto.builder()
                .upc(carToOrder)
                .orderQuantity(new Random().nextInt(6))
                .build();

        List<CarOrderLineDto> carOrderLineList = new ArrayList<>();
        carOrderLineList.add(carOrderLine);

        CarOrderDto carOrder = CarOrderDto.builder()
                .customerId(customer.getId())
                .customerRef(UUID.randomUUID().toString())
                .carOrderLines(carOrderLineList)
                .build();
        CarOrderDto saveOrder = carOrderService.placeOrder(customer.getId(),carOrder);
    }

    private String getRandomCarUpc(){
        return carUpcs.get(new Random().nextInt(carUpcs.size() - 0));
    }
}

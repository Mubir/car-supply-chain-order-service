package com.mubir.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;

import com.mubir.order.config.JmsConfig;
import com.mubir.order.domain.CarOrder;
import com.mubir.order.domain.CarOrderLine;
import com.mubir.order.domain.CarOrderStatusEnum;
import com.mubir.order.domain.Customer;
import com.mubir.order.repositories.CarOrderRepository;
import com.mubir.order.repositories.CustomerRepository;
import com.mubir.order.service.car.CarServiceImpl;
import com.mubir.order.web.model.CarDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

//@ExtendWith(WireMockExtension.class)
@SpringBootTest
@ExtendWith(WireMockExtension.class)
class CarOrderManagerImplIT {
    @Autowired
    CarOrderManager carOrderManager;
    @Autowired
    CarOrderRepository carOrderRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WireMockServer wireMockServer;

    Customer testCostomer;

    UUID carId = UUID.randomUUID();

    @Transactional
    static class RestTemplateBuilderProvider
    {
        @Bean(destroyMethod = "stop")
        public WireMockServer wireMockServer(){
            WireMockServer server = with(wireMockConfig().port(8083));
            server.start();
            return server;
        }
    }

    @BeforeEach
    void setUp()
    {
        testCostomer = customerRepository.save(Customer.builder().customerName("test").build());
    }
    @Test
    void testNewToAllocated() throws JsonProcessingException,InterruptedException
    {
        CarDto carDto = CarDto.builder().id(carId).upc("12345").build();
        wireMockServer.stubFor(get(CarServiceImpl.CAR_PATH_V1+"12345")
        .willReturn(okJson(objectMapper.writeValueAsString(carDto))));

        CarOrder carOrder = createCarOrder();

        CarOrder saveCarOrder = carOrderManager.newCarOrder(carOrder);

        await().untilAsserted(() ->{
            CarOrder foundOrder = carOrderRepository.findById(carOrder.getId()).get();

            assertEquals(CarOrderStatusEnum.ALLOCATION_PENDING,foundOrder.getOrderStatus());
        });

        CarOrder saveCarOrderTwo = carOrderRepository.findById(saveCarOrder.getId()).get();
        assertNotNull(saveCarOrder);
        assertEquals(CarOrderStatusEnum.ALLOCATED,saveCarOrderTwo.getOrderStatus());
    }

    @Test
    void testFailedValidation() throws JsonProcessingException{
        CarDto carDto = CarDto.builder().id(carId).upc("12345").build();
        wireMockServer.stubFor(get(CarServiceImpl.CAR_PATH_V1+"12345")
                .willReturn(okJson(objectMapper.writeValueAsString(carDto))));

        CarOrder carOrder = createCarOrder();
        carOrder.setCustomerRef("fail-validation");
        CarOrder saveCarOrder = carOrderManager.newCarOrder(carOrder);

        await().untilAsserted(() ->{
            CarOrder foundOrder = carOrderRepository.findById(carOrder.getId()).get();

            assertEquals(CarOrderStatusEnum.VALIDATION_EXCEPTION,foundOrder.getOrderStatus());
        });

    }
    @Test
    void testNewToPickedUp() throws JsonProcessingException
    {
        CarDto carDto = CarDto.builder().id(carId).upc("12345").build();
        wireMockServer.stubFor(get(CarServiceImpl.CAR_PATH_V1+"12345")
                .willReturn(okJson(objectMapper.writeValueAsString(carDto))));

        CarOrder carOrder = createCarOrder();

        CarOrder saveCarOrder = carOrderManager.newCarOrder(carOrder);

        await().untilAsserted(() ->{
            CarOrder foundOrder = carOrderRepository.findById(carOrder.getId()).get();

            assertEquals(CarOrderStatusEnum.ALLOCATED,foundOrder.getOrderStatus());
        });

        carOrderManager.carOrderPickedUp(saveCarOrder.getId());
        await().untilAsserted(() ->{
            CarOrder foundOrder = carOrderRepository.findById(carOrder.getId()).get();

            assertEquals(CarOrderStatusEnum.PICKED_UP,foundOrder.getOrderStatus());
        });

        CarOrder pickedUpOrder = carOrderRepository.findById(saveCarOrder.getId()).get();

        assertEquals(CarOrderStatusEnum.PICKED_UP,pickedUpOrder.getOrderStatus());

    }


    @Test
    void testAllocationFailure() throws JsonProcessingException
    {
        CarDto carDto = CarDto.builder().id(carId).upc("12345").build();
        wireMockServer.stubFor(get(CarServiceImpl.CAR_PATH_V1+"12345")
                .willReturn(okJson(objectMapper.writeValueAsString(carDto))));

        CarOrder carOrder = createCarOrder();
        carOrder.setCustomerRef("fail-allocation");
        CarOrder saveCarOrder = carOrderManager.newCarOrder(carOrder);

        await().untilAsserted(() ->{
            CarOrder foundOrder = carOrderRepository.findById(carOrder.getId()).get();

            assertEquals(CarOrderStatusEnum.ALLOCATION_EXCEPTION,foundOrder.getOrderStatus());
        });

        AllocationFailureEvent allocationFailureEvent = (AllocationFailureEvent)jmsTemplate.receiveAndConvert(JmsConfig.ALLOCATE_FAILURE_QUEUE);

        assertNotNull(allocationFailureEvent);
        assertThat(allocationFailureEvent.getOrder()).isEqualTo(saveCarOrder.getId());
    }

    @Test
    void testPartialAllocation() throws JsonProcessingException
    {
        CarDto carDto = CarDto.builder().id(carId).upc("12345").build();
        wireMockServer.stubFor(get(CarServiceImpl.CAR_PATH_V1+"12345")
                .willReturn(okJson(objectMapper.writeValueAsString(carDto))));

        CarOrder carOrder = createCarOrder();

        CarOrder saveCarOrder = carOrderManager.newCarOrder(carOrder);

        await().untilAsserted(() ->{
            CarOrder foundOrder = carOrderRepository.findById(carOrder.getId()).get();

            assertEquals(CarOrderStatusEnum.PENDING_INVENTORY,foundOrder.getOrderStatus());
        });
    }

    @Test
    void testValidationPendingToCancel() throws JsonProcessingException
    {
        CarDto carDto = CarDto.builder().id(carId).upc("12345").build();
        wireMockServer.stubFor(get(CarServiceImpl.CAR_PATH_V1+"12345")
                .willReturn(okJson(objectMapper.writeValueAsString(carDto))));

        CarOrder carOrder = createCarOrder();
        carOrder.setCustomerRef("dont-validate");
        CarOrder saveCarOrder = carOrderManager.newCarOrder(carOrder);

        await().untilAsserted(() ->{
            CarOrder foundOrder = carOrderRepository.findById(carOrder.getId()).get();

            assertEquals(CarOrderStatusEnum.VALIDATION_PENDING,foundOrder.getOrderStatus());
        });
        carOrderManager.cancelOrder(saveCarOrder.getId());
        await().untilAsserted(() ->{
            CarOrder foundOrder = carOrderRepository.findById(carOrder.getId()).get();

            assertEquals(CarOrderStatusEnum.CANCELLED,foundOrder.getOrderStatus());
        });
    }

    @Test
    void testAllocationPendingToCancel() throws JsonProcessingException
    {
        CarDto carDto = CarDto.builder().id(carId).upc("12345").build();
        wireMockServer.stubFor(get(CarServiceImpl.CAR_PATH_V1+"12345")
                .willReturn(okJson(objectMapper.writeValueAsString(carDto))));

        CarOrder carOrder = createCarOrder();
        carOrder.setCustomerRef("dont-allocate");
        CarOrder saveCarOrder = carOrderManager.newCarOrder(carOrder);

        await().untilAsserted(() ->{
            CarOrder foundOrder = carOrderRepository.findById(carOrder.getId()).get();

            assertEquals(CarOrderStatusEnum.ALLOCATION_PENDING,foundOrder.getOrderStatus());
        });
        carOrderManager.cancelOrder(saveCarOrder.getId());
        await().untilAsserted(() ->{
            CarOrder foundOrder = carOrderRepository.findById(carOrder.getId()).get();

            assertEquals(CarOrderStatusEnum.CANCELLED,foundOrder.getOrderStatus());
        });
    }

    @Test
    void testAllocatedToCancel() throws JsonProcessingException
    {
        CarDto carDto = CarDto.builder().id(carId).upc("12345").build();
        wireMockServer.stubFor(get(CarServiceImpl.CAR_PATH_V1+"12345")
                .willReturn(okJson(objectMapper.writeValueAsString(carDto))));

        CarOrder carOrder = createCarOrder();
      //  carOrder.setCustomerRef("dont-allocate");
        CarOrder saveCarOrder = carOrderManager.newCarOrder(carOrder);

        await().untilAsserted(() ->{
            CarOrder foundOrder = carOrderRepository.findById(carOrder.getId()).get();

            assertEquals(CarOrderStatusEnum.ALLOCATED,foundOrder.getOrderStatus());
        });
        carOrderManager.cancelOrder(saveCarOrder.getId());
        await().untilAsserted(() ->{
            CarOrder foundOrder = carOrderRepository.findById(carOrder.getId()).get();

            assertEquals(CarOrderStatusEnum.CANCELLED,foundOrder.getOrderStatus());
        });
    }
    public CarOrder createCarOrder()
    {
        CarOrder carOrder = CarOrder.builder()
                .customer(testCostomer)
                .build();

        Set<CarOrderLine> lines = new HashSet<>();

        lines.add(CarOrderLine.builder()
        .carId(carId)
        .upc("12345")
        .orderQuantity(1)
        .carOrder(carOrder)
        .build());

        carOrder.setCarOrderLines(lines);
        return carOrder;
    }
}

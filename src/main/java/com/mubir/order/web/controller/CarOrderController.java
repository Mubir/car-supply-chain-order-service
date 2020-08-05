package com.mubir.order.web.controller;

import com.mubir.order.domain.CarOrder;
import com.mubir.order.service.CarOrderService;
import com.mubir.order.web.model.CarOrderDto;
import com.mubir.order.web.model.CarOrderPagedList;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/customers/{customerId}/")
@RestController
public class CarOrderController {
    private static final Integer  DEFAULT_PAGE_NUMBER =0;
    private static final Integer  DEFAULT_PAGE_SIZE=25;

    private final CarOrderService carOrderService;

    public CarOrderController(CarOrderService carOrderService) {
        this.carOrderService = carOrderService;
    }

    @GetMapping("orders")
    public CarOrderPagedList listOrders(@PathVariable("customerId")UUID customerId,
                                        @RequestParam(value ="pageNumber",required = false)Integer pageNumber,
                                        @RequestParam(value ="pageSize",required = false) Integer pageSize)
    {
        if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        return carOrderService.listOrder(customerId, PageRequest.of(pageNumber,pageSize));
    }

    @PostMapping("orders")
    @ResponseStatus(HttpStatus.CREATED)
    public CarOrderDto placeOrder(@PathVariable("customerId")UUID customerId,
                                  @RequestBody CarOrderDto carOrderDto)
    {
        return carOrderService.placeOrder(customerId,carOrderDto);
    }

    @GetMapping("orders/{orderId}")
    public CarOrderDto getOrder(@PathVariable("customerId") UUID customerId,@PathVariable("orderId")UUID orderId)
    {
        return carOrderService.getOrderById(customerId,orderId);
    }
    @PutMapping("/orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickupOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId) {
    carOrderService.pickupOrder(customerId,orderId);
    }
}

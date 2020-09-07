package com.mubir.order.web.controller;

import com.mubir.order.service.car.CustomerService;
import com.mubir.order.web.model.CustomerPagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers/")
public class CustomerController {

    private static final Integer   DEFAULT_PAGE_NUMBER =0;
    private static final Integer DEFAULT_PAGE_SIZE=25;

    private final CustomerService customerService;

    public CustomerPagedList listCustomers(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                           @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return customerService.listCustomer(PageRequest.of(pageNumber, pageSize));
    }
}

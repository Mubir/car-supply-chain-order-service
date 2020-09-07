package com.mubir.order.service.car;

import com.mubir.order.web.model.CustomerPagedList;

import org.springframework.data.domain.Pageable;

public interface CustomerService {
    CustomerPagedList listCustomer(Pageable pageable);
}

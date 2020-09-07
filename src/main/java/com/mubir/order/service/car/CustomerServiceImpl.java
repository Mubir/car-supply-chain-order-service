package com.mubir.order.service.car;

import com.mubir.order.domain.Customer;
import com.mubir.order.repositories.CustomerRepository;
import com.mubir.order.web.mapper.CustomerMapper;
import com.mubir.order.web.model.CustomerPagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import org.springframework.data.domain.Pageable;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService{
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerPagedList listCustomer(Pageable pageable) {
        Page<Customer> customerPage = customerRepository.findAll( pageable);
        return new CustomerPagedList(customerPage.stream()
        .map(customerMapper::customerToDto).collect(Collectors.toList()),
                PageRequest.of(customerPage.getPageable().getPageNumber(),customerPage.getPageable().getPageSize()),
                customerPage.getTotalElements());
    }
}

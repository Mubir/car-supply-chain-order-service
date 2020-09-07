package com.mubir.order.web.mapper;

import com.mubir.order.domain.Customer;
import com.mubir.order.web.model.CustomerDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface CustomerMapper {
    CustomerDto customerToDto(Customer customer);

    Customer dtoToCustomer(CustomerDto dto);
}

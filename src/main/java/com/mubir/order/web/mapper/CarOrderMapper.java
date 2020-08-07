package com.mubir.order.web.mapper;

import com.mubir.order.domain.CarOrder;
import com.mubir.order.web.model.CarOrderDto;
import org.mapstruct.MapMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {DateMapper.class, CarOrderLineMapper.class})
public interface CarOrderMapper {
    @Mapping(target = "customerId",source = "customer.id")
    CarOrderDto carOrderToDto (CarOrder carOrder);
    CarOrder dtoToCarOrder (CarOrderDto carOrderDto);
}

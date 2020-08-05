package com.mubir.order.web.mapper;

import com.mubir.order.domain.CarOrder;
import com.mubir.order.web.model.CarOrderDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class, CarOrderLineMapper.class})
public interface CarOrderMapper {
    CarOrderDto carOrderToDto (CarOrder carOrder);
    CarOrder dtoToCarOrder (CarOrderDto carOrderDto);
}

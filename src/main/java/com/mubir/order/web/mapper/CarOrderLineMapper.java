package com.mubir.order.web.mapper;

import com.mubir.order.domain.CarOrderLine;
import com.mubir.order.web.model.CarOrderLineDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(uses={DateMapper.class})
@DecoratedWith(CarOderLineMapperDecorator.class)
public interface CarOrderLineMapper {
    CarOrderLineDto carOrderLineToDto(CarOrderLine carOrderLine);
    CarOrderLine dtoToCarOrderLine(CarOrderLineDto carOrderLineDto);
}

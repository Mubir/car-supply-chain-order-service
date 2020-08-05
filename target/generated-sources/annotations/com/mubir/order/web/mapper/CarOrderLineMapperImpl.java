package com.mubir.order.web.mapper;

import com.mubir.order.domain.CarOrderLine;
import com.mubir.order.domain.CarOrderLine.CarOrderLineBuilder;
import com.mubir.order.web.model.CarOrderLineDto;
import com.mubir.order.web.model.CarOrderLineDto.CarOrderLineDtoBuilder;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-08-05T10:39:51+0900",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 11.0.4 (Oracle Corporation)"
)
@Component
public class CarOrderLineMapperImpl implements CarOrderLineMapper {

    @Autowired
    private DateMapper dateMapper;

    @Override
    public CarOrderLineDto carOrderLineToDto(CarOrderLine carOrderLine) {
        if ( carOrderLine == null ) {
            return null;
        }

        CarOrderLineDtoBuilder carOrderLineDto = CarOrderLineDto.builder();

        carOrderLineDto.id( carOrderLine.getId() );
        if ( carOrderLine.getVersion() != null ) {
            carOrderLineDto.version( carOrderLine.getVersion().intValue() );
        }
        carOrderLineDto.createdDate( dateMapper.asOffsetDateTime( carOrderLine.getCreatedDate() ) );
        carOrderLineDto.lastModifiedDate( dateMapper.asOffsetDateTime( carOrderLine.getLastModifiedDate() ) );
        carOrderLineDto.carId( carOrderLine.getCarId() );
        carOrderLineDto.orderQuantity( carOrderLine.getOrderQuantity() );

        return carOrderLineDto.build();
    }

    @Override
    public CarOrderLine dtoToCarOrderLine(CarOrderLineDto carOrderLineDto) {
        if ( carOrderLineDto == null ) {
            return null;
        }

        CarOrderLineBuilder carOrderLine = CarOrderLine.builder();

        carOrderLine.id( carOrderLineDto.getId() );
        if ( carOrderLineDto.getVersion() != null ) {
            carOrderLine.version( carOrderLineDto.getVersion().longValue() );
        }
        carOrderLine.createdDate( dateMapper.asTimestamp( carOrderLineDto.getCreatedDate() ) );
        carOrderLine.lastModifiedDate( dateMapper.asTimestamp( carOrderLineDto.getLastModifiedDate() ) );
        carOrderLine.carId( carOrderLineDto.getCarId() );
        carOrderLine.orderQuantity( carOrderLineDto.getOrderQuantity() );

        return carOrderLine.build();
    }
}

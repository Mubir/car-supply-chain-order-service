package com.mubir.order.web.mapper;

import com.mubir.order.domain.CarOrder;
import com.mubir.order.domain.CarOrder.CarOrderBuilder;
import com.mubir.order.domain.CarOrderLine;
import com.mubir.order.domain.OrderStatusEnum;
import com.mubir.order.web.model.CarOrderDto;
import com.mubir.order.web.model.CarOrderDto.CarOrderDtoBuilder;
import com.mubir.order.web.model.CarOrderLineDto;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-08-05T10:39:50+0900",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 11.0.4 (Oracle Corporation)"
)
@Component
public class CarOrderMapperImpl implements CarOrderMapper {

    @Autowired
    private DateMapper dateMapper;
    @Autowired
    private CarOrderLineMapper carOrderLineMapper;

    @Override
    public CarOrderDto carOrderToDto(CarOrder carOrder) {
        if ( carOrder == null ) {
            return null;
        }

        CarOrderDtoBuilder carOrderDto = CarOrderDto.builder();

        carOrderDto.id( carOrder.getId() );
        if ( carOrder.getVersion() != null ) {
            carOrderDto.version( carOrder.getVersion().intValue() );
        }
        carOrderDto.createdDate( dateMapper.asOffsetDateTime( carOrder.getCreatedDate() ) );
        carOrderDto.lastModifiedDate( dateMapper.asOffsetDateTime( carOrder.getLastModifiedDate() ) );
        carOrderDto.customerRef( carOrder.getCustomerRef() );
        carOrderDto.carOrderLines( carOrderLineSetToCarOrderLineDtoList( carOrder.getCarOrderLines() ) );
        carOrderDto.orderStatus( orderStatusEnumToOrderStatusEnum( carOrder.getOrderStatus() ) );
        carOrderDto.orderStatusCallbackUrl( carOrder.getOrderStatusCallbackUrl() );

        return carOrderDto.build();
    }

    @Override
    public CarOrder dtoToCarOrder(CarOrderDto carOrderDto) {
        if ( carOrderDto == null ) {
            return null;
        }

        CarOrderBuilder carOrder = CarOrder.builder();

        carOrder.id( carOrderDto.getId() );
        if ( carOrderDto.getVersion() != null ) {
            carOrder.version( carOrderDto.getVersion().longValue() );
        }
        carOrder.createdDate( dateMapper.asTimestamp( carOrderDto.getCreatedDate() ) );
        carOrder.lastModifiedDate( dateMapper.asTimestamp( carOrderDto.getLastModifiedDate() ) );
        carOrder.customerRef( carOrderDto.getCustomerRef() );
        carOrder.carOrderLines( carOrderLineDtoListToCarOrderLineSet( carOrderDto.getCarOrderLines() ) );
        carOrder.orderStatus( orderStatusEnumToOrderStatusEnum1( carOrderDto.getOrderStatus() ) );
        carOrder.orderStatusCallbackUrl( carOrderDto.getOrderStatusCallbackUrl() );

        return carOrder.build();
    }

    protected List<CarOrderLineDto> carOrderLineSetToCarOrderLineDtoList(Set<CarOrderLine> set) {
        if ( set == null ) {
            return null;
        }

        List<CarOrderLineDto> list = new ArrayList<CarOrderLineDto>( set.size() );
        for ( CarOrderLine carOrderLine : set ) {
            list.add( carOrderLineMapper.carOrderLineToDto( carOrderLine ) );
        }

        return list;
    }

    protected com.mubir.order.web.model.OrderStatusEnum orderStatusEnumToOrderStatusEnum(OrderStatusEnum orderStatusEnum) {
        if ( orderStatusEnum == null ) {
            return null;
        }

        com.mubir.order.web.model.OrderStatusEnum orderStatusEnum1;

        switch ( orderStatusEnum ) {
            case NEW: orderStatusEnum1 = com.mubir.order.web.model.OrderStatusEnum.NEW;
            break;
            case READY: orderStatusEnum1 = com.mubir.order.web.model.OrderStatusEnum.READY;
            break;
            case PICK_UP: orderStatusEnum1 = com.mubir.order.web.model.OrderStatusEnum.PICK_UP;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + orderStatusEnum );
        }

        return orderStatusEnum1;
    }

    protected Set<CarOrderLine> carOrderLineDtoListToCarOrderLineSet(List<CarOrderLineDto> list) {
        if ( list == null ) {
            return null;
        }

        Set<CarOrderLine> set = new HashSet<CarOrderLine>( Math.max( (int) ( list.size() / .75f ) + 1, 16 ) );
        for ( CarOrderLineDto carOrderLineDto : list ) {
            set.add( carOrderLineMapper.dtoToCarOrderLine( carOrderLineDto ) );
        }

        return set;
    }

    protected OrderStatusEnum orderStatusEnumToOrderStatusEnum1(com.mubir.order.web.model.OrderStatusEnum orderStatusEnum) {
        if ( orderStatusEnum == null ) {
            return null;
        }

        OrderStatusEnum orderStatusEnum1;

        switch ( orderStatusEnum ) {
            case NEW: orderStatusEnum1 = OrderStatusEnum.NEW;
            break;
            case READY: orderStatusEnum1 = OrderStatusEnum.READY;
            break;
            case PICK_UP: orderStatusEnum1 = OrderStatusEnum.PICK_UP;
            break;
            default: throw new IllegalArgumentException( "Unexpected enum constant: " + orderStatusEnum );
        }

        return orderStatusEnum1;
    }
}

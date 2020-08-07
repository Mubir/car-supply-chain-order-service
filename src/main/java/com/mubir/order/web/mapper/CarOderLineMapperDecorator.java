package com.mubir.order.web.mapper;

import com.mubir.order.domain.CarOrderLine;
import com.mubir.order.service.car.CarService;
import com.mubir.order.web.model.CarDto;
import com.mubir.order.web.model.CarOrderLineDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Optional;

public abstract class CarOderLineMapperDecorator implements CarOrderLineMapper{
    private CarService carService;
    private CarOrderLineMapper carOrderLineMapper;
    @Autowired
    public void setCarService(CarService carService)
    {
        this.carService =carService;
    }
    @Autowired
    @Qualifier("delegate")
    public void setCarOrderLineMapper(CarOrderLineMapper carOrderLineMapper) {
        this.carOrderLineMapper = carOrderLineMapper;
    }

    @Override
    public CarOrderLineDto carOrderLineToDto(CarOrderLine line) {
        CarOrderLineDto orderLineDto = carOrderLineMapper.carOrderLineToDto(line);
        Optional<CarDto> carDtoOptional = carService.getCarByUpc(line.getUpc());


        carDtoOptional.ifPresent(carDto -> {
            orderLineDto.setCarName(carDto.getCarName());
            orderLineDto.setCarModel(carDto.getCarModel());
            orderLineDto.setPrice(carDto.getPrice());
            orderLineDto.setCarId(carDto.getId());
        });

            return orderLineDto;
        }
    }



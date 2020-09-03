package com.mubir.order.service.car;

import com.mubir.order.web.model.CarDto;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;
@ConfigurationProperties(prefix = "mubir.micro",ignoreUnknownFields = false)
@Service
public class CarServiceImpl implements CarService{



    public static final  String CAR_PATH_V1= "/api/v1/car/";
    public static final  String CAR_UPC_PATH_V1= "/api/v1/carUpc/";
    private final RestTemplate restTemplate;
    private String carServiceHost;

    public void setCarServiceHost(String carServiceHost) {
        this.carServiceHost = carServiceHost;
    }

    public CarServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Optional<CarDto> getCarById(UUID id) {
        return Optional.of(restTemplate.getForObject(carServiceHost+CAR_PATH_V1+id.toString()
                ,CarDto.class));
    }

    @Override
    public Optional<CarDto> getCarByUpc(String upc) {
        return Optional.of(restTemplate.getForObject(carServiceHost+CAR_UPC_PATH_V1+upc.toString()
                ,CarDto.class));
    }
}

package com.mubir.order.service.car;

import com.mubir.order.web.model.CarDto;

import java.util.Optional;
import java.util.UUID;

public interface CarService {
    Optional<CarDto> getCarById(UUID id);
    Optional<CarDto> getCarByUpc(String upc);
}

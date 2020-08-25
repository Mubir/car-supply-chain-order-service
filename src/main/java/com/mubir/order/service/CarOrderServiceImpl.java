package com.mubir.order.service;

import com.mubir.order.domain.CarOrder;
import com.mubir.order.domain.CarOrderStatusEnum;
import com.mubir.order.domain.Customer;
import com.mubir.order.repositories.CarOrderRepository;
import com.mubir.order.repositories.CustomerRepository;
import com.mubir.order.web.mapper.CarOrderMapper;
import com.mubir.order.web.model.CarOrderDto;
import com.mubir.order.web.model.CarOrderPagedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CarOrderServiceImpl implements CarOrderService {
    private final CarOrderRepository carOrderRepository;
    private final CustomerRepository customerRepository;
    private final CarOrderMapper carOrderMapper;
    private final ApplicationEventPublisher publisher;

    public CarOrderServiceImpl(CarOrderRepository carOrderRepository,
                               CustomerRepository customerRepository,
                               CarOrderMapper carOrderMapper,
                               ApplicationEventPublisher publisher) {
        this.carOrderRepository = carOrderRepository;
        this.customerRepository = customerRepository;
        this.carOrderMapper = carOrderMapper;
        this.publisher = publisher;
    }

    @Override
    public CarOrderPagedList listOrder(UUID customerId, Pageable pageable) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isPresent()) {
            Page<CarOrder> carOrderPage = carOrderRepository.findAllByCustomer(customerOptional.get(), pageable);
            return new CarOrderPagedList(carOrderPage.stream().map(carOrderMapper::carOrderToDto)
                    .collect(Collectors.toList()), PageRequest.of(carOrderPage.getPageable().getPageNumber(),
                    carOrderPage.getPageable().getPageSize()), carOrderPage.getTotalElements());
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public CarOrderDto placeOrder(UUID customerId, CarOrderDto carOrderDto) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isPresent()) {
            CarOrder carOrder = carOrderMapper.dtoToCarOrder(carOrderDto);
            carOrder.setId(null); // should not be set by outside client
            carOrder.setCustomer(customerOptional.get());
            carOrder.setOrderStatus(CarOrderStatusEnum.NEW);

            carOrder.getCarOrderLines().forEach(line -> line.setCarOrder(carOrder));

            CarOrder saveCarOrder = carOrderRepository.saveAndFlush(carOrder);
            log.warn("Car saved" + carOrder.getId());
            return carOrderMapper.carOrderToDto(saveCarOrder);
        }
        throw new RuntimeException("Invalid customer ");
    }

    @Override
    public CarOrderDto getOrderById(UUID customerId, UUID orderId) {
        return carOrderMapper.carOrderToDto(getOrder(customerId,orderId));
    }

    @Override
    public void pickupOrder(UUID customerId, UUID orderId) {
        CarOrder carOrder = getOrder(customerId, orderId);
        carOrder.setOrderStatus(CarOrderStatusEnum.PICKED_UP);
        carOrderRepository.save(carOrder);
    }

    private CarOrder getOrder(UUID customerId, UUID orderId) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isPresent()) {
            Optional<CarOrder> carOrderOptional = carOrderRepository.findById(orderId);
            if (carOrderOptional.isPresent()) {
                CarOrder carOrder = carOrderOptional.get();
                if (carOrder.getCustomer().getId().equals(customerId))
                    return carOrder;
            }
            throw new RuntimeException("Order not found");
        }
        throw  new RuntimeException("cusotmer invalid");
    }
}

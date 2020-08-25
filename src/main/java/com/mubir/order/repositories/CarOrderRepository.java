package com.mubir.order.repositories;

import com.mubir.order.domain.CarOrder;
import com.mubir.order.domain.CarOrderStatusEnum;
import com.mubir.order.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.UUID;

public interface CarOrderRepository  extends JpaRepository<CarOrder, UUID> {
    Page<CarOrder> findAllByCustomer(Customer customer, Pageable pageable);

    List<CarOrder> findAllByOrderStatus(CarOrderStatusEnum carOrderStatusEnum);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    CarOrder findOneById(UUID id);
}

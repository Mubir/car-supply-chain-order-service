package com.mubir.order.repositories;

import com.mubir.order.domain.CarOrderLine;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface CarOrderLineRepository extends PagingAndSortingRepository<CarOrderLine, UUID> {
}

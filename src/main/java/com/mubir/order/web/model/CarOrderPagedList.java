package com.mubir.order.web.model;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class CarOrderPagedList extends PageImpl<CarOrderDto> {

    public CarOrderPagedList(List<CarOrderDto> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public CarOrderPagedList(List<CarOrderDto> content) {
        super(content);
    }
}

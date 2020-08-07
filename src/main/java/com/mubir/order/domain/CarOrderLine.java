package com.mubir.order.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.UUID;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class CarOrderLine extends BaseEntity {
    @Builder
    public CarOrderLine(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate,
                        CarOrder carOrder, UUID carId,String upc, Integer orderQuantity, Integer quantityAllocated) {
        super(id, version, createdDate, lastModifiedDate);
        this.carOrder = carOrder;
        this.carId = carId;
        this.upc = upc;
        this.orderQuantity = orderQuantity;
        this.quantityAllocated = quantityAllocated;
    }

    @ManyToOne
    private CarOrder carOrder;
    private UUID carId;
    private String upc;
    private Integer orderQuantity = 0;
    private Integer quantityAllocated = 0;


}

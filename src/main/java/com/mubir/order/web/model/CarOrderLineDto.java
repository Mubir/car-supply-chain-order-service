package com.mubir.order.web.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CarOrderLineDto extends BaseItem{
    @Builder
    public CarOrderLineDto(UUID id, Integer version, OffsetDateTime createdDate, OffsetDateTime lastModifiedDate, String upc, String carName, UUID carId, Integer orderQuantity) {
        super(id, version, createdDate, lastModifiedDate);
        this.upc = upc;
        this.carName = carName;
        this.carId = carId;
        this.orderQuantity = orderQuantity;
    }

    private String upc;
    private String carName;
    private UUID carId;
    private Integer orderQuantity = 0;
}

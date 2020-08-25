package com.mubir.order.web.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CarOrderDto extends BaseItem{
    @Builder
    public CarOrderDto(UUID id, Integer version, OffsetDateTime createdDate, OffsetDateTime lastModifiedDate, UUID customerId, String customerRef, List<CarOrderLineDto> carOrderLines, String orderStatus, String orderStatusCallbackUrl) {
        super(id, version, createdDate, lastModifiedDate);
        this.customerId = customerId;
        this.customerRef = customerRef;
        this.carOrderLines = carOrderLines;
        this.orderStatus = orderStatus;
        this.orderStatusCallbackUrl = orderStatusCallbackUrl;
    }

    private UUID customerId;
    private String customerRef;
    private List<CarOrderLineDto> carOrderLines;
    private String orderStatus;
    private String orderStatusCallbackUrl;
}

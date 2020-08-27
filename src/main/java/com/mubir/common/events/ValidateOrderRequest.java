package com.mubir.common.events;

import com.mubir.order.web.model.CarOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateOrderRequest {
    private CarOrderDto carOrderDto;
}

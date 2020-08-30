package com.mubir.common.events;

import com.mubir.order.web.model.CarOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AllocateOrderResult {
    private CarOrderDto carOrderDto;
    private Boolean allocationError =false;
    private Boolean pendingInventory =false;
}

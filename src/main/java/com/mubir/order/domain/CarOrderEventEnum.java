package com.mubir.order.domain;

public enum CarOrderEventEnum {
    VALIDATE_ORDER,VALIDATION_PASSED,VALIDATION_FAILED,ALLOCATE_ORDER,ALLOCATION_SUCCESS,
    ALLOCATION_ON_INVENTORY,ALLOCATION_FAILED,CARORDER_PICKED_UP
}

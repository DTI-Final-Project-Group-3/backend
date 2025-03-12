package com.warehub.warehub.common.enums;

import lombok.Getter;

@Getter
public enum MutationConstant {
    STATUS_PENDING(1L),
    STATUS_COMPLETED(2L),
    STATUS_CANCELLED(3L),
    STATUS_DECLINED(4L),
    STATUS_EXPIRED(5L),

    TYPE_INBOUND_MANUAL_MUTATION(1L),
    TYPE_OUTBOUND_MANUAL_MUTATION(6L),
    TYPE_INBOUND_AUTO_MUTATION(2L),
    TYPE_OUTBOUND_AUTO_MUTATION(7L),
    TYPE_UPDATE_INVENTORY(3L),
    TYPE_CREATE_INVENTORY(4L),
    TYPE_DELETE_INVENTORY(5L);

    private final Long value;

    MutationConstant(Long value) {
        this.value = value;
    }
}

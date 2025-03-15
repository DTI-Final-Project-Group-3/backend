package com.warehub.warehub.common.utils;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class DateConverter {


    public static OffsetDateTime convertStarDate(LocalDate startLocalDate){
        if (startLocalDate == null) return null;
        ZoneOffset utc7Offset = ZoneOffset.ofHours(7);
        return startLocalDate.atStartOfDay().atOffset(utc7Offset);
    }

    public static OffsetDateTime convertEndDate(LocalDate endLocalDate){
        if (endLocalDate == null) return  null;
        ZoneOffset utc7Offset = ZoneOffset.ofHours(7);
        return endLocalDate.plusDays(1).atStartOfDay().atOffset(utc7Offset);
    }
}

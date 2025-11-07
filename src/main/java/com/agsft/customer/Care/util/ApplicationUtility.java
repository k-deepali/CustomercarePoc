package com.agsft.customer.Care.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ApplicationUtility {
    public static LocalDate convertDateToLocalDate(Date date){
        return  date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}

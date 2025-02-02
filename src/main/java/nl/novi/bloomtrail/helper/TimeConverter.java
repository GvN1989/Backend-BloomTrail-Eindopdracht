package nl.novi.bloomtrail.helper;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class TimeConverter {

    private TimeConverter() {

    }

    public static LocalTime convertToLocalTime(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    // ✅ Convert LocalTime to Date (adds default date component)
    public static Date convertToDate(LocalTime localTime) {
        if (localTime == null) return null;
        return Date.from(localTime.atDate(java.time.LocalDate.of(1970, 1, 1))
                .atZone(ZoneId.systemDefault()).toInstant());
    }

}

package practice.expiry_rescue_app.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    private DateUtil() {
        // Private constructor to prevent instantiation
    }

    public static long getDaysBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    public static long getDaysUntilExpiry(LocalDateTime expiryDate) {
        return getDaysBetween(LocalDateTime.now(), expiryDate);
    }

    public static boolean isExpired(LocalDateTime expiryDate) {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public static boolean isExpiringSoon(LocalDateTime expiryDate, int daysBeforeExpiry) {
        long daysUntil = getDaysUntilExpiry(expiryDate);
        return daysUntil >= 0 && daysUntil <= daysBeforeExpiry;
    }

    public static LocalDateTime addDays(LocalDateTime date, int days) {
        return date.plusDays(days);
    }

    public static LocalDateTime subtractDays(LocalDateTime date, int days) {
        return date.minusDays(days);
    }
}

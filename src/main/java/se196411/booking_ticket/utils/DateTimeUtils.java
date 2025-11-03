package se196411.booking_ticket.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Utility class for handling date and time with proper timezone
 */
public class DateTimeUtils {

    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    /**
     * Get current LocalDateTime in Vietnam timezone (UTC+7)
     * @return Current LocalDateTime in Asia/Ho_Chi_Minh timezone
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(VIETNAM_ZONE);
    }

    /**
     * Get current LocalDateTime in specified timezone
     * @param zoneId The timezone to use
     * @return Current LocalDateTime in specified timezone
     */
    public static LocalDateTime now(ZoneId zoneId) {
        return LocalDateTime.now(zoneId);
    }

    /**
     * Get Vietnam timezone
     * @return Asia/Ho_Chi_Minh ZoneId
     */
    public static ZoneId getVietnamZone() {
        return VIETNAM_ZONE;
    }
}


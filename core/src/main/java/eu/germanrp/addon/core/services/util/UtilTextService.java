package eu.germanrp.addon.core.services.util;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * @author RettichLP
 */
@RequiredArgsConstructor
public class UtilTextService {

    /**
     * Converts a given time in seconds to an easy readable time in {@code HH:mm:ss} format or {@code mm:ss} if duration is less than
     * an hour
     *
     * @param seconds time in seconds
     *
     * @return converted time in readable format
     */
    public String parseTimer(long seconds) {
        return seconds >= 3600 ? String.format("%02d:%02d:%02d", seconds / 3600, seconds % 3600 / 60, seconds % 60) : String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    /**
     * Converts a given time in milliseconds to an easy readable time in {@code XXd XXh XXm XXs} format
     *
     * @param milliseconds time in milliseconds
     *
     * @return converted time in readable format
     */
    public String parseTimerWithTimeUnit(long milliseconds) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        long minutes = TimeUnit.SECONDS.toMinutes(seconds);
        long hours = TimeUnit.MINUTES.toHours(minutes);
        long days = TimeUnit.HOURS.toDays(hours);
        return String.format("%02dd %02dh %02dm %02ds", days, hours % 24, minutes % 60, seconds % 60);
    }

    /**
     * Converts a given {@code Long} with specific {@code TimeUnit} to an easy readable time {@code String}
     *
     * @param timeUnit TimeUnit, in which the given value is provided
     * @param time     Long of the time to be converted
     *
     * @return converted time in readable format
     */
    public String parseTime(TimeUnit timeUnit, long time) {
        long dd = timeUnit.toDays(time);
        long hh = timeUnit.toHours(time) % 24;
        long mm = timeUnit.toMinutes(time) % 60;
        long ss = timeUnit.toSeconds(time) % 60;

        String days = dd > 0 ? dd + ":" : "";
        String hours = dd > 0 || hh > 0 ? (hh < 10 ? "0" + hh : hh) + ":" : "";
        String minutes = (mm < 10 ? "0" + mm : mm) + ":";
        String seconds = (ss < 10 ? "0" + ss : String.valueOf(ss));

        return days + hours + minutes + seconds;
    }
}

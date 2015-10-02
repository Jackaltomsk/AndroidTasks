package projects.my.stopwatch.common;

import android.os.SystemClock;

/**
 * Содержит в себе методы работы со временем.
 */
public class Time {
    /**
     * Реализует расчет разности текущего и переданного времени.
     * @param since Время, вычитаемое из текущего.
     * @return Возвращает разность времени.
     */
    public static long calculateElapsed(long since) {
        return SystemClock.elapsedRealtime() - since;
    }
}
package projects.my.stopwatch;

import android.os.SystemClock;

import java.text.DateFormat;
import java.util.Date;

/**
 * Содержит в себе методы работы со временем.
 */
public class Time {
    /**
     * Реализует расчет разности текущего и переданного времени.
     * @param since Время, вычитаемое из текущего.
     * @return Возвращает разность времени.
     */
    static long calculateElapsed(long since) {
        return SystemClock.elapsedRealtime() - since;
    }
}
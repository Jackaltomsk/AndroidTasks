package projects.my.stopwatch.common;

import android.os.SystemClock;
import android.text.format.DateUtils;

/**
 * Содержит в себе методы работы со временем.
 */
public class Time {
    public final static long ONE_SECOND = 1000;
    public static long MAXIMUM_TIME_AMOUNT = Long.MAX_VALUE;

    /**
     * Реализует расчет разности текущего и переданного времени.
     * @param since Время, вычитаемое из текущего.
     * @return Возвращает разность времени.
     */
    public static long calculateElapsed(long since) {
        return SystemClock.elapsedRealtime() - since;
    }

    /**
     * Реализует перевод мс в секунды.
     * @param mils Время в милисекундах.
     * @return Возвращает секунды.
     */
    public static long milsToSeconds(long mils) {
        if (mils < 0) return 0;
        else return mils / ONE_SECOND;
    }

    /**
     * Реализует форматирование ткущего времени.
     * @param mils Милисекунды.
     * @return Возвращает строку вида "MM:SS".
     */
    public static String formatElapsedTime(long mils) {
        return DateUtils.formatElapsedTime(mils / ONE_SECOND);
    }
}
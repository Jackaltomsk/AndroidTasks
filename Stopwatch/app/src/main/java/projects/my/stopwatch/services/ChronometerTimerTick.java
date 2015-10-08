package projects.my.stopwatch.services;

/**
 * Интерфейс обработчика события изменения времени.
 */
public interface ChronometerTimerTick {
    /**
     * Событие с частотой 1Гц.
     * @param mils Текущее положение времени (в мс.)
     */
    void onTick(long mils);

    /**
     * Событие на завершение отсчета.
     */
    void onFinish();
}

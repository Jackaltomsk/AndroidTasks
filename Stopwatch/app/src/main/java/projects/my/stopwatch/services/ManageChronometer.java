package projects.my.stopwatch.services;

/**
 * Интерфейс хронометра.
 */
public interface ManageChronometer extends ChronoTimerManager {
    /**
     * Реализует установку обработчика событий изменения времени.
     * @param tickListener Обработчик события.
     */
    void setChronoTickListener(ChronometerTimerTick tickListener);

    /**
     * Реализует запуска хронометра.
     */
    void startChronometer();

    /**
     * Реализует останов хронометра.
     */
    void stopChronometer();

    /**
     * Реализует останов хронометра.
     */
    void dropChronometer();

    /**
     * Реализует получение текущего статуса хронометра.
     * @return Возвращает флаг работы хронометра.
     */
    boolean isChronometerRunning();

    /**
     * Реализует получение текущего времени хронометра.
     * @return Возвращает текущее время, в мс.
     */
    long getChronoElapsed();
}

package projects.my.stopwatch.services;

/**
 * Интерфейс таймера.
 */
public interface ManageTimer extends ChronoTimerManager {
    /**
     * Реализует установку обработчика событий изменения времени.
     * @param tickListener обработчик события.
     */
    void setTimerTickListener(ChronometerTimerTick tickListener);

    /**
     * Реализует запуска хронометра.
     * @param seconds Задержка таймера в секундах.
     */
    void startTimer(long seconds);

    /**
     * Реализует останов таймера.
     */
    void stopTimer();

    /**
     * Реализует останов таймера.
     */
    void dropTimer();

    /**
     * Реализует получение текущего статуса таймера.
     * @return Возвращает флаг работы таймера.
     */
    boolean getIsTimerRunning();

    /**
     * Реализует получение текущего времени таймера.
     * @return Возвращает текущее время, в мс.
     */
    long getTimerElapsed();
}

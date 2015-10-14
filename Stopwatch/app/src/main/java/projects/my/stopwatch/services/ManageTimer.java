package projects.my.stopwatch.services;

/**
 * Интерфейс таймера.
 */
public interface ManageTimer extends ChronoTimerManager {
    /**
     * Реализует установку обработчика событий изменения времени.
     * @param tickListener
     */
    void setTimerTickListener(ChronometerTimerTick tickListener);
    void startTimer(long seconds);
    void stopTimer();
    void dropTimer();
    boolean getIsTimerRunning();
    long getTimerElapsed();
}

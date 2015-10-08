package projects.my.stopwatch.services;

/**
 * Интерфейс хронометра.
 */
public interface ManageChronometer extends ChronoTimerManager {
    /**
     * Реализует установку обработчика событий изменения времени.
     * @param tickListener
     */
    void setChronoTickListener(ChronometerTimerTick tickListener);
    void startChronometer();
    void stopChronometer();
    void dropChronometer();
    boolean getIsChronometerRunning();
    long getChronoElapsed();
}

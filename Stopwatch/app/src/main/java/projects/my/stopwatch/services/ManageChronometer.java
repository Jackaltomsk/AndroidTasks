package projects.my.stopwatch.services;

/**
 * Интерфейс хронометра.
 */
public interface ManageChronometer extends ChronoTimerManager {
    void setChronoTickListener(ChronometerTimerTick tickListener);
    void startChronometer();
    void stopChronometer();
    void dropChronometer();
    boolean getIsChronometerRunning();
}

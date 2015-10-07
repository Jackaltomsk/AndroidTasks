package projects.my.stopwatch.services;

/**
 * Интерфейс таймера.
 */
public interface ManageTimer extends ChronoTimerManager {
    void setTimerTickListener(ChronometerTimerTick tickListener);
    void startTimer();
    void stopTimer();
    void dropTimer();
    boolean getIsTimerRunning();
}

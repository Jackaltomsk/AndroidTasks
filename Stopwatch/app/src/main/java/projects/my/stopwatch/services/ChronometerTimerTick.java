package projects.my.stopwatch.services;

/**
 * Интерфейс обработчика события изменения времени.
 */
public interface ChronometerTimerTick {
    public void onTick(String timeView);
    public void onFinish();
}

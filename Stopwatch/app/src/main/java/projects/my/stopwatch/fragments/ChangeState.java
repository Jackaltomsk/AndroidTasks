package projects.my.stopwatch.fragments;

/**
 * Интерфейс смены состояния кнопки старта-останова.
 */
public interface ChangeState {
    /**
     * Реализует смену названия пункта меню при старте/остановке хронометра/таймера.
     */
    void stateChanged(boolean isRunning);
}

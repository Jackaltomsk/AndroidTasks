package projects.my.stopwatch.fragments;

import projects.my.stopwatch.services.ChronoTimerManager;

/**
 * Интерфейс управления фрагментами с таймером.
 */
public interface FragmentTimeManager {
    /**
     * Реализует запуск отсчета.
     */
    void start();

    /**
     * Реализует останов отсчета.
     */
    void stop();

    /**
     * Реализует сброс времени.
     */
    void drop();

    /**
     * Реализует возврат строкового представления времени.
     * @return Строковое представление времени в формате "MM:SS".
     */
    String getTimeValue();

    /**
     * Реализует обработку события подключения сервиса времени.
     * @param service Поставщик времени.
     */
    void handleConnected(ChronoTimerManager service);
}

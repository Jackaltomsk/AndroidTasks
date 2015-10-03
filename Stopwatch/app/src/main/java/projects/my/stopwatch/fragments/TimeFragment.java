package projects.my.stopwatch.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.os.SystemClock;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import projects.my.stopwatch.ChronoService;
import projects.my.stopwatch.R;
import projects.my.stopwatch.StopwatchActivity;
import projects.my.stopwatch.common.Time;

public class TimeFragment extends Fragment
    implements StopwatchActivity.ChronoConnectedListener {
    private long currentTime;
    private static final String CURRENT_TIME_KEY = "CURRENT_TIME_KEY";
    private boolean isRunning;
    private static final String IS_RUNNING = "IS_RUNNING";
    private Chronometer chronometer;
    private StopwatchActivity activity;
    private MenuItem startStopItem;

    public TimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (StopwatchActivity) getActivity();
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            currentTime = savedInstanceState.getLong(CURRENT_TIME_KEY);
            setIsRunning(savedInstanceState.getBoolean(IS_RUNNING));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.time_fragment, container, false);
        chronometer = (Chronometer)view.findViewById(R.id.chronometer);

        // Установка тулбара.
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_stopwatch, menu);
        // Установка названия действия в соответсвии с текущим статусом таймера.
        startStopItem = menu.findItem(R.id.start_counter);
        if (startStopItem == null) {
            throw new NullPointerException("Не найден пункт меню 'Запустить'");
        }
        stateChanged();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.start_counter:
                if (!isRunning) startTimer();
                else stopTimer();
                break;
            case R.id.drop_counter:
                resetTimer();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRunning) startTimer();
        else {
            chronometer.setBase(Time.calculateElapsed(currentTime));
            stateChanged();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        if (isRunning) currentTime = Time.calculateElapsed(chronometer.getBase());
        outState.putLong(CURRENT_TIME_KEY, currentTime);
        outState.putBoolean(IS_RUNNING, isRunning);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        internalStopTimer();
        activity.getChronoService().stopNotify(true);
    }

    /**
     * Реализует старт таймера.
     */
    public void startTimer() {
        // Запущен в двух случаях: 1) изменилась ориентация экрана; 2) вернули фокус на активити.
        if (isRunning) {
            // Если вернули фокус, то времени прошло больше, чем сохранено.
            if (Time.calculateElapsed(chronometer.getBase()) > currentTime) {
                currentTime = Time.calculateElapsed(chronometer.getBase());
            }
        }
        chronometer.setBase(Time.calculateElapsed(currentTime));
        chronometer.start();
        setIsRunning(true);

        if (activity.getChronoService() != null) {
            activity.getChronoService().startNotify(currentTime, true);
        }
    }

    /**
     * Реализует останов таймера.
     */
    public void stopTimer() {
        internalStopTimer();
        setIsRunning(false);
        activity.getChronoService().stopNotify(false);
    }

    /**
     * Реализует сброс таймера.
     */
    public void resetTimer() {
        stopTimer();
        chronometer.setBase(SystemClock.elapsedRealtime());
        currentTime = 0;
    }

    @Override
    public void handleConnected(ChronoService service) {
        if (isRunning) startTimer();
    }

    /**
     * Реализует установку флага запуска таймера и оповещение активити о смене статуса.
     * @param flag
     */
    private void setIsRunning(boolean flag) {
        isRunning = flag;
        stateChanged();
    }

    /**
     * Реализует останов таймера без изменения флага работы.
     */
    private void internalStopTimer() {
        chronometer.stop();
        if (isRunning) currentTime = Time.calculateElapsed(chronometer.getBase());
    }

    private void stateChanged() {
        if (startStopItem != null) {
            startStopItem.setTitle(isRunning ?
                    R.string.menu_stop_counter_title : R.string.menu_start_counter_title);
        }
    }
}

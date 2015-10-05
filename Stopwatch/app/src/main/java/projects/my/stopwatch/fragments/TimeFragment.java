package projects.my.stopwatch.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.Toast;

import projects.my.stopwatch.activities.SettingsActivity;
import projects.my.stopwatch.services.ChronoService;
import projects.my.stopwatch.R;
import projects.my.stopwatch.activities.StopwatchActivity;
import projects.my.stopwatch.common.Time;

public class TimeFragment extends Fragment
    implements StopwatchActivity.ChronoConnectedListener {

    private static final String CURRENT_TIME_KEY = "CURRENT_TIME_KEY";
    private static final String IS_RUNNING = "IS_RUNNING";
    private static final String BACKGROUND_COLOR = "BACKGROUND_COLOR";
    public static final int REQUEST_COLOR_CODE = 1;
    private long currentTime;
    private boolean isRunning;
    private int backgroundColor;
    private Chronometer chronometer;
    private ChronoService service;
    private MenuItem startStopItem;

    public TimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        TypedArray typedArray = getActivity().getTheme().
                obtainStyledAttributes(new int[]{android.R.attr.background});
        backgroundColor = typedArray.getColor(0, 0xFF00FF);
        typedArray.recycle();

        if (savedInstanceState != null) {
            currentTime = savedInstanceState.getLong(CURRENT_TIME_KEY);
            backgroundColor = savedInstanceState.getInt(BACKGROUND_COLOR);
            setIsRunning(savedInstanceState.getBoolean(IS_RUNNING));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.time_fragment, container, false);
        chronometer = (Chronometer)view.findViewById(R.id.chronometer);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity == null) {
            throw new ClassCastException("Activity фрагмента должен наследовать от" +
                    " AppCompatActivity");
        }

        // Установка тулбара.
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);

        if (backgroundColor != 0) {
            View fragmentView = getActivity().findViewById(R.id.stopwatch_fragment_container);
            fragmentView.setBackground(new ColorDrawable(backgroundColor));
        }

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
        switch (item.getItemId()) {
            case R.id.start_counter:
                if (!isRunning) startTimer();
                else stopTimer();
                break;
            case R.id.drop_counter:
                resetTimer();
                break;
            case R.id.settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivityForResult(intent, REQUEST_COLOR_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_COLOR_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                int colorId = data.getIntExtra("color", android.R.color.white);
                handleBackgroundColorChange(new ColorDrawable(colorId));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast tst = Toast.makeText(getActivity(), "Request cancelled", Toast.LENGTH_SHORT);
                tst.show();
            }
        }
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
        outState.putInt(BACKGROUND_COLOR, backgroundColor);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        internalStopTimer();
        if (service != null) service.stopNotify(true);
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

        if (service != null) service.startNotify(currentTime, true);
    }

    /**
     * Реализует останов таймера.
     */
    public void stopTimer() {
        internalStopTimer();
        setIsRunning(false);
        if (service != null) service.stopNotify(false);
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
        this.service = service;
        if (isRunning) startTimer();
    }

    public CharSequence getChronoText() {
        return chronometer.getText();
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

    /**
     * Реализует смену названия пункта меню при старте/остановке хронометра.
     */
    private void stateChanged() {
        if (startStopItem != null) {
            startStopItem.setTitle(isRunning ?
                    R.string.menu_stop_counter_title : R.string.menu_start_counter_title);
        }
    }

    /**
     * Реализует смену цвета фона хронометра.
     */
    private void handleBackgroundColorChange(ColorDrawable color) {
        View view = getActivity().findViewById(R.id.stopwatch_fragment_container);
        Drawable bc = view.getBackground();
        ColorDrawable colorOne;
        TransitionDrawable td;

        // Если фон еще не менялся.
        if (bc == null) {
            TypedArray typedArray = getActivity().getTheme()
                    .obtainStyledAttributes(new int[]{android.R.attr.background});
            colorOne = new ColorDrawable(typedArray.getColor(0, 0xFF00FF));
            typedArray.recycle();
        }
        else {
            if (bc instanceof TransitionDrawable) {
                colorOne = (ColorDrawable) ((TransitionDrawable) bc).getDrawable(1);
            }
            else colorOne = (ColorDrawable) bc;
        }

        backgroundColor = color.getColor();
        ColorDrawable[] colors = { colorOne, color };
        td = new TransitionDrawable(colors);
        view.setBackground(td);
        td.startTransition(2000);
    }
}

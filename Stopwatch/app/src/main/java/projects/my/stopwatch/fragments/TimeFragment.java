package projects.my.stopwatch.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import projects.my.stopwatch.activities.SettingsActivity;
import projects.my.stopwatch.services.ChronoService;
import projects.my.stopwatch.R;
import projects.my.stopwatch.activities.StopwatchActivity;

public class TimeFragment extends Fragment
    implements StopwatchActivity.ChronoConnectedListener {

    private static final String BACKGROUND_COLOR = "BACKGROUND_COLOR";
    public static final int REQUEST_COLOR_CODE = 1;
    private int backgroundColor;
    private ChronoService service;
    private MenuItem startStopItem;
    private TextView chronometerTime;

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
            backgroundColor = savedInstanceState.getInt(BACKGROUND_COLOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.time_fragment, container, false);
        chronometerTime = (TextView) view.findViewById(R.id.chronometer_time);

        // Установка тулбара.
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

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
        //stateChanged();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.start_counter:
                if (!service.getIsChronometerRunning()) startTimer();
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
        startTimer();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(BACKGROUND_COLOR, backgroundColor);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        service.setTickListener(null);
        service.dropChronometer();
    }

    /**
     * Реализует старт таймера.
     */
    public void startTimer() {
        // Запущен в двух случаях: 1) изменилась ориентация экрана; 2) вернули фокус на активити.
        if (service != null) {
            service.setTickListener(new ChronoService.ChronometerTimerTick() {
                @Override
                public void Tick(String timeView) {
                    chronometerTime.setText(timeView);
                }
            });
            service.startChronometer();
            stateChanged(true);
        }
    }

    /**
     * Реализует останов таймера.
     */
    public void stopTimer() {
        service.stopChronometer();
        stateChanged(false);
    }

    /**
     * Реализует сброс таймера.
     */
    public void resetTimer() {
        service.dropChronometer();
        stateChanged(false);
    }

    @Override
    public void handleConnected(ChronoService service) {
        this.service = service;
    }

    public CharSequence getChronoText() {
        return chronometerTime.getText();
    }

    /**
     * Реализует смену названия пункта меню при старте/остановке хронометра.
     */
    private void stateChanged(boolean isRunning) {
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

package projects.my.stopwatch.fragments;

import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import projects.my.stopwatch.R;
import projects.my.stopwatch.activities.StopwatchActivity;
import projects.my.stopwatch.services.ChronoTimerManager;
import projects.my.stopwatch.services.ChronometerTimerTick;
import projects.my.stopwatch.services.ManageTimer;

public class TimeFragment extends Fragment
    implements StopwatchActivity.ChronoConnectedListener {

    private static final String BACKGROUND_COLOR = "BACKGROUND_COLOR";
    private int backgroundColor;
    private ManageTimer service;
    private TextView chronometerTime;

    public TimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);

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

        if (backgroundColor != 0) {
            chronometerTime = (TextView) view.findViewById(R.id.chronometer_time);
            chronometerTime.setBackground(new ColorDrawable(backgroundColor));
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (service != null) startTimer();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(BACKGROUND_COLOR, backgroundColor);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //service.setChronoTickListener(null);
        //service.dropChronometer();
        service.setTimerTickListener(null);
        //service.dropTimer();
    }

    /**
     * Реализует старт таймера.
     */
    public void startTimer() {
        // Запущен в двух случаях: 1) изменилась ориентация экрана; 2) вернули фокус на активити.
        if (service != null) {
            if (!service.getIsTimerRunning())  {
                /*service.setTimerTickListener(new ChronometerTimerTick() {
                    @Override
                    public void onTick(String timeView) {
                        chronometerTime.setText(timeView);
                    }
                });*/
                service.startTimer();
            }
        }
    }

    /**
     * Реализует останов таймера.
     */
    public void stopTimer() {
        //service.stopChronometer();
        service.stopTimer();
    }

    /**
     * Реализует сброс таймера.
     */
    public void resetTimer() {
        //service.dropChronometer();
        service.dropTimer();
        chronometerTime.setText(R.string.empty_time);
    }

    @Override
    public void handleConnected(ChronoTimerManager service) {
        this.service = (ManageTimer) service;
        if (this.service == null) {
            throw new ClassCastException("service должен реализовывать ManageChronometer");
        }
        this.service.setTimerTickListener(new ChronometerTimerTick() {
            @Override
            public void onTick(String timeView) {
                chronometerTime.setText(timeView);
            }

            @Override
            public void onFinish() {
                chronometerTime.setText(getResources().getText(R.string.empty_time));
            }
        });
    }

    public CharSequence getChronoText() {
        return chronometerTime.getText();
    }

    /**
     * Реализует смену цвета фона хронометра.
     */
    public void handleBackgroundColorChange(ColorDrawable color) {
        View view = getActivity().findViewById(R.id.chronometer_time);
        Drawable bc = view.getBackground();
        ColorDrawable colorOne;
        TransitionDrawable td;

        // Если фон еще не менялся.
        if (bc == null) colorOne = new ColorDrawable(backgroundColor);
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

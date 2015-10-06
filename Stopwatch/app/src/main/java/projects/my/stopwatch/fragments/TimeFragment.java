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

import projects.my.stopwatch.services.ChronoService;
import projects.my.stopwatch.R;
import projects.my.stopwatch.activities.StopwatchActivity;

public class TimeFragment extends Fragment
    implements StopwatchActivity.ChronoConnectedListener {

    private static final String BACKGROUND_COLOR = "BACKGROUND_COLOR";
    private int backgroundColor;
    private ChronoService service;
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

        if (backgroundColor != 0) {
            chronometerTime = (TextView) view.findViewById(R.id.chronometer_time);
            chronometerTime.setBackground(new ColorDrawable(backgroundColor));
        }

        return view;
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
            if (!service.getIsChronometerRunning()) {
                service.setTickListener(new ChronoService.ChronometerTimerTick() {
                    @Override
                    public void Tick(String timeView) {
                        chronometerTime.setText(timeView);
                    }
                });
                service.startChronometer();
            }
        }
    }

    /**
     * Реализует останов таймера.
     */
    public void stopTimer() {
        service.stopChronometer();
    }

    /**
     * Реализует сброс таймера.
     */
    public void resetTimer() {
        service.dropChronometer();
    }

    @Override
    public void handleConnected(ChronoService service) {
        this.service = service;
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

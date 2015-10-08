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
import projects.my.stopwatch.common.Time;
import projects.my.stopwatch.services.ChronoTimerManager;
import projects.my.stopwatch.services.ChronometerTimerTick;
import projects.my.stopwatch.services.ManageChronometer;

/**
 * Фрагмент, отображающий результат работы хронометра (от нуля).
 */
public class TimeFragment extends Fragment
    implements StopwatchActivity.ChronoConnectedListener, FragmentTimeManager,
        StopwatchActivity.BackgroundColorChange {

    private static final String BACKGROUND_COLOR = "BACKGROUND_COLOR";
    private static final String TITLE = "CHRONOMETER";
    private int backgroundColor;
    private ManageChronometer service;
    private TextView chronometerTime;

    public static TimeFragment newInstance() {
        TimeFragment fragment = new TimeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(BACKGROUND_COLOR, backgroundColor);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (service != null) service.setChronoTickListener(null);
    }

    @Override
    public void start() {
        if (service != null) {
            if (!service.getIsChronometerRunning()) {
                service.startChronometer();
            }
        }
    }

    @Override
    public void stop() {
        service.stopChronometer();
    }

    @Override
    public void drop() {
        service.dropChronometer();
        chronometerTime.setText(R.string.empty_time);
    }

    @Override
    public String getTimeValue() {
        return chronometerTime.getText().toString();
    }

    @Override
    public void handleConnected(ChronoTimerManager service) {
        this.service = (ManageChronometer) service;
        if (this.service == null) {
            throw new ClassCastException("service должен реализовывать ManageChronometer");
        }
        this.service.setChronoTickListener(new ChronometerTimerTick() {
            @Override
            public void onTick(long mils) {
                if (chronometerTime != null) chronometerTime.setText(Time.formatElapsedTime(mils));
            }

            @Override
            public void onFinish() {
                if (chronometerTime != null) chronometerTime.setText(getResources().getText(R.string.empty_time));
            }
        });
        if (chronometerTime != null) {
            chronometerTime.setText(Time.formatElapsedTime(this.service.getChronoElapsed()));
        }
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public boolean getIsRunning() {
        return service.getIsChronometerRunning();
    }

    @Override
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


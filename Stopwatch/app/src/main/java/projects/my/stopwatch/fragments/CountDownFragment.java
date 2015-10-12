package projects.my.stopwatch.fragments;

import android.app.Fragment;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
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
import projects.my.stopwatch.services.ManageTimer;

/**
 * Фрагмент, отображающий результат работы таймера (отсчет до нуля).
 */
public class CountDownFragment extends Fragment
    implements StopwatchActivity.ChronoConnectedListener, FragmentTimeManager {

    private static final String BACKGROUND_COLOR = "BACKGROUND_COLOR";
    private static final String TITLE = "TIMER";
    private int backgroundColor;
    private ManageTimer service;
    private TextView timerTime;

    public static CountDownFragment newInstance() {
        CountDownFragment fragment = new CountDownFragment();
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
        View view = inflater.inflate(R.layout.countdown_fragment, container, false);

        if (backgroundColor != 0) {
            timerTime = (TextView) view.findViewById(R.id.countdown_time);
            timerTime.setBackground(new ColorDrawable(backgroundColor));
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
        if (service != null) service.setTimerTickListener(null);
    }

    @Override
    public void start() {
        if (service != null) {
            if (!service.getIsTimerRunning()) {
                service.startTimer();
            }
        }
    }

    @Override
    public void stop() {
        if (service != null) service.stopTimer();
    }

    @Override
    public void drop() {
        if (service != null) {
            service.dropTimer();
            timerTime.setText(R.string.empty_time);
        }
    }

    @Override
    public String getTimeValue() {
        return timerTime.getText().toString();
    }

    @Override
    public void handleConnected(ChronoTimerManager service) {
        this.service = (ManageTimer) service;
        if (this.service == null) {
            throw new ClassCastException("service должен реализовывать ManageChronometer");
        }
        this.service.setTimerTickListener(new ChronometerTimerTick() {
            @Override
            public void onTick(long mils) {
                if (timerTime != null) timerTime.setText(Time.formatElapsedTime(mils));
            }

            @Override
            public void onFinish() {
                if (timerTime != null) timerTime.setText(getResources().getText(R.string.empty_time));
            }
        });
        if (timerTime != null) {
            timerTime.setText(Time.formatElapsedTime(this.service.getTimerElapsed()));
        }
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public boolean getIsRunning() {
        if (service != null) return service.getIsTimerRunning();
        else return false;
    }
}

package projects.my.stopwatch;

import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Fragment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

public class TimeFragment extends Fragment {

    private long currentTime;
    private boolean isRunning;
    private Chronometer chronometer;
    private final String CURRENT_TIME_KEY = "CURRENT_TIME_KEY";
    private final String IS_RUNNING = "IS_RUNNING";

    public TimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            currentTime = savedInstanceState.getLong(CURRENT_TIME_KEY);
            isRunning = savedInstanceState.getBoolean(IS_RUNNING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int orientation = getResources().getConfiguration().orientation;
        int viewId = orientation == Configuration.ORIENTATION_PORTRAIT ?
                R.id.chronometer_portrait : R.id.chronometer_landscape;
        View view = inflater.inflate(R.layout.time_fragment, container, false);
        chronometer = (Chronometer)view.findViewById(viewId);

        if (isRunning) startTimer();
        else chronometer.setBase(Time.calculateElapsed(currentTime));

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        internalStopTimer();
        outState.putLong(CURRENT_TIME_KEY, currentTime);
        outState.putBoolean(IS_RUNNING, isRunning);
    }

    /**
     * Реализует старт таймера.
     */
    public void startTimer() {
        chronometer.setBase(Time.calculateElapsed(currentTime));
        chronometer.start();
        isRunning = true;
    }

    /**
     * Реализует останов таймера.
     */
    public void stopTimer() {
        internalStopTimer();
        isRunning = false;
    }

    /**
     * Реализует сброс таймера.
     */
    public void resetTimer() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        currentTime = 0;
    }
    /**
     * Реализует останов таймера без изменения флага работы.
     */
    private void internalStopTimer() {
        chronometer.stop();
        if (isRunning) currentTime = Time.calculateElapsed(chronometer.getBase());
    }
}

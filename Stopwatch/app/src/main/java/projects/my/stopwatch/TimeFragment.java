package projects.my.stopwatch;

import android.os.Bundle;
import android.app.Fragment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

public class TimeFragment extends Fragment {
    private long currentTime;
    private static final String CURRENT_TIME_KEY = "CURRENT_TIME_KEY";
    private boolean isRunning;
    private static final String IS_RUNNING = "IS_RUNNING";
    private Chronometer chronometer;
    private ChronometerState activity;

    /**
     * Интерфейс оповещения о смене состояния хронометра.
     */
    public interface ChronometerState {
        public void stateChanged(boolean isRunning);
    }

    public TimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            activity = (ChronometerState)getActivity();
        }
        catch (ClassCastException ex) {
            throw new ClassCastException(activity.toString() +
                    " должен реализовывать OnArticleSelectedListener");
        }

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
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRunning) startTimer();
        else {
            chronometer.setBase(Time.calculateElapsed(currentTime));
            activity.stateChanged(false);
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
    }

    public boolean getIsRunning() {
        return isRunning;
    }

    private void setIsRunning(boolean flag) {
        isRunning = flag;
        activity.stateChanged(isRunning);
    }

    /**
     * Реализует старт таймера.
     */
    public void startTimer() {
        // Когда хронометр работал в фоне.
        if (Time.calculateElapsed(chronometer.getBase()) > currentTime) {
            currentTime = Time.calculateElapsed(chronometer.getBase());
        }
        else {
            chronometer.setBase(Time.calculateElapsed(currentTime));
            //chronometer.start();
            //setIsRunning(true);
        }
        chronometer.start();
        setIsRunning(true);
    }

    /**
     * Реализует останов таймера.
     */
    public void stopTimer() {
        internalStopTimer();
        setIsRunning(false);
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

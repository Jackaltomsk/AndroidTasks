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
    private long mCurrentTime;
    private static String sCurrentTimeKey = "CURRENT_TIME_KEY";
    private boolean mIsRunning;
    private static String sIsRunning = "IS_RUNNING";
    private Chronometer mChronometer;

    public TimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentTime = savedInstanceState.getLong(sCurrentTimeKey);
            mIsRunning = savedInstanceState.getBoolean(sIsRunning);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int orientation = getResources().getConfiguration().orientation;
        int viewId = orientation == Configuration.ORIENTATION_PORTRAIT ?
                R.id.chronometer_portrait : R.id.chronometer_landscape;
        View view = inflater.inflate(R.layout.time_fragment, container, false);
        mChronometer = (Chronometer)view.findViewById(viewId);

        if (mIsRunning) startTimer();
        else mChronometer.setBase(Time.calculateElapsed(mCurrentTime));
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        internalStopTimer();
        outState.putLong(sCurrentTimeKey, mCurrentTime);
        outState.putBoolean(sIsRunning, mIsRunning);
    }

    /**
     * Реализует старт таймера.
     */
    public void startTimer() {
        mChronometer.setBase(Time.calculateElapsed(mCurrentTime));
        mChronometer.start();
        mIsRunning = true;
    }

    /**
     * Реализует останов таймера.
     */
    public void stopTimer() {
        internalStopTimer();
        mIsRunning = false;
    }

    /**
     * Реализует сброс таймера.
     */
    public void resetTimer() {
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mCurrentTime = 0;
    }
    /**
     * Реализует останов таймера без изменения флага работы.
     */
    private void internalStopTimer() {
        mChronometer.stop();
        if (mIsRunning) mCurrentTime = Time.calculateElapsed(mChronometer.getBase());
    }
}

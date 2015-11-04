package projects.my.stopwatch.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import projects.my.stopwatch.R;
import projects.my.stopwatch.activities.StopwatchActivity;
import projects.my.stopwatch.common.Time;
import projects.my.stopwatch.services.ChronoTimerManager;
import projects.my.stopwatch.services.ChronometerTimerTick;
import projects.my.stopwatch.services.ManageTimer;

/**
 * Фрагмент, отображающий результат работы таймера (отсчет до нуля).
 */
public class CountDownFragment extends Fragment
    implements StopwatchActivity.ChronoConnectedListener, FragmentTimeManager {

    private static final String TAG = CountDownFragment.class.getSimpleName();
    private static final String TITLE = "TIMER";
    private ManageTimer service;
    private TextView timerTime;

    public static CountDownFragment newInstance() {
        return new CountDownFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_countdown, container, false);
        timerTime = (TextView) view.findViewById(R.id.countdown_time);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (service != null) service.setTimerTickListener(null);
    }

    @Override
    public void start() {
        if (service != null) {
            if (!service.isTimerRunning()) {
                long seconds = Time.milsToSeconds(getTimeSet());

                service.startTimer(seconds);
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
    public boolean isRunning() {
        return service != null && service.isTimerRunning();
    }

    @Override
    public long getTimeSet() {
        EditText edit = (EditText) getActivity().findViewById(R.id.input_countdown_seconds);
        String number = edit.getText().toString();
        long ms;
        try {
            ms = Long.parseLong(number) * Time.ONE_SECOND;
        }
        catch (NumberFormatException ex) {
            Log.e(TAG, "Не распарсено значение " + number);
            ms = 0;
        }
        return ms;
    }
}

package projects.my.stopwatch.fragments;

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
    implements StopwatchActivity.ChronoConnectedListener, FragmentTimeManager {

    private static final String TITLE = "CHRONOMETER";
    private ManageChronometer service;
    private TextView chronometerTime;

    public static TimeFragment newInstance() {
        return new TimeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time, container, false);
        chronometerTime = (TextView) view.findViewById(R.id.chronometer_time);
        if (service != null) {
            chronometerTime.setText(Time.formatElapsedTime(service.getChronoElapsed()));
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (service != null) {
            service.setChronoTickListener(null);
            service = null;
        }
    }

    @Override
    public void start() {
        if (service != null) {
            if (!service.isChronometerRunning()) {
                service.startChronometer();
            }
        }
    }

    @Override
    public void stop() {
        if (service != null) service.stopChronometer();
    }

    @Override
    public void drop() {
        if (service != null) {
            service.dropChronometer();
            chronometerTime.setText(R.string.empty_time);
        }
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
                if (chronometerTime != null) chronometerTime.setText(
                        getResources().getText(R.string.empty_time));
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
    public boolean isRunning() {
        return service != null && service.isChronometerRunning();
    }

    @Override
    public long getTimeSet() {
        throw new UnsupportedOperationException("Для хронометра операция не поддерживается");
    }
}


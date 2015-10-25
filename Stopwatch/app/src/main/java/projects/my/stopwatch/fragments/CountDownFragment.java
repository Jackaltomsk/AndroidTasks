package projects.my.stopwatch.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.SQLException;

import projects.my.stopwatch.R;
import projects.my.stopwatch.activities.StopwatchActivity;
import projects.my.stopwatch.common.Time;
import projects.my.stopwatch.services.ChronoTimerManager;
import projects.my.stopwatch.services.ChronometerTimerTick;
import projects.my.stopwatch.services.ManageTimer;
import projects.my.timerdb.dao.GenericDao;
import projects.my.timerdb.infrastructure.DbManager;
import projects.my.timerdb.models.TimeCutoff;
import projects.my.timerdb.models.TimeManager;

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
        CountDownFragment fragment = new CountDownFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.countdown_fragment, container, false);
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
            if (!service.getIsTimerRunning()) {
                long seconds = getTimerTimeSet();

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
    public boolean getIsRunning() {
        if (service != null) return service.getIsTimerRunning();
        else return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_timer_set: {
                try {
                    GenericDao<TimeManager> daoTimeManager = DbManager.getDbContext()
                            .getGenericDao(TimeManager.class);
                    TimeManager timer = daoTimeManager.queryBuilder().where().eq(
                            TimeManager.NAME_FILED, TimeManager.TIMER_NAME).queryForFirst();
                    TimeCutoff cutoff = new TimeCutoff(getTimerTimeSet(), true);
                    cutoff.setTimeManager(timer);
                    GenericDao<TimeCutoff> daoCutoff = DbManager.getDbContext()
                            .getGenericDao(TimeCutoff.class);
                    daoCutoff.create(cutoff);
                } catch (SQLException e) {
                    Log.e(TAG, "Ошибка добавления установок таймера.");
                    throw new RuntimeException(e);
                }

                break;
            }
            default: return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private long getTimerTimeSet() {
        EditText edit = (EditText) getActivity().findViewById(R.id.input_countdown_seconds);
        String number = edit.getText().toString();
        long seconds;
        try {
            seconds = Long.parseLong(number);
        }
        catch (NumberFormatException ex) {
            Log.e(TAG, "Не распарсено значение " + number);
            seconds = 0;
        }
        return seconds;
    }

}

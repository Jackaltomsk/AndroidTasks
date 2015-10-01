package projects.my.stopwatch;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.SystemClock;
import android.text.format.DateUtils;
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
    private Chronometer.OnChronometerTickListener tickListener;

    // Инфраструктура оповещений.
    private static final int ntfId = 1;
    private Notification.Builder ntfBuilder;
    private NotificationManager ntfManager;

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
        createNotificationInfrastructure();

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
        ntfManager.cancel(ntfId);
    }

    public boolean getIsRunning() {
        return isRunning;
    }

    /**
     * Реализует старт таймера.
     */
    public void startTimer() {
        // Запущен в двух случаях: 1) изменилась ориентация экрана; 2) вернули фокус на активити.
        if (isRunning) {
            // Если вернули фокус, то времени в таймере пройдет больше, чем сохранено.
            if (Time.calculateElapsed(chronometer.getBase()) > currentTime) {
                currentTime = Time.calculateElapsed(chronometer.getBase());
            }
        }
        chronometer.setOnChronometerTickListener(tickListener);
        chronometer.setBase(Time.calculateElapsed(currentTime));
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
        stopTimer();
        chronometer.setBase(SystemClock.elapsedRealtime());
        currentTime = 0;
    }

    /**
     * Реализует установку флага запуска таймера и оповещение активити о смене статуса.
     * @param flag
     */
    private void setIsRunning(boolean flag) {
        isRunning = flag;
        activity.stateChanged(isRunning);
    }

    /**
     * Реализует останов таймера без изменения флага работы.
     */
    private void internalStopTimer() {
        chronometer.stop();
        if (isRunning) currentTime = Time.calculateElapsed(chronometer.getBase());
    }

    private void createNotificationInfrastructure() {
        ntfManager = (NotificationManager)getActivity()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent activityIntent = new Intent(getActivity(), getActivity().getClass());
        PendingIntent intent = PendingIntent.getActivity(getActivity(), 0, activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        ntfBuilder = new Notification.Builder(getActivity())
                .setSmallIcon(R.drawable.timer)
                .setContentTitle(getResources().getString(R.string.chronometer_notification_title))
                .setContentIntent(intent)
                .setPriority(Notification.PRIORITY_HIGH);

        tickListener = new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                Notification ntf = ntfBuilder
                        .setContentText(chronometer.getText())
                        .build();
                ntf.flags |= Notification.FLAG_NO_CLEAR;
                ntf.category = Notification.CATEGORY_ALARM;
                ntfManager.notify(ntfId, ntf);
            }
        };
    }
}

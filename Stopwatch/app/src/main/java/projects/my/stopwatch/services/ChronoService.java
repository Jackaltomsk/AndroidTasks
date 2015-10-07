package projects.my.stopwatch.services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.widget.Chronometer;

import projects.my.stopwatch.R;
import projects.my.stopwatch.common.Time;

/**
 * Сервис нотификаций для таймеров/хронометров.
 */
public class ChronoService extends Service
        implements ManageChronometer, ManageTimer {
    private final ChronoBinder chronoBinder = new ChronoBinder();
    private boolean ntfInfCreated;
    private NotificationManager ntfManager;
    private Notification.Builder ntfBuilder;
    private final int ntfId = 1;

    private Chronometer chronometer;
    private CountDownTimer timer;
    private long chronoTime;
    private long timerTime;
    private final long oneSecond = 1000;
    private boolean isChronometerRunning;
    private boolean isTimerRunning;
    private ChronometerTimerTick chronoTickListener;
    private ChronometerTimerTick timerTickListener;

    @Override
    public void setChronoTickListener(ChronometerTimerTick tickListener) {
        chronoTickListener = tickListener;
    }

    public void startChronometer() {
        if (chronoTime > 0) chronometer.setBase(Time.calculateElapsed(chronoTime));
        else chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        isChronometerRunning = true;
        //timer.start();
    }

    public void stopChronometer() {
        chronometer.stop();
        isChronometerRunning = false;
    }

    public void dropChronometer() {
        this.stopChronometer();
        chronoTime = 0;
    }

    @Override
    public boolean getIsChronometerRunning() {
        return isChronometerRunning;
    }

    @Override
    public void setTimerTickListener(ChronometerTimerTick tickListener) {
        timerTickListener = tickListener;
    }

    @Override
    public void startTimer() {
        createTimer();
        timer.start();
        isTimerRunning = true;
    }

    @Override
    public void stopTimer() {
        timer.cancel();
        timer = null;
        isTimerRunning = false;
    }

    @Override
    public void dropTimer() {
        stopTimer();
        timerTime = 0;
    }

    @Override
    public boolean getIsTimerRunning() {
        return isTimerRunning;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        chronometer = new Chronometer(this);
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {

                chronoTime += oneSecond;
                if (chronoTickListener != null) {
                    chronoTickListener.onTick(DateUtils.formatElapsedTime(chronoTime / oneSecond));
                }
                sendNotification(chronoTime, getResources().getString(R.string.chronometer_notification_title));
            }
        });
    }

    private void createTimer() {
        timerTime = timerTime < oneSecond ? 6000 : timerTime;
        timer = new CountDownTimer(timerTime, oneSecond) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (timerTickListener != null) {
                    timerTime -= oneSecond;
                    timerTickListener.onTick(DateUtils.formatElapsedTime(
                            millisUntilFinished / oneSecond));
                }
                sendNotification(millisUntilFinished, getResources()
                        .getString(R.string.timer_notification_title));
            }

            @Override
            public void onFinish() {
                timerTickListener.onFinish();
                sendNotification(0, "Done");
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return chronoBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isChronometerRunning)
        stopChronometer();
        stopNotify(true);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopChronometer();
        stopNotify(true);
    }

    public class ChronoBinder extends Binder {
        public ChronoService getService() {
            return ChronoService.this;
        }
    }

    /**
     * Реализует создание инфраструктуры для нотификации.
     * @param activity Активити, для которого выполняется нотификацияю
     */
    public void createNotificationInfrastructure(Activity activity) {
        if (!ntfInfCreated) {
            ntfManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Intent activityIntent = new Intent(activity, activity.getClass());
            PendingIntent intent = PendingIntent.getActivity(activity, 0, activityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            ntfBuilder = new Notification.Builder(activity)
                    .setSmallIcon(R.drawable.ic_stat_timer)
                    .setContentIntent(intent)
                    .setPriority(Notification.PRIORITY_HIGH);
            ntfInfCreated = true;
        }
    }

    private void sendNotification(final long currentTime, String callerName) {
        Notification ntf = ntfBuilder
                .setContentTitle(callerName)
                .setContentText(DateUtils.formatElapsedTime(currentTime / oneSecond))
                .build();
        ntf.flags |= Notification.FLAG_NO_CLEAR;
        ntfManager.notify(ntfId, ntf);
    }

    /**
     * Реализует останов нотификаций.
     * @param clear Флаг очистки области уведомлений.
     */
    private void stopNotify(boolean clear) {
        if (clear) ntfManager.cancelAll();
    }
}

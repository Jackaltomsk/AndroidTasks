package projects.my.stopwatch.services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;

import projects.my.stopwatch.R;
import projects.my.stopwatch.common.Time;

/**
 * Сервис нотификаций для таймеров/хронометров.
 */
public class ChronoService extends Service
        implements ManageChronometer, ManageTimer {
    private final static long DEFAULT_CHRONOMETER_TIME = 12 * Time.ONE_SECOND;
    private final int chronoNtfId = 0;
    private final int timerNtfId = 1;
    private boolean ntfInfCreated;
    private boolean isChronometerRunning;
    private boolean isTimerRunning;
    private long chronoTime;
    private long timerTime;
    private String chronoTitle;
    private String timerTitle;
    private final ChronoBinder chronoBinder = new ChronoBinder();
    private NotificationManager ntfManager;
    private Notification.Builder ntfBuilder;
    private CountDownTimer chronometer;
    private CountDownTimer timer;
    private ChronometerTimerTick chronoTickListener;
    private ChronometerTimerTick timerTickListener;

    public class ChronoBinder extends Binder {
        public ChronoService getService() {
            return ChronoService.this;
        }
    }

    @Override
    public void setChronoTickListener(ChronometerTimerTick tickListener) {
        chronoTickListener = tickListener;
    }

    public void startChronometer() {
        chronometer = createTimer(true, chronoTime);
        chronometer.start();
        isChronometerRunning = true;
    }

    public void stopChronometer() {
        chronometer.cancel();
        chronometer = null;
        isChronometerRunning = false;
    }

    public void dropChronometer() {
        if (isChronometerRunning) stopChronometer();
        chronoTime = 0;
        ntfManager.cancel(chronoNtfId);
    }

    @Override
    public boolean getIsChronometerRunning() {
        return isChronometerRunning;
    }

    @Override
    public long getChronoElapsed() {
        return chronoTime;
    }

    @Override
    public void setTimerTickListener(ChronometerTimerTick tickListener) {
        timerTickListener = tickListener;
    }

    @Override
    public void startTimer() {
        timer = createTimer(false, timerTime);
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
        if (isTimerRunning) stopTimer();
        timerTime = 0;
        ntfManager.cancel(timerNtfId);
    }

    @Override
    public boolean getIsTimerRunning() {
        return isTimerRunning;
    }

    @Override
    public long getTimerElapsed() {
        return timerTime;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Resources res = getResources();
        timerTitle = res.getString(R.string.timer_notification_title);
        chronoTitle = res.getString(R.string.chronometer_notification_title);
    }

    private CountDownTimer createTimer(final boolean isCountUp, long startTime) {
        final long localStartTime;
        if (isCountUp) { // если отсчитываем в роли хронометра
            localStartTime = Time.MAXIMUM_TIME_AMOUNT - startTime;
        }
        else {
            localStartTime = startTime < Time.ONE_SECOND ?
                    DEFAULT_CHRONOMETER_TIME :
                    DEFAULT_CHRONOMETER_TIME - startTime;
        }

        CountDownTimer timer = new CountDownTimer(localStartTime, Time.ONE_SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isCountUp) {
                    if (chronoTickListener != null) {
                        chronoTime += Time.ONE_SECOND;
                        chronoTickListener.onTick(chronoTime);
                    }
                    sendNotification(chronoTime, chronoTitle, chronoNtfId, false);
                }
                else {
                    if (timerTickListener != null) {
                        timerTime += Time.ONE_SECOND;
                        timerTickListener.onTick(DEFAULT_CHRONOMETER_TIME - timerTime);
                    }
                    sendNotification(DEFAULT_CHRONOMETER_TIME - timerTime, timerTitle, timerNtfId,
                            false);
                }
            }

            @Override
            public void onFinish() {
                timerTickListener.onFinish();
                sendNotification(0, getResources().getString(R.string.timer_on_finish_title), timerNtfId, true);
                timerTime = 0;
            }
        };
        return timer;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return chronoBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shutdown();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        shutdown();
    }

    private void shutdown() {
        dropChronometer();
        dropTimer();
        ntfManager.cancelAll();
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
                    .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                    .setContentIntent(intent)
                    .setPriority(Notification.PRIORITY_HIGH);
            ntfInfCreated = true;
        }
    }

    private void sendNotification(final long currentTime, String callerName, int ntfId,
                                  boolean isFinal) {
        Notification ntf = ntfBuilder
                .setContentTitle(callerName)
                .setContentText(Time.formatElapsedTime(currentTime))
                .build();
        ntf.flags |= Notification.FLAG_NO_CLEAR;
        if (isFinal) {
            ntf.defaults |= Notification.DEFAULT_VIBRATE;
            ntf.defaults |= Notification.DEFAULT_SOUND;
        }
        ntfManager.notify(ntfId, ntf);
    }
}

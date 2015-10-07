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
import android.util.Log;
import android.widget.Chronometer;
import android.widget.Toast;

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
    private final long oneSecond = 1000;
    private boolean chronometerRunning;
    private boolean timerRunning;
    private ChronometerTimerTick tickListener;

    public void startChronometer() {
        if (chronoTime > 0) chronometer.setBase(Time.calculateElapsed(chronoTime));
        else chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        chronometerRunning = true;
        //timer.start();
    }

    public void stopChronometer() {
        chronometer.stop();
        chronometerRunning = false;
    }

    public void dropChronometer() {
        this.stopChronometer();
        chronoTime = 0;
    }

    @Override
    public void startTimer() {
        timer.start();
        timerRunning = true;
    }

    @Override
    public void stopTimer() {
        timer.cancel();
        timerRunning = false;
    }

    @Override
    public void dropTimer() {
        stopTimer();
        timer = null;
    }

    public interface ChronometerTimerTick {
        public void Tick(String timeView);
    }

    public void setTickListener(ChronometerTimerTick listener) {
        this.tickListener = listener;
    }

    public boolean getIsChronometerRunning() {
        return chronometerRunning;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        chronometer = new Chronometer(this);
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {

                chronoTime += oneSecond;
                if (tickListener != null) {
                    tickListener.Tick(DateUtils.formatElapsedTime(chronoTime / oneSecond));
                }
                sendNotification(chronoTime,
                        getResources().getString(R.string.chronometer_notification_title));
            }
        });
        timer = new CountDownTimer(60000, oneSecond) {
            @Override
            public void onTick(long millisUntilFinished) {
                //chronoTime += oneSecond;
                if (tickListener != null) {
                    tickListener.Tick(DateUtils.formatElapsedTime(chronoTime / oneSecond));
                }
                sendNotification(chronoTime,
                        getResources().getString(R.string.chronometer_notification_title));
            }

            @Override
            public void onFinish() {
                Toast.makeText(getBaseContext(), "finish", Toast.LENGTH_SHORT);
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

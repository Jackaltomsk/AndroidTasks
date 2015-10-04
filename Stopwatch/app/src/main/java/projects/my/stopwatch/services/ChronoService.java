package projects.my.stopwatch.services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;

import projects.my.stopwatch.R;

/**
 * Сервис нотификаций для таймеров/хронометров.
 */
public class ChronoService extends Service {
    private final ChronoBinder chronoBinder = new ChronoBinder();
    private boolean ntfInfCreated;
    private NotificationManager ntfManager;
    private Notification.Builder ntfBuilder;
    private Thread thread;
    private final int ntfId = 1;

    public class ChronoBinder extends Binder {
        public ChronoService getService() {
            return ChronoService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return chronoBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopNotify(true);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopNotify(true);
    }

    /**
     * Реализует создание инфраструктуры для нотификации.
     * @param activity Активити, для которого выполняется нотификацияю
     */
    public void createNotificationInfrastructure(Activity activity) {
        if (!ntfInfCreated) {
            ntfManager = (NotificationManager)activity
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            Intent activityIntent = new Intent(activity, activity.getClass());
            PendingIntent intent = PendingIntent.getActivity(activity, 0, activityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            ntfBuilder = new Notification.Builder(activity)
                    .setSmallIcon(R.drawable.timer)
                    .setContentTitle(getResources().getString(R.string.chronometer_notification_title))
                    .setContentIntent(intent)
                    .setPriority(Notification.PRIORITY_HIGH);
            ntfInfCreated = true;
        }
    }

    /**
     * Реализует запуск нотификаций частотой 1Гц в отдельном потоке.
     * @param currentTime Стартовое время, в мс.
     * @param isStepForward В какую сторону изменять переданное время.
     */
    public void startNotify(final long currentTime, final boolean isStepForward) {
        if (ntfInfCreated) {
            if (thread != null) thread.interrupt();
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    long oneSecond = 1000;
                    long threadTime = currentTime / oneSecond;
                    while (threadTime >= 0) {
                        Notification ntf = ntfBuilder.setContentText(
                                DateUtils.formatElapsedTime(threadTime))
                                .build();
                        ntf.flags |= Notification.FLAG_NO_CLEAR;
                        ntf.category = Notification.CATEGORY_ALARM;
                        ntfManager.notify(ntfId, ntf);
                        try {
                            Thread.sleep(oneSecond);
                            if (isStepForward) threadTime++;
                            else threadTime--;
                        } catch (InterruptedException e) {
                            Log.e("Thread interruption", e.toString());
                            return;
                        }
                    }
                }
            });
            thread.start();
        }
    }

    /**
     * Реализует останов нотификаций.
     * @param clear Флаг очистки области уведомлений.
     */
    public void stopNotify(boolean clear) {
        if (thread != null) thread.interrupt();
        if (clear) ntfManager.cancelAll();
    }
}

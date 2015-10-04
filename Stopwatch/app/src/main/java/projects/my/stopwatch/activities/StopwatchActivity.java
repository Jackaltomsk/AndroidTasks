package projects.my.stopwatch.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import projects.my.stopwatch.services.ChronoService;
import projects.my.stopwatch.R;
import projects.my.stopwatch.fragments.TimeFragment;

public class StopwatchActivity extends AppCompatActivity {
    private TimeFragment timeFragment;
    private ServiceConnection chronoConnection;
    private ChronoService chronoService;
    private boolean bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        chronoConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                ChronoService.ChronoBinder binder = (ChronoService.ChronoBinder) service;
                chronoService = binder.getService();
                chronoService.createNotificationInfrastructure(StopwatchActivity.this);
                timeFragment.handleConnected(chronoService);
                bound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                bound = false;
            }
        };

        Intent intent = new Intent(this, ChronoService.class);
        bindService(intent, chronoConnection, BIND_AUTO_CREATE);

        FragmentManager manager = getFragmentManager();
        if (savedInstanceState == null) {
            // Добавление фрагмента в разметку окна, если пересоздается активити.
            FragmentTransaction transaction = manager.beginTransaction();
            timeFragment = new TimeFragment();
            transaction.add(R.id.stopwatch_fragment_container, timeFragment);
            transaction.commit();
        }
        else {
            timeFragment = (TimeFragment) manager.findFragmentById(
                    R.id.stopwatch_fragment_container);
        }
    }

    public interface ChronoConnectedListener {
        /**
         * Реализует обработку события биндинга сервиса.
         * @param service Сервис хронометра.
         */
        public void handleConnected(ChronoService service);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bound) {
            unbindService(chronoConnection);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public ChronoService getChronoService() {
        return chronoService;
    }
}

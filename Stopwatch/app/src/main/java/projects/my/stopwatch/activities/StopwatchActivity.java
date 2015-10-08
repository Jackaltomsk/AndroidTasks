package projects.my.stopwatch.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import projects.my.stopwatch.services.ChronoService;
import projects.my.stopwatch.R;
import projects.my.stopwatch.fragments.TimeFragment;
import projects.my.stopwatch.services.ChronoTimerManager;

public class StopwatchActivity extends AppCompatActivity {
    public static final int REQUEST_COLOR_CODE = 1;
    private boolean bound;
    private TimeFragment timeFragment;
    private ServiceConnection chronoConnection;
    private ChronoService chronoService;
    private MenuItem startStopItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        createServiceBinding();
        createTimeFragment(savedInstanceState);
        // Установка тулбара.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public interface ChronoConnectedListener {

        /**
         * Реализует обработку события биндинга сервиса.
         * @param service Сервис хронометра.
         */
        public void handleConnected(ChronoTimerManager service);
    }

    public interface ListItemGetter {
        public String getItemText();
        public void sendItemText(String text);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bound) {
            unbindService(chronoConnection);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stopwatch, menu);
        startStopItem = menu.findItem(R.id.start_counter);
        super.onCreateOptionsMenu(menu);
        stateChanged(chronoService.getIsTimerRunning());
        return true;
    }

    private void createServiceBinding() {
        Intent intent = new Intent(this, ChronoService.class);
        startService(intent);
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
        bindService(intent, chronoConnection, BIND_AUTO_CREATE);
    }

    private void createTimeFragment(Bundle savedInstanceState) {
        FragmentManager manager = getFragmentManager();
        if (savedInstanceState == null) {
            // Добавление фрагмента в разметку окна, если пересоздается активити.
            FragmentTransaction transaction = manager.beginTransaction();
            timeFragment = new TimeFragment();
            transaction.add(R.id.time_fragment_frame, timeFragment);
            transaction.commit();
        }
        else {
            timeFragment = (TimeFragment) manager.findFragmentById(R.id.time_fragment_frame);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.start_counter:
                if (!chronoService.getIsChronometerRunning()) {
                    timeFragment.start();
                    stateChanged(true);
                }
                else {
                    timeFragment.stop();
                    stateChanged(false);
                }
                break;
            case R.id.drop_counter:
                timeFragment.drop();
                stateChanged(false);
                break;
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, REQUEST_COLOR_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_COLOR_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                int colorId = data.getIntExtra("color", android.R.color.white);
                timeFragment.handleBackgroundColorChange(new ColorDrawable(colorId));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast tst = Toast.makeText(this, "Request cancelled", Toast.LENGTH_SHORT);
                tst.show();
            }
        }
    }

    /**
     * Реализует смену названия пункта меню при старте/остановке хронометра.
     */
    private void stateChanged(boolean isRunning) {
        startStopItem.setTitle(isRunning ? R.string.menu_stop_counter_title : R.string.menu_start_counter_title);
    }
}

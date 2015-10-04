package projects.my.stopwatch.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import projects.my.stopwatch.services.ChronoService;
import projects.my.stopwatch.R;
import projects.my.stopwatch.fragments.TimeFragment;

public class StopwatchActivity extends AppCompatActivity {
    private TimeFragment timeFragment;
    private ServiceConnection chronoConnection;
    private ChronoService chronoService;
    private boolean bound;
    private final static String TIME_LIST = "TIME_LIST";
    private ArrayList<String> listItems;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        createListAdapter(savedInstanceState);
        createServiceBinding();
        createTimeFragment(savedInstanceState);
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
        outState.putStringArrayList(TIME_LIST, listItems);
    }

    public ChronoService getChronoService() {
        return chronoService;
    }

    public void handleAddtimeClick(View view) {
        CharSequence text = timeFragment.getChronoText();
        listItems.add(text.toString());
        adapter.notifyDataSetChanged();
    }

    private void createServiceBinding() {
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
    }

    private void createTimeFragment(Bundle savedInstanceState) {
        FragmentManager manager = getFragmentManager();
        if (savedInstanceState == null) {
            // Добавление фрагмента в разметку окна, если пересоздается активити.
            FragmentTransaction transaction = manager.beginTransaction();
            timeFragment = new TimeFragment();
            transaction.add(R.id.frameLayout, timeFragment);
            transaction.commit();
        }
        else {
            timeFragment = (TimeFragment) manager.findFragmentById(R.id.frameLayout);
        }
    }

    private void createListAdapter(Bundle savedInstanceState) {
        if (savedInstanceState == null) listItems = new ArrayList<>();
        else listItems = savedInstanceState.getStringArrayList(TIME_LIST);

        adapter = new ArrayAdapter<>(this, R.layout.listview_item, R.id.textItem, listItems);
        ListView list = (ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);
    }
}

package projects.my.stopwatch.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Arrays;

import projects.my.stopwatch.adapters.StopwatchPagerAdapter;
import projects.my.stopwatch.fragments.CountDownFragment;
import projects.my.stopwatch.fragments.FragmentTimeManager;
import projects.my.stopwatch.fragments.ListviewFragment;
import projects.my.stopwatch.services.ChronoService;
import projects.my.stopwatch.R;
import projects.my.stopwatch.fragments.TimeFragment;
import projects.my.stopwatch.services.ChronoTimerManager;

public class StopwatchActivity extends AppCompatActivity
        implements ListviewFragment.OnListActionListener {
    public static final int REQUEST_COLOR_CODE = 1;
    private boolean bound;
    private TimeFragment chronoFragment;
    private CountDownFragment countDownFragment;
    private FragmentTimeManager currentFragment;
    private ServiceConnection chronoConnection;
    private ChronoService chronoService;
    private MenuItem startStopItem;
    private StopwatchPagerAdapter pageAdapter;
    private ViewPager pager;

    public interface ChronoConnectedListener {

        /**
         * Реализует обработку события биндинга сервиса.
         * @param service Сервис хронометра.
         */
        public void handleConnected(ChronoTimerManager service);
    }

    public interface BackgroundColorChange {
        public void handleBackgroundColorChange(ColorDrawable color);
    }

    @Override
    public String getItemText() {
        return chronoFragment.getTimeValue();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        createServiceBinding();
        setToolbar();

        chronoFragment = TimeFragment.newInstance();
        countDownFragment = CountDownFragment.newInstance();
        currentFragment = chronoFragment;

        android.app.FragmentManager manager = getFragmentManager();
        FragmentTransaction tr = manager.beginTransaction();
        tr.attach(chronoFragment).attach(countDownFragment).commit();

        pageAdapter = new StopwatchPagerAdapter(getFragmentManager(),
                Arrays.asList((Fragment) chronoFragment, countDownFragment));
        pager = (ViewPager) findViewById(R.id.fragmentPager);
        pager.setAdapter(pageAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        if (chronoService != null) stateChanged(chronoService.getIsTimerRunning());

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(pager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                currentFragment = (FragmentTimeManager) pageAdapter.getItem(position);
                stateChanged(currentFragment.getIsRunning());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                return;
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                return;
            }
        });

        return true;
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void createServiceBinding() {
        Intent intent = new Intent(this, ChronoService.class);
        startService(intent);
        final Activity act = this;
        chronoConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                ChronoService.ChronoBinder binder = (ChronoService.ChronoBinder) service;
                chronoService = binder.getService();
                chronoService.createNotificationInfrastructure(StopwatchActivity.this);
                chronoFragment.handleConnected(chronoService);
                countDownFragment.handleConnected(chronoService);
                //Toast.makeText(act, "Service connected", Toast.LENGTH_SHORT).show();
                bound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                bound = false;
            }
        };
        bindService(intent, chronoConnection, BIND_AUTO_CREATE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.start_counter:
                if (!currentFragment.getIsRunning()) {
                    currentFragment.start();
                    stateChanged(true);
                }
                else {
                    currentFragment.stop();
                    stateChanged(false);
                }
                break;
            case R.id.drop_counter:
                currentFragment.drop();
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
                chronoFragment.handleBackgroundColorChange(new ColorDrawable(colorId));
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
        startStopItem.setTitle(isRunning ? R.string.menu_stop_counter_title :
                R.string.menu_start_counter_title);
    }
}

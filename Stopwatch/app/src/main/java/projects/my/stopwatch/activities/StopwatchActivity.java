package projects.my.stopwatch.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import projects.my.stopwatch.R;
import projects.my.stopwatch.adapters.StopwatchPagerAdapter;
import projects.my.stopwatch.fragments.CountDownFragment;
import projects.my.stopwatch.fragments.FragmentTimeManager;
import projects.my.stopwatch.fragments.ListviewFragment;
import projects.my.stopwatch.services.ChronoService;
import projects.my.stopwatch.fragments.TimeFragment;
import projects.my.stopwatch.services.ChronoTimerManager;
import projects.my.timerdb.dao.GenericDao;
import projects.my.timerdb.infrastructure.DbContext;
import projects.my.timerdb.infrastructure.DbManager;
import projects.my.timerdb.models.Properties;

public class StopwatchActivity extends AppCompatActivity
        implements ListviewFragment.OnListActionListener {
    private static final int REQUEST_COLOR_CODE = 1;
    private static final String BACKGROUND_COLOR = "BACKGROUND_COLOR";
    private boolean bound;
    private int backgroundColor;
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

    @Override
    public String getItemText() {
        return currentFragment.getTimeValue();
    }

    @Override
    public boolean canAddItemText() {
        return currentFragment.getIsRunning();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Создаем контекст единожды и для всего приложения.
        // Уничтожать его явно смысла нет - учечка памяти ничтожна и будет ликвидирована
        // по закрытии приложения.
        if (!DbManager.isContextSet()) DbManager.setDbContext(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        initViewPager();
        setToolbar();
        createServiceBinding();
        initBackground(savedInstanceState);
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
        if (chronoService != null && currentFragment != null) {
            stateChanged(currentFragment.getIsRunning());
        }
        setupTabs();
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BACKGROUND_COLOR, backgroundColor);
    }

    /**
     * Реализует инициализацию табовю.
     */
    private void setupTabs() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(pager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                updateCurrentFragment(position);
                stateChanged(currentFragment.getIsRunning());
                pager.setCurrentItem(position);
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
    }

    /**
     * Реализует обновление текущего(выбранного) фрагмента.
     * @param position Позиция фрагмента в пейджере.
     */
    private void updateCurrentFragment(int position) {
        currentFragment = (FragmentTimeManager) pageAdapter.getItem(position);
    }

    /**
     * Реализует инициализапцию вьюпейджера и адаптера страниц.
     */
    private void initViewPager() {
        pager = (ViewPager) findViewById(R.id.fragmentPager);
        pageAdapter = new StopwatchPagerAdapter(getFragmentManager());
        pager.setAdapter(pageAdapter);

        // Пререндерим вью для фрагментов.
        Fragment[] fragments = pageAdapter.getFragments();
        for (int i = 0; i < fragments.length; i++) {
            pageAdapter.instantiateItem(pager, i);
        }
        currentFragment = (FragmentTimeManager) pageAdapter.getItem(0);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updateCurrentFragment(position);
                stateChanged(currentFragment.getIsRunning());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                return;
            }
        });
    }

    /**
     * Реализует инициализацию тулбара.
     */
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Реализует запуск и биндинг сервиса.
     */
    private void createServiceBinding() {
        Intent intent = new Intent(this, ChronoService.class);
        startService(intent);
        chronoConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                ChronoService.ChronoBinder binder = (ChronoService.ChronoBinder) service;
                chronoService = binder.getService();
                chronoService.createNotificationInfrastructure(StopwatchActivity.this);

                for (Fragment fmg : pageAdapter.getFragments()) {
                    if (fmg instanceof FragmentTimeManager) {
                        ((FragmentTimeManager) fmg).handleConnected(chronoService);
                    }
                }
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
                    // Останавливаем все работающие хронометры/таймеры, кроме текущего.
                    List<Fragment> af = new ArrayList<>();
                    Collections.addAll(af, pageAdapter.getFragments());
                    af.remove(currentFragment);
                    for (Fragment fr : af) {
                        FragmentTimeManager frt = (FragmentTimeManager) fr;
                        if (frt.getIsRunning()) frt.stop();
                    }

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
            case R.id.preferences: {
                throw new UnsupportedOperationException("Окно настроек не реализовано");
                //break;
            }
            default: return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_COLOR_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                int colorId = data.getIntExtra("color", android.R.color.white);
                handleBackgroundColorChange(new ColorDrawable(colorId));
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
        if (startStopItem != null) {
            startStopItem.setTitle(isRunning ? R.string.menu_stop_counter_title :
                    R.string.menu_start_counter_title);
        }
    }

    /**
     * Реализует инициализацию цвета бэкграунда активити.
     * @param savedInstanceState
     */
    private void initBackground(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            backgroundColor = savedInstanceState.getInt(BACKGROUND_COLOR);
        }
        else {
            TypedArray typedArray = getTheme().
                    obtainStyledAttributes(new int[]{android.R.attr.background});
            backgroundColor = typedArray.getColor(0, 0xFF00FF);
            typedArray.recycle();
        }

        View mainContainer = findViewById(R.id.stopwatch_main_container);
        mainContainer.setBackground(new ColorDrawable(backgroundColor));
    }

    /**
     * Реализует сменю цвета бэкграунда.
     * @param color Цвет.
     */
    private void handleBackgroundColorChange(ColorDrawable color) {
        View mainContainer = findViewById(R.id.stopwatch_main_container);
        Drawable bc = mainContainer.getBackground();
        ColorDrawable colorOne;
        TransitionDrawable td;

        // Если фон еще не менялся.
        if (bc == null) colorOne = new ColorDrawable(backgroundColor);
        else {
            if (bc instanceof TransitionDrawable) {
                colorOne = (ColorDrawable) ((TransitionDrawable) bc).getDrawable(1);
            }
            else colorOne = (ColorDrawable) bc;
        }

        backgroundColor = color.getColor();
        ColorDrawable[] colors = { colorOne, color };
        td = new TransitionDrawable(colors);
        mainContainer.setBackground(td);
        td.startTransition(2000);
    }
}

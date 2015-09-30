package projects.my.stopwatch;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class StopwatchActivity extends AppCompatActivity implements TimeFragment.ChronometerState {
    private MenuItem startStopItem;
    private boolean isChronoRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        if (savedInstanceState == null) {
            // Добавление фрагмента в разметку окна, если запуск - первый.
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            TimeFragment timeFragment = new TimeFragment();
            transaction.add(R.id.stopwatch_fragment_container, timeFragment);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stopwatch, menu);
        // Установка названия действия в соответсвии с текущим статусом таймера.
        startStopItem = menu.findItem(R.id.start_counter);
        if (startStopItem == null) {
            throw new NullPointerException("Не найден пункт меню 'Запустить'");
        }
        stateChanged(isChronoRunning);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Вся логика пока завязана на один фрагмент, так что его и получаем.
        TimeFragment timeFragment = (TimeFragment)getFragmentManager()
                .findFragmentById(R.id.stopwatch_fragment_container);
        if (timeFragment == null) {
            throw new NullPointerException("Не найден фрагмент с хронометром.");
        }

        switch (id) {
            case R.id.start_counter:
                if (!timeFragment.getIsRunning()) timeFragment.startTimer();
                else timeFragment.stopTimer();
                break;
            case R.id.drop_counter:
                timeFragment.resetTimer();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void stateChanged(boolean isRunning) {
        // В момент вызова метода меню еще может быть неинициализировано.
        if (startStopItem == null) isChronoRunning = isRunning;
        else {
            startStopItem.setTitle(isRunning ?
                    R.string.menu_stop_counter_title : R.string.menu_start_counter_title);
        }
    }
}

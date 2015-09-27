package projects.my.stopwatch;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class StopwatchActivity extends AppCompatActivity {

    private int currentStopwatchTitle;
    private final String CURRENT_STOPWATCH_STATE = "CURRENT_STOPWATCH_STATE";
    MenuItem startStopItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        if (savedInstanceState == null) {
            currentStopwatchTitle = R.string.menu_start_counter_title;

            // Добавление фрагмента в разметку окна, если запуск - первый.
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            TimeFragment timeFragment = new TimeFragment();
            transaction.add(R.id.stopwatch_fragment_container, timeFragment);
            transaction.commit();
        }
        else currentStopwatchTitle = savedInstanceState.getInt(CURRENT_STOPWATCH_STATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stopwatch, menu);
        // Установка названия действия в соответсвии с текущим статусом таймера.
        startStopItem = menu.findItem(R.id.start_counter);
        startStopItem.setTitle(currentStopwatchTitle);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Вся логика пока завязана на один фрагмент, так что его и получаем.
        TimeFragment timeFragment = (TimeFragment)getFragmentManager()
                .findFragmentById(R.id.stopwatch_fragment_container);

        switch (id) {
            case R.id.start_counter:
                String titleName = item.getTitle().toString();

                if (titleName.equals(getResources().getString(R.string.menu_start_counter_title))) {
                    currentStopwatchTitle = R.string.menu_stop_counter_title;
                    timeFragment.startTimer();
                }
                else {
                    currentStopwatchTitle = R.string.menu_start_counter_title;
                    timeFragment.stopTimer();
                }
                item.setTitle(currentStopwatchTitle);
                break;
            case R.id.drop_counter:
                timeFragment.stopTimer();
                timeFragment.resetTimer();
                currentStopwatchTitle = R.string.menu_start_counter_title;
                startStopItem.setTitle(currentStopwatchTitle);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_STOPWATCH_STATE, currentStopwatchTitle);
    }
}

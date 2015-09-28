package projects.my.stopwatch;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class StopwatchActivity extends AppCompatActivity {
    private int mCurrentStopwatchTitle;
    private static String sCurrentStopwatchState = "CURRENT_STOPWATCH_STATE";
    private MenuItem mStartStopItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        if (savedInstanceState == null) {
            mCurrentStopwatchTitle = R.string.menu_start_counter_title;
            // Добавление фрагмента в разметку окна, если запуск - первый.
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            TimeFragment timeFragment = new TimeFragment();
            transaction.add(R.id.stopwatch_fragment_container, timeFragment);
            transaction.commit();
        }
        else mCurrentStopwatchTitle = savedInstanceState.getInt(sCurrentStopwatchState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stopwatch, menu);
        // Установка названия действия в соответсвии с текущим статусом таймера.
        mStartStopItem = menu.findItem(R.id.start_counter);
        if (mStartStopItem == null) {
            throw new NullPointerException("Не найден пункт меню 'Запустить'");
        }
        mStartStopItem.setTitle(mCurrentStopwatchTitle);
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
                String titleName = item.getTitle().toString();
                if (titleName.equals(getResources().getString(R.string.menu_start_counter_title))) {
                    mCurrentStopwatchTitle = R.string.menu_stop_counter_title;
                    timeFragment.startTimer();
                }
                else {
                    mCurrentStopwatchTitle = R.string.menu_start_counter_title;
                    timeFragment.stopTimer();
                }
                item.setTitle(mCurrentStopwatchTitle);
                break;
            case R.id.drop_counter:
                timeFragment.stopTimer();
                timeFragment.resetTimer();
                mCurrentStopwatchTitle = R.string.menu_start_counter_title;
                mStartStopItem.setTitle(mCurrentStopwatchTitle);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(sCurrentStopwatchState, mCurrentStopwatchTitle);
    }
}

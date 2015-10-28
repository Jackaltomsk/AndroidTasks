package projects.my.timerdb.dao.extensions;

import android.util.Log;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import projects.my.timerdb.dao.GenericDao;
import projects.my.timerdb.models.TimeCutoff;

/**
 * Методы работы с отчечками.
 */
public class TimeCutoffExtension extends BaseExtension<TimeCutoff> {

    private static final String TAG = TimeCutoffExtension.class.getSimpleName();

    public TimeCutoffExtension(GenericDao<TimeCutoff> dao) throws NullPointerException {
        super(dao);
    }

    /**
     * Реализует получение осхраненных значений таймера.
     * @return Возвращает массив значений времени.
     */
    public long[] getSavedTimers() {
        try {
            List<TimeCutoff> time = dao.queryBuilder().where().eq(TimeCutoff.ISTIMERSTATE_FIELD,
                    true).query();
            long[] timeValues = new long[time.size()];
            for (int i = 0; i < timeValues.length; i++) {
                timeValues[i] = time.get(i).getCutoff();
            }
            return timeValues;
        }
        catch (SQLException e) {
            Log.e(TAG, "Ошибка сохранения цвета в БД");
        }
        return new long[0];
    }
}

package projects.my.timerdb.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Модель отсечек.
 */
@DatabaseTable
public class TimeCutoff extends BaseEntity {

    public final static String ISTIMERSTATE_FIELD = "isTimerState";

    @DatabaseField(foreign = true, canBeNull = false)
    private TimeManager manager;

    @DatabaseField(canBeNull = false)
    private long cutoff;

    @DatabaseField(columnName = ISTIMERSTATE_FIELD, index = true)
    private boolean isTimerState;

    public TimeCutoff() {}

    public TimeCutoff(long cutoff, boolean isTimerState) {
        this.cutoff = cutoff;
        this.isTimerState = isTimerState;
    }

    public TimeManager getTimeManager() {
        return manager;
    }

    public void setTimeManager(TimeManager manager) {
        this.manager = manager;
    }

    public long getCutoff() {
        return cutoff;
    }

    public void setCutoff(long cutoff) {
        this.cutoff = cutoff;
    }

    public boolean isTimerState() {
        return isTimerState;
    }

    public void setIsTimerState(boolean isTimerState) {
        this.isTimerState = isTimerState;
    }
}

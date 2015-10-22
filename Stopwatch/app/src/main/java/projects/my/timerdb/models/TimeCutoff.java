package projects.my.timerdb.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Модель отсечек.
 */
@DatabaseTable
public class TimeCutoff {

    @DatabaseField(generatedId = true)
    private int Id;

    @DatabaseField(foreign = true, canBeNull = false)
    private TimeManager Manager;

    @DatabaseField(canBeNull = false)
    private String Cutoff;

    public TimeManager getTimeManager() {
        return Manager;
    }

    public void setTimeManager(TimeManager manager) {
        Manager = manager;
    }

    public String getCutoff() {
        return Cutoff;
    }

    public void setCutoff(String cutoff) {
        Cutoff = cutoff;
    }
}

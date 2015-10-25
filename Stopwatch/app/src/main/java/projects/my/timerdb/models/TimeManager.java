package projects.my.timerdb.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Модель менеджеров времени.
 */
@DatabaseTable()
public class TimeManager extends BaseEntity {
    public static final String CHRONOMETER_NAME = "chronometer";
    public static final String TIMER_NAME = "timer";
    public static final String NAME_FILED = "name";

    @DatabaseField(columnName = NAME_FILED ,canBeNull = false, unique = true)
    private String name;

    @ForeignCollectionField(eager = true)
    private Collection<TimeCutoff> cutoffs;

    public TimeManager() {
        cutoffs = new ArrayList<>();
    }

    public TimeManager(String name) {
        this();
        this.setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.isEmpty()) this.name = name;
    }

    public TimeCutoff[] getCutoffs() {
        return cutoffs.toArray(new TimeCutoff[cutoffs.size()]);
    }

    public boolean addCutoff(TimeCutoff cutoff) {
        cutoff.setTimeManager(this);
        return cutoffs.add(cutoff);
    }

    public boolean removeCutoff(TimeCutoff cutoff) {
        cutoff.setTimeManager(null);
        return cutoffs.remove(cutoff);
    }
}

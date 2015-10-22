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
public class TimeManager {

    @DatabaseField(generatedId = true)
    private int Id;

    @DatabaseField(canBeNull = false)
    private String Name;

    @ForeignCollectionField(eager = true)
    private Collection<TimeCutoff> Cutoffs;

    public TimeManager() {
        Cutoffs = new ArrayList<>();
    }

    public TimeCutoff[] getCutoffs() {
        return Cutoffs.toArray(new TimeCutoff[Cutoffs.size()]);
    }

    public boolean addCutoff(TimeCutoff cutoff) {
        cutoff.setTimeManager(this);
        return Cutoffs.add(cutoff);
    }

    public boolean removeCutoff(TimeCutoff cutoff) {
        cutoff.setTimeManager(null);
        return Cutoffs.remove(cutoff);
    }

    public void setName(String name) {
        if (name != null && !name.isEmpty()) Name = name;
    }
}

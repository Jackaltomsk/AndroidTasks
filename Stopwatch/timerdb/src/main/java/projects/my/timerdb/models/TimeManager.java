package projects.my.timerdb.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.sun.javaws.exceptions.InvalidArgumentException;

/**
 * Модель менеджеров времени.
 */
@DatabaseTable(tableName = "time_manager")
public class TimeManager {
    @DatabaseField(generatedId = true)
    private int Id;

    @DatabaseField(canBeNull = false)
    private String Name;

    public void setName(String name) throws InvalidArgumentException {
        if (name != null && !name.isEmpty()) Name = name;
    }
}

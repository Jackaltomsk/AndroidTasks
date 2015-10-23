package projects.my.timerdb.models;

import com.j256.ormlite.field.DatabaseField;

/**
 * Абстрактный родитель сущностей БД.
 */
public abstract class BaseEntity {

    @DatabaseField(generatedId = true)
    protected int id;

    @Override
    public String toString() {
        return String.valueOf(id) + ", " + this.getClass().getSimpleName();
    }
}

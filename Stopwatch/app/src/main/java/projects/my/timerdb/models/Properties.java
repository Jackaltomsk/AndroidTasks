package projects.my.timerdb.models;

import android.graphics.Color;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import projects.my.timerdb.dao.GenericDao;

/**
 * Модель основных свойств приложения.
 */
@DatabaseTable()
public class Properties extends BaseEntity {

    public static final String COLOR = "color";
    public static final String NAME_FIELD = "name";

    @DatabaseField(columnName = NAME_FIELD, canBeNull = false, unique = true)
    private String name;

    @DatabaseField(canBeNull = false)
    private String value;

    public Properties(){}

    public Properties(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

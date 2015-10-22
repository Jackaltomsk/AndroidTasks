package projects.my.timerdb.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Модель основных свойств приложения.
 */
@DatabaseTable()
public class Properties {

    @DatabaseField(generatedId = true)
    private int Id;

    @DatabaseField
    private int Color;

    public int getColor() {
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }
}

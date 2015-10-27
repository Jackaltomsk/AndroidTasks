package projects.my.timerdb.dao.extensions;

import android.util.Log;

import java.sql.SQLException;

import projects.my.timerdb.dao.GenericDao;
import projects.my.timerdb.models.Properties;

/**
 * Расширение операция со свойствами.
 */
public class PropertiesExtension extends BaseExtension<Properties> {

    private static final String TAG = PropertiesExtension.class.getSimpleName();

    public PropertiesExtension(GenericDao<Properties> dao) {
        super(dao);
    }

    /**
     * Реализует сохранение/обновление в БД свойства с переданным значением цвета.
     * @param colorId Цвет.
     * @return Возвращает признак успешности операции.
     */
    public boolean setNewColor(int colorId) {
        Properties prop = getColorProperty();
        if (prop == null) { // создадим запись с цветом
            prop = new Properties(Properties.COLOR, String.valueOf(colorId));
        } else prop.setValue(String.valueOf(colorId));
        try {
            dao.createOrUpdate(prop);
            return true;
        }
        catch (SQLException e) {
            Log.e(TAG, "Ошибка сохранения цвета в БД");
        }
        return false;
    }

    /**
     * Реализует получение цвета из записи, если она существует в БД.
     * @return Возвращает цвет, если существует запись с цветом. Null - при обратном.
     */
    public Integer getColor() {
        Properties props = getColorProperty();
        if (props != null) {
            try {
                int color = Integer.parseInt(props.getValue());
                return color;
            }
            catch (NumberFormatException e) {
                Log.e(TAG, "Не распарсено значение " + props.getValue());
            }
        }
        return null;
    }

    /**
     * Реализует получение свойства с цветом.
     * @return Возвращает свойство с цветом. Null, если не найдено.
     */
    public Properties getColorProperty() {
        Properties props = null;
        try {
            props = dao.queryBuilder().where().eq(Properties.NAME_FIELD, Properties.COLOR)
                    .queryForFirst();
        }
        catch (SQLException e) {
            Log.e(TAG, "Ошибка получения свойства с цветом.");
        }
        return props;
    }
}

package projects.my.timerdb.infrastructure;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Содержит в себе синглтон контекста БД и методы управления его состоянием.
 */
public class DbManager {

    private static DbContext context;

    public static boolean isContextSet() {
        return context != null;
    }

    public static DbContext getDbContext(){
        return context;
    }

    /**
     * Инициализирует контекст БД при старте приложения.
     * @param context Текущий контекст приложения.
     */
    public static void setDbContext(Context context){
        DbManager.context = OpenHelperManager.getHelper(context, DbContext.class);
    }

    /**
     * Реализует удаление БД
     * @param context Текущий контекст приложения.
     * @param name Название БД.
     * @return
     */
    public static boolean deleteDb(Context context, String name) {
        return context.deleteDatabase(DbContext.DATABASE_NAME);
    }

    /**
     * Освобождает ресурсы. Вызывать следует в момент закрытия приложения.
     * Если контекст - единый для всего приложения, можно рассмотреть вариант отказа от вызова
     * метода вовсе.
     */
    public static void releaseDbContext(){
        OpenHelperManager.releaseHelper();
        context = null;
    }
}

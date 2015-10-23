package projects.my.timerdb.infrastructure;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import projects.my.timerdb.models.Properties;
import projects.my.timerdb.models.TimeCutoff;
import projects.my.timerdb.models.TimeManager;

/**
 * Контекст БД.
 */
public class DbContext extends OrmLiteSqliteOpenHelper {
    private static final String TAG = DbContext.class.getSimpleName();

    //имя файла базы данных который будет храниться в /data/data/APPNAME/DATABASE_NAME.db
    private static final String DATABASE_NAME ="timer.db";

    //с каждым увеличением версии, при нахождении в устройстве БД с предыдущей версией будет
    // выполнен метод onUpgrade();
    private static final int DATABASE_VERSION = 1;

    public DbContext(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Выполняется, когда файл с БД не найден на устройстве
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource){
        try
        {
            TableUtils.createTable(connectionSource, Properties.class);
            TableUtils.createTable(connectionSource, TimeManager.class);
            TableUtils.createTable(connectionSource, TimeCutoff.class);
        }
        catch (SQLException e){
            Log.e(TAG, "error creating DB " + DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }

    //Выполняется, когда БД имеет версию отличную от текущей
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer,
                          int newVer){
        try{
            //Так делают ленивые, гораздо предпочтительнее не удаляя БД аккуратно вносить изменения
            TableUtils.dropTable(connectionSource, Properties.class, true);
            TableUtils.dropTable(connectionSource, TimeManager.class, true);
            TableUtils.dropTable(connectionSource, TimeCutoff.class, true);
            onCreate(db, connectionSource);
        }
        catch (SQLException e){
            Log.e(TAG,"error upgrading db "+ DATABASE_NAME + "from ver "+oldVer);
            throw new RuntimeException(e);
        }
    }

    //выполняется при закрытии приложения
    @Override
    public void close(){
        super.close();
    }
}

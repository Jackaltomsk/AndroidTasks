package projects.my.timerdb.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import projects.my.timerdb.models.BaseEntity;

/**
 * Реализация общей логики для всех сущностей БД.
 */
public class GenericDao<T extends BaseEntity> extends BaseDaoImpl<T, Integer> {
    public GenericDao(ConnectionSource connectionSource, Class<T> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }
}

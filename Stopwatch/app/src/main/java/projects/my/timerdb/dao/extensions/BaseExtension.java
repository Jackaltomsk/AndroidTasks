package projects.my.timerdb.dao.extensions;

import java.io.Closeable;

import projects.my.timerdb.dao.GenericDao;
import projects.my.timerdb.models.BaseEntity;

/**
 * Суперкласс для расширений станлартного репозитория.
 */
public abstract class BaseExtension<T extends BaseEntity> implements Closeable {

    protected GenericDao<T> dao;

    protected BaseExtension(GenericDao<T> dao) throws NullPointerException {
        if (dao == null) throw new NullPointerException("GenericDao равно null.");
        this.dao = dao;
    }

    public void close() {
        this.dao = null;
    }
}
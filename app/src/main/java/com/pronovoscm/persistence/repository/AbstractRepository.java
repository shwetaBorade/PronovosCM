package com.pronovoscm.persistence.repository;


import com.pronovoscm.persistence.domain.DaoSession;

/**
 * Abstract repository all repositories extend from.
 *
 * @author Matt Fleshman
 */
public abstract class AbstractRepository {

    private DaoSession daoSession;

    public AbstractRepository(DaoSession daoSession) {
        super();
        if (daoSession == null) {
            throw new IllegalArgumentException("DAO Session cannot be null.");
        }
        this.daoSession = daoSession;
    }

    /**
     * Gets DAO session.
     *
     * @return DAO session, never null.
     */
    public DaoSession getDaoSession() {
        return daoSession;
    }

}

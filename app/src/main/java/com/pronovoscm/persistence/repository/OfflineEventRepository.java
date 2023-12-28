package com.pronovoscm.persistence.repository;

import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.OfflineEvent;
import com.pronovoscm.persistence.domain.OfflineEventDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Defines all methods for querying offline event table
 *
 * @author Nitin Bhawsar
 */

public class OfflineEventRepository extends AbstractRepository {

    public OfflineEventRepository(DaoSession daoSession) {
        super(daoSession);
    }

    /**
     * load event using id
     *
     * @param id
     * @return
     */
    public OfflineEvent getEventById(Long id) {
        if (id == null) {
            return null;
        }
        return getDaoSession().getOfflineEventDao().load(id);
    }

    /**
     * Returns all offline event ordered by date ascending stored in event table ..
     *
     * @return list of all employees.
     */
    public List<OfflineEvent> getAllOfflineEvent() {

        QueryBuilder<OfflineEvent> queryBuilder = getDaoSession().getOfflineEventDao().queryBuilder();
        queryBuilder.orderAsc(OfflineEventDao.Properties.Date);

        return queryBuilder.build().list();
    }

    /**
     * delete offline event task form offline_event table
     *
     * @param taskId ID of Task
     */
    public void deleteOfflineEvent(Long taskId) {
        OfflineEventDao eventDao = getDaoSession().getOfflineEventDao();
        OfflineEvent event = getEventById(taskId);
        if (event != null)
            eventDao.deleteInTx(event);

    }


    public void deleteOfflineEvent(OfflineEvent offlineEvent) {
        OfflineEventDao eventDao = getDaoSession().getOfflineEventDao();
        eventDao.deleteInTx(offlineEvent);
    }

}

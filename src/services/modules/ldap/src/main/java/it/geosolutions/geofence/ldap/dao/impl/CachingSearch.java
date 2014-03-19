package it.geosolutions.geofence.ldap.dao.impl;

import com.googlecode.genericdao.search.Search;
import it.geosolutions.geofence.core.dao.RestrictedGenericDAO;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Caches search results for a very short period of time in order to allow
 * massive amounts of searches to still be performant.
 *
 * Created by Jesse on 3/19/14.
 */
public class CachingSearch<ENTITY, DAO extends RestrictedGenericDAO<ENTITY>> {

    private final DAO dao;
    private final Search search;
    private long lastUpdate = 0L;
    private Lock cacheLock = new ReentrantLock();
    private List<ENTITY> entityCache = null;
    private long cacheTime;

    public CachingSearch(long cacheTime, Search search, DAO dao) {
        this.dao = dao;
        this.search = search;
        this.cacheTime = cacheTime;
    }

    public List<ENTITY> search() {
        this.cacheLock.lock();
        try {
            if (this.entityCache != null && System.currentTimeMillis() - this.lastUpdate < this.cacheTime) {
               return this.entityCache;
            }

            this.entityCache = this.dao.search(this.search);
            this.lastUpdate = System.currentTimeMillis();

            return this.entityCache;
        } finally {
            this.cacheLock.unlock();
        }
    }
}

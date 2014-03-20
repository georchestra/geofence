/*
 *  Copyright (C) 2007 - 2012 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.geosolutions.geofence.ldap.dao.impl;

import com.codahale.metrics.MetricRegistry;
import it.geosolutions.geofence.core.dao.RestrictedGenericDAO;
import it.geosolutions.geofence.dao.utils.LdapUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.Search;

/**
 * Base DAO Implementation using LDAP services.
 * 
 * It uses a spring-ldap LdapTemplate to communicate with the LDAP server.
 * Currently only read type operations are supported (findAll, find, search).
 * 
 * A backup DAO can be defined. It will be used as a primary source for id lookups.
 * 
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 * @param <R>
 *
 */
public abstract class BaseDAO<T extends RestrictedGenericDAO<R>, R> implements RestrictedGenericDAO<R> {

	private LdapTemplate ldapTemplate;
	
	private String searchBase;
	
	private String searchFilter;
	
	private AttributesMapper attributesMapper;
	 
	T dao;

    @Autowired
    protected MetricRegistry metricRegistry;

    /**
	 * Sets the backup DAO.
	 * 
	 * @param dao the dao to set
	 */
	public void setDao(T dao) {
		this.dao = dao;
	}

	/**
	 * Sets the base name for users in LDAP server.
	 * 
	 * @param searchBase the searchBase to set
	 */
	public void setSearchBase(String searchBase) {
		this.searchBase = searchBase;
	}
	
	/**
	 * Sets the filter used to identify objects from the base name.
	 * 
	 * @param searchFilter the searchFilter to set
	 */
	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}

	/**
	 * Sets the AttributeMapper used to build objects from LDAP
	 * objects.
	 * 
	 * @param attributesMapper the attributesMapper to set
	 */
	public void setAttributesMapper(AttributesMapper attributesMapper) {
		this.attributesMapper = attributesMapper;
	}

	/**
	 * Sets the LDAP communication object.
	 * 
	 * @param ldapTemplate the ldapTemplate to set
	 */
	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}
	
	@Override
	public List<R> findAll() {
        com.codahale.metrics.Timer.Context context = metricRegistry.timer(getClass().getName() + "_findAll()").time();
        try {
    		return search(searchFilter);
        } finally {
            context.stop();
        }
	}

	@Override
	public R find(Long id) {
        com.codahale.metrics.Timer.Context context = metricRegistry.timer(getClass().getName() + "_find(id)").time();
        try {
            // try to load the user from db, first
            R object = searchOnDb(id);
            if(object != null) {
                return object;
            }
            List<R> objects = search( new Filter("id", id) );
            if(objects == null || objects.size() == 0) {
                return null;
            }
            return objects.get(0);
        } finally {
            context.stop();
        }
	}
	
	@Override
	public List<R> search(ISearch search) {
        List<R> objects = new ArrayList<R>();
        if(search.getFilters().size() == 0) {
            // no filter
            return findAll();
        }
        for(Filter filter : search.getFilters()) {
            if(filter != null) {
                List<R> filteredObjects = search(filter);
                objects.addAll(filteredObjects);
            }
        }
		return objects;
	}
	
	@Override
	public int count(ISearch search) {
        com.codahale.metrics.Timer.Context context = metricRegistry.timer(getClass().getName() + "_count()").time();
        try {
    		return search(search).size();
        } finally {
            context.stop();
        }
	}
	
	@Override
	public void persist(R... entities) {
		// insert not implemented
	}


	@Override
	public R merge(R entity) {
		// update not implemented
		return entity;
	}


	@Override
	public boolean remove(R entity) {	
		// remove not implemented
		return false;
	}


	@Override
	public boolean removeById(Long id) {
		// remove not implemented
		return false;
	}
	
	/**
	 * Does a direct lookup for the distinguished name given.
	 * 
	 * @param dn distinguished name to lookup
	 * @return
	 */
	public R lookup(String dn) {
        com.codahale.metrics.Timer.Context context = metricRegistry.timer(getClass().getName() + "_lookup(dn)").time();
        try {
            final R object = (R) ldapTemplate.lookup(dn, attributesMapper);
            updateIdsFromDatabase(Arrays.asList(object));
            return object;
        } finally {
            context.stop();
        }
	}
	
	/**
	 * Search the given user id on the backup DAO, if defined.
	 * The id can be a classic id (>0) or an extId (<0).
	 * It's an extId if it's the id read from the LDAP server.
	 * 
	 * @param id
	 * @return
	 */
	private R searchOnDb(Long id) {
		if(dao != null) {
			if(id < 0) {
				// If negative, it's an extId (id from LDAP server)
				// we must search on that attribute
				Search search = new Search();
				Filter filter = new Filter("extId", id+"");
				List<Filter> filters = new ArrayList<Filter>();
				filters.add(filter);
				search.setFilters(filters);
				List<R> objects = dao.search(search);
				if(objects.size() > 0) {
					return objects.get(0);
				}
			} else {
				// else it's a classic id
				R object = (R) dao.find(id);
				if(object != null) {
					return object;
				}
			}	
		}
		
		return null;
	}

	/**
	 * Search using the given filter on the LDAP server.
	 * Each result object is mapped with the given mapper.
	 * Given base and filter are used.
	 * 
	 * @param base
	 * @param filter
	 * @param mapper
	 * @return
	 */
	public List search(String base, Filter filter, AttributesMapper mapper) {
        com.codahale.metrics.Timer.Context context = metricRegistry.timer(getClass().getName() + "_updateIdsFromDatabase").time();
        try {
            final List list = LdapUtils.search(ldapTemplate, base, filter, mapper);
            updateIdsFromDatabase(list);
            return list;
        } finally {
            context.stop();
        }
	}
	
	/**
	 * Search using the given filter on the LDAP server.
	 * Each result object is mapped with the given mapper.
	 * Given base and filter are used.
	 * 
	 * @param base
	 * @param filter
	 * @param mapper
	 * @return
	 */
	public List search(String base, String filter, AttributesMapper mapper) {
        com.codahale.metrics.Timer.Context context = metricRegistry.timer(getClass().getName() + "_updateIdsFromDatabase").time();
        try {
            final List list = LdapUtils.search(ldapTemplate, base, filter, mapper);
            updateIdsFromDatabase(list);
            return list;
        } finally {
            context.stop();
        }
	}

    protected abstract void updateIdsFromDatabase(List list);

    /**
	 * Search using the given filter on the LDAP server.
	 * Uses default base, filter and mapper.
	 * 
	 * @param base
	 * @param filter
	 * @param mapper
	 * @return
	 */
	public List search(Filter filter) {
		return search(searchBase, filter, attributesMapper);		
	}
	
	/**
	 * Search using the given filter on the LDAP server.
	 * Uses default base, filter and mapper.
	 * 
	 * @param base
	 * @param filter
	 * @param mapper
	 * @return
	 */
	public List search(String filter) {
		return search(searchBase, filter, attributesMapper);		
	}

}

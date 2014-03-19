/*
 *  Copyright (C) 2007 - 2011 GeoSolutions S.A.S.
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

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import it.geosolutions.geofence.core.dao.UserGroupDAO;
import it.geosolutions.geofence.core.model.GSUser;
import it.geosolutions.geofence.core.model.UserGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserGroupDAO implementation, using an LDAP server as a primary source, and the original
 * JPA based DAO as a backup.
 * 
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 *
 */
public class UserGroupDAOLdapImpl extends BaseDAO<UserGroupDAO,UserGroup> implements UserGroupDAO {	
	/**
	 * 
	 */
	public UserGroupDAOLdapImpl() {
		super();
		// set default search base and filter for groups
		setSearchBase("ou=Groups");
		setSearchFilter("objectClass=posixGroup");
	}

    @Override
    protected void updateIdsFromDatabase(List list) {
        Map<String, UserGroup> ids = new HashMap<String, UserGroup>();
        for (Object entity : list) {
            if (entity instanceof UserGroup) {
                UserGroup gsUser = (UserGroup) entity;

                ids.put(gsUser.getExtId(), gsUser);
            } else {
                return;
            }
        }
        final Search search = new Search();
        search.addFilter(Filter.in("extId", ids.keySet()));
        final List<UserGroup> userGroups = dao.search(search);
        for (UserGroup userGroup : userGroups) {
            ids.get(userGroup.getExtId()).setId(userGroup.getId());
        }
    }
}

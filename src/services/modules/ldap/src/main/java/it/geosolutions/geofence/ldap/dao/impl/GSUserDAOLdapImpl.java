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

import it.geosolutions.geofence.core.dao.GSUserDAO;
import it.geosolutions.geofence.core.model.GSUser;
import it.geosolutions.geofence.core.model.UserGroup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

import org.springframework.ldap.core.AttributesMapper;

import com.googlecode.genericdao.search.Filter;


/**
 * GSUserDAO implementation, using an LDAP server as a primary source, and the original
 * JPA based DAO as a backup.
 *  
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 *
 */
public class GSUserDAOLdapImpl extends BaseDAO<GSUserDAO,GSUser> implements GSUserDAO {
		
	private String groupsBase = "ou=Groups";
	
	private AttributesMapper groupsAttributesMapper;
	String userDn = "uid=%s,ou=People";
    private String groupMemberValue = "%s";

    private final String GEORCHESTRA_ADMIN_GROUP = "ADMINISTRATOR";


    /**
	 * 
	 */
	public GSUserDAOLdapImpl() {
		super();
		// set default search base and filter for users
		setSearchBase("ou=People");
		setSearchFilter("objectClass=inetOrgPerson");
	}

	/**
	 * Sets the base name for groups in LDAP server.
	 * Used to extract groups bounded to the user.
	 * 
	 * @param groupsBase the groupsBase to set
	 */
	public void setGroupsBase(String groupsBase) {
		this.groupsBase = groupsBase;
	}

	/**
	 * Sets the userDn template, to quickly locate a user into an LDAP server,
	 * by its distinguished name.
	 * It's a template, filled with the user name (use %s as a placeholder for that=.
	 *  
	 * @param userDn the userDn to set
	 */
	public void setUserDn(String userDn) {
		this.userDn = userDn;
	}
	

	/**
	 * Sets the AttributeMapper used to build UserGroup objects from LDAP
	 * objects.
	 * 
         * @param groupsAttributesMapper the groupsAttributesMapper to set
	 */
	public void setGroupsAttributesMapper(AttributesMapper groupsAttributesMapper) {
		this.groupsAttributesMapper = groupsAttributesMapper;
	}

	

	@Override
	public Set<UserGroup> getGroups(Long id) {		
		GSUser user = find(id);
		fillWithGroups(user);
		return user.getGroups();		
	}
	
	/**
	 * Gets the list of user groups from the LDAP server for the given user.
	 * 
	 * @param user
	 * @return
	 */
	private Set<UserGroup> getGroups(GSUser user) {	
		Set<UserGroup> groups = new HashSet<UserGroup>();
        Filter filter = new Filter("member", String.format(groupMemberValue, user.getName()));
		List<UserGroup> groupsList = search(groupsBase, filter, groupsAttributesMapper);				
		groups.addAll(groupsList);
		return groups;
	}


	@Override
	public GSUser getFull(Long id) {			
		GSUser user = find(id);
		if(user != null) {
			return fillWithGroups(user);
		}
		return null;
	}

	@Override
	public GSUser getFull(String name) {		
		return fillWithGroups(lookup(String.format(userDn, name)));
	}

	/**
	 * Check if the user belongs to the group ADMINISTRATOR (that tells in georchestra
	 * that the user is geoserver admin). If so, set it to admin in geofence.
	 * @param user
	 */
	private void setGeorchestraAdmin(GSUser user) {
		for (Iterator<UserGroup> it = user.getGroups().iterator(); it.hasNext(); ) {
			UserGroup group = it.next();
			if(group.getName().equals(GEORCHESTRA_ADMIN_GROUP)) {
				user.setAdmin(true);
				break;
			}
	    }
	}


	/**
	 * Updates the groups list for the given user.
	 * 
	 * @param gsUser
	 * @return
	 */
	private GSUser fillWithGroups(GSUser user) {		
		user.setGroups(getGroups(user));
		setGeorchestraAdmin(user);
		return user;
	}

    public void setGroupMemberValue(String groupMemberValue) {
        this.groupMemberValue = groupMemberValue;
    }
}

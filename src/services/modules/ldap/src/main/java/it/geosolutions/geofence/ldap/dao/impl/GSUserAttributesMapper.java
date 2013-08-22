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
 */package it.geosolutions.geofence.ldap.dao.impl;

import it.geosolutions.geofence.core.model.GSUser;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
/**
 * AttributeMapper for GSUser objects.
 * 
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 *
 */
public class GSUserAttributesMapper extends BaseAttributesMapper {

	

	@Override
	public Object mapFromAttributes(Attributes attrs) throws NamingException {
		GSUser user = new GSUser();
		user.setId(Long.parseLong(getAttribute(attrs, "id")));
		user.setExtId(-user.getId()+"");
		user.setName(getAttribute(attrs, "username"));
		user.setEmailAddress(getAttribute(attrs, "email"));
		user.setEnabled(true);
		user.setFullName(getAttribute(attrs, "name") + " "
				+ getAttribute(attrs, "surname"));
		user.setPassword(getAttribute(attrs, "password"));
		
		if (user.getName().equals("admin")) {
			user.setAdmin(true);
		}
		
		for (String attName : ldapAttributeMappings.keySet()) {
			if (attName.startsWith("metadata.")) {
				user.getMetadata().put(attName.substring(9), getAttribute(attrs, attName));
			}
		}
				
		return user;
	}
	
	

}

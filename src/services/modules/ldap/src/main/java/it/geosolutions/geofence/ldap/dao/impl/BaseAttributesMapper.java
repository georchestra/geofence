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

import it.geosolutions.geofence.dao.ldap.LdapAttributesMapper;

import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

/**
 * Base class for the LDAP attribute mappers.
 * Implements mappings from LDAP attributes to DAO ones.
 * 
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 *
 */
public abstract class BaseAttributesMapper implements LdapAttributesMapper {
	protected Map<String,String> ldapAttributeMappings;
	
	/**
	 * Sets mappings from ldap attributes to GSUser and UserGroup ones.
	 * 
	 * @param ldapAttributeMappings the ldapAttributeMappings to set
	 */
	public void setLdapAttributeMappings(Map<String, String> ldapAttributeMappings) {
		this.ldapAttributeMappings = ldapAttributeMappings;
	}
	
	/**
	 * Gets the LDAP mapping for the given DAO attribute.
	 * 
	 * @param attributeName
	 * @return
	 */
	public String getLdapAttribute(String attributeName) {
		return ldapAttributeMappings.get(attributeName);
	}
	
	/**
	 * Gets an attribute value from the given list.
	 * Automatically maps the attributeName (in DAO form) to LDAP form.
	 * 
	 * @param attrs
	 * @return
	 * @throws NamingException
	 */
	protected String getAttribute(Attributes attrs, 
			String attributeName)
			throws javax.naming.NamingException {
		Attribute attrValue = attrs.get(ldapAttributeMappings.get(attributeName));
		if(attrValue != null) {			
			Object value = attrValue.get();
			if (value instanceof String) {
				return (String) value;
			} if (value instanceof byte[]) {
				return new String((byte[]) value);
			} else {
				return value.toString();
			}
		}
		return "";
	}
}

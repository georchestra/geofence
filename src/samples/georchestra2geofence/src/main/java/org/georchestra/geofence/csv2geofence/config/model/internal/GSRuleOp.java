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
package org.georchestra.geofence.csv2geofence.config.model.internal;

import java.util.List;

/**
 *
 * @author fgravin
 */
public class GSRuleOp {

    String workspace;
    String layerName;
    String verb;
    List<String> groups;
	/**
	 * @return the workspace
	 */
	public String getWorkspace() {
		return workspace;
	}
	/**
	 * @param workspace the workspace to set
	 */
	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}
	/**
	 * @return the layerName
	 */
	public String getLayerName() {
		return layerName;
	}
	/**
	 * @param layerName the layerName to set
	 */
	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}
	/**
	 * @return the verb
	 */
	public String getVerb() {
		return verb;
	}
	/**
	 * @param verb the verb to set
	 */
	public void setVerb(String verb) {
		this.verb = verb;
	}
	/**
	 * @return the groups
	 */
	public List<String> getGroups() {
		return groups;
	}
	/**
	 * @param groups the groups to set
	 */
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
    
    
}

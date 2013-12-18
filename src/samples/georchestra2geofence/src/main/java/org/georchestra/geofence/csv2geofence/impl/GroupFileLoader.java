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
package org.georchestra.geofence.csv2geofence.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.georchestra.geofence.csv2geofence.config.model.internal.GroupOp;


/**
 * Loads a rule file, converting lines into GSRuleOps.
 *
 * @author fgravin
 */
public class GroupFileLoader {

    private final static Logger LOGGER = LogManager.getLogger(GSRuleFileLoader.class);

    public GroupFileLoader() {
    }

    public List<GroupOp> load(File file, Properties prop) throws FileNotFoundException, IOException {

        List<GroupOp> ret = new ArrayList<GroupOp>();

        String line = "";
        int lineNumber = 0;
        
        BufferedReader br = new BufferedReader(new FileReader(file));
        while((line = br.readLine()) != null) {
        	GroupOp group = loadLine(line, lineNumber, prop);
            if(group != null) {
                ret.add(group);
            }
            lineNumber++;
        }
        return ret;
    }
    
    private GroupOp loadLine(final String line, int lineNumber, Properties prop) {
    
    	String regexp = prop.getProperty("ldapGroupPattern");
    	GroupOp groupOp = new GroupOp();
    	Pattern pattern = Pattern.compile(regexp);
    	Matcher matcher = pattern.matcher(line);
    	
    	if(matcher.matches()) {
    		groupOp.setName(matcher.group(1));
    		return groupOp;
    	}
    	else {
    		LOGGER.debug("No match found for goups, line #" + lineNumber);
    		return null;
    	}
    }
}

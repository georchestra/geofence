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
import java.util.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.georchestra.geofence.csv2geofence.config.model.internal.GSRuleOp;


/**
 * Loads a rule file, converting lines into GSRuleOps.
 *
 * @author fgravin
 */
public class GSRuleFileLoader {

    private final static Logger LOGGER = LogManager.getLogger(GSRuleFileLoader.class);

    public GSRuleFileLoader() {
    }

    public List<GSRuleOp> load(File file) throws FileNotFoundException, IOException {

        List<GSRuleOp> ret = new ArrayList<GSRuleOp>();

        String line = "";
        int lineNumber = 0;
        
        BufferedReader br = new BufferedReader(new FileReader(file));
        while((line = br.readLine()) != null) {
            GSRuleOp rule = loadLine(line, lineNumber);
            if(rule != null) {
                ret.add(rule);
            }
            
            lineNumber++;
        }
        return ret;
    }
    
    private GSRuleOp loadLine(final String line, int lineNumber) {
    
        GSRuleOp ruleOp = new GSRuleOp();
        
        // Avoid command lines
        if(line.startsWith("#")) {
            return null;
        }
        
        // Avoid Geoserver catalog security definition "mode=hidden"
        if(line.startsWith("mode=")) {
            return null;
        }
        
        StringTokenizer eqToken = new StringTokenizer(line, "=");
        if(eqToken.countTokens() != 2) {
            LOGGER.error("Rule format error, should be target=group1,group2 ...");
            LOGGER.error("line "+ lineNumber + " : " + line);
        }
        String target = eqToken.nextToken();
        String groups = eqToken.nextToken();
        
        StringTokenizer targets = new StringTokenizer(target, ".");
        if(targets.countTokens() != 3) {
            LOGGER.error("Rule target format error, should be workspace.layer.rights");
            LOGGER.error("line "+ lineNumber + " : " + line);
        }
        ruleOp.setWorkspace(targets.nextToken());
        ruleOp.setLayerName(targets.nextToken());
        ruleOp.setVerb(targets.nextToken());
        
        ruleOp.setGroups(loadGroups(groups));
        
        return ruleOp;
    }
    
    private List<String> loadGroups(final String groups) {
        List<String> ret = new ArrayList<String>();
        StringTokenizer tGroups = new StringTokenizer(groups, ",");
        while (tGroups.hasMoreTokens()) {
            ret.add(tGroups.nextToken());
        }
        return ret;
    }

}

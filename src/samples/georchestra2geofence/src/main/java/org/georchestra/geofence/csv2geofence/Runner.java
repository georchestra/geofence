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
package org.georchestra.geofence.csv2geofence;

import it.geosolutions.geofence.services.rest.GeoFenceClient;
import it.geosolutions.geofence.services.rest.model.RESTBatch;
import it.geosolutions.geofence.services.rest.model.RESTBatchOperation;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.client.ServerWebApplicationException;
import org.georchestra.geofence.csv2geofence.config.model.internal.GSRuleOp;
import org.georchestra.geofence.csv2geofence.config.model.internal.GroupOp;
import org.georchestra.geofence.csv2geofence.config.model.internal.RunInfo;
import org.georchestra.geofence.csv2geofence.impl.GSRuleFileLoader;
import org.georchestra.geofence.csv2geofence.impl.GSRulesProcessor;
import org.georchestra.geofence.csv2geofence.impl.GroupFileLoader;
import org.georchestra.geofence.csv2geofence.impl.GroupsProcessor;


/**
 * Main logic.
 *
 * Invoked by the main() method.
 *
 * @author fgravin
 */
public class Runner {
    protected static final Log LOGGER = LogFactory.getLog(Runner.class.getPackage().getName());

    private RunInfo runInfo;

    public Runner(RunInfo runInfo) {
        this.runInfo = runInfo;
    }

    public void run() throws IOException {

        RESTBatch batch = new RESTBatch();
        
        LOGGER.info("Load properties files...");
        Properties prop = new Properties();
        prop.load(new FileInputStream(runInfo.getConfigurationFile()));
        
        // Process the rule files
        LOGGER.info("Scanning rule files...");
        for (File file : runInfo.getRuleFiles()) {
            LOGGER.info("Processing rule file '" + file+"'");
            List<RESTBatchOperation> batchOps = processRuleFile(file);
            batch.getList().addAll(batchOps);
        }
        
        // Process group files
        LOGGER.info("Scanning group files...");
        for (File file : runInfo.getGroupFiles()) {
            LOGGER.info("Processing group file '" + file+"'");
            List<RESTBatchOperation> batchOps = processGroupFile(file, prop);
            batch.getList().addAll(batchOps);
        }
        
        GeoFenceClient geoFenceClient = new GeoFenceClient();
        geoFenceClient.setGeostoreRestUrl(prop.getProperty("geofenceRestUrl"));
        geoFenceClient.setUsername(prop.getProperty("geofenceLogin"));
        geoFenceClient.setPassword(prop.getProperty("geofencePassword"));
        
        if( runInfo.getOutputFile() != null) {
            Writer xmlWriter = null;
            xmlWriter = new FileWriter(runInfo.getOutputFile());
            LOGGER.info("Creating XML command file " + runInfo.getOutputFile());
            JAXB.marshal(batch, xmlWriter);
            xmlWriter.flush();
            xmlWriter.close();
            LOGGER.info("XML command file saved.");
        }
        
        if(runInfo.isSendRequested()) {
            LOGGER.info("Sending "+batch.getList().size()+" commands to GeoFence...");
            try {
                geoFenceClient.getBatchService().exec(batch);
                LOGGER.info("GeoFence data updated");
            } catch (ServerWebApplicationException ex) {
                LOGGER.error("GeoFence error (HTTP:"+ex.getStatus()+"): " + ex.getMessage() , ex);
                LOGGER.error("GeoFence data have not been updated");
            }
        }

    }

    private static List<RESTBatchOperation> processRuleFile(File ruleFile) throws IOException  {
        // load and parse file

        List<GSRuleOp> ruleOps;
        try {
        	GSRuleFileLoader loader = new GSRuleFileLoader();
            ruleOps = loader.load(ruleFile);
            GSRulesProcessor processor = new GSRulesProcessor();
            return processor.buildBatchOps(ruleOps);

        } catch (IOException e) {
            LOGGER.warn("Error loading file '"+ruleFile+"': " + e.getMessage(), e);
            throw e;
        }
    }
    
    private static List<RESTBatchOperation> processGroupFile(File ruleFile, Properties prop) throws IOException  {
        // load and parse file

        List<GroupOp> groupOps;
        try {
        	GroupFileLoader loader = new GroupFileLoader();
            groupOps = loader.load(ruleFile, prop);
            GroupsProcessor processor = new GroupsProcessor();
            return processor.buildBatchOps(groupOps);
        } catch (IOException e) {
            LOGGER.warn("Error loading file '"+ruleFile+"': " + e.getMessage(), e);
            throw e;
        }
    }
}

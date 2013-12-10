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

import it.geosolutions.geofence.services.rest.model.RESTBatchOperation;
import it.geosolutions.geofence.services.rest.model.RESTInputGroup;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.georchestra.geofence.csv2geofence.config.model.internal.GroupOp;


/**
 * Transforms GsRuleOps into RESTBatchoperations
 *
 * @author fgravin
 */
public class GroupsProcessor {

    protected static final Log LOGGER = LogFactory.getLog(GroupsProcessor.class.getPackage().getName());

    /**
     * @param ops
     * @param availableGroups may be augmented
     * @param ruleMapping
     * @return
     */
    public List<RESTBatchOperation> buildBatchOps(List<GroupOp> ops) {
        List<RESTBatchOperation> ret = new ArrayList<RESTBatchOperation>(ops.size());

        for (GroupOp op : ops) {
            LOGGER.debug("Preparing for output " + op);
            List<RESTBatchOperation> restOps = buildBatchOperation(op);
            ret.addAll(restOps);
        }

        return ret;
    }

    /**
     * @param ruleOp
     * @param availableGroups may be augmented
     * @param ruleMapping
     * @return
     */
    protected List<RESTBatchOperation> buildBatchOperation(GroupOp groupOp) {

        List<RESTBatchOperation> ret = new ArrayList<RESTBatchOperation>();

        RESTInputGroup group = new RESTInputGroup();
        group.setName(groupOp.getName());
        group.setEnabled(true);
        
        RESTBatchOperation restOp = new RESTBatchOperation();
        restOp.setService(RESTBatchOperation.ServiceName.groups);
        restOp.setType(RESTBatchOperation.TypeName.insert);
        restOp.setPayload(group);
        ret.add(restOp);

        return ret;
    }
}

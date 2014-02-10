/*
 * $ Header: it.geosolutions.geofence.gui.client.service.RulesManagerRemoteService,v. 0.1 9-feb-2011 13.01.52 created by afabiani <alessio.fabiani at geo-solutions.it> $
 * $ Revision: 0.1 $
 * $ Date: 9-feb-2011 13.01.52 $
 *
 * ====================================================================
 *
 * Copyright (C) 2007 - 2011 GeoSolutions S.A.S.
 * http://www.geo-solutions.it
 *
 * GPLv3 + Classpath exception
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 *
 * ====================================================================
 *
 * This software consists of voluntary contributions made by developers
 * of GeoSolutions.  For more information on GeoSolutions, please see
 * <http://www.geo-solutions.it/>.
 *
 */
package it.geosolutions.geofence.gui.client.service;

import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import it.geosolutions.geofence.gui.client.ApplicationException;
import it.geosolutions.geofence.gui.client.model.Rule;
import it.geosolutions.geofence.gui.client.model.data.LayerAttribUI;
import it.geosolutions.geofence.gui.client.model.data.LayerCustomProps;
import it.geosolutions.geofence.gui.client.model.data.LayerDetailsInfo;
import it.geosolutions.geofence.gui.client.model.data.LayerLimitsInfo;
import it.geosolutions.geofence.gui.client.model.data.LayerStyle;


/**
 * The Interface RulesManagerRemoteService.
 */
@RemoteServiceRelativePath("RulesManagerRemoteService")
public interface RulesManagerRemoteService extends RemoteService
{

    /**
     * Gets the rules.
     *
     * @param config
     *            the config
     * @param full
     *            the full
     * @return the rules
     * @throws ApplicationException
     *             the application exception
     */
    public PagingLoadResult<Rule> getRules(int offset, int limit, boolean full, String groupFilter,
            String wsFilter, String layerFilter) throws ApplicationException;
    
    /**
     * Save rule
     *
     * @param rule
     *            the rule
     * @param callback
     *            the callback
     */
    public void saveRule(Rule rules) throws ApplicationException;

    /**
     * Delete rule
     *
     * @param rule
     *            the rule
     * @param callback
     *            the callback
     */
    public void deleteRule(Rule rules) throws ApplicationException;

    /**
     * Find rule
     *
     * @param rule
     *            the rule
     * @param callback
     *            the callback
     * @return
     * @throws Exception
     * @throws ResourceNotFoundFault
     */
    public void findRule(Rule rules) throws ApplicationException, Exception;

    /**
     * Shifts the priority of the rules having <TT>priority &gt;= priorityStart</TT>
     * down by <TT>offset</TT>.
     *
     * @return the number of rules updated.
     */
    public void shift(long priorityStart, long offset) throws ApplicationException;

    /**
     * Swaps the priorities of two rules.
     */
    public void swap(long id1, long id2) throws ApplicationException;

    /**
     * Save all rules.
     *
     * @param rules
     *            the rules
     * @throws ApplicationException
     *             the application exception
     */
    public void saveAllRules(List<Rule> rules) throws ApplicationException;

    /**
     * Gets the layer custom props.
     *
     * @param config
     *            the config
     * @param rule
     *            the rule
     * @return the layer custom props
     * @throws ApplicationException
     *             the application exception
     */
//    public PagingLoadResult<LayerCustomProps> getLayerCustomProps(int offset, int limit, Rule rule)
//        throws ApplicationException;

    /**
     * Sets the details props.
     *
     * @param ruleId
     *            the rule id
     * @param customProps
     *            the custom props
     * @throws ApplicationException
     *             the application exception
     */
    public void setDetailsProps(Long ruleId, List<LayerCustomProps> customProps) throws ApplicationException;

    /**
     * Gets the layer attributes.
     *
     * @param rule
     *            the rule
     * @return the layer attributes
     * @throws ApplicationException
     *             the application exception
     */
    public List<LayerAttribUI> getLayerAttributes(Rule rule) throws ApplicationException;

    /**
     * @param ruleId
     * @param layerAttributes
     * @throws ApplicationException
     */
    public void setLayerAttributes(Long ruleId, List<LayerAttribUI> layerAttributes) throws ApplicationException;

    /**
     * @param layerDetailsForm
     * @return LayerDetailsInfo
     * @throws ApplicationException
     */
    public LayerDetailsInfo saveLayerDetailsInfo(LayerDetailsInfo layerDetailsForm, List<LayerStyle> layerStyles)
        throws ApplicationException;

    /**
     * @param rule
     * @return LayerDetailsForm
     * @throws ApplicationException
     */
    public LayerDetailsInfo getLayerDetailsInfo(Rule rule) throws ApplicationException;

    /**
     * @param layerLimitsForm
     * @return LayerLimitsInfo
     * @throws ApplicationException
     */
    public LayerLimitsInfo saveLayerLimitsInfo(LayerLimitsInfo layerLimitsForm) throws ApplicationException;

    /**
     * @param rule
     * @return LayerLimitsInfo
     * @throws ApplicationException
     */
    public LayerLimitsInfo getLayerLimitsInfo(Rule rule) throws ApplicationException;

}

/*
 * $ Header: it.geosolutions.geofence.gui.server.service.IRulesManagerService,v. 0.1 9-feb-2011 13.02.31 created by afabiani <alessio.fabiani at geo-solutions.it> $
 * $ Revision: 0.1 $
 * $ Date: 9-feb-2011 13.02.31 $
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
package it.geosolutions.geofence.gui.server.service;

import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadResult;

import it.geosolutions.geofence.gui.client.ApplicationException;
import it.geosolutions.geofence.gui.client.model.Rule;
import it.geosolutions.geofence.gui.client.model.data.LayerAttribUI;
import it.geosolutions.geofence.gui.client.model.data.LayerCustomProps;
import it.geosolutions.geofence.gui.client.model.data.LayerDetailsInfo;
import it.geosolutions.geofence.gui.client.model.data.LayerLimitsInfo;
import it.geosolutions.geofence.gui.client.model.data.LayerStyle;


// TODO: Auto-generated Javadoc
/**
 * The Interface IRulesManagerService.
 */
public interface IRulesManagerService
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
    public PagingLoadResult<Rule> getRules(int offset, int limit, boolean full) throws ApplicationException;

    public void setGroupFilter(String filter);
    public void setWsFilter(String filter);
    public void setLayerFilter(String filter);

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
     */
//    public PagingLoadResult<LayerCustomProps> getLayerCustomProps(int offset, int limit, Rule rule);

    /**
     * Sets the details props.
     *
     * @param ruleId
     *            the rule id
     * @param customProps
     *            the custom props
     */
    public void setDetailsProps(Long ruleId, List<LayerCustomProps> customProps);


    /**
     * Gets the layer attributes.
     *
     * @param rule
     *            the rule
     * @return the layer attributes
     */
    public List<LayerAttribUI> getLayerAttributes(Rule rule);

    /**
     * @param ruleId
     * @param layerAttributes
     */
    public void setLayerAttributes(Long ruleId, List<LayerAttribUI> layerAttributes);

    /**
     * @param layerDetailsForm
     * @return LayerDetailsForm
     */
    public LayerDetailsInfo saveLayerDetailsInfo(LayerDetailsInfo layerDetailsForm, List<LayerStyle> layerStyles);

    /**
     * @param rule
     * @return LayerDetailsForm
     */
    public LayerDetailsInfo getLayerDetailsInfo(Rule rule);

    /**
     * Save single rule
     *
     * @param rule
     */
    public void saveRule(Rule rule) throws ApplicationException;

    /**
     * Save single rule
     *
     * @param rule
     * @return
     * @throws ResourceNotFoundFault
     */
    public void findRule(Rule rule) throws ApplicationException, Exception;

    /**
     * Delete single rule
     *
     * @param rule
     */
    public void deleteRule(Rule rule) throws ApplicationException;

    /**
     * Shifts the priority of the rules having <TT>priority &gt;= priorityStart</TT>
     * down by <TT>offset</TT>.
     *
     * @return the number of rules updated.
     */
    public void shift(long priorityStart, long offset);

    /**
     * Swaps the priorities of two rules.
     */
    public void swap(long id1, long id2);

    /**
     * @param layerLimitsForm
     * @return LayerLimitsInfo
     */
    public LayerLimitsInfo saveLayerLimitsInfo(LayerLimitsInfo layerLimitsForm);

    /**
     * @param rule
     * @return LayerLimitsInfo
     */
    public LayerLimitsInfo getLayerLimitsInfo(Rule rule);

}

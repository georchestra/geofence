/*
 * $ Header: it.geosolutions.geofence.gui.server.gwt.RulesManagerServiceImpl,v. 0.1 9-feb-2011 13.02.25 created by afabiani <alessio.fabiani at geo-solutions.it> $
 * $ Revision: 0.1 $
 * $ Date: 9-feb-2011 13.02.25 $
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
package it.geosolutions.geofence.gui.server.gwt;

import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import it.geosolutions.geofence.gui.client.ApplicationException;
import it.geosolutions.geofence.gui.client.model.Rule;
import it.geosolutions.geofence.gui.client.model.data.LayerAttribUI;
import it.geosolutions.geofence.gui.client.model.data.LayerCustomProps;
import it.geosolutions.geofence.gui.client.model.data.LayerDetailsInfo;
import it.geosolutions.geofence.gui.client.model.data.LayerLimitsInfo;
import it.geosolutions.geofence.gui.client.model.data.LayerStyle;
import it.geosolutions.geofence.gui.client.service.RulesManagerRemoteService;
import it.geosolutions.geofence.gui.server.service.IRulesManagerService;
import it.geosolutions.geofence.gui.spring.ApplicationContextUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: Auto-generated Javadoc
/**
 * The Class RulesManagerServiceImpl.
 */
public class RulesManagerServiceImpl extends RemoteServiceServlet implements RulesManagerRemoteService
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5342510982782032063L;

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** The rules manager service. */
    private IRulesManagerService rulesManagerService;
    
    /**
     * Instantiates a new rules manager service impl.
     */
    public RulesManagerServiceImpl()
    {
        this.rulesManagerService = (IRulesManagerService) ApplicationContextUtil.getInstance().getBean("rulesManagerServiceGWT");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * it.geosolutions.geofence.gui.client.service.ProfilesManagerRemoteService#getRules(com.extjs
     * .gxt.ui.client.data.PagingLoadConfig)
     */
    public PagingLoadResult<Rule> getRules(int offset, int limit, boolean full, String groupFilter,
            String wsFilter, String layerFilter) throws ApplicationException
    {
        PagingLoadResult<Rule> ret = rulesManagerService.getRules(offset, limit, full, groupFilter, wsFilter, layerFilter);

        return ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * it.geosolutions.geofence.gui.client.service.RulesManagerRemoteService#saveAllRules(java.util
     * .List)
     */
    public void saveRule(Rule rule) throws ApplicationException
    {
        rulesManagerService.saveRule(rule);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * it.geosolutions.geofence.gui.client.service.RulesManagerRemoteService#saveAllRules(java.util
     * .List)
     */
    public void deleteRule(Rule rule) throws ApplicationException
    {
        rulesManagerService.deleteRule(rule);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * it.geosolutions.geofence.gui.client.service.RulesManagerRemoteService#saveAllRules(java.util
     * .List)
     */
    public void saveAllRules(List<Rule> rules) throws ApplicationException
    {
        rulesManagerService.saveAllRules(rules);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * it.geosolutions.geofence.gui.client.service.RulesManagerRemoteService#getLayerCustomProps(
     * com.extjs.gxt.ui.client.data.PagingLoadConfig, it.geosolutions.geofence.gui.client.model.Rule)
     */
//    public PagingLoadResult<LayerCustomProps> getLayerCustomProps(int offset, int limit, Rule rule)
//        throws ApplicationException
//    {
//        return rulesManagerService.getLayerCustomProps(offset, limit, rule);
//    }

    /*
     * (non-Javadoc)
     *
     * @see
     * it.geosolutions.geofence.gui.client.service.RulesManagerRemoteService#setDetailsProps(java
     * .lang.Long, it.geosolutions.geofence.gui.client.model.data.LayerCustomProps)
     */
    public void setDetailsProps(Long ruleId, List<LayerCustomProps> customProps) throws ApplicationException
    {
        rulesManagerService.setDetailsProps(ruleId, customProps);
    }

    public void shift(long priorityStart, long offset) throws ApplicationException
    {
        rulesManagerService.shift(priorityStart, offset);
    }

    public void swap(long id1, long id2) throws ApplicationException
    {
        rulesManagerService.swap(id1, id2);
    }

    public void findRule(Rule rule) throws ApplicationException, Exception
    {
        rulesManagerService.findRule(rule);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * it.geosolutions.geofence.gui.client.service.RulesManagerRemoteService#setLayerAttributes(java
     * .lang.Long, java.util.List)
     */
    public void setLayerAttributes(Long ruleId, List<LayerAttribUI> layerAttributes) throws ApplicationException
    {
        rulesManagerService.setLayerAttributes(ruleId, layerAttributes);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * it.geosolutions.geofence.gui.client.service.RulesManagerRemoteService#saveLayerDetails(it.
     * geosolutions.geofence.gui.client.model.data.LayerDetailsForm)
     */
    public LayerDetailsInfo saveLayerDetailsInfo(LayerDetailsInfo layerDetailsForm, List<LayerStyle> layerStyles)
        throws ApplicationException
    {
        return rulesManagerService.saveLayerDetailsInfo(layerDetailsForm, layerStyles);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * it.geosolutions.geofence.gui.client.service.RulesManagerRemoteService#getLayerDetailsInfo(
     * it.geosolutions.geofence.gui.client.model.Rule)
     */
    public LayerDetailsInfo getLayerDetailsInfo(Rule rule) throws ApplicationException
    {
        return rulesManagerService.getLayerDetailsInfo(rule);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * it.geosolutions.geofence.gui.client.service.RulesManagerRemoteService#getLayerAttributes(com
     * .extjs.gxt.ui.client.data.PagingLoadConfig, it.geosolutions.geofence.gui.client.model.Rule)
     */
    public List<LayerAttribUI> getLayerAttributes(Rule rule) throws ApplicationException
    {
        return rulesManagerService.getLayerAttributes(rule);
    }

    /* (non-Javadoc)
     * @see it.geosolutions.geofence.gui.client.service.RulesManagerRemoteService#saveLayerLimitsInfo(it.geosolutions.geofence.gui.client.model.data.LayerLimitsInfo)
     */
    public LayerLimitsInfo saveLayerLimitsInfo(LayerLimitsInfo layerLimitsForm) throws ApplicationException
    {
        return rulesManagerService.saveLayerLimitsInfo(layerLimitsForm);
    }

    /* (non-Javadoc)
     * @see it.geosolutions.geofence.gui.client.service.RulesManagerRemoteService#getLayerLimitsInfo(it.geosolutions.geofence.gui.client.model.Rule)
     */
    public LayerLimitsInfo getLayerLimitsInfo(Rule rule) throws ApplicationException
    {
        return rulesManagerService.getLayerLimitsInfo(rule);
    }
}

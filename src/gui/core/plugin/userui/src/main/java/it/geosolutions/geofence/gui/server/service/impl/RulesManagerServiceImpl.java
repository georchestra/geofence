/*
 * $ Header: it.geosolutions.geofence.gui.server.service.impl.RulesManagerServiceImpl,v. 0.1 9-feb-2011 13.03.26 created by afabiani <alessio.fabiani at geo-solutions.it> $
 * $ Revision: 0.1 $
 * $ Date: 9-feb-2011 13.03.26 $
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
package it.geosolutions.geofence.gui.server.service.impl;

import it.geosolutions.geofence.core.model.LayerAttribute;
import it.geosolutions.geofence.core.model.LayerDetails;
import it.geosolutions.geofence.core.model.RuleLimits;
import it.geosolutions.geofence.core.model.enums.AccessType;
import it.geosolutions.geofence.core.model.enums.GrantType;
import it.geosolutions.geofence.core.model.enums.LayerType;
import it.geosolutions.geofence.gui.client.ApplicationException;
import it.geosolutions.geofence.gui.client.model.GSInstance;
import it.geosolutions.geofence.gui.client.model.GSUser;
import it.geosolutions.geofence.gui.client.model.Rule;
import it.geosolutions.geofence.gui.client.model.UserGroup;
import it.geosolutions.geofence.gui.client.model.data.LayerAttribUI;
import it.geosolutions.geofence.gui.client.model.data.LayerCustomProps;
import it.geosolutions.geofence.gui.client.model.data.LayerDetailsInfo;
import it.geosolutions.geofence.gui.client.model.data.LayerLimitsInfo;
import it.geosolutions.geofence.gui.client.model.data.LayerStyle;
import it.geosolutions.geofence.gui.client.model.data.rpc.RpcPageLoadResult;
import it.geosolutions.geofence.gui.server.service.IRulesManagerService;
import it.geosolutions.geofence.gui.service.GeofenceRemoteService;
import it.geosolutions.geofence.services.dto.RuleFilter;
import it.geosolutions.geofence.services.dto.RuleFilter.SpecialFilterType;
import it.geosolutions.geofence.services.dto.ShortRule;
import it.geosolutions.geofence.services.exception.NotFoundServiceEx;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTFeatureType;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.util.Collections;
import org.springframework.dao.DuplicateKeyException;

/**
 * The Class RulesManagerServiceImpl.
 */
@Component("rulesManagerServiceGWT")
public class RulesManagerServiceImpl implements IRulesManagerService {

	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/** The geofence remote service. */
	@Autowired
	private GeofenceRemoteService geofenceRemoteService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.geosolutions.geofence.gui.server.service.IFeatureService#loadFeature
	 * (com.extjs.gxt.ui. client.data.PagingLoadConfig, java.lang.String)
	 */
	public PagingLoadResult<Rule> getRules(int offset, int limit, boolean full)
			throws ApplicationException {
		int start = offset;

		List<Rule> ruleListDTO = new ArrayList<Rule>();

		long rulesCount = geofenceRemoteService.getRuleAdminService()
				.getCountAll();

		Long t = new Long(rulesCount);

		int page = (start == 0) ? start : (start / limit);

		RuleFilter any = new RuleFilter(SpecialFilterType.ANY);
		List<ShortRule> rulesList = geofenceRemoteService.getRuleAdminService()
				.getList(any, page, limit);

		if (rulesList == null) {
			if (logger.isErrorEnabled()) {
				logger.error("No rule found on server");
			}
			throw new ApplicationException("No rule found on server");
		}

		Iterator<ShortRule> it = rulesList.iterator();

		while (it.hasNext()) {
			ShortRule short_rule = it.next();

			it.geosolutions.geofence.core.model.Rule remote_rule;
			try {
				remote_rule = geofenceRemoteService.getRuleAdminService().get(
						short_rule.getId());
			} catch (NotFoundServiceEx e) {
				if (logger.isErrorEnabled()) {
					logger.error("Details for rule " + short_rule.getPriority()
							+ " not found on Server!");
				}
				throw new ApplicationException("Details for profile "
						+ short_rule.getPriority() + " not found on Server!");
			}

			Rule local_rule = new Rule();

			local_rule.setId(short_rule.getId());
			local_rule.setPriority(remote_rule.getPriority());

			if (remote_rule.getGsuser() == null) {
				GSUser all = new GSUser();
				all.setId(-1);
				all.setName("*");
				local_rule.setUser(all);
			} else {
				it.geosolutions.geofence.core.model.GSUser remote_user = remote_rule
						.getGsuser();
				GSUser local_user = new GSUser();
				local_user.setId(remote_user.getId());
				local_user.setName(remote_user.getName());
				local_rule.setUser(local_user);
			}

			if (remote_rule.getUserGroup() == null) {
				UserGroup all = new UserGroup();
				all.setId(-1);
				all.setName("*");
				local_rule.setProfile(all);
			} else {
				it.geosolutions.geofence.core.model.UserGroup remote_profile = remote_rule
						.getUserGroup();
				UserGroup local_profile = new UserGroup();
				local_profile.setId(remote_profile.getId());
				local_profile.setName(remote_profile.getName());
				local_rule.setProfile(local_profile);
			}

			if (remote_rule.getInstance() == null) {
				GSInstance all = new GSInstance();
				all.setId(-1);
				all.setName("*");
				all.setBaseURL("*");
			} else {
				it.geosolutions.geofence.core.model.GSInstance remote_instance = remote_rule
						.getInstance();
				GSInstance local_instance = new GSInstance();
				local_instance.setId(remote_instance.getId());
				local_instance.setName(remote_instance.getName());
				local_instance.setBaseURL(remote_instance.getBaseURL());
				local_instance.setUsername(remote_instance.getUsername());
				local_instance.setPassword(remote_instance.getPassword());
				local_rule.setInstance(local_instance);
			}

			local_rule
					.setService((remote_rule.getService() != null) ? remote_rule
							.getService() : "*");
			local_rule
					.setRequest((remote_rule.getRequest() != null) ? remote_rule
							.getRequest() : "*");
			local_rule
					.setWorkspace((remote_rule.getWorkspace() != null) ? remote_rule
							.getWorkspace() : "*");
			local_rule.setLayer((remote_rule.getLayer() != null) ? remote_rule
					.getLayer() : "*");
			local_rule.setGrant((remote_rule.getAccess() != null) ? remote_rule
					.getAccess().toString() : "ALLOW");

			ruleListDTO.add(local_rule);
		}

		return new RpcPageLoadResult<Rule>(ruleListDTO, offset, t.intValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.geosolutions.geofence.gui.server.service.IRulesManagerService#saveAllRules
	 * (java.util.List)
	 */
	public void saveRule(Rule rule) throws ApplicationException {

        if(logger.isDebugEnabled())
            logger.debug("Trying to save rule " + rule);

        it.geosolutions.geofence.core.model.Rule modelRule = new it.geosolutions.geofence.core.model.Rule(
                rule.getPriority(), getUser(rule.getUser()),
                getProfile(rule.getProfile()),
                getInstance(rule.getInstance()),
                "*".equals(rule.getService()) ? null : rule.getService(),
                "*".equals(rule.getRequest()) ? null : rule.getRequest(),
                "*".equals(rule.getWorkspace()) ? null : rule.getWorkspace(),
                "*".equals(rule.getLayer()) ? null : rule.getLayer(),
                getAccessType(rule.getGrant()));

        try {
            if (rule.getId() == -1) { // REQUEST FOR INSERT
                modelRule.setId(null);
                    geofenceRemoteService.getRuleAdminService().insert(modelRule);
            } else {                  // REQUEST FOR UPDATE
                long ruleId = rule.getId();
                modelRule.setId(ruleId);
                try {
                    geofenceRemoteService.getRuleAdminService().update(modelRule);
                    it.geosolutions.geofence.core.model.Rule loaded = geofenceRemoteService.getRuleAdminService().get(ruleId);
                    if (! loaded.getAccess().name().equalsIgnoreCase(rule.getGrant())) {
                        geofenceRemoteService.getRuleAdminService().setDetails(ruleId, null);
                        geofenceRemoteService.getRuleAdminService().setLimits(ruleId, null);
                    }
                } catch (NotFoundServiceEx e) {
                    logger.error("Error updating a rule", e);
                    throw new ApplicationException("Error updating a rule");
                }
            }
        } catch (DuplicateKeyException e) {
			String message = "A similar rule is already present";
			logger.error(message);
			throw new ApplicationException(message);
        }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.geosolutions.geofence.gui.server.service.IRulesManagerService#saveAllRules
	 * (java.util.List)
	 */
	public void deleteRule(Rule rule) throws ApplicationException {

		if (rule.getId() != -1) {
			try {
				geofenceRemoteService.getRuleAdminService()
						.delete(rule.getId());
			} catch (NotFoundServiceEx e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/*
     *
     */
	public void updatePriorities(Rule rule, long shift) {
		geofenceRemoteService.getRuleAdminService()
				.shift(rule.getPriority(), 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.geosolutions.geofence.gui.server.service.IRulesManagerService#saveAllRules
	 * (java.util.List)
	 */
	public void saveAllRules(List<Rule> rules) throws ApplicationException {
		for (ShortRule rule : geofenceRemoteService.getRuleAdminService()
				.getAll()) {
			try {
				geofenceRemoteService.getRuleAdminService()
						.delete(rule.getId());
			} catch (NotFoundServiceEx e) {
				logger.error(e.getMessage(), e);
				throw new ApplicationException(e.getMessage(), e);
			}
		}

		for (Rule localRule : rules) {
			it.geosolutions.geofence.core.model.Rule rule = new it.geosolutions.geofence.core.model.Rule(
					localRule.getPriority(), getUser(localRule.getUser()),
					getProfile(localRule.getProfile()),
					getInstance(localRule.getInstance()), "*".equals(localRule
							.getService()) ? null : localRule.getService(),
					"*".equals(localRule.getRequest()) ? null : localRule
							.getRequest(),
					"*".equals(localRule.getWorkspace()) ? null : localRule
							.getWorkspace(),
					"*".equals(localRule.getLayer()) ? null : localRule
							.getLayer(), getAccessType(localRule.getGrant()));
			geofenceRemoteService.getRuleAdminService().insert(rule);
		}
	}

	/**
	 * Gets the access type.
	 * 
	 * @param grant
	 *            the grant
	 * @return the access type
	 */
	private GrantType getAccessType(String grant) {
		if (grant != null) {
			return GrantType.valueOf(grant);
		} else {
			return GrantType.ALLOW;
		}
	}

	/**
	 * Gets the single instance of RulesManagerServiceImpl.
	 * 
	 * @param instance
	 *            the instance
	 * @return single instance of RulesManagerServiceImpl
	 */
	private it.geosolutions.geofence.core.model.GSInstance getInstance(
			GSInstance instance) {
		it.geosolutions.geofence.core.model.GSInstance remote_instance = null;
		try {
			if ((instance != null) && (instance.getId() != -1)) {
				remote_instance = geofenceRemoteService
						.getInstanceAdminService().get(instance.getId());
			}
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}

		return remote_instance;
	}

	/**
	 * Gets the profile.
	 * 
	 * @param profile
	 *            the profile
	 * @return the profile
	 */
	private it.geosolutions.geofence.core.model.UserGroup getProfile(
			UserGroup profile) {
		it.geosolutions.geofence.core.model.UserGroup remote_profile = null;
		try {
			if ((profile != null) && (profile.getId() != -1)) {
				remote_profile = geofenceRemoteService
						.getUserGroupAdminService().get(profile.getId());
			}
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}

		return remote_profile;
	}

	/**
	 * Gets the profile.
	 * 
	 * @param profile
	 *            the profile
	 * @return the profile
	 */
	private it.geosolutions.geofence.core.model.GSUser getUser(GSUser user) {
		it.geosolutions.geofence.core.model.GSUser remote_user = null;
		try {
			if ((user != null) && (user.getId() != -1)) {
				remote_user = geofenceRemoteService.getUserAdminService().get(
						user.getId());
			}
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}

		return remote_user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.geosolutions.geofence.gui.server.service.IRulesManagerService#
	 * getLayerCustomProps(com.extjs .gxt.ui.client.data.PagingLoadConfig,
	 * it.geosolutions.geofence.gui.client.model.Rule)
	 */
//	public PagingLoadResult<LayerCustomProps> getLayerCustomProps(int offset,
//			int limit, Rule rule) {
//		int start = offset;
//		Long t = new Long(0);
//
//		List<LayerCustomProps> customPropsDTO = new ArrayList<LayerCustomProps>();
//
//		if ((rule != null) && (rule.getId() >= 0)) {
//			try {
//				Map<String, String> customProperties = geofenceRemoteService
//						.getRuleAdminService().getDetailsProps(rule.getId());
//
//				if (customProperties == null) {
//					if (logger.isErrorEnabled()) {
//						logger.error("No property found on server");
//					}
//					throw new ApplicationException("No rule found on server");
//				}
//
//				long rulesCount = customProperties.size();
//
//				t = new Long(rulesCount);
//
//				int page = (start == 0) ? start : (start / limit);
//
//				SortedSet<String> sortedset = new TreeSet<String>(
//						customProperties.keySet());
//				Iterator<String> it = sortedset.iterator();
//
//				while (it.hasNext()) {
//					String key = it.next();
//					LayerCustomProps property = new LayerCustomProps();
//					property.setPropKey(key);
//					property.setPropValue(customProperties.get(key));
//					customPropsDTO.add(property);
//				}
//
//				// for (String key : customProperties.keySet()) {
//				//
//				// LayerCustomProps property = new LayerCustomProps();
//				// property.setPropKey(key);
//				// property.setPropValue(customProperties.get(key));
//				// customPropsDTO.add(property);
//				// }
//			} catch (Exception e) {
//				// do nothing!
//			}
//		}
//
//		return new RpcPageLoadResult<LayerCustomProps>(customPropsDTO, offset,
//				t.intValue());
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.geosolutions.geofence.gui.server.service.IRulesManagerService#
	 * setDetailsProps(java.lang .Long,
	 * it.geosolutions.geofence.gui.client.model.data.LayerCustomProps)
	 */
	public void setDetailsProps(Long ruleId, List<LayerCustomProps> customProps) {

        logger.error("TODO: rule refactoring!!! custom props have been removed");

		Map<String, String> props = new HashMap<String, String>();

		for (LayerCustomProps prop : customProps) {
			props.put(prop.getPropKey(), prop.getPropValue());
		}

		LayerDetails details = null;
		try {
			details = geofenceRemoteService.getRuleAdminService().get(ruleId)
					.getLayerDetails();

			if (details == null) {
				details = new LayerDetails();

				it.geosolutions.geofence.core.model.Rule rule = geofenceRemoteService
						.getRuleAdminService().get(ruleId);
				it.geosolutions.geofence.core.model.GSInstance gsInstance = rule
						.getInstance();
				GeoServerRESTReader gsreader = new GeoServerRESTReader(
						gsInstance.getBaseURL(), gsInstance.getUsername(),
						gsInstance.getPassword());

				if ((rule.getWorkspace() == null)
						&& !rule.getLayer().equalsIgnoreCase("*")) {
					// RESTLayerGroup group =
					// gsreader.getLayerGroup(rule.getLayer());
					details.setType(LayerType.LAYERGROUP);
				} else {
					RESTLayer layer = gsreader.getLayer(rule.getLayer());

					if (layer.getType().equals(RESTLayer.TYPE.VECTOR)) {
						details.setType(LayerType.VECTOR);
					} else {
						details.setType(LayerType.RASTER);
					}
				}

                // REMOVED BY ETj
//				details.setCustomProps(props); 
//				geofenceRemoteService.getRuleAdminService().setDetails(ruleId,
//						details);
			} else {
//				geofenceRemoteService.getRuleAdminService().setDetailsProps(
//						ruleId, props);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.geosolutions.geofence.gui.server.service.IRulesManagerService#
	 * getLayerAttributes(com.extjs .gxt.ui.client.data.PagingLoadConfig,
	 * it.geosolutions.geofence.gui.client.model.Rule)
	 */
	public List<LayerAttribUI> getLayerAttributes(Rule rule) {

		LayerDetails layerDetails = null;
		List<LayerAttribUI> layerAttributesDTO = new ArrayList<LayerAttribUI>();

		try {

			layerDetails = geofenceRemoteService.getRuleAdminService()
					.get(rule.getId()).getLayerDetails();

			layerAttributesDTO = loadAttribute(rule);

			if ((layerDetails != null) && (layerAttributesDTO != null)) {
				Set<LayerAttribute> layerAttributes = layerDetails
						.getAttributes();

				if (layerAttributes.size() > 0) {
					if (layerDetails.getType().equals(LayerType.VECTOR)) {
						// ///////////////////////
						// Vector Layer
						// ///////////////////////

						Iterator<LayerAttribute> iterator = layerAttributes
								.iterator();

						while (iterator.hasNext()) {
							LayerAttribute layerAttribute = iterator.next();

							for (int i = 0; i < layerAttributesDTO.size(); i++) {
								String attrName = layerAttributesDTO.get(i)
										.getName();
								if (layerAttribute.getName().equalsIgnoreCase(
										attrName)) {
									LayerAttribUI layAttrUI = new LayerAttribUI();
									layAttrUI.setName(layerAttribute.getName());
									layAttrUI.setDataType(layerAttribute
											.getDatatype());
									layAttrUI.setAccessType(layerAttribute
											.getAccess().toString());

									layerAttributesDTO.set(i, layAttrUI);
								}
							}
						}
					}
				}
			}

		} catch (NotFoundServiceEx e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationException(e.getMessage(), e);
		}

		return layerAttributesDTO;
	}

	/**
	 * @param rule
	 * @return List<LayerAttribUI>
	 */
	private List<LayerAttribUI> loadAttribute(Rule rule) {
		List<LayerAttribUI> layerAttributesDTO = new ArrayList<LayerAttribUI>();
		Set<LayerAttribute> layerAttributes = null;

		GSInstance gsInstance = rule.getInstance();
		GeoServerRESTReader gsreader;

		try {
			gsreader = new GeoServerRESTReader(gsInstance.getBaseURL(),
					gsInstance.getUsername(), gsInstance.getPassword());

			RESTLayer layer = gsreader.getLayer(rule.getLayer());

			if (layer.getType().equals(RESTLayer.TYPE.VECTOR)) {
				layerAttributes = new HashSet<LayerAttribute>();

				// ///////////////////////
				// Vector Layer
				// ///////////////////////

				RESTFeatureType featureType = gsreader.getFeatureType(layer);

				for (RESTFeatureType.Attribute attribute : featureType
						.getAttributes()) {
					LayerAttribute attr = new LayerAttribute();
					attr.setName(attribute.getName());
					attr.setDatatype(attribute.getBinding());

					layerAttributes.add(attr);
				}

				layerAttributesDTO = new ArrayList<LayerAttribUI>();

				Iterator<LayerAttribute> iterator = layerAttributes.iterator();

				while (iterator.hasNext()) {
					LayerAttribute layerAttribute = iterator.next();

					LayerAttribUI layAttrUI = new LayerAttribUI();
					layAttrUI.setName(layerAttribute.getName());
					layAttrUI.setDataType(layerAttribute.getDatatype());

					layerAttributesDTO.add(layAttrUI);
				}
			} else {
				// ///////////////////////
				// Raster Layer
				// ///////////////////////
				layerAttributesDTO = null;
			}

		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationException(e.getMessage(), e);
		}

		return layerAttributesDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.geosolutions.geofence.gui.server.service.IRulesManagerService#
	 * setLayerAttributes(java.lang .Long, java.util.List)
	 */
	public void setLayerAttributes(Long ruleId,
			List<LayerAttribUI> layerAttributes) {

		LayerDetails details = null;

		try {
			details = geofenceRemoteService.getRuleAdminService().get(ruleId)
					.getLayerDetails();

			if (details == null) {
				details = new LayerDetails();
				details.setType(LayerType.VECTOR);
			}

			Set<LayerAttribute> layerAttribs = new HashSet<LayerAttribute>();

			Iterator<LayerAttribUI> iterator = layerAttributes.iterator();
			while (iterator.hasNext()) {
				LayerAttribUI layerAttribUI = iterator.next();

				String accessType = layerAttribUI.getAccessType();

				if (accessType != null) {
					LayerAttribute attr = new LayerAttribute();

					attr.setName(layerAttribUI.getName());
					attr.setDatatype(layerAttribUI.getDataType());

					if (accessType.equalsIgnoreCase("NONE")) {
						attr.setAccess(AccessType.NONE);
					} else if (accessType.equalsIgnoreCase("READONLY")) {
						attr.setAccess(AccessType.READONLY);
					} else {
						attr.setAccess(AccessType.READWRITE);
					}

					layerAttribs.add(attr);
				}

			}

			details.setAttributes(layerAttribs);
			geofenceRemoteService.getRuleAdminService().setDetails(ruleId,
					details);

		} catch (NotFoundServiceEx e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.geosolutions.geofence.gui.server.service.IRulesManagerService#
	 * saveLayerDetails(it.geosolutions
	 * .geofence.gui.client.model.data.LayerDetailsForm)
	 */
	public LayerDetailsInfo saveLayerDetailsInfo(
			LayerDetailsInfo layerDetailsInfo, List<LayerStyle> layerStyles) {

		Long ruleId = layerDetailsInfo.getRuleId();
		LayerDetails layerDetails = null;

		try {
			layerDetails = geofenceRemoteService.getRuleAdminService()
					.get(ruleId).getLayerDetails();

			LayerDetails details = null;
			if (layerDetails == null) {
				details = new LayerDetails();
			} else {
				details = layerDetails;
			}

			it.geosolutions.geofence.core.model.Rule rule = geofenceRemoteService
					.getRuleAdminService().get(ruleId);
			it.geosolutions.geofence.core.model.GSInstance gsInstance = rule
					.getInstance();
			GeoServerRESTReader gsreader = new GeoServerRESTReader(
					gsInstance.getBaseURL(), gsInstance.getUsername(),
					gsInstance.getPassword());
			RESTLayer layer = gsreader.getLayer(rule.getLayer());

			// ///////////////////////////////////
			// Saving the layer details info
			// ///////////////////////////////////

			if (layer.getType().equals(RESTLayer.TYPE.VECTOR)) {
				details.setType(LayerType.VECTOR);
				details.setCqlFilterRead(layerDetailsInfo.getCqlFilterRead());
				details.setCqlFilterWrite(layerDetailsInfo.getCqlFilterWrite());
			} else {
				details.setType(LayerType.RASTER);
			}

			details.setDefaultStyle(layerDetailsInfo.getDefaultStyle());

			String allowedArea = layerDetailsInfo.getAllowedArea();
			if (allowedArea != null) {
				MultiPolygon the_geom = null;
				WKTReader wktReader = new WKTReader();
				Geometry geometry = wktReader.read(allowedArea);
				if (geometry instanceof MultiPolygon) {
					the_geom = (MultiPolygon) geometry;
					the_geom.setSRID(Integer
							.valueOf(layerDetailsInfo.getSrid()).intValue());
					details.setArea(the_geom);
				} else if (geometry instanceof Polygon) {
					GeometryFactory factory = new GeometryFactory();
					the_geom = new MultiPolygon(
							new Polygon[] { (Polygon) geometry }, factory);
				}

				if (the_geom != null) {
					the_geom.setSRID(Integer
							.valueOf(layerDetailsInfo.getSrid()).intValue());
					details.setArea(the_geom);
				}
			} else {
				details.setArea(null);
			}
			
			details.setAreaMetadataField(layerDetailsInfo.getAllowedAreaMetadataField());

			// ///////////////////////////////////
			// Saving the available styles if any
			// ///////////////////////////////////

			Set<String> allowedStyles = new HashSet<String>();

			Iterator<LayerStyle> iterator = layerStyles.iterator();

			while (iterator.hasNext()) {
				LayerStyle style = iterator.next();

				if (style.isEnabled()) {
					allowedStyles.add(style.getStyle());
				}
			}

			if (layerDetails == null) {
				details.setAllowedStyles(allowedStyles);
			} else {
				geofenceRemoteService.getRuleAdminService().setAllowedStyles(
						ruleId, allowedStyles);
			}

			geofenceRemoteService.getRuleAdminService().setDetails(ruleId,
					details);

		} catch (NotFoundServiceEx e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationException(e.getMessage(), e);
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationException(e.getMessage(), e);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationException(e.getMessage(), e);
		}

		return layerDetailsInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeit.geosolutions.geofence.gui.server.service.IRulesManagerService#
	 * getLayerDetailsInfo(it. geosolutions.geofence.gui.client.model.Rule)
	 */
	public LayerDetailsInfo getLayerDetailsInfo(Rule rule) {
		Long ruleId = rule.getId();
		LayerDetails layerDetails = null;
		LayerDetailsInfo layerDetailsInfo = null;

		try {
			layerDetails = geofenceRemoteService.getRuleAdminService()
					.get(ruleId).getLayerDetails();

			if (layerDetails != null) {
				layerDetailsInfo = new LayerDetailsInfo();
				layerDetailsInfo.setRuleId(ruleId);
				layerDetailsInfo.setCqlFilterRead(layerDetails
						.getCqlFilterRead());
				layerDetailsInfo.setCqlFilterWrite(layerDetails
						.getCqlFilterWrite());
				layerDetailsInfo
						.setDefaultStyle(layerDetails.getDefaultStyle());
				
				layerDetailsInfo.setAllowedAreaMetadataField(layerDetails.getAreaMetadataField());

				MultiPolygon the_geom = null;
				Geometry geometry = layerDetails.getArea();

				if (geometry instanceof MultiPolygon) {
					the_geom = (MultiPolygon) geometry;
				} else if (geometry instanceof Polygon) {
					GeometryFactory factory = new GeometryFactory();
					the_geom = new MultiPolygon(new Polygon[] {(Polygon) geometry}, factory);
				}

				if (the_geom != null) {
					layerDetailsInfo.setAllowedArea(the_geom.toText());
					layerDetailsInfo
							.setSrid(String.valueOf(the_geom.getSRID()));
				} else {
					layerDetailsInfo.setAllowedArea(null);
					layerDetailsInfo.setSrid(null);
				}

				if (layerDetails.getType().equals(LayerType.RASTER)) {
					layerDetailsInfo.setType("raster");
				} else {
					layerDetailsInfo.setType("vector");
				}
			}

		} catch (NotFoundServiceEx e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationException(e.getMessage(), e);
		}

		return layerDetailsInfo;
	}

	public void shift(long priorityStart, long offset) {
		if (priorityStart != -1) {
			geofenceRemoteService.getRuleAdminService().shift(priorityStart,
					offset);
		}
	}

	public void swap(long id1, long id2) {
		if ((id1 != -1) && (id2 != -1)) {
			geofenceRemoteService.getRuleAdminService().swap(id1, id2);
		}

	}

	public void findRule(Rule rule) throws ApplicationException, Exception {
		it.geosolutions.geofence.core.model.Rule ret = null;
		ret = geofenceRemoteService.getRuleAdminService().get(rule.getId());
		// return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.geosolutions.geofence.gui.server.service.IRulesManagerService#
	 * saveLayerLimitsInfo(it.
	 * geosolutions.geofence.gui.client.model.data.LayerLimitsInfo)
	 */
	public LayerLimitsInfo saveLayerLimitsInfo(LayerLimitsInfo layerLimitsForm) {

		Long ruleId = layerLimitsForm.getRuleId();
		RuleLimits ruleLimits = null;

		try {
			ruleLimits = geofenceRemoteService.getRuleAdminService()
					.get(ruleId).getRuleLimits();

			if (ruleLimits == null) {
				ruleLimits = new RuleLimits();
			}

			String allowedArea = layerLimitsForm.getAllowedArea();

			if (allowedArea != null) {
				MultiPolygon the_geom = null;
				WKTReader wktReader = new WKTReader();
				Geometry geometry = wktReader.read(allowedArea);
				if (geometry instanceof MultiPolygon) {
					the_geom = (MultiPolygon) geometry;
					the_geom.setSRID(Integer
							.valueOf(layerLimitsForm.getSrid()).intValue());
					ruleLimits.setAllowedArea(the_geom);
				} else if (geometry instanceof Polygon) {
					GeometryFactory factory = new GeometryFactory();
					the_geom = new MultiPolygon(
							new Polygon[] { (Polygon) geometry }, factory);
				}

				if (the_geom != null) {
					the_geom.setSRID(Integer
							.valueOf(layerLimitsForm.getSrid()).intValue());
					ruleLimits.setAllowedArea(the_geom);
				}
			} else {
				ruleLimits.setAllowedArea(null);
			}

			geofenceRemoteService.getRuleAdminService().setLimits(ruleId,
					ruleLimits);

		} catch (NotFoundServiceEx e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationException(e.getMessage(), e);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationException(e.getMessage(), e);
		}

		return layerLimitsForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.geosolutions.geofence.gui.server.service.IRulesManagerService#
	 * getLayerLimitsInfo(it. geosolutions.geofence.gui.client.model.Rule)
	 */
	public LayerLimitsInfo getLayerLimitsInfo(Rule rule) {
		Long ruleId = rule.getId();
		RuleLimits ruleLimits = null;
		LayerLimitsInfo layerLimitsInfo = null;

		try {
			ruleLimits = geofenceRemoteService.getRuleAdminService()
					.get(ruleId).getRuleLimits();

			if (ruleLimits != null) {
				layerLimitsInfo = new LayerLimitsInfo();
				layerLimitsInfo.setRuleId(ruleId);

				MultiPolygon the_geom = ruleLimits.getAllowedArea();

				if (the_geom != null) {
					layerLimitsInfo.setAllowedArea(the_geom.toText());
					layerLimitsInfo.setSrid(String.valueOf(the_geom.getSRID()));
				} else {
					layerLimitsInfo.setAllowedArea(null);
					layerLimitsInfo.setSrid(null);
				}
			}
		} catch (NotFoundServiceEx e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationException(e.getMessage(), e);
		}

		return layerLimitsInfo;
	}
}

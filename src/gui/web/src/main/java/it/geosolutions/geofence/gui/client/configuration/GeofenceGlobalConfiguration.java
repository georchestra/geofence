/*
 * $ Header: it.geosolutions.geofence.gui.client.configuration.GeofenceGlobalConfiguration,v. 0.1 14-gen-2011 19.27.01 created by afabiani <alessio.fabiani at geo-solutions.it> $
 * $ Revision: 0.1 $
 * $ Date: 14-gen-2011 19.27.01 $
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
package it.geosolutions.geofence.gui.client.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// TODO: Auto-generated Javadoc
/**
 * The Class GeofenceGlobalConfiguration.
 */
@Component("geofenceGlobalConfiguration")
public class GeofenceGlobalConfiguration implements IGeofenceConfiguration {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3377836318526396981L;

	/** The profile bean manager. */
	@Autowired
	private IUserBeanManager userBeanManager;

	private String baseLayerURL;
	private String baseLayerName;
	private String baseLayerTitle;
	private String baseLayerFormat;
	private String baseLayerStyle;
	private String mapCenterLon;
	private String mapCenterLat;
	private String mapZoom;
    private String mapMaxResolution;
    private String mapMaxExtent;
    private String mapProjection;

    /**
	 * Gets the profile bean manager.
	 * 
	 * @return the profile bean manager
	 */
	public IUserBeanManager getUserBeanManager() {
		// TODO Auto-generated method stub
		return userBeanManager;
	}

	/**
	 * @param baseLayerURL the baseLayerURL to set
	 */
	public void setBaseLayerURL(String baseLayerURL) {
		this.baseLayerURL = baseLayerURL;
	}

	/**
	 * @return the baseLayerURL
	 */
	public String getBaseLayerURL() {
		return baseLayerURL;
	}

	/**
	 * @param baseLayerName the baseLayerName to set
	 */
	public void setBaseLayerName(String baseLayerName) {
		this.baseLayerName = baseLayerName;
	}

	/**
	 * @return the baseLayerName
	 */
	public String getBaseLayerName() {
		return baseLayerName;
	}

	/**
	 * @param baseLayerTitle the baseLayerTitle to set
	 */
	public void setBaseLayerTitle(String baseLayerTitle) {
		this.baseLayerTitle = baseLayerTitle;
	}

	/**
	 * @return the baseLayerTitle
	 */
	public String getBaseLayerTitle() {
		return baseLayerTitle;
	}

	/**
	 * @param baseLayerFormat the baseLayerFormat to set
	 */
	public void setBaseLayerFormat(String baseLayerFormat) {
		this.baseLayerFormat = baseLayerFormat;
	}

	/**
	 * @return the baseLayerFormat
	 */
	public String getBaseLayerFormat() {
		return baseLayerFormat;
	}

	/**
	 * @param baseLayerStyle the baseLayerStyle to set
	 */
	public void setBaseLayerStyle(String baseLayerStyle) {
		this.baseLayerStyle = baseLayerStyle;
	}

	/**
	 * @return the baseLayerStyle
	 */
	public String getBaseLayerStyle() {
		return baseLayerStyle;
	}

	/**
	 * @return the mapCenterLon
	 */
	public String getMapCenterLon() {
		return mapCenterLon;
	}

	/**
	 * @param mapCenterLon the mapCenterLon to set
	 */
	public void setMapCenterLon(String mapCenterLon) {
		this.mapCenterLon = mapCenterLon;
	}

	/**
	 * @return the mapCenterLat
	 */
	public String getMapCenterLat() {
		return mapCenterLat;
	}

	/**
	 * @param mapCenterLat the mapCenterLat to set
	 */
	public void setMapCenterLat(String mapCenterLat) {
		this.mapCenterLat = mapCenterLat;
	}

	/**
	 * @return the mapZoom
	 */
	public String getMapZoom() {
		return mapZoom;
	}

	/**
	 * @param mapZoom the mapZoom to set
	 */
	public void setMapZoom(String mapZoom) {
		this.mapZoom = mapZoom;
	}

    public String getMapMaxResolution() {
        return mapMaxResolution;
    }

    public void setMapMaxResolution(String mapMaxResolution) {
        this.mapMaxResolution = mapMaxResolution;
    }

    public String getMapMaxExtent() {
        return mapMaxExtent;
    }

    public void setMapMaxExtent(String mapMaxExtent) {
        this.mapMaxExtent = mapMaxExtent;
    }

    public String getMapProjection() {
        return mapProjection;
    }

    public void setMapProjection(String mapProjection) {
        this.mapProjection = mapProjection;
    }

}

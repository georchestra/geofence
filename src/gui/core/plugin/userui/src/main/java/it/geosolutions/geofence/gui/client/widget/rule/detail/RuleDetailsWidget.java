/*
 * $ Header: it.geosolutions.geofence.gui.client.widget.rule.detail.RuleDetailsWidget,v. 0.1 25-feb-2011 16.30.38 created by afabiani <alessio.fabiani at geo-solutions.it> $
 * $ Revision: 0.1 $
 * $ Date: 25-feb-2011 16.30.38 $
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
package it.geosolutions.geofence.gui.client.widget.rule.detail;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ComponentManager;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

import it.geosolutions.geofence.gui.client.GeofenceEvents;
import it.geosolutions.geofence.gui.client.Resources;
import it.geosolutions.geofence.gui.client.i18n.I18nProvider;
import it.geosolutions.geofence.gui.client.model.Rule;
import it.geosolutions.geofence.gui.client.model.data.LayerDetailsInfo;
import it.geosolutions.geofence.gui.client.service.WorkspacesManagerRemoteServiceAsync;


// TODO: Auto-generated Javadoc
/**
 * The Class RuleDetailsWidget.
 */
public class RuleDetailsWidget extends ContentPanel
{

    /** The rule. */
    private Rule theRule;

    /** The rule details info. */
    private RuleDetailsInfoWidget ruleDetailsInfo;

    /** The rule details grid. */
    private RuleDetailsGridWidget ruleDetailsGrid;

    /** The tool bar. */
    private ToolBar toolBar;

    /** The save layer details button. */
    private Button saveLayerDetailsButton;

    private Button cancelButton;

    /**
    * Instantiates a new rule details widget.
    *
    * @param model
    *            the model
    * @param workspacesService
    *            the workspaces service
    */
    public RuleDetailsWidget(Rule model, WorkspacesManagerRemoteServiceAsync workspacesService)
    {
        this.theRule = model;
        this.toolBar = new ToolBar();

        setHeaderVisible(false);
        setFrame(true);
        setHeight(380);
        setWidth(690);
        setLayout(new FitLayout());

        ruleDetailsInfo = new RuleDetailsInfoWidget(this.theRule, workspacesService, this);
        add(ruleDetailsInfo.getFormPanel());

        ruleDetailsGrid = new RuleDetailsGridWidget(this.theRule, workspacesService, this);
        add(ruleDetailsGrid.getGrid());

        super.setMonitorWindowResize(true);

        setScrollMode(Scroll.AUTOY);

        this.saveLayerDetailsButton = new Button("Save");
        saveLayerDetailsButton.setIcon(Resources.ICONS.save());
        saveLayerDetailsButton.disable();

        saveLayerDetailsButton.addListener(Events.OnClick, new Listener<ButtonEvent>()
            {

                public void handleEvent(ButtonEvent be)
                {

                    disableSaveButton();

                    LayerDetailsInfo detailsInfoModel = ruleDetailsInfo.getModelData();
                    Dispatcher.forwardEvent(GeofenceEvents.SAVE_LAYER_DETAILS, detailsInfoModel);

                    Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                        new String[] { "GeoServer Rules: Layer Details", "Apply Changes" });

                }
            });

        cancelButton = new Button("Cancel");
        cancelButton.addListener(Events.OnClick, new Listener<ButtonEvent>()
            {
                public void handleEvent(ButtonEvent be)
                {
                    // /////////////////////////////////////////////////////////
                    // Getting the Rule details edit dialogs and hiding this
                    // /////////////////////////////////////////////////////////
                    ComponentManager.get().get(I18nProvider.getMessages().ruleDialogId()).hide();
                }
            });

        this.toolBar.add(new FillToolItem());
        this.toolBar.add(saveLayerDetailsButton);
        this.toolBar.add(cancelButton);
        setBottomComponent(this.toolBar);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.widget.Component#onWindowResize(int, int)
     */
    @Override
    protected void onWindowResize(int width, int height)
    {
//        super.setWidth(width - 5);
        super.layout();
    }

    /**
     * Sets the rule details info.
     *
     * @param layerCustomPropsInfo
     *            the new rule details info
     */
    public void setRuleDetailsInfo(RuleDetailsInfoWidget layerCustomPropsInfo)
    {
        this.ruleDetailsInfo = layerCustomPropsInfo;
    }

    /**
     * Gets the rule details info.
     *
     * @return the rule details info
     */
    public RuleDetailsInfoWidget getRuleDetailsInfo()
    {
        return ruleDetailsInfo;
    }

    /**
     * Gets the rule details grid.
     *
     * @return the rule details grid
     */
    public RuleDetailsGridWidget getRuleDetailsGrid()
    {
        return ruleDetailsGrid;
    }

    /**
    * Sets the rule details grid.
    *
    * @param ruleDetailsGrid
    *            the new rule details grid
    */
    public void setRuleDetailsGrid(RuleDetailsGridWidget ruleDetailsGrid)
    {
        this.ruleDetailsGrid = ruleDetailsGrid;
    }

	/**
	 * @return the toolBar
	 */
	public ToolBar getToolBar() {
		return toolBar;
	}

	/**
     * Disable save button.
     */
    public void disableSaveButton()
    {
        if (this.saveLayerDetailsButton.isEnabled())
        {
            this.saveLayerDetailsButton.disable();
        }
    }

    /**
     * Enable save button.
     */
    public void enableSaveButton()
    {
        if (!this.saveLayerDetailsButton.isEnabled())
        {
            this.saveLayerDetailsButton.enable();
        }
    }

}

/*
 * $ Header: it.geosolutions.geofence.gui.client.widget.dialog.RuleDetailsEditDialog,v. 0.1 10-feb-2011 11.50.22 created by afabiani <alessio.fabiani at geo-solutions.it> $
 * $ Revision: 0.1 $
 * $ Date: 10-feb-2011 11.50.22 $
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
package it.geosolutions.geofence.gui.client.widget.dialog;

import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.user.client.ui.FormPanel;

import it.geosolutions.geofence.gui.client.GeofenceEvents;
import it.geosolutions.geofence.gui.client.i18n.I18nProvider;
import it.geosolutions.geofence.gui.client.model.Rule;
import it.geosolutions.geofence.gui.client.service.RulesManagerRemoteServiceAsync;
import it.geosolutions.geofence.gui.client.service.WorkspacesManagerRemoteServiceAsync;
import it.geosolutions.geofence.gui.client.widget.SaveStaus;
import it.geosolutions.geofence.gui.client.widget.SaveStaus.EnumSaveStatus;
import it.geosolutions.geofence.gui.client.widget.rule.detail.LayerAttributesTabItem;
import it.geosolutions.geofence.gui.client.widget.rule.detail.LayerCustomPropsTabItem;
import it.geosolutions.geofence.gui.client.widget.rule.detail.RuleDetailsTabItem;
import it.geosolutions.geofence.gui.client.widget.rule.detail.RuleLimitsTabItem;
import it.geosolutions.geofence.gui.client.widget.tab.TabWidget;


/**
 * The Class RuleDetailsEditDialog.
 */
public class RuleDetailsEditDialog extends Dialog
{

    /** The Constant RULE_DETAILS_DIALOG_ID. */
    public static final String RULE_DETAILS_DIALOG_ID = "ruleDetailsDialog";

    /** The Constant RULE_LAYER_ATTRIBUTES_DIALOG_ID. */
    public static final String RULE_LAYER_ATTRIBUTES_DIALOG_ID = "ruleLayerAttributesDialog";

    /** The Constant RULE_LAYER_CUSTOM_PROPS_DIALOG_ID. */
    public static final String RULE_LAYER_CUSTOM_PROPS_DIALOG_ID = "ruleLayerCustomPropsDialog";

    /** The Constant RULE_LIMITS_DIALOG_ID. */
    public static final String RULE_LIMITS_DIALOG_ID = "ruleLimitsDialog";

    /** The form panel. */
    private FormPanel formPanel;

    /** The preview fp. */
    private com.extjs.gxt.ui.client.widget.form.FormPanel previewFP;

    /** The save status. */
    private SaveStaus saveStatus;

    /** The done. */
    private Button done;

    /** The wkt. */
    protected String wkt;

    /** The model. */
    private Rule model;

    /** The rules manager service remote. */
    private RulesManagerRemoteServiceAsync rulesManagerServiceRemote;

    private WorkspacesManagerRemoteServiceAsync workspacesManagerServiceRemote;

    /** The tab widget. */
    private TabWidget tabWidget;

    /**
     * Instantiates a new rule details edit dialog.
     *
     * @param rulesManagerServiceRemote
     *            the rules manager service remote
     */
    public RuleDetailsEditDialog(RulesManagerRemoteServiceAsync rulesManagerServiceRemote,
        WorkspacesManagerRemoteServiceAsync workspacesManagerServiceRemote)
    {
        this.rulesManagerServiceRemote = rulesManagerServiceRemote;
        this.workspacesManagerServiceRemote = workspacesManagerServiceRemote;
        setTabWidget(new TabWidget());

        setResizable(false);
        setButtons("");
        setClosable(true);
        setModal(true);
        setWidth(700);
        setHeight(477);
        setId(I18nProvider.getMessages().ruleDialogId());

        add(this.getTabWidget());

        // this.addListener(Events.Hide, new Listener<WindowEvent>() {
        //
        // public void handleEvent(WindowEvent be) {
        // reset();
        // }
        // });
        //
        // this.addListener(Events.Show, new Listener<WindowEvent>() {
        //
        // public void handleEvent(WindowEvent be) {
        // mapPreviewWidget.getMapWidget().getMap().zoomToMaxExtent();
        // // mapPreviewWidget.getMapWidget().getMap().zoomTo(1);
        // mapPreviewWidget.getMapWidget().getMap().updateSize();
        // }
        // });
        //
        // this.createUpload();
        // this.createMapPreview();

        // add(this.previewFP);
    }

    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.Dialog#createButtons()
     */
    @Override
    protected void createButtons()
    {
        super.createButtons();

        this.saveStatus = new SaveStaus();
        this.saveStatus.setAutoWidth(true);

        getButtonBar().add(saveStatus);

        getButtonBar().add(new FillToolItem());

//        this.done = new Button("Done", new SelectionListener<ButtonEvent>() {
//            @Override
//            public void componentSelected(ButtonEvent ce) {
//                hide();
//            }
//        });
//
//        addButton(done);
    }

    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.Window#show()
     */
    @Override
    public void show()
    {
        this.show(true);
    }
    
    public void show(boolean loadModel)
    {
        if (getModel() != null)
        {

            String layer = model.getLayer();
            String workspace = model.getWorkspace();
            String grant = model.getGrant();

            if ((layer != null) && !layer.equalsIgnoreCase("*") && grant.equalsIgnoreCase("ALLOW"))
            {
                setHeading("Editing Details for Rule #" + model.getPriority());

                //if (workspace.equals("*") && !layer.equalsIgnoreCase("*"))
                //else
                {
                    TabItem ruleDetailsTabItem = new RuleDetailsTabItem(RULE_DETAILS_DIALOG_ID, model,
                            workspacesManagerServiceRemote, loadModel);
                    this.tabWidget.add(ruleDetailsTabItem);

                    if ((model.getLayer() != null) && !model.getLayer().equalsIgnoreCase("*"))
                    {
                        TabItem layerAttributesItem = new LayerAttributesTabItem(RULE_LAYER_ATTRIBUTES_DIALOG_ID, model,
                                rulesManagerServiceRemote);
                        this.tabWidget.add(layerAttributesItem);

                        // AF: Refactor ... remove layer custom props
                        /*TabItem layersCustomPropsItem = new LayerCustomPropsTabItem(RULE_LAYER_CUSTOM_PROPS_DIALOG_ID,
                        model, rulesManagerServiceRemote);
                        this.tabWidget.add(layersCustomPropsItem);*/

                        this.tabWidget.setSelection(ruleDetailsTabItem);
                    }
                }
            }
            else if (grant.equalsIgnoreCase("LIMIT"))
            {
                setHeading("Editing Limits for Rule #" + model.getPriority());

                TabItem ruleLimitsTabItem = new RuleLimitsTabItem(RULE_LIMITS_DIALOG_ID, model,
                        rulesManagerServiceRemote, loadModel);
                this.tabWidget.add(ruleLimitsTabItem);
                
                this.tabWidget.setSelection(ruleLimitsTabItem);
            }
            else
            {
                Dispatcher.forwardEvent(GeofenceEvents.SEND_ALERT_MESSAGE,
                    new String[]
                    {
                        "Rule Properties Editor",
                        "Rule details editor actually enabled only for Rules which specifies a Layer on the filter."
                    });
            }

//            // TODO: Temporary. To be removed as soon as the rule editor will be completed!
//            if (layer == null || layer.equalsIgnoreCase("*")) {
//                Dispatcher.forwardEvent(GeofenceEvents.SEND_ALERT_MESSAGE,
//                        new String[] {
//                            "Rule Properties Editor",
//                            "Rule details editor actually enabled only for Rules which specifies a Layer on the filter."
//                        }
//                );
//            } else {
//                setHeading("Editing Details for Rule #" + model.getPriority() );
//                TabItem ruleDetailsTabItem = new RuleDetailsTabItem(RULE_DETAILS_DIALOG_ID, model, workspacesManagerServiceRemote);
//                this.tabWidget.add(ruleDetailsTabItem);
//
//                if (model.getLayer() != null && !model.getLayer().equalsIgnoreCase("*")) {
//                    TabItem layerAttributesItem = new LayerAttributesTabItem(RULE_LAYER_ATTRIBUTES_DIALOG_ID, model, rulesManagerServiceRemote);
//                    TabItem layersCustomPropsItem = new LayerCustomPropsTabItem(RULE_LAYER_CUSTOM_PROPS_DIALOG_ID, model, rulesManagerServiceRemote);
//
//                    this.tabWidget.add(layerAttributesItem);
//                    this.tabWidget.add(layersCustomPropsItem);
//
//                    this.tabWidget.setSelection(ruleDetailsTabItem);
//                }
//            }
        }
        
        super.show();

    }

    /**
     * Reset.
     */
    public void reset()
    {
//        this.done.disable();
        this.tabWidget.removeAll();
        this.saveStatus.clearStatus("");
    }

    /**
     * Sets the save status.
     *
     * @param status
     *            the status
     * @param message
     *            the message
     */
    public void setSaveStatus(EnumSaveStatus status, EnumSaveStatus message)
    {
        this.saveStatus.setIconStyle(status.getValue());
        this.saveStatus.setText(message.getValue());
    }

    /**
     * Sets the model.
     *
     * @param model
     *            the new model
     */
    public void setModel(Rule model)
    {
        this.model = model;
    }

    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.Component#getModel()
     */
    public Rule getModel()
    {
        return model;
    }

    /**
     * Sets the tab widget.
     *
     * @param tabWidget
     *            the new tab widget
     */
    public void setTabWidget(TabWidget tabWidget)
    {
        this.tabWidget = tabWidget;
    }

    /**
     * Gets the tab widget.
     *
     * @return the tab widget
     */
    public TabWidget getTabWidget()
    {
        return tabWidget;
    }
}

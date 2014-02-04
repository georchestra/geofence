/*
 * $ Header: it.geosolutions.geofence.gui.client.widget.EditRuleWidget,v. 0.1 25-feb-2011 16.31.40 created by afabiani <alessio.fabiani at geo-solutions.it> $
 * $ Revision: 0.1 $
 * $ Date: 25-feb-2011 16.31.40 $
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
package it.geosolutions.geofence.gui.client.widget;

import it.geosolutions.geofence.gui.client.GeofenceEvents;
import it.geosolutions.geofence.gui.client.form.GeofenceFormWidget;
import it.geosolutions.geofence.gui.client.i18n.I18nProvider;
import it.geosolutions.geofence.gui.client.model.BeanKeyValue;
import it.geosolutions.geofence.gui.client.model.GSInstance;
import it.geosolutions.geofence.gui.client.model.GSUser;
import it.geosolutions.geofence.gui.client.model.Rule;
import it.geosolutions.geofence.gui.client.model.UserGroup;
import it.geosolutions.geofence.gui.client.model.data.Grant;
import it.geosolutions.geofence.gui.client.model.data.Layer;
import it.geosolutions.geofence.gui.client.model.data.Request;
import it.geosolutions.geofence.gui.client.model.data.Service;
import it.geosolutions.geofence.gui.client.model.data.Workspace;
import it.geosolutions.geofence.gui.client.service.GsUsersManagerRemoteServiceAsync;
import it.geosolutions.geofence.gui.client.service.InstancesManagerRemoteServiceAsync;
import it.geosolutions.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;
import it.geosolutions.geofence.gui.client.service.RulesManagerRemoteServiceAsync;
import it.geosolutions.geofence.gui.client.service.WorkspacesManagerRemoteServiceAsync;
import it.geosolutions.geofence.gui.client.view.RulesView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.user.client.rpc.AsyncCallback;


// TODO: Auto-generated Javadoc
/**
 * The Class EditRuleWidget.
 */
public class EditRuleWidget extends GeofenceFormWidget
{

    /** The column priority width. */
    private static final int COLUMN_PRIORITY_WIDTH = 30;

    /** The Constant COLUMN_USER_WIDTH. */
    private static final int COLUMN_USER_WIDTH = 130; // 130;

    /** The Constant COLUMN_PROFILE_WIDTH. */
    private static final int COLUMN_PROFILE_WIDTH = 130; // 160;

    /** The Constant COLUMN_INSTANCE_WIDTH. */
    private static final int COLUMN_INSTANCE_WIDTH = 150; // 160;

    /** The Constant COLUMN_SERVICE_WIDTH. */
    private static final int COLUMN_SERVICE_WIDTH = 120; // 100;

    /** The Constant COLUMN_REQUEST_WIDTH. */
    private static final int COLUMN_REQUEST_WIDTH = 180; // 190;

    /** The Constant COLUMN_WORKSPACE_WIDTH. */
    private static final int COLUMN_WORKSPACE_WIDTH = 150; // 130;

    /** The Constant COLUMN_LAYER_WIDTH. */
    private static final int COLUMN_LAYER_WIDTH = 150; // 130;

    /** The Constant COLUMN_GRANT_WIDTH. */
    private static final int COLUMN_GRANT_WIDTH = 100; // 100;

    /** The submit event. */
    private EventType submitEvent;

    /** The close on submit. */
    private boolean closeOnSubmit;

    /** The rules service. */
    private RulesManagerRemoteServiceAsync rulesService;

    /** The gs users service. */
    private GsUsersManagerRemoteServiceAsync gsUsersService;

    /** The profiles service. */
    private ProfilesManagerRemoteServiceAsync profilesService;

    /** The instances service. */
    private InstancesManagerRemoteServiceAsync instancesService;

    /** The workspaces service. */
    private WorkspacesManagerRemoteServiceAsync workspacesService;

    /** The loader. */
    private PagingLoader<PagingLoadResult<ModelData>> loader;

    /** The model. */
    public Rule model = new Rule();

    /** The store. */
    public ListStore<Rule> store = new ListStore<Rule>();

    /** The grid. */
    public Grid<Rule> grid;

    /** The status. */
    public String status = "UPDATE";

    /** The unique. */
    boolean unique = false;

    /** The parent grid. */
    public Grid<Rule> parentGrid;

    /** The priority edited. */
    boolean priorityEdited = false;

    /** The rules view. */
    RulesView rulesView;

    boolean onExecute = false;

    private static ListStore<Workspace> workspaces = new ListStore<Workspace>();

    private static ListStore<Layer> layers = new ListStore<Layer>();

    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.Window#show()
     */
    @Override
    public void show() {
        super.show();
        
        initializeWorkspaces(model.getInstance());
    }

    /**
     * Instantiates a new edits the rule widget.
     *
     * @param submitEvent
     *            the submit event
     * @param closeOnSubmit
     *            the close on submit
     * @param rulesService
     *            the rules service
     * @param gsUsersService
     *            the gs users service
     * @param profilesService
     *            the profiles service
     * @param instancesService
     *            the instances service
     * @param workspacesService
     *            the workspaces service
     */
    public EditRuleWidget(EventType submitEvent, boolean closeOnSubmit,
        RulesManagerRemoteServiceAsync rulesService,
        GsUsersManagerRemoteServiceAsync gsUsersService,
        ProfilesManagerRemoteServiceAsync profilesService,
        InstancesManagerRemoteServiceAsync instancesService,
        WorkspacesManagerRemoteServiceAsync workspacesService)
    {
        super();
        this.submitEvent = submitEvent;
        this.closeOnSubmit = closeOnSubmit;
        this.rulesService = rulesService;
        this.gsUsersService = gsUsersService;
        this.profilesService = profilesService;
        this.instancesService = instancesService;
        this.workspacesService = workspacesService;
    }

    /**
     * Instantiates a new edits the rule widget.
     *
     * @param models
     *            the models
     */
    public EditRuleWidget(List<Rule> models)
    {
        createStore();
        this.store.add(models);
        initGrid();
    }

    /**
     * Instantiates a new edits the rule widget.
     */
    public EditRuleWidget()
    {
        createStore();
        initGrid();
    }

    /**
     * Gets the submit event.
     *
     * @return the submit event
     */
    protected EventType getSubmitEvent()
    {
        return this.submitEvent;
    }

    /*
     * (non-Javadoc)
     *
     * @see it.geosolutions.geofence.gui.client.form.IForm#execute()
     */
    public void execute()
    {
        this.saveStatus.setBusy("Operation in progress");

        if (status.equals("UPDATE"))
        {
            Dispatcher.forwardEvent(GeofenceEvents.RULE_SAVE, model);
            onExecute = true;
        }
        else
        {
            Dispatcher.forwardEvent(GeofenceEvents.RULE_ADD, model);
            onExecute = true;
        }

        if (this.closeOnSubmit)
        {
            cancel();
        }

        this.injectEvent();
    }

    /*
     * (non-Javadoc)
     *
     * @see it.geosolutions.geofence.gui.client.form.GeofenceFormWidget#addComponentToForm ()
     */
    @Override
    public void addComponentToForm()
    {

        if (grid != null)
        {
            this.formPanel.add(grid);
        }
        addOtherComponents();
    }

    /**
     * Prepare column model.
     *
     * @return the column model
     */
    public ColumnModel prepareColumnModel()
    {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig rulePriorityColumn = new ColumnConfig();
        rulePriorityColumn.setId(BeanKeyValue.PRIORITY.getValue());
        rulePriorityColumn.setWidth(COLUMN_PRIORITY_WIDTH);
        rulePriorityColumn.setRenderer(this.createPriorityCustomField()); // CustomField//createUsersComboBox
        rulePriorityColumn.setMenuDisabled(false);
        rulePriorityColumn.setSortable(true);
        configs.add(rulePriorityColumn);

        ColumnConfig ruleProfileColumn = new ColumnConfig();
        ruleProfileColumn.setId(BeanKeyValue.PROFILE.getValue());
        ruleProfileColumn.setHeader("Group");
        ruleProfileColumn.setWidth(COLUMN_PROFILE_WIDTH);
        ruleProfileColumn.setRenderer(this.createProfilesComboBox());
        ruleProfileColumn.setMenuDisabled(true);
        ruleProfileColumn.setSortable(false);
        configs.add(ruleProfileColumn);

        ColumnConfig ruleInstanceColumn = new ColumnConfig();
        ruleInstanceColumn.setId(BeanKeyValue.INSTANCE.getValue());
        ruleInstanceColumn.setHeader("Instance");
        ruleInstanceColumn.setWidth(COLUMN_INSTANCE_WIDTH);
        ruleInstanceColumn.setRenderer(this.createInstancesComboBox());
        ruleInstanceColumn.setMenuDisabled(true);
        ruleInstanceColumn.setSortable(false);
        configs.add(ruleInstanceColumn);

        ColumnConfig ruleServiceColumn = new ColumnConfig();
        ruleServiceColumn.setId(BeanKeyValue.SERVICE.getValue());
        ruleServiceColumn.setHeader("Service");
        ruleServiceColumn.setWidth(COLUMN_SERVICE_WIDTH);
        ruleServiceColumn.setRenderer(this.createServicesComboBox());
        ruleServiceColumn.setMenuDisabled(true);
        ruleServiceColumn.setSortable(false);
        configs.add(ruleServiceColumn);

        ColumnConfig ruleServiceRequestColumn = new ColumnConfig();
        ruleServiceRequestColumn.setId(BeanKeyValue.REQUEST.getValue());
        ruleServiceRequestColumn.setHeader("Request");
        ruleServiceRequestColumn.setWidth(COLUMN_REQUEST_WIDTH);
        ruleServiceRequestColumn.setRenderer(this.createServicesRequestComboBox());
        ruleServiceRequestColumn.setMenuDisabled(true);
        ruleServiceRequestColumn.setSortable(false);
        configs.add(ruleServiceRequestColumn);

        ColumnConfig ruleServiceWorkspacesColumn = new ColumnConfig();
        ruleServiceWorkspacesColumn.setId(BeanKeyValue.WORKSPACE.getValue());
        ruleServiceWorkspacesColumn.setHeader("Workspace");
        ruleServiceWorkspacesColumn.setWidth(COLUMN_WORKSPACE_WIDTH);
        ruleServiceWorkspacesColumn.setRenderer(this.createServiceWorkspacesComboBox());
        ruleServiceWorkspacesColumn.setMenuDisabled(true);
        ruleServiceWorkspacesColumn.setSortable(false);
        configs.add(ruleServiceWorkspacesColumn);

        ColumnConfig ruleWorkspaceLayersColumn = new ColumnConfig();
        ruleWorkspaceLayersColumn.setId(BeanKeyValue.LAYER.getValue());
        ruleWorkspaceLayersColumn.setHeader("Layer");
        ruleWorkspaceLayersColumn.setWidth(COLUMN_LAYER_WIDTH);
        ruleWorkspaceLayersColumn.setRenderer(this.createWorkspacesLayersComboBox());
        ruleWorkspaceLayersColumn.setMenuDisabled(true);
        ruleWorkspaceLayersColumn.setSortable(false);
        configs.add(ruleWorkspaceLayersColumn);

        ColumnConfig ruleGrantsColumn = new ColumnConfig();
        ruleGrantsColumn.setId(BeanKeyValue.GRANT.getValue());
        ruleGrantsColumn.setHeader("Grant");
        ruleGrantsColumn.setWidth(COLUMN_GRANT_WIDTH);
        ruleGrantsColumn.setRenderer(this.createGrantsComboBox());
        ruleGrantsColumn.setMenuDisabled(true);
        ruleGrantsColumn.setSortable(false);
        configs.add(ruleGrantsColumn);

        return new ColumnModel(configs);
    }

    /**
     * Creates the priority custom field.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<Rule> createPriorityCustomField()
    {
        GridCellRenderer<Rule> comboRendered = new GridCellRenderer<Rule>()
            {

                private boolean init;

                public Object render(final Rule model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<Rule> store, Grid<Rule> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<Rule>>()
                            {

                                public void handleEvent(GridEvent<Rule> be)
                                {
                                    for (int i = 0; i < be.getGrid().getStore().getCount(); i++)
                                    {
                                        if ((be.getGrid().getView().getWidget(i, be.getColIndex()) != null) &&
                                                (be.getGrid().getView().getWidget(i, be.getColIndex()) instanceof BoxComponent))
                                        {
                                            ((BoxComponent) be.getGrid().getView().getWidget(i,
                                                    be.getColIndex())).setWidth(be.getWidth() - 10);
                                        }
                                    }
                                }
                            });
                    }

                    // TODO: generalize this!
                    ArrayList<Rule> list = new ArrayList<Rule>();

                    for (int i = 0; i < store.getCount(); i++)
                    {
                        Rule rule = ((Rule) store.getAt(i));
                        list.add(rule);
                    }

                    final TextField<String> priorityCustomField = new TextField<String>(); // (ListField)
                    priorityCustomField.setId("rulePriorityCombo");
                    priorityCustomField.setName("rulePriorityCombo");
                    priorityCustomField.setEmptyText("*");
                    priorityCustomField.setFieldLabel(BeanKeyValue.PRIORITY.getValue()); // DisplayField
                    priorityCustomField.setValue(BeanKeyValue.PRIORITY.getValue());
                    priorityCustomField.setReadOnly(false);

                    priorityCustomField.setWidth(COLUMN_PRIORITY_WIDTH - 10);
                    priorityCustomField.show();

                    if (model.getPriority() != -1)
                    {
                        long name2 = model.getPriority();
                        priorityCustomField.setValue(Long.valueOf(name2).toString());
                    }
                    else
                    {
                        priorityCustomField.setValue("-1");
                    }

                    KeyListener keyListener = new KeyListener()
                        {

                            @Override
                            public void componentKeyUp(ComponentEvent event)
                            {
                                if (event.getKeyCode() == '\r')
                                {
                                    event.cancelBubble();

                                    priorityEdited = true;
                                    try
                                    {
                                        model.setPriority(Long.parseLong((String) priorityCustomField.getRawValue()));
                                    }
                                    catch (Exception e)
                                    {
                                        Dispatcher.forwardEvent(GeofenceEvents.SEND_ALERT_MESSAGE,
                                            new String[]
                                            {
                                                I18nProvider.getMessages().remoteServiceName(),
                                                e.getMessage()
                                            });
                                    }
                                    Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO,
                                        model);
                                }
                            }
                        };

                    priorityCustomField.addKeyListener(keyListener);

                    priorityCustomField.addListener(Events.Blur, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                priorityEdited = true;
                                try
                                {
                                    model.setPriority(Long.parseLong((String) priorityCustomField.getRawValue()));
                                }
                                catch (Exception e)
                                {
                                    Dispatcher.forwardEvent(GeofenceEvents.SEND_ALERT_MESSAGE,
                                        new String[]
                                        {
                                            I18nProvider.getMessages().remoteServiceName(),
                                            e.getMessage()
                                        });
                                }
                                Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO,
                                    model);
                            }

                        });
                    
                    
                    
                    return priorityCustomField;
                }

            };

        return comboRendered;
    }

    /**
     * Creates the users combo box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<Rule> createUsersComboBox()
    {
        GridCellRenderer<Rule> comboRendered = new GridCellRenderer<Rule>()
            {

                private boolean init;

                public Object render(final Rule model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<Rule> store, Grid<Rule> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<Rule>>()
                            {

                                public void handleEvent(GridEvent<Rule> be)
                                {
                                    for (int i = 0; i < be.getGrid().getStore().getCount(); i++)
                                    {
                                        if ((be.getGrid().getView().getWidget(i, be.getColIndex()) != null) &&
                                                (be.getGrid().getView().getWidget(i, be.getColIndex()) instanceof BoxComponent))
                                        {
                                            ((BoxComponent) be.getGrid().getView().getWidget(i,
                                                    be.getColIndex())).setWidth(be.getWidth() - 10);
                                        }
                                    }
                                }
                            });
                    }

                    // TODO: generalize this!
                    ComboBox<GSUser> usersComboBox = new ComboBox<GSUser>();
                    usersComboBox.setId("ruleUsersCombo");
                    usersComboBox.setName("ruleUsersCombo");
                    usersComboBox.setEmptyText("(No profile available)");
                    usersComboBox.setDisplayField(BeanKeyValue.NAME.getValue());
                    usersComboBox.setEditable(false);
                    usersComboBox.setStore(getAvailableUsers());
                    usersComboBox.setTypeAhead(true);
                    usersComboBox.setTriggerAction(TriggerAction.ALL);
                    usersComboBox.setWidth(100);
                    usersComboBox.setMinListWidth(350);

                    if (model.getUser() != null)
                    {
                        usersComboBox.setValue(model.getUser());
                    }

                    usersComboBox.addListener(Events.Select, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                    new String[]
                                    {
                                        "GeoServer Rules",
                                        "Rule " + model.getPriority() + ": Rule changed"
                                    });

                                model.setUser((GSUser) be.getField().getValue());
                                Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO, model);
                            }
                        });

                    return usersComboBox;
                }

                /**
                 * TODO: Call User Service here!!
                 *
                 * @return
                 */
                private ListStore<GSUser> getAvailableUsers()
                {
                    RpcProxy<PagingLoadResult<GSUser>> userProxy = new RpcProxy<PagingLoadResult<GSUser>>()
                        {

                            @Override
                            protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<GSUser>> callback)
                            {
                                gsUsersService.getGsUsers(((PagingLoadConfig) loadConfig).getOffset(), ((PagingLoadConfig) loadConfig).getLimit(), true, callback);
                            }

                        };

                    BasePagingLoader<PagingLoadResult<ModelData>> usersLoader =
                        new BasePagingLoader<PagingLoadResult<ModelData>>(
                            userProxy);
                    usersLoader.setRemoteSort(false);

                    ListStore<GSUser> availableUsers = new ListStore<GSUser>(usersLoader);

                    return availableUsers;
                }
            };

        return comboRendered;
    }

    /*
     * (non-Javadoc)
     *
     * @see it.geosolutions.geofence.gui.client.form.GeofenceFormWidget#cancel()
     */
    @SuppressWarnings("deprecation")
    @Override
    public void cancel()
    {
        resetComponents();
        super.close();
        hide();
    }

    /**
     * Reset components.
     */
    public void resetComponents()
    {
        if ((grid != null) && (grid.getStore() != null))
        {
            this.grid.getStore().removeAll();
        }

        this.saveStatus.clearStatus("");

        if (!this.onExecute)
        {
            Dispatcher.forwardEvent(GeofenceEvents.LOAD_RULES);
        }

        this.onExecute = false;
    }

    /*
     * (non-Javadoc)
     *
     * @see it.geosolutions.geofence.gui.client.widget.AddGenericAOIWidget# addOtherComponents()
     */
    /**
     * Adds the other components.
     */
    public void addOtherComponents()
    {
    }

    /*
     * (non-Javadoc)
     *
     * @see it.geosolutions.geofence.gui.client.form.GeofenceFormWidget#initSize()
     */
    @Override
    public void initSize()
    {
        setHeading( /* TODO: I18nProvider.getMessages().addAoiDialogTitle() */"Edit rule");
        setSize(1205, 175);
    }

    /*
     * (non-Javadoc)
     *
     * @see it.geosolutions.geofence.gui.client.form.GeofenceFormWidget#initSizeFormPanel ()
     */
    @Override
    public void initSizeFormPanel()
    {
        formPanel.setHeaderVisible(false);
    }

    /*
     * (non-Javadoc)
     *
     * @see it.geosolutions.geofence.gui.client.form.GeofenceFormWidget#injectEvent()
     */
    @Override
    public void injectEvent()
    {
        Dispatcher.forwardEvent(getSubmitEvent(), this.model);
    }

    /**
     * Sets the rule service.
     *
     * @param rulesManagerServiceRemote
     *            the new rule service
     */
    public void setRuleService(RulesManagerRemoteServiceAsync rulesManagerServiceRemote)
    {
        this.rulesService = rulesManagerServiceRemote;
    }

    /**
     * Sets the user service.
     *
     * @param usersManagerServiceRemote
     *            the new user service
     */
    public void setUserService(GsUsersManagerRemoteServiceAsync usersManagerServiceRemote)
    {
        this.gsUsersService = usersManagerServiceRemote;
    }

    /**
     * Creates the profiles combo box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<Rule> createProfilesComboBox()
    {
        GridCellRenderer<Rule> comboRendered = new GridCellRenderer<Rule>()
            {

                private boolean init;

                public Object render(final Rule model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<Rule> store, Grid<Rule> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<Rule>>()
                            {

                                public void handleEvent(GridEvent<Rule> be)
                                {
                                    for (int i = 0; i < be.getGrid().getStore().getCount(); i++)
                                    {
                                        if ((be.getGrid().getView().getWidget(i, be.getColIndex()) != null) &&
                                                (be.getGrid().getView().getWidget(i, be.getColIndex()) instanceof BoxComponent))
                                        {
                                            ((BoxComponent) be.getGrid().getView().getWidget(i,
                                                    be.getColIndex())).setWidth(be.getWidth() - 10);
                                        }
                                    }
                                }
                            });
                    }

                    // TODO: generalize this!
                    ComboBox<UserGroup> profilesComboBox = new ComboBox<UserGroup>();
                    profilesComboBox.setId("ruleProfilesCombo");
                    profilesComboBox.setName("ruleProfilesCombo");
                    profilesComboBox.setEmptyText("(No profile available)");
                    profilesComboBox.setDisplayField(BeanKeyValue.NAME.getValue());
                    profilesComboBox.setEditable(false);
                    profilesComboBox.setStore(getAvailableProfiles());
                    profilesComboBox.setTypeAhead(true);
                    profilesComboBox.setTriggerAction(TriggerAction.ALL);
                    profilesComboBox.setWidth(100);
                    profilesComboBox.setMinListWidth(350);

                    if (model.getProfile() != null)
                    {
                        profilesComboBox.setValue(model.getProfile());
                        profilesComboBox.setSelection(Arrays.asList(model.getProfile()));
                    }

                    profilesComboBox.addListener(Events.Select, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                model.setProfile((UserGroup) be.getField().getValue());
                                Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO, model);
                            }
                        });

                    return profilesComboBox;
                }

                /**
                 * TODO: Call Profile Service here!!
                 *
                 * @return
                 */
                private ListStore<UserGroup> getAvailableProfiles()
                {
                    ListStore<UserGroup> availableProfiles = new ListStore<UserGroup>();
                    RpcProxy<PagingLoadResult<UserGroup>> profileProxy = new RpcProxy<PagingLoadResult<UserGroup>>()
                        {

                            @Override
                            protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<UserGroup>> callback)
                            {
                                profilesService.getProfiles(((PagingLoadConfig) loadConfig).getOffset(), ((PagingLoadConfig) loadConfig).getLimit(), true, callback);
                            }

                        };

                    BasePagingLoader<PagingLoadResult<ModelData>> profilesLoader =
                        new BasePagingLoader<PagingLoadResult<ModelData>>(
                            profileProxy);
                    profilesLoader.setRemoteSort(false);
                    
                    availableProfiles = new ListStore<UserGroup>(profilesLoader) {
                        protected void onLoad(LoadEvent le) {
                            super.onLoad(le);
                            this.sort(BeanKeyValue.NAME.getValue(),SortDir.ASC);
                            
                            ListStore<UserGroup> tmpStore = new ListStore<UserGroup>();
                            for(int i=0;i<this.getCount();++i) {
                                boolean present = false;
                                for(int j=0;j<tmpStore.getCount();++j) {
                                      if(tmpStore.getAt(j).getName().equals(this.getAt(i).getName())) {
                                          present = true;
                                          break;
                                      }
                                }
                                if(!present) {
                                    tmpStore.add(this.getAt(i));
                                }
                            }
                            this.removeAll();
                            for(int i=0;i<tmpStore.getCount();++i) {
                              this.add(tmpStore.getAt(i));
                            }
                        }
                  };
                  return availableProfiles;
                }
            };

        return comboRendered;
    }

    /**
     * Creates the instances combo box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<Rule> createInstancesComboBox()
    {
        GridCellRenderer<Rule> comboRendered = new GridCellRenderer<Rule>()
            {

                private boolean init;

                public Object render(final Rule model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<Rule> store, Grid<Rule> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<Rule>>()
                            {

                                public void handleEvent(GridEvent<Rule> be)
                                {
                                    for (int i = 0; i < be.getGrid().getStore().getCount(); i++)
                                    {
                                        if ((be.getGrid().getView().getWidget(i, be.getColIndex()) != null) &&
                                                (be.getGrid().getView().getWidget(i, be.getColIndex()) instanceof BoxComponent))
                                        {
                                            ((BoxComponent) be.getGrid().getView().getWidget(i,
                                                    be.getColIndex())).setWidth(be.getWidth() - 10);
                                        }
                                    }
                                }
                            });
                    }

                    // TODO: generalize this!
                    ComboBox<GSInstance> instancesComboBox = new ComboBox<GSInstance>();
                    instancesComboBox.setId("ruleInstancesCombo");
                    instancesComboBox.setName("ruleInstancesCombo");
                    instancesComboBox.setEmptyText("(No instances available)");
                    instancesComboBox.setDisplayField(BeanKeyValue.NAME.getValue());
                    instancesComboBox.setEditable(false);
                    instancesComboBox.setStore(getAvailableInstances());
                    instancesComboBox.setTypeAhead(true);
                    instancesComboBox.setTriggerAction(TriggerAction.ALL);
                    instancesComboBox.setWidth(120);

                    if (model.getInstance() != null)
                    {
                        instancesComboBox.setValue(model.getInstance());
                        instancesComboBox.setSelection(Arrays.asList(model.getInstance()));
                    }
                    else
                    {
                        GSInstance all = new GSInstance();
                        all.setId(-1);
                        all.setName("*");
                        all.setBaseURL("*");
                        instancesComboBox.setValue(all);
                        instancesComboBox.setSelection(Arrays.asList(all));
                    }

                    instancesComboBox.addListener(Events.Select, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                final GSInstance instance = (GSInstance) be.getField().getValue();

                                model.setInstance(instance);
                                model.setService("*");
                                model.setRequest("*");
                                model.setWorkspace("*");
                                model.setLayer("*");
                                
                                getAvailableWorkspaces(instance);
                                Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO, model);
                            }

                            /**
                             * TODO: Call User Service here!!
                             *
                             * @param rule
                             *
                             * @return
                             */
                            private ListStore<Workspace> getAvailableWorkspaces(final GSInstance gsInstance)
                            {
                                /*RpcProxy<PagingLoadResult<Workspace>> workspacesProxy = new RpcProxy<PagingLoadResult<Workspace>>()
                                    {

                                        @Override
                                        protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<Workspace>> callback)
                                        {
                                            workspacesService.getWorkspaces(((PagingLoadConfig) loadConfig).getOffset(), ((PagingLoadConfig) loadConfig).getLimit(),
                                                (gsInstance != null) ? gsInstance.getBaseURL() : null, gsInstance,
                                                callback);
                                            
                                        }

                                    };

                                BasePagingLoader<PagingLoadResult<ModelData>> workspacesLoader =
                                    new BasePagingLoader<PagingLoadResult<ModelData>>(
                                        workspacesProxy);
                                workspacesLoader.setRemoteSort(false);

                                ListStore<Workspace> availableWorkspaces = new ListStore<Workspace>(
                                        workspacesLoader);

                                return availableWorkspaces;*/
                                
                                initializeWorkspaces(gsInstance);
                                
                                return null;
                            }
                        });

                    return instancesComboBox;
                }

                /**
                 * TODO: Call Instance Service here!!
                 *
                 * @return
                 */
                private ListStore<GSInstance> getAvailableInstances()
                {
                    RpcProxy<PagingLoadResult<GSInstance>> gsInstancesProxy =
                        new RpcProxy<PagingLoadResult<GSInstance>>()
                        {

                            @Override
                            protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<GSInstance>> callback)
                            {
                                instancesService.getInstances(((PagingLoadConfig) loadConfig).getOffset(), ((PagingLoadConfig) loadConfig).getLimit(), true, callback);
                            }

                        };

                    BasePagingLoader<PagingLoadResult<ModelData>> gsInstancesLoader =
                        new BasePagingLoader<PagingLoadResult<ModelData>>(
                            gsInstancesProxy);
                    gsInstancesLoader.setRemoteSort(false);

                    ListStore<GSInstance> availableInstances = new ListStore<GSInstance>(
                            gsInstancesLoader);

                    return availableInstances;
                }
            };

        return comboRendered;
    }

    /**
     * Creates the services combo box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<Rule> createServicesComboBox()
    {
        GridCellRenderer<Rule> comboRendered = new GridCellRenderer<Rule>()
            {

                private boolean init;

                public Object render(final Rule model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<Rule> store, Grid<Rule> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<Rule>>()
                            {

                                public void handleEvent(GridEvent<Rule> be)
                                {
                                    for (int i = 0; i < be.getGrid().getStore().getCount(); i++)
                                    {
                                        if ((be.getGrid().getView().getWidget(i, be.getColIndex()) != null) &&
                                                (be.getGrid().getView().getWidget(i, be.getColIndex()) instanceof BoxComponent))
                                        {
                                            ((BoxComponent) be.getGrid().getView().getWidget(i,
                                                    be.getColIndex())).setWidth(be.getWidth() - 10);
                                        }
                                    }
                                }
                            });
                    }

                    // TODO: generalize this!
                    final ComboBox<Service> servicesComboBox = new ComboBox<Service>();
                    servicesComboBox.setId("ruleServicesCombo");
                    servicesComboBox.setName("ruleServicesCombo");
                    servicesComboBox.setEmptyText("(No service available)");
                    servicesComboBox.setDisplayField(BeanKeyValue.SERVICE.getValue());
                    servicesComboBox.setStore(getAvailableServices(model.getInstance()));
                    servicesComboBox.setEditable(true);
                    servicesComboBox.setTypeAhead(true);
                    servicesComboBox.setTriggerAction(TriggerAction.ALL);
                    servicesComboBox.setWidth(90);

                    KeyListener keyListener = new KeyListener()
                        {

                            @Override
                            public void componentKeyUp(ComponentEvent event)
                            {
                                if (event.getKeyCode() == '\r')
                                {
                                    event.cancelBubble();

                                    model.setService(servicesComboBox.getRawValue());
                                    //model.setRequest("*");
                                    Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO,
                                        model);
                                }
                            }
                        };

                    servicesComboBox.addKeyListener(keyListener);

                    servicesComboBox.addListener(Events.Blur, new Listener<FieldEvent>()
                            {

                                public void handleEvent(FieldEvent be)
                                {
                                    model.setService(servicesComboBox.getRawValue());
                                    //model.setRequest("*");
                                    Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO,
                                        model);
                                }

                            });
                    
                    if (model.getService() != null)
                    {
                        servicesComboBox.setValue(new Service(model.getService()));
                        servicesComboBox.setSelection(Arrays.asList(new Service(model.getService())));
                    }

                    servicesComboBox.addListener(Events.Select, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                final Service service = (Service) be.getField().getValue();

                                model.setService(service.getService());
                                model.setRequest("*");
                                Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO, model);
                            }

                        });

                    return servicesComboBox;
                }

                /**
                 * TODO: Call Services Service here!!
                 *
                 * @param gsInstance
                 *
                 * @return
                 */
                private ListStore<Service> getAvailableServices(GSInstance gsInstance)
                {
                    ListStore<Service> availableServices = new ListStore<Service>();
                    List<Service> services = new ArrayList<Service>();

                    Service all = new Service("*");

                    services.add(all);

                    if ((gsInstance != null) && (gsInstance.getBaseURL() != null) &&
                            !gsInstance.getBaseURL().equals("*"))
                    {
                        Service wms = new Service("WMS");
                        Service wcs = new Service("WCS");
                        Service wfs = new Service("WFS");
                        Service gwc = new Service("GWC");

                        services.add(wms);
                        services.add(wcs);
                        services.add(wfs);
                        services.add(gwc);
                    }

                    availableServices.add(services);

                    return availableServices;
                }
            };

        return comboRendered;
    }

    /**
     * Creates the services request combo box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<Rule> createServicesRequestComboBox()
    {
        GridCellRenderer<Rule> comboRendered = new GridCellRenderer<Rule>()
            {

                private boolean init;

                public Object render(final Rule model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<Rule> store, Grid<Rule> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<Rule>>()
                            {

                                public void handleEvent(GridEvent<Rule> be)
                                {
                                    for (int i = 0; i < be.getGrid().getStore().getCount(); i++)
                                    {
                                        if ((be.getGrid().getView().getWidget(i, be.getColIndex()) != null) &&
                                                (be.getGrid().getView().getWidget(i, be.getColIndex()) instanceof BoxComponent))
                                        {
                                            ((BoxComponent) be.getGrid().getView().getWidget(i,
                                                    be.getColIndex())).setWidth(be.getWidth() - 10);
                                        }
                                    }
                                }
                            });
                    }

                    // TODO: generalize this!
                    final ComboBox<Request> serviceRequestsComboBox = new ComboBox<Request>();
                    serviceRequestsComboBox.setId("ruleServicesRequestCombo");
                    serviceRequestsComboBox.setName("ruleServicesRequestCombo");
                    serviceRequestsComboBox.setEmptyText("(No service request available)");
                    serviceRequestsComboBox.setDisplayField(BeanKeyValue.REQUEST.getValue());
                    serviceRequestsComboBox.setStore(getAvailableServicesRequest(model.getInstance(),
                            model.getService()));
                    serviceRequestsComboBox.setEditable(true);
                    serviceRequestsComboBox.setTypeAhead(true);
                    serviceRequestsComboBox.setTriggerAction(TriggerAction.ALL);
                    serviceRequestsComboBox.setWidth(150);

                    KeyListener keyListener = new KeyListener()
                        {

                            @Override
                            public void componentKeyUp(ComponentEvent event)
                            {
                                if (event.getKeyCode() == '\r')
                                {
                                    event.cancelBubble();

                                    model.setRequest(serviceRequestsComboBox.getRawValue());
                                    Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO,
                                        model);
                                }
                            }
                        };

                    serviceRequestsComboBox.addKeyListener(keyListener);

                    serviceRequestsComboBox.addListener(Events.Blur, new Listener<FieldEvent>()
                            {

                                public void handleEvent(FieldEvent be)
                                {
                                    model.setRequest(serviceRequestsComboBox.getRawValue());
                                    Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO, model);
                                }
                            });

                    if (model.getService() != null)
                    {
                        serviceRequestsComboBox.setValue(new Request(model.getRequest()));
                        serviceRequestsComboBox.setSelection(Arrays.asList(new Request(model.getRequest())));
                    }

                    serviceRequestsComboBox.addListener(Events.Select, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                final Request request = (Request) be.getField().getValue();

                                model.setRequest(request.getRequest());
                                Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO, model);
                            }
                        });

                    return serviceRequestsComboBox;
                }

                /**
                 * TODO: Call Services Service here!!
                 *
                 * @param gsInstance
                 * @param rulesService
                 *
                 * @return
                 */
                private ListStore<Request> getAvailableServicesRequest(GSInstance gsInstance,
                    String service)
                {
                    ListStore<Request> availableServicesRequests = new ListStore<Request>();
                    List<Request> requests = new ArrayList<Request>();

                    Request all = new Request("*");
                    Request getCapabilities = new Request("GetCapabilities");

                    requests.add(all);
                    requests.add(getCapabilities);

                    if ((service != null) && service.equalsIgnoreCase("WMS"))
                    {
                        Request getMap = new Request("GetMap");
                        Request getFeatureInfo = new Request("GetFeatureInfo");
                        Request describeLayer = new Request("DescribeLayer");

                        requests.add(getMap);
                        requests.add(getFeatureInfo);
                        requests.add(describeLayer);
                    }

                    if ((service != null) && service.equalsIgnoreCase("WCS"))
                    {
                        Request getCoverage = new Request("GetCoverage");
                        Request describeCoverage = new Request("DescribeCoverage");

                        requests.add(getCoverage);
                        requests.add(describeCoverage);
                    }

                    if ((service != null) && service.equalsIgnoreCase("WFS"))
                    {
                        Request getFeatureType = new Request("GetFeatureType");
                        Request describeFeatureType = new Request("DescribeFeatureType");
                        Request getFeature = new Request("GetFeature");
                        Request getGmlObject = new Request("GetGmlObject");
                        Request lockFeature = new Request("LockFeature");
                        Request getFeatureWithLock = new Request("GetFeatureWithLock");
                        Request transaction = new Request("Transaction");

                        requests.add(getFeatureType);
                        requests.add(describeFeatureType);
                        requests.add(getFeature);
                        requests.add(getGmlObject);
                        requests.add(lockFeature);
                        requests.add(getFeatureWithLock);
                        requests.add(transaction);
                    }

                    availableServicesRequests.add(requests);

                    return availableServicesRequests;
                }
            };

        return comboRendered;
    }

    /**
     * Creates the service workspaces combo box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<Rule> createServiceWorkspacesComboBox()
    {
        GridCellRenderer<Rule> comboRendered = new GridCellRenderer<Rule>()
            {

                private boolean init;
                
                public Object render(final Rule model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<Rule> store, Grid<Rule> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<Rule>>()
                            {

                                public void handleEvent(GridEvent<Rule> be)
                                {
                                    for (int i = 0; i < be.getGrid().getStore().getCount(); i++)
                                    {
                                        if ((be.getGrid().getView().getWidget(i, be.getColIndex()) != null) &&
                                                (be.getGrid().getView().getWidget(i, be.getColIndex()) instanceof BoxComponent))
                                        {
                                            ((BoxComponent) be.getGrid().getView().getWidget(i,
                                                    be.getColIndex())).setWidth(be.getWidth() - 10);
                                        }
                                    }
                                }
                            });
                    }

                    // TODO: generalize this!
                    ComboBox<Workspace> workspacesComboBox = new ComboBox<Workspace>();
                    workspacesComboBox.setId("ruleWorkspacesCombo");
                    workspacesComboBox.setName("ruleWorkspacesCombo");

                    workspacesComboBox.setDisplayField(BeanKeyValue.WORKSPACE.getValue());
                    workspacesComboBox.setStore(workspaces);
                    workspacesComboBox.setEditable(true);
                    workspacesComboBox.setTypeAhead(true);
                    workspacesComboBox.setTriggerAction(TriggerAction.ALL);
                    workspacesComboBox.setWidth(120);
                    workspacesComboBox.setMinListWidth(500);

                    if (model.getWorkspace() != null)
                    {
                        workspacesComboBox.setValue(new Workspace(model.getWorkspace()));
                        workspacesComboBox.setSelection(Arrays.asList(new Workspace(model.getWorkspace())));
                    }
                    workspacesComboBox.setEmptyText("(No workspace available)");
                    workspacesComboBox.addListener(Events.Select, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                final Workspace workspace = (Workspace) be.getField().getValue();

                                model.setWorkspace(workspace.getWorkspace());
                                model.setLayer("*");
                                getAvailableLayers(model.getInstance(), model.getWorkspace(), model.getService());
                                Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO, model);
                            }

                            /**
                             * TODO: Call User Service here!!
                             *
                             * @param workspace
                             * @param rule
                             *
                             * @return
                             */
                            private ListStore<Layer> getAvailableLayers(final GSInstance gsInstance,
                                final String workspace, final String service)
                            {
//                                RpcProxy<PagingLoadResult<Layer>> workspacesProxy = new RpcProxy<PagingLoadResult<Layer>>()
//                                    {
//
//                                        @Override
//                                        protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<Layer>> callback)
//                                        {
//                                            workspacesService.getLayers(((PagingLoadConfig) loadConfig).getOffset(), ((PagingLoadConfig) loadConfig).getLimit(),
//                                                (gsInstance != null) ? gsInstance.getBaseURL() : null, gsInstance,
//                                                workspace, service, callback);
//                                        }
//
//                                    };
//
//                                BasePagingLoader<PagingLoadResult<ModelData>> workspacesLoader =
//                                    new BasePagingLoader<PagingLoadResult<ModelData>>(
//                                        workspacesProxy);
//                                workspacesLoader.setRemoteSort(false);
//
//                                ListStore<Layer> availableWorkspaceLayers = new ListStore<Layer>(workspacesLoader);
//
//                                return availableWorkspaceLayers;
                                
                                initializeLayers(gsInstance, workspace, service);
                                
                                return null;
                            }

                        });

                    return workspacesComboBox;
                }
            };

        return comboRendered;
    }

    /**
     * Creates the workspaces layers combo box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<Rule> createWorkspacesLayersComboBox()
    {
        GridCellRenderer<Rule> comboRendered = new GridCellRenderer<Rule>()
            {

                private boolean init;

                public Object render(final Rule model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<Rule> store, Grid<Rule> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<Rule>>()
                            {

                                public void handleEvent(GridEvent<Rule> be)
                                {
                                    for (int i = 0; i < be.getGrid().getStore().getCount(); i++)
                                    {
                                        if ((be.getGrid().getView().getWidget(i, be.getColIndex()) != null) &&
                                                (be.getGrid().getView().getWidget(i, be.getColIndex()) instanceof BoxComponent))
                                        {
                                            ((BoxComponent) be.getGrid().getView().getWidget(i,
                                                    be.getColIndex())).setWidth(be.getWidth() - 10);
                                        }
                                    }
                                }
                            });
                    }

                    // TODO: generalize this!
                    ComboBox<Layer> workspaceLayersComboBox = new ComboBox<Layer>();
                    workspaceLayersComboBox.setId("ruleLayersCombo");
                    workspaceLayersComboBox.setName("ruleLayersCombo");

                    workspaceLayersComboBox.setDisplayField(BeanKeyValue.LAYER.getValue());
                    workspaceLayersComboBox.setStore(layers);
                    workspaceLayersComboBox.setEditable(true);
                    workspaceLayersComboBox.setTypeAhead(true);
                    workspaceLayersComboBox.setTriggerAction(TriggerAction.ALL);
                    workspaceLayersComboBox.setWidth(120);
                    workspaceLayersComboBox.setMinListWidth(350);

                    if (model.getLayer() != null)
                    {
                        workspaceLayersComboBox.setValue(new Layer(model.getLayer()));
                        workspaceLayersComboBox.setSelection(Arrays.asList(new Layer(model.getLayer())));
                    }
                    workspaceLayersComboBox.setEmptyText("(No layer available)");
                    workspaceLayersComboBox.addListener(Events.Select, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                final Layer layer = (Layer) be.getField().getValue();

                                model.setLayer(layer.getLayer());
                                Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO, model);
                            }
                        });

                    return workspaceLayersComboBox;
                }
            };

        return comboRendered;
    }

    /**
     * Creates the grants combo box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<Rule> createGrantsComboBox()
    {
        GridCellRenderer<Rule> comboRendered = new GridCellRenderer<Rule>()
            {

                private boolean init;

                public Object render(final Rule model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<Rule> store, Grid<Rule> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<Rule>>()
                            {

                                public void handleEvent(GridEvent<Rule> be)
                                {
                                    for (int i = 0; i < be.getGrid().getStore().getCount(); i++)
                                    {
                                        if ((be.getGrid().getView().getWidget(i, be.getColIndex()) != null) &&
                                                (be.getGrid().getView().getWidget(i, be.getColIndex()) instanceof BoxComponent))
                                        {
                                            ((BoxComponent) be.getGrid().getView().getWidget(i,
                                                    be.getColIndex())).setWidth(be.getWidth() - 10);
                                        }
                                    }
                                }
                            });
                    }

                    // TODO: generalize this!
                    final ComboBox<Grant> grantsComboBox = new ComboBox<Grant>();
                    grantsComboBox.setId("grantsCombo");
                    grantsComboBox.setName("grantsCombo");

                    grantsComboBox.setDisplayField(BeanKeyValue.GRANT.getValue());
                    grantsComboBox.setEditable(false);
                    grantsComboBox.setStore(getAvailableGrants());
                    grantsComboBox.setTypeAhead(true);
                    grantsComboBox.setTriggerAction(TriggerAction.ALL);
                    grantsComboBox.setWidth(70);

                    if (model.getGrant() != null)
                    {
                        grantsComboBox.setValue(new Grant(model.getGrant()));
                        grantsComboBox.setSelection(Arrays.asList(new Grant(model.getGrant())));
                    }
                    grantsComboBox.setEmptyText("(No grant types available)");
                    grantsComboBox.addListener(Events.Select, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                final Grant grant = (Grant) be.getField().getValue();

                                final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>()
                                    {
                                        public void handleEvent(MessageBoxEvent ce)
                                        {
                                            Button btn = ce.getButtonClicked();

                                            if (btn.getText().equalsIgnoreCase("Yes"))
                                            {
                                                model.setGrant(grant.getGrant());
                                                Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO,
                                                    model);

                                                Info.display("MessageBox", "Rule Grant changed", btn.getText());
                                            }
                                            else
                                            {
                                                grantsComboBox.setValue(new Grant(model.getGrant()));
                                            }
                                        }
                                    };

                                if (!grant.getGrant().equalsIgnoreCase(model.getGrant()) && (model.getId() != -1))
                                {
                                    MessageBox.confirm("Confirm",
                                        "Grant is changed. Saving the rule the details or rule limits will be deleted. Are you sure you want to do that?",
                                        l);
                                }
                                else
                                {
                                    model.setGrant(grant.getGrant());
                                    Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO, model);
                                }

                            }
                        });

                    return grantsComboBox;
                }

                /**
                 * TODO: Call User Service here!!
                 *
                 * @param workspace
                 * @param rule
                 *
                 * @return
                 */
                private ListStore<Grant> getAvailableGrants()
                {
                    ListStore<Grant> availableRuleGrants = new ListStore<Grant>();
                    List<Grant> grants = new ArrayList<Grant>();

                    Grant allow = new Grant("ALLOW");
                    Grant deny = new Grant("DENY");
                    Grant limit = new Grant("LIMIT");

                    grants.add(allow);
                    grants.add(deny);
                    grants.add(limit);

                    availableRuleGrants.add(grants);

                    return availableRuleGrants;
                }
            };

        return comboRendered;
    }

    /**
     * Creates the store.
     */
    public void createStore()
    {
        store = new ListStore<Rule>();
        if (store != null)
        {
            store.setSortField(BeanKeyValue.PRIORITY.getValue());
            store.setSortDir(SortDir.ASC);
        }

        setUpLoadListener();
    }

    /**
     * Gets the loader.
     *
     * @return the loader
     */
    public PagingLoader<PagingLoadResult<ModelData>> getLoader()
    {
        return loader;
    }

    /**
     * Clear grid elements.
     */
    public void clearGridElements()
    {
        this.store.removeAll();
    }

    /**
     * Sets the up load listener.
     */
    private void setUpLoadListener()
    {
        loader.addLoadListener(new LoadListener()
            {

                @Override
                public void loaderBeforeLoad(LoadEvent le)
                {
                    /*
                     * if (!toolBar.isEnabled()) toolBar.enable();
                     */
                }

                @Override
                public void loaderLoad(LoadEvent le)
                {

                    // TODO: change messages here!!

                    BasePagingLoadResult<?> result = le.getData();
                    if (!result.getData().isEmpty())
                    {
                        int size = result.getData().size();
                        String message = "";
                        if (size == 1)
                        {
                            message = I18nProvider.getMessages().recordLabel();
                        }
                        else
                        {
                            message = I18nProvider.getMessages().recordPluralLabel();
                        }
                        Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                            new String[]
                            {
                                I18nProvider.getMessages().remoteServiceName(),
                                I18nProvider.getMessages().foundLabel() + " " + result.getData().size() +
                                " " + message
                            });
                    }
                    else
                    {
                        Dispatcher.forwardEvent(GeofenceEvents.SEND_ALERT_MESSAGE,
                            new String[]
                            {
                                I18nProvider.getMessages().remoteServiceName(),
                                I18nProvider.getMessages().recordNotFoundMessage()
                            });
                    }
                }

            });
    }

    /**
     * Sets the grid properties.
     */
    public void setGridProperties()
    {

        grid.setLoadMask(true);
        grid.setAutoWidth(true);
        if (grid.getStore() != null)
        {
            grid.getStore().setSortField(BeanKeyValue.PRIORITY.getValue());
            grid.getStore().setSortDir(SortDir.ASC);
        }
    }

    /**
     * Inits the grid.
     */
    public void initGrid()
    {
        ColumnModel cm = prepareColumnModel();

        grid = new Grid<Rule>(store, cm);
        grid.setBorders(true);

        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.setHeight("70px");
        grid.setLazyRowRender(0);
        setGridProperties();
    }

    /**
     * Inits the grid.
     *
     * @param store
     *            the store
     */
    public void initGrid(ListStore<Rule> store)
    {
        ColumnModel cm = prepareColumnModel();

        grid = new Grid<Rule>(store, cm);
        grid.setBorders(true);

        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.setHeight("70px");
        grid.setLazyRowRender(0);
        setGridProperties();
    }

    /**
     * Sets the profile.
     *
     * @param model
     *            the new profile
     */
    public void setModel(Rule model)
    {
        this.model = model;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.widget.Component#getModel()
     */
    @SuppressWarnings({ "unchecked" })
    public Rule getModel()
    {
        return model;
    }

    /**
     * Sets the instance service.
     *
     * @param instancesManagerServiceRemote
     *            the new instance service
     */
    public void setInstanceService(InstancesManagerRemoteServiceAsync instancesManagerServiceRemote)
    {
        this.instancesService = instancesManagerServiceRemote;
    }

    /**
     * Sets the workspace service.
     *
     * @param workspacesManagerServiceRemote
     *            the new workspace service
     */
    public void setWorkspaceService(
        WorkspacesManagerRemoteServiceAsync workspacesManagerServiceRemote)
    {
        this.workspacesService = workspacesManagerServiceRemote;
    }

    /**
     * Sets the profile service.
     *
     * @param profilesManagerServiceRemote
     *            the new profile service
     */
    public void setProfileService(ProfilesManagerRemoteServiceAsync profilesManagerServiceRemote)
    {
        this.profilesService = profilesManagerServiceRemote;
    }

    /**
     * Sets the gs user service.
     *
     * @param usersManagerServiceRemote
     *            the new gs user service
     */
    public void setGsUserService(GsUsersManagerRemoteServiceAsync usersManagerServiceRemote)
    {
        this.gsUsersService = usersManagerServiceRemote;
    }

    /**
     * Gets the RuleGridWidget.
     *
     * @return the RuleGridWidget
     */
    public Grid<Rule> getParentGrid()
    {
        return parentGrid;
    }

    /**
     * Sets the RuleGridWidget.
     *
     * @param parentGrid
     *            the new RuleGridWidget
     */
    public void setParentGrid(Grid<Rule> parentGrid)
    {
        this.parentGrid = parentGrid;
    }

    /**
     * Gets the rules view.
     *
     * @return the rules view
     */
    public RulesView getRulesView()
    {
        return rulesView;
    }

    /**
     * Sets the rules view.
     *
     * @param rulesView
     *            the new rules view
     */
    public void setRulesView(RulesView rulesView)
    {
        this.rulesView = rulesView;
    }
    
    /**
     * @param gsInstance
     */
    private void initializeWorkspaces(
            final GSInstance gsInstance) {
        workspacesService.getWorkspaces(0, 0, (gsInstance != null) ? gsInstance.getBaseURL() : null, gsInstance,
                new AsyncCallback<PagingLoadResult<Workspace>>() {

                    public void onFailure(Throwable caught) {
                        workspaces.removeAll();
                    }

                    public void onSuccess(PagingLoadResult<Workspace> result) {
                        workspaces.removeAll();
                        workspaces.add(result.getData());
                        workspaces.sort("workspace", Style.SortDir.ASC);
                        
                        initializeLayers(gsInstance, model.getWorkspace(), model.getService());
                    }
                });
    }
    
    /**
     * @param gsInstance
     * @param workspace
     * @param service
     */
    private void initializeLayers(
            final GSInstance gsInstance,
            final String workspace, final String service) {
        workspacesService.getLayers(0, 0, (gsInstance != null) ? gsInstance.getBaseURL() : null, gsInstance, workspace, service, 
                new AsyncCallback<PagingLoadResult<Layer>>() {

            public void onFailure(Throwable caught) {
                layers.removeAll();
            }

            public void onSuccess(PagingLoadResult<Layer> result) {
                layers.removeAll();
                layers.add(result.getData());
    			layers.sort("layer", Style.SortDir.ASC);
            }
        });
    }
}

/*
 * $ Header: it.geosolutions.geofence.gui.client.widget.RuleGridWidget,v. 0.1 25-feb-2011 16.31.40 created by afabiani <alessio.fabiani at geo-solutions.it> $
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

import it.geosolutions.geofence.gui.client.Constants;
import it.geosolutions.geofence.gui.client.GeofenceEvents;
import it.geosolutions.geofence.gui.client.Resources;
import it.geosolutions.geofence.gui.client.i18n.I18nProvider;
import it.geosolutions.geofence.gui.client.model.BeanKeyValue;
import it.geosolutions.geofence.gui.client.model.UserGroup;
import it.geosolutions.geofence.gui.client.model.Rule;
import it.geosolutions.geofence.gui.client.service.GsUsersManagerRemoteServiceAsync;
import it.geosolutions.geofence.gui.client.service.InstancesManagerRemoteServiceAsync;
import it.geosolutions.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;
import it.geosolutions.geofence.gui.client.service.RulesManagerRemoteServiceAsync;
import it.geosolutions.geofence.gui.client.service.WorkspacesManagerRemoteServiceAsync;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.google.gwt.user.client.rpc.AsyncCallback;

// TODO: Auto-generated Javadoc
/**
 * The Class RuleGridWidget.
 */
public class RuleGridWidget extends GeofenceGridWidget<Rule> {

	/** The Constant COLUMN_PRIORITY_WIDTH. */
	private static final int COLUMN_PRIORITY_WIDTH = 30;

	/** The Constant COLUMN_USER_WIDTH. */
	private static final int COLUMN_USER_WIDTH = 80; // 130;

	/** The Constant COLUMN_PROFILE_WIDTH. */
	private static final int COLUMN_PROFILE_WIDTH = 180; // 160;

	/** The Constant COLUMN_INSTANCE_WIDTH. */
	private static final int COLUMN_INSTANCE_WIDTH = 80; // 60;

	/** The Constant COLUMN_SERVICE_WIDTH. */
	private static final int COLUMN_SERVICE_WIDTH = 60; // 100;

	/** The Constant COLUMN_REQUEST_WIDTH. */
	private static final int COLUMN_REQUEST_WIDTH = 150; // 190;

	/** The Constant COLUMN_WORKSPACE_WIDTH. */
	private static final int COLUMN_WORKSPACE_WIDTH = 180; // 130;

	/** The Constant COLUMN_LAYER_WIDTH. */
	private static final int COLUMN_LAYER_WIDTH = 180; // 130;

	/** The Constant COLUMN_GRANT_WIDTH. */
	private static final int COLUMN_GRANT_WIDTH = 80; // 100;

	/** The Constant COLUMN_EDIT_RULE_WIDTH. */
	private static final int COLUMN_EDIT_RULE_WIDTH = 80;

	/** The Constant COLUMN_RULE_DETAILS_WIDTH. */
	private static final int COLUMN_RULE_DETAILS_WIDTH = 80;

	/** The Constant COLUMN_REMOVE_RULE_WIDTH. */
	private static final int COLUMN_REMOVE_RULE_WIDTH = 80;

	/** The Constant COLUMN_ADD_RULE_WIDTH. */
	private static final int COLUMN_ADD_RULE_WIDTH = 30;

	/** The Constant COLUMN_UP_RULE_WIDTH. */
	private static final int COLUMN_UP_RULE_WIDTH = 30;

	/** The Constant COLUMN_DOWN_RULE_WIDTH. */
	private static final int COLUMN_DOWN_RULE_WIDTH = 30;

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

	/** The proxy. */
	private RpcProxy<PagingLoadResult<Rule>> proxy;

	/** The loader. */
	private PagingLoader<PagingLoadResult<ModelData>> loader;

	/** The tool bar. */
	private PagingToolBar toolBar;

	/** The edit rule widget. */
	private EditRuleWidget editRuleWidget;

	/** The parent edit rule widget. */
	public RuleGridWidget parentEditRuleWidget;

	/** The button rendered. */
	private GridCellRenderer<Rule> buttonRendered;
	
	/** grid filters **/
	private String groupFilter;
	private String wsFilter;
	private String layerFilter;
	/**
	 * Instantiates a new rule grid widget.
	 * 
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
	public RuleGridWidget(RulesManagerRemoteServiceAsync rulesService,
			GsUsersManagerRemoteServiceAsync gsUsersService,
			ProfilesManagerRemoteServiceAsync profilesService,
			InstancesManagerRemoteServiceAsync instancesService,
			WorkspacesManagerRemoteServiceAsync workspacesService) {
		super();
		this.rulesService = rulesService;
		this.gsUsersService = gsUsersService;
		this.profilesService = profilesService;
		this.instancesService = instancesService;
		this.workspacesService = workspacesService;
		this.parentEditRuleWidget = this;
	}

	/**
	 * Instantiates a new rule grid widget.
	 * 
	 * @param models
	 *            the models
	 */
	public RuleGridWidget(List<Rule> models) {
		super(models);
		if (this.parentEditRuleWidget == null) {
			this.parentEditRuleWidget = this;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.geosolutions.geofence.gui.client.widget.GEOFENCEGridWidget#
	 * setGridProperties ()
	 */
	@Override
	public void setGridProperties() {
		grid.setHeight(Constants.SOUTH_PANEL_DIMENSION - 25);

		if (grid.getStore() != null) {
			grid.getStore().setSortField(BeanKeyValue.PRIORITY.getValue());
			grid.getStore().setSortDir(SortDir.ASC);
		}

		grid.addListener(Events.RowDoubleClick,
				new Listener<GridEvent<Rule>>() {
					public void handleEvent(GridEvent<Rule> be) {
						Rule ruleModel = be.getModel();
						Dispatcher.forwardEvent(
								GeofenceEvents.EDIT_RULE_UPDATE,
								new GridStatus(grid, ruleModel));
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.geosolutions.geofence.gui.client.widget.GEOFENCEGridWidget#
	 * prepareColumnModel()
	 */
	@Override
	public ColumnModel prepareColumnModel() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig rulePriorityColumn = new ColumnConfig();
		rulePriorityColumn.setId(BeanKeyValue.PRIORITY.getValue());
		rulePriorityColumn.setWidth(COLUMN_PRIORITY_WIDTH);
		rulePriorityColumn.setMenuDisabled(false);
		rulePriorityColumn.setSortable(true);
		configs.add(rulePriorityColumn);

		ColumnConfig ruleUserColumn = new ColumnConfig();
		ruleUserColumn.setId(BeanKeyValue.USER.getValue());
		ruleUserColumn.setHeader("User");
		ruleUserColumn.setWidth(COLUMN_USER_WIDTH);
		ruleUserColumn.setRenderer(this.createUsersCustomField()); // CustomField//createUsersComboBox
		ruleUserColumn.setMenuDisabled(false);
		ruleUserColumn.setSortable(true);
		configs.add(ruleUserColumn);

		ColumnConfig ruleProfileColumn = new ColumnConfig();
		ruleProfileColumn.setId(BeanKeyValue.PROFILE.getValue());
		ruleProfileColumn.setHeader("Group");
		ruleProfileColumn.setWidth(COLUMN_PROFILE_WIDTH);
		ruleProfileColumn.setRenderer(this.createProfilesCustomField()); // createProfilesComboBox
		ruleProfileColumn.setMenuDisabled(false);
		ruleProfileColumn.setSortable(true);
		configs.add(ruleProfileColumn);

		ColumnConfig ruleInstanceColumn = new ColumnConfig();
		ruleInstanceColumn.setId(BeanKeyValue.INSTANCE.getValue());
		ruleInstanceColumn.setHeader("Instance");
		ruleInstanceColumn.setWidth(COLUMN_INSTANCE_WIDTH);
		ruleInstanceColumn.setRenderer(this.createInstancesCustomField()); // createInstancesComboBox
		ruleInstanceColumn.setMenuDisabled(false);
		ruleInstanceColumn.setSortable(true);
		configs.add(ruleInstanceColumn);

		ColumnConfig ruleServiceColumn = new ColumnConfig();
		ruleServiceColumn.setId(BeanKeyValue.SERVICE.getValue());
		ruleServiceColumn.setHeader("Service");
		ruleServiceColumn.setWidth(COLUMN_SERVICE_WIDTH);
		ruleServiceColumn.setRenderer(this.createServicesCustomField()); // createServicesComboBox
		ruleServiceColumn.setMenuDisabled(true);
		ruleServiceColumn.setSortable(false);
		configs.add(ruleServiceColumn);

		ColumnConfig ruleServiceRequestColumn = new ColumnConfig();
		ruleServiceRequestColumn.setId(BeanKeyValue.REQUEST.getValue());
		ruleServiceRequestColumn.setHeader("Request");
		ruleServiceRequestColumn.setWidth(COLUMN_REQUEST_WIDTH);
		ruleServiceRequestColumn.setRenderer(this
				.createServicesRequestCustomField()); // createServicesRequestComboBox
		ruleServiceRequestColumn.setMenuDisabled(false);
		ruleServiceRequestColumn.setSortable(true);
		configs.add(ruleServiceRequestColumn);

		ColumnConfig ruleServiceWorkspacesColumn = new ColumnConfig();
		ruleServiceWorkspacesColumn.setId(BeanKeyValue.WORKSPACE.getValue());
		ruleServiceWorkspacesColumn.setHeader("Workspace");
		ruleServiceWorkspacesColumn.setWidth(COLUMN_WORKSPACE_WIDTH);
		ruleServiceWorkspacesColumn.setRenderer(this
				.createServiceWorkspacesCustomField());
		ruleServiceWorkspacesColumn.setMenuDisabled(false);
		ruleServiceWorkspacesColumn.setSortable(true);
		configs.add(ruleServiceWorkspacesColumn);

		ColumnConfig ruleWorkspaceLayersColumn = new ColumnConfig();
		ruleWorkspaceLayersColumn.setId(BeanKeyValue.LAYER.getValue());
		ruleWorkspaceLayersColumn.setHeader("Layer");
		ruleWorkspaceLayersColumn.setWidth(COLUMN_LAYER_WIDTH);
		ruleWorkspaceLayersColumn.setRenderer(this
				.createWorkspacesLayersCustomField());
		ruleWorkspaceLayersColumn.setMenuDisabled(false);
		ruleWorkspaceLayersColumn.setSortable(true);
		configs.add(ruleWorkspaceLayersColumn);

		ColumnConfig ruleGrantsColumn = new ColumnConfig();
		ruleGrantsColumn.setId(BeanKeyValue.GRANT.getValue());
		ruleGrantsColumn.setHeader("Grant");
		ruleGrantsColumn.setWidth(COLUMN_GRANT_WIDTH);
		ruleGrantsColumn.setRenderer(this.createGrantsCustomField());
		ruleGrantsColumn.setMenuDisabled(false);
		ruleGrantsColumn.setSortable(true);
		configs.add(ruleGrantsColumn);

		ColumnConfig editRuleColumn = new ColumnConfig();
		editRuleColumn.setId("editRuleDetails");
		editRuleColumn.setWidth(COLUMN_EDIT_RULE_WIDTH);
		editRuleColumn.setRenderer(this.editRuleButton());
		editRuleColumn.setMenuDisabled(true);
		editRuleColumn.setSortable(false);
		configs.add(editRuleColumn);

		ColumnConfig detailsActionColumn = new ColumnConfig();
		detailsActionColumn.setId("ruleDetails");
		detailsActionColumn.setWidth(COLUMN_RULE_DETAILS_WIDTH);
		detailsActionColumn.setRenderer(this.createRuleDetailsButton());
		detailsActionColumn.setMenuDisabled(true);
		detailsActionColumn.setSortable(false);
		configs.add(detailsActionColumn);

		ColumnConfig removeActionColumn = new ColumnConfig();
		removeActionColumn.setId("removeRule");
		removeActionColumn.setWidth(COLUMN_REMOVE_RULE_WIDTH);
		removeActionColumn.setRenderer(this.createRuleDeleteButton());
		removeActionColumn.setMenuDisabled(true);
		removeActionColumn.setSortable(false);
		configs.add(removeActionColumn);

		ColumnConfig addActionColumn = new ColumnConfig();
		addActionColumn.setId("addRule");
		addActionColumn.setWidth(COLUMN_ADD_RULE_WIDTH);
		addActionColumn.setRenderer(this.createRuleAddButton());
		addActionColumn.setMenuDisabled(true);
		addActionColumn.setSortable(false);
		configs.add(addActionColumn);

		ColumnConfig priorityUpActionColumn = new ColumnConfig();
		priorityUpActionColumn.setId("rulePriorityUp");
		priorityUpActionColumn.setWidth(COLUMN_UP_RULE_WIDTH);
		priorityUpActionColumn.setRenderer(this.createRulePriorityUpButton());
		priorityUpActionColumn.setMenuDisabled(true);
		priorityUpActionColumn.setSortable(false);
		configs.add(priorityUpActionColumn);

		ColumnConfig priorityDownActionColumn = new ColumnConfig();
		priorityDownActionColumn.setId("rulePriorityDwn");
		priorityDownActionColumn.setWidth(COLUMN_DOWN_RULE_WIDTH);
		priorityDownActionColumn.setRenderer(this
				.createRulePriorityDownButton());
		priorityDownActionColumn.setMenuDisabled(true);
		priorityDownActionColumn.setSortable(false);
		configs.add(priorityDownActionColumn);

		return new ColumnModel(configs);
	}

	/**
	 * Creates the users custom field.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> createUsersCustomField() {
		GridCellRenderer<Rule> comboRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				ArrayList<Rule> list = new ArrayList<Rule>();

				for (int i = 0; i < store.getCount(); i++) {
					Rule rule = ((Rule) store.getAt(i));
					list.add(rule);
				}

				LabelField usersCustomField = new LabelField();
				usersCustomField.setId("ruleUsersCombo");
				usersCustomField.setName("ruleUsersCombo");
				usersCustomField.setEmptyText("*");
				usersCustomField.setFieldLabel(BeanKeyValue.NAME.getValue());
				usersCustomField.setValue(BeanKeyValue.NAME.getValue());
				usersCustomField.setReadOnly(true);

				usersCustomField.setWidth(COLUMN_USER_WIDTH - 10);
				usersCustomField.show();

				if ((model.getUser() != null)
						&& (model.getUser().getName() != null)) {
					String name2 = model.getUser().getName();
					usersCustomField.setValue(name2);
				} else {
					usersCustomField.setValue("*");
				}

				return usersCustomField;
			}
		};

		return comboRendered;
	}

	/**
	 * Creates the profiles custom field.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> createProfilesCustomField() {
		GridCellRenderer<Rule> comboRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				LabelField profilesCustomField = new LabelField();
				profilesCustomField.setId("ruleProfilesCombo");
				profilesCustomField.setName("ruleProfilesCombo");
				profilesCustomField.setEmptyText("(No group available)");
				profilesCustomField.setFieldLabel(BeanKeyValue.NAME.getValue());
				profilesCustomField.setValue(BeanKeyValue.NAME.getValue());
				profilesCustomField.setReadOnly(false);
				profilesCustomField.setWidth(COLUMN_PROFILE_WIDTH - 10);

				if ((model.getProfile() != null)
						&& (model.getProfile().getName() != null)) {
					String name2 = model.getProfile().getName();
					profilesCustomField.setValue(name2);
				} else {
					profilesCustomField.setValue("*");
				}

				return profilesCustomField;
			}
		};

		return comboRendered;
	}

	/**
	 * Creates the instances custom field.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> createInstancesCustomField() {
		GridCellRenderer<Rule> comboRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				LabelField instancesCustomField = new LabelField();
				instancesCustomField.setId("ruleInstancesCombo");
				instancesCustomField.setName("ruleInstancesCombo");
				instancesCustomField.setEmptyText("(No instances available)");
				instancesCustomField
						.setFieldLabel(BeanKeyValue.NAME.getValue());
				instancesCustomField.setReadOnly(false);
				instancesCustomField.setWidth(COLUMN_INSTANCE_WIDTH - 10);

				if ((model.getInstance() != null)
						&& (model.getInstance().getName() != null)) {
					instancesCustomField
							.setValue(model.getInstance().getName());
				} else {
					instancesCustomField.setValue("*");
				}

				return instancesCustomField;
			}

		};

		return comboRendered;
	}

	/**
	 * Creates the services custom field.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> createServicesCustomField() {
		GridCellRenderer<Rule> comboRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				final LabelField servicesCustomField = new LabelField();
				servicesCustomField.setId("ruleServicesCombo");
				servicesCustomField.setName("ruleServicesCombo");
				servicesCustomField.setEmptyText("(No service available)");
				servicesCustomField.setFieldLabel(BeanKeyValue.SERVICE
						.getValue());
				servicesCustomField.setReadOnly(true);
				servicesCustomField.setWidth(COLUMN_SERVICE_WIDTH - 10);

				if (model.getService() != null) {
					servicesCustomField.setValue(model.getService());
				} else {
					servicesCustomField.setValue("*");
				}

				return servicesCustomField;
			}

		};

		return comboRendered;
	}

	/**
	 * Creates the services request custom field.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> createServicesRequestCustomField() {
		GridCellRenderer<Rule> comboRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				final LabelField serviceRequestsCustomField = new LabelField();
				serviceRequestsCustomField.setId("ruleServicesRequestCombo");
				serviceRequestsCustomField.setName("ruleServicesRequestCombo");
				serviceRequestsCustomField
						.setEmptyText("(No service request available)");
				serviceRequestsCustomField.setFieldLabel(BeanKeyValue.REQUEST
						.getValue());
				serviceRequestsCustomField.setReadOnly(true);
				serviceRequestsCustomField.setWidth(COLUMN_SERVICE_WIDTH - 10);

				if (model.getService() != null) {
					serviceRequestsCustomField.setValue(model.getRequest());
				} else {
					serviceRequestsCustomField.setValue("*");
				}

				return serviceRequestsCustomField;
			}

		};

		return comboRendered;
	}

	/**
	 * Creates the service workspaces custom field.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> createServiceWorkspacesCustomField() {
		GridCellRenderer<Rule> comboRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				LabelField workspacesCustomField = new LabelField();
				workspacesCustomField.setId("ruleWorkspacesCombo");
				workspacesCustomField.setName("ruleWorkspacesCombo");

				workspacesCustomField.setFieldLabel(BeanKeyValue.WORKSPACE
						.getValue());
				workspacesCustomField.setReadOnly(false);
				workspacesCustomField.setWidth(COLUMN_WORKSPACE_WIDTH - 10);

				if (model.getWorkspace() != null) {
					workspacesCustomField.setValue(model.getWorkspace());
				} else {
					workspacesCustomField.setValue("*");
				}

				workspacesCustomField.setEmptyText("(No workspace available)");

				return workspacesCustomField;
			}

		};

		return comboRendered;
	}

	/**
	 * Creates the workspaces layers custom field.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> createWorkspacesLayersCustomField() {
		GridCellRenderer<Rule> comboRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				LabelField workspaceLayersCustomField = new LabelField();
				workspaceLayersCustomField.setId("ruleLayersCombo");
				workspaceLayersCustomField.setName("ruleLayersCombo");

				workspaceLayersCustomField.setFieldLabel(BeanKeyValue.LAYER
						.getValue());
				workspaceLayersCustomField.setReadOnly(false);
				workspaceLayersCustomField.setWidth(COLUMN_LAYER_WIDTH - 10);

				if (model.getLayer() != null) {
					workspaceLayersCustomField.setValue(model.getLayer());
				} else {
					workspaceLayersCustomField.setValue("*");
				}
				workspaceLayersCustomField.setEmptyText("(No layer available)");

				return workspaceLayersCustomField;
			}

		};

		return comboRendered;
	}

	/**
	 * Creates the grants custom field.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> createGrantsCustomField() {
		GridCellRenderer<Rule> comboRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				LabelField grantsCustomField = new LabelField();
				grantsCustomField.setId("grantsCombo");
				grantsCustomField.setName("grantsCombo");

				grantsCustomField.setFieldLabel(BeanKeyValue.GRANT.getValue());
				grantsCustomField.setReadOnly(false);
				grantsCustomField.setWidth(COLUMN_GRANT_WIDTH - 10);

				if (model.getGrant() != null) {
					grantsCustomField.setValue(model.getGrant());
				} else {
					grantsCustomField.setValue("ALLOW");
				}

				grantsCustomField.setEmptyText("(No grant types available)");

				return grantsCustomField;
			}

		};

		return comboRendered;
	}

	/**
	 * Creates the rule delete button.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> createRuleDeleteButton() {
		GridCellRenderer<Rule> buttonRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				Button removeRuleButton = new Button("Remove");
				removeRuleButton.setIcon(Resources.ICONS.delete());
				removeRuleButton.setEnabled(true);

				removeRuleButton.addListener(Events.OnClick,
						new Listener<ButtonEvent>() {

							public void handleEvent(ButtonEvent be) {

								final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
									public void handleEvent(MessageBoxEvent ce) {
										Button btn = ce.getButtonClicked();

										if (btn.getText().equalsIgnoreCase(
												"Yes")) {
											Dispatcher.forwardEvent(
													GeofenceEvents.RULE_DEL,
													model);
											Dispatcher
													.forwardEvent(
															GeofenceEvents.SEND_INFO_MESSAGE,
															new String[] {
																	"GeoServer Rules",
																	"Remove Rule #"
																			+ model.getPriority() });
										}
									}
								};

								MessageBox
										.confirm(
												"Confirm",
												"The Rule will be deleted. Are you sure you want to do that?",
												l);
							}
						});

				return removeRuleButton;
			}

		};

		return buttonRendered;
	}

	/**
	 * Edits the rule button.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> editRuleButton() {
		buttonRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, final Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				Button ruleDetailsButton = new Button("Edit rule");
				ruleDetailsButton.setIcon(Resources.ICONS.table());
				// TODO: add correct tooltip text here!
				ruleDetailsButton.setToolTip("Edit this rule");
				ruleDetailsButton.setEnabled(true);

				ruleDetailsButton.addListener(Events.OnClick,
						new Listener<ButtonEvent>() {

							public void handleEvent(ButtonEvent be) {
								Dispatcher.forwardEvent(
										GeofenceEvents.EDIT_RULE_UPDATE,
										new GridStatus(grid, model));
							}
						});

				return ruleDetailsButton;
			}

		};

		return buttonRendered;
	}

	/**
	 * Creates the rule details button.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> createRuleDetailsButton() {
		GridCellRenderer<Rule> buttonRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				Button ruleDetailsButton = new Button("Details");
				ruleDetailsButton.setIcon(Resources.ICONS.table());
				ruleDetailsButton.setToolTip("Show Rule Details");

				if (!model.getLayer().equalsIgnoreCase("*")
						&& model.getGrant().equalsIgnoreCase("ALLOW")) {
					ruleDetailsButton.setEnabled(true);
				} else if (model.getGrant().equalsIgnoreCase("LIMIT")) {
					ruleDetailsButton.setEnabled(true);
				} else {
					ruleDetailsButton.setEnabled(false);
				}

				ruleDetailsButton.addListener(Events.OnClick,
						new Listener<ButtonEvent>() {

							public void handleEvent(ButtonEvent be) {
								Dispatcher
										.forwardEvent(
												GeofenceEvents.EDIT_RULE_DETAILS,
												model);
							}
						});

				return ruleDetailsButton;
			}

		};

		return buttonRendered;
	}

	/**
	 * Creates the rule add button.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> createRuleAddButton() {
		GridCellRenderer<Rule> buttonRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, final Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				Button ruleAddButton = new Button();
				ruleAddButton.setBorders(false);
				ruleAddButton.setIcon(Resources.ICONS.add());
				// TODO: add correct tooltip text here!
				ruleAddButton.setEnabled(true);

				ruleAddButton.addListener(Events.OnClick,
						new Listener<ButtonEvent>() {

							public void handleEvent(ButtonEvent be) {
								Rule new_rule = Constants.getInstance()
										.createNewRule(model);
								Dispatcher.forwardEvent(
										GeofenceEvents.EDIT_RULE,
										new GridStatus(grid, new_rule));
							}
						});

				return ruleAddButton;
			}

		};

		return buttonRendered;
	}

	/**
	 * Creates the rule priority up button.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> createRulePriorityUpButton() {
		GridCellRenderer<Rule> buttonRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				Button priorityUpButton = new Button();
				priorityUpButton.setBorders(false);
				priorityUpButton.setIcon(Resources.ICONS.arrowUp());
				// TODO: add correct tooltip text here!
				priorityUpButton.setEnabled(true);

				priorityUpButton.addListener(Events.OnClick,
						new Listener<ButtonEvent>() {

							public void handleEvent(ButtonEvent be) {
								Dispatcher
										.forwardEvent(
												GeofenceEvents.SEND_INFO_MESSAGE,
												new String[] {
														"GeoServer Rules",
														"Selected Rule #"
																+ model.getPriority() });

								Dispatcher.forwardEvent(
										GeofenceEvents.RULE_PRIORITY_UP, model);
							}
						});

				return priorityUpButton;
			}

		};

		return buttonRendered;
	}

	/**
	 * Creates the rule priority down button.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> createRulePriorityDownButton() {
		GridCellRenderer<Rule> buttonRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				Button priorityDownButton = new Button();
				priorityDownButton.setBorders(false);
				priorityDownButton.setIcon(Resources.ICONS.arrowDown());
				// TODO: add correct tooltip text here!
				priorityDownButton.setEnabled(true);

				priorityDownButton.addListener(Events.OnClick,
						new Listener<ButtonEvent>() {

							public void handleEvent(ButtonEvent be) {
								Dispatcher
										.forwardEvent(
												GeofenceEvents.SEND_INFO_MESSAGE,
												new String[] {
														"GeoServer Rules",
														"Selected Rule #"
																+ model.getPriority() });

								Dispatcher.forwardEvent(
										GeofenceEvents.RULE_PRIORITY_DOWN,
										model);
							}
						});

				return priorityDownButton;
			}

		};

		return buttonRendered;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.geosolutions.geofence.gui.client.widget.GEOFENCEGridWidget#createStore
	 * ()
	 */
	@Override
	public void createStore() {
		this.toolBar = new PagingToolBar(
				it.geosolutions.geofence.gui.client.Constants.DEFAULT_PAGESIZE);
		
		// Loader for rulesService
		this.proxy = new RpcProxy<PagingLoadResult<Rule>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<Rule>> callback) {
				rulesService.getRules(
						((PagingLoadConfig) loadConfig).getOffset(),
						((PagingLoadConfig) loadConfig).getLimit(), true,
						groupFilter, wsFilter, layerFilter,
						callback);
			}
		};
		loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
		loader.setRemoteSort(false);
		loader.setSortField(BeanKeyValue.PRIORITY.getValue());
		loader.setSortDir(SortDir.ASC);
		store = new ListStore<Rule>(loader);

		if (store != null) {
			store.setSortField(BeanKeyValue.PRIORITY.getValue());
			store.setSortDir(SortDir.ASC);
		}

		// Search tool
		SearchFilterField<Rule> filter = new SearchFilterField<Rule>() {

			@Override
			protected boolean doSelect(Store<Rule> store, Rule parent,
					Rule record, String property, String filter) {

				String field = ((UserGroup) parent
						.get(BeanKeyValue.PROFILE.getValue())).getName();
				field = field.toLowerCase();
				if (field.indexOf(filter.toLowerCase()) != -1) {
					return true;
				}

				field = parent.get(BeanKeyValue.WORKSPACE.getValue());
				field = field.toLowerCase();
				if (field.indexOf(filter.toLowerCase()) != -1) {
					return true;
				}

				field = parent.get(BeanKeyValue.LAYER.getValue());
				field = field.toLowerCase();
				if (field.indexOf(filter.toLowerCase()) != -1) {
					return true;
				}

				return false;
			}

		};

		filter.setWidth(200);
		filter.setIcon(Resources.ICONS.search());

		// Bind the filter field to your grid store (grid.getStore())
		filter.bind(store);

		// Apply Changes button
		// TODO: generalize this!
		Button addRuleButton = new Button("Add Rule");
		addRuleButton.setIcon(Resources.ICONS.add());

		addRuleButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {

			public void handleEvent(ButtonEvent be) {
				Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
						new String[] { "GeoServer Rules", "Add Rule" });

				Rule new_rule = new Rule();
				new_rule.setId(-1);
				new_rule.setPriority(-1);
				new_rule = Constants.getInstance().createNewRule(new_rule);
				Dispatcher.forwardEvent(GeofenceEvents.EDIT_RULE,
						new GridStatus(grid, new_rule));

			}
		});
		
		TextField<String> groupTextFiled = new TextField<String>();
		groupTextFiled.setFieldLabel("Group");
		groupTextFiled.setEmptyText("Group");
		groupTextFiled.setAllowBlank(true);
		groupTextFiled.setMinLength(2);
		groupTextFiled.addListener(Events.Change, new Listener<FieldEvent>() {
			public void handleEvent(FieldEvent fe) {
				groupFilter = (String)fe.getField().getValue();
			}
		});
		TextField<String> wsTextFiled = new TextField<String>();
		wsTextFiled.setFieldLabel("Workspace");
		wsTextFiled.setEmptyText("Worspace");
		wsTextFiled.setAllowBlank(true);
		wsTextFiled.setMinLength(2);
		wsTextFiled.addListener(Events.Change, new Listener<FieldEvent>() {
			public void handleEvent(FieldEvent fe) {
				wsFilter = (String)fe.getField().getValue();
			}
		});
		TextField<String> layerTextFiled = new TextField<String>();
		layerTextFiled.setFieldLabel("Layer");
		layerTextFiled.setEmptyText("Layer");
		layerTextFiled.setAllowBlank(true);
		layerTextFiled.setMinLength(2);
		layerTextFiled.addListener(Events.Change, new Listener<FieldEvent>() {
			public void handleEvent(FieldEvent fe) {
				layerFilter = (String)fe.getField().getValue();
			}
		});

		this.toolBar.bind(loader);
		this.toolBar.add(new SeparatorToolItem());
		this.toolBar.add(addRuleButton);
		this.toolBar.add(new SeparatorToolItem());
		this.toolBar.add(groupTextFiled);
		this.toolBar.add(wsTextFiled);
		this.toolBar.add(layerTextFiled);
		this.toolBar.add(new SeparatorToolItem());
		this.toolBar.add(filter);
		this.toolBar.add(new SeparatorToolItem());
		setUpLoadListener();

	}

	/**
	 * Gets the loader.
	 * 
	 * @return the loader
	 */
	public PagingLoader<PagingLoadResult<ModelData>> getLoader() {
		return loader;
	}

	/**
	 * Gets the tool bar.
	 * 
	 * @return the tool bar
	 */
	public PagingToolBar getToolBar() {
		return toolBar;
	}

	/**
	 * Clear grid elements.
	 */
	public void clearGridElements() {
		this.store.removeAll();
		this.toolBar.clear();
		this.toolBar.disable();
	}

	/**
	 * Sets the up load listener.
	 */
	private void setUpLoadListener() {
		loader.addLoadListener(new LoadListener() {

			@Override
			public void loaderBeforeLoad(LoadEvent le) {
				if (!toolBar.isEnabled()) {
					toolBar.enable();
				}
			}

			@Override
			public void loaderLoad(LoadEvent le) {

				// TODO: change messages here!!

				BasePagingLoadResult<?> result = le.getData();
				if (!result.getData().isEmpty()) {
					int size = result.getData().size();
					String message = "";
					if (size == 1) {
						message = I18nProvider.getMessages().recordLabel();
					} else {
						message = I18nProvider.getMessages()
								.recordPluralLabel();
					}
					Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
							new String[] {
									I18nProvider.getMessages()
											.remoteServiceName(),
									I18nProvider.getMessages().foundLabel()
											+ " " + result.getData().size()
											+ " " + message });
				} else {
					Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
							new String[] {
									I18nProvider.getMessages()
											.remoteServiceName(),
									I18nProvider.getMessages()
											.recordNotFoundMessage() });
				}
			}

		});
	}

}

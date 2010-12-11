/*
 * Copyright 2004-2010 Luciano Vernaschi
 *
 * This file is part of MeshCMS.
 *
 * MeshCMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MeshCMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MeshCMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cromoteca.meshcms.client.ui.controlpanel;

import com.cromoteca.meshcms.client.core.AuthorizableServerCall;
import com.cromoteca.meshcms.client.core.MeshCMS;
import com.cromoteca.meshcms.client.server.SitesConfiguration;
import com.cromoteca.meshcms.client.server.VirtualWebSite;
import com.cromoteca.meshcms.client.toolbox.Null;
import com.cromoteca.meshcms.client.ui.fields.BooleanField;
import com.cromoteca.meshcms.client.ui.fields.BooleanField.BooleanConnector;
import com.cromoteca.meshcms.client.ui.fields.Field;
import com.cromoteca.meshcms.client.ui.fields.FieldConnector;
import com.cromoteca.meshcms.client.ui.fields.FieldList;
import com.cromoteca.meshcms.client.ui.fields.PathField;
import com.cromoteca.meshcms.client.ui.fields.StringArrayConnector;
import com.cromoteca.meshcms.client.ui.fields.TextField;
import com.cromoteca.meshcms.client.ui.filemanager.FileManager;
import com.cromoteca.meshcms.client.ui.widgets.ButtonBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class VirtualSitesEditor extends FlowPanel implements FileManagerPanel {
	private FieldList fields;
	private FileManager fileManager;
	private SitesConfiguration configuration;

	public VirtualSitesEditor() {
		callServer();
	}

	private void callServer() {
		new AuthorizableServerCall<SitesConfiguration>() {
				@Override
				public void callServer() {
					MeshCMS.SERVER.getVirtualSites(getAsyncCallback());
				}

				@Override
				public void onResult(SitesConfiguration result) {
					configuration = result;
					buildLayout();
				}
			}.run();
	}

	private void buildLayout() {
		fields = new FieldList();

		CaptionPanel generalFieldset = new CaptionPanel(MeshCMS.CONSTANTS
						.sitesGeneral());
		add(generalFieldset);

		FlowPanel generalPanel = new FlowPanel();
		generalFieldset.add(generalPanel);

		Field dirsAsDomainsField = new BooleanField(MeshCMS.CONSTANTS
						.sitesDirsAsDomains());
		generalPanel.add(dirsAsDomainsField);
		dirsAsDomainsField.setConnector(new BooleanConnector() {
				@Override
				public void storeValue(boolean value) {
					configuration.setUseDirsAsDomains(value);
				}

				@Override
				public boolean loadValue() {
					return configuration.isUseDirsAsDomains();
				}
			});
		fields.add(dirsAsDomainsField);

		Field manageTripleWs = new BooleanField(MeshCMS.CONSTANTS
						.sitesManageTripleWs());
		generalPanel.add(manageTripleWs);
		manageTripleWs.setConnector(new BooleanConnector() {
				@Override
				public void storeValue(boolean value) {
					configuration.setManageTripleWs(value);
				}

				@Override
				public boolean loadValue() {
					return configuration.isManageTripleWs();
				}
			});
		fields.add(manageTripleWs);

		Field mainWebSiteDomainsField = new TextField(false,
				MeshCMS.CONSTANTS.sitesMainWebSiteDomains()).bigger().bigger();
		generalPanel.add(mainWebSiteDomainsField);
		mainWebSiteDomainsField.setConnector(new StringArrayConnector() {
				@Override
				public void storeValue(String[] value) {
					configuration.setMainWebSiteDomains(value);
				}

				@Override
				public String[] loadValue() {
					return configuration.getMainWebSiteDomains();
				}
			});
		fields.add(mainWebSiteDomainsField);
		addSiteList(false);
		addSiteList(true);

		ButtonBar buttonBar = new ButtonBar();
		add(buttonBar);

		Button updateButton = new Button(MeshCMS.CONSTANTS.genericUpdate(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						new AuthorizableServerCall<Null>() {
								@Override
								public void callServer() {
									if (fields.verifyAll()) {
										fields.storeAll();
										MeshCMS.SERVER.saveVirtualSites(configuration,
											getAsyncCallback());
									}
								}

								@Override
								public void onResult(Null result) {
									fileManager.refreshView();
								}
							}.run();
					}
				});
		buttonBar.add(updateButton);
		fields.addActionButton(updateButton);
		fields.verifyAll();
	}

	private void addSiteList(final boolean external) {
		DisclosurePanel disclosurePanel = new DisclosurePanel(external
				? MeshCMS.CONSTANTS.sitesExternalList()
				: MeshCMS.CONSTANTS.sitesVirtualList());
		disclosurePanel.setOpen(true);
		disclosurePanel.setAnimationEnabled(true);
		add(disclosurePanel);

		final FlowPanel sitesPanel = new FlowPanel();
		disclosurePanel.add(sitesPanel);
		sitesPanel.add(new Button(MeshCMS.CONSTANTS.genericAdd(),
				new ClickHandler() {
				public void onClick(ClickEvent event) {
					VirtualWebSite webSite = new VirtualWebSite();
					webSite.setExternal(external);
					webSite.setCMSEnabled(true);
					addSite(sitesPanel, webSite, true);
					configuration.getWebSites().add(webSite);
				}
			}));

		for (final VirtualWebSite webSite : configuration.getWebSites()) {
			if (external == webSite.isExternal()) {
				addSite(sitesPanel, webSite, false);
			}
		}
	}

	private void addSite(FlowPanel parent, final VirtualWebSite webSite,
		boolean insert) {
		final CaptionPanel fieldset = new CaptionPanel();

		if (insert) {
			parent.insert(fieldset, 1);
		} else {
			parent.add(fieldset);
		}

		FlowPanel panel = new FlowPanel();
		fieldset.add(panel);

		Field rootField;

		if (webSite.isExternal()) {
			rootField = new TextField(true, MeshCMS.CONSTANTS.sitesExternalRootPath()).bigger()
						.bigger();
		} else {
			rootField = new PathField(true, MeshCMS.CONSTANTS.sitesVirtualRootPath(),
					null).bigger();
		}

		panel.add(rootField);
		rootField.setConnector(new FieldConnector() {
				public void storeValue(String value) {
					webSite.setRoot(value);
				}

				public String retrieveValue() {
					return webSite.getRoot();
				}
			});
		fields.add(rootField);

		Field aliasesField = new TextField(false,
				MeshCMS.CONSTANTS.sitesHeaderAliases()).bigger().bigger();
		panel.add(aliasesField);
		aliasesField.setConnector(new StringArrayConnector() {
				@Override
				public void storeValue(String[] value) {
					webSite.setAliases(value);
				}

				@Override
				public String[] loadValue() {
					return webSite.getAliases();
				}
			});
		fields.add(aliasesField);

		Field cmsEnabledField = new BooleanField(MeshCMS.CONSTANTS.sitesHeaderCMS());
		panel.add(cmsEnabledField);
		cmsEnabledField.setConnector(new BooleanConnector() {
				@Override
				public void storeValue(boolean value) {
					webSite.setCMSEnabled(value);
				}

				@Override
				public boolean loadValue() {
					return webSite.isCMSEnabled();
				}
			});
		fields.add(cmsEnabledField);
		panel.add(new Button(MeshCMS.CONSTANTS.genericRemove(),
				new ClickHandler() {
				public void onClick(ClickEvent event) {
					configuration.getWebSites().remove(webSite);
					fields.removeAll(fieldset);
					fields.verifyAll();
					fieldset.removeFromParent();
				}
			}));

		if (insert) {
			fields.verifyAll();
		}
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public Widget asWidget() {
		return this;
	}

	public String getTabCaption() {
		return MeshCMS.CONSTANTS.homeSites();
	}
}

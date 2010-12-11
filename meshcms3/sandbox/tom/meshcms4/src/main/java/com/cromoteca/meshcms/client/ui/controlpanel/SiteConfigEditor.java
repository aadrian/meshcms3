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
import com.cromoteca.meshcms.client.server.SiteConfiguration;
import com.cromoteca.meshcms.client.toolbox.Null;
import com.cromoteca.meshcms.client.toolbox.Pair;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.client.toolbox.Strings;
import com.cromoteca.meshcms.client.ui.fields.BooleanField;
import com.cromoteca.meshcms.client.ui.fields.BooleanField.BooleanConnector;
import com.cromoteca.meshcms.client.ui.fields.Field;
import com.cromoteca.meshcms.client.ui.fields.FieldConnector;
import com.cromoteca.meshcms.client.ui.fields.FieldList;
import com.cromoteca.meshcms.client.ui.fields.IntegerField;
import com.cromoteca.meshcms.client.ui.fields.IntegerField.IntegerConnector;
import com.cromoteca.meshcms.client.ui.fields.PathField;
import com.cromoteca.meshcms.client.ui.fields.SelectionField;
import com.cromoteca.meshcms.client.ui.fields.SortedSelectionField;
import com.cromoteca.meshcms.client.ui.fields.TextField;
import com.cromoteca.meshcms.client.ui.filemanager.FileManager;
import com.cromoteca.meshcms.client.ui.widgets.ButtonBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class SiteConfigEditor extends FlowPanel implements FileManagerPanel {
	private FileManager fileManager;

	public SiteConfigEditor() {
		buildLayout();
	}

	private void buildLayout() {
		new AuthorizableServerCall<Pair<SiteConfiguration, HashMap<String, String>>>() {
				@Override
				public void callServer() {
					MeshCMS.SERVER.getSiteConfigurationAndLocales(getAsyncCallback());
				}

				@Override
				public void onResult(
					Pair<SiteConfiguration, HashMap<String, String>> result) {
					buildLayout(result.getFirstObject(), result.getSecondObject());
				}
			}.run();
	}

	private void buildLayout(final SiteConfiguration configuration,
		final Map<String, String> locales) {
		clear();

		final FieldList fields = new FieldList();
		CaptionPanel siteInfoFieldset = new CaptionPanel(MeshCMS.CONSTANTS
						.configSiteInfo());
		add(siteInfoFieldset);

		FlowPanel siteInfoPanel = new FlowPanel();
		siteInfoFieldset.add(siteInfoPanel);

		Field siteNameField = new TextField(false,
				MeshCMS.CONSTANTS.configSiteName()).bigger();
		siteInfoPanel.add(siteNameField);
		siteNameField.setConnector(new FieldConnector() {
				public void storeValue(String value) {
					configuration.setSiteName(value);
				}

				public String retrieveValue() {
					return configuration.getSiteName();
				}
			});
		fields.add(siteNameField);

		Field siteSloganField = new TextField(false,
				MeshCMS.CONSTANTS.configSiteSlogan()).bigger();
		siteInfoPanel.add(siteSloganField);
		siteSloganField.setConnector(new FieldConnector() {
				public void storeValue(String value) {
					configuration.setSiteSlogan(value);
				}

				public String retrieveValue() {
					return configuration.getSiteSlogan();
				}
			});
		fields.add(siteSloganField);

		Field siteHostField = new TextField(false,
				MeshCMS.CONSTANTS.configSiteHost()).bigger();
		siteInfoPanel.add(siteHostField);
		siteHostField.setConnector(new FieldConnector() {
				public void storeValue(String value) {
					configuration.setSiteHost(value);
				}

				public String retrieveValue() {
					return configuration.getSiteHost();
				}
			});
		fields.add(siteHostField);

		Field siteOwnerField = new TextField(false,
				MeshCMS.CONSTANTS.configSiteOwner()).bigger();
		siteInfoPanel.add(siteOwnerField);
		siteOwnerField.setConnector(new FieldConnector() {
				public void storeValue(String value) {
					configuration.setSiteOwner(value);
				}

				public String retrieveValue() {
					return configuration.getSiteOwner();
				}
			});
		fields.add(siteOwnerField);

		Field siteOwnerURLField = new TextField(false,
				MeshCMS.CONSTANTS.configSiteOwnerURL()).bigger();
		siteInfoPanel.add(siteOwnerURLField);
		siteOwnerURLField.setConnector(new FieldConnector() {
				public void storeValue(String value) {
					configuration.setSiteOwnerURL(value);
				}

				public String retrieveValue() {
					return configuration.getSiteOwnerURL();
				}
			});
		fields.add(siteOwnerURLField);

		CaptionPanel interfaceFieldset = new CaptionPanel(MeshCMS.CONSTANTS
						.configInterface());
		add(interfaceFieldset);

		FlowPanel interfacePanel = new FlowPanel();
		interfaceFieldset.add(interfacePanel);

		Field langField = new SortedSelectionField(MeshCMS.CONSTANTS
						.configSiteLanguage()) {
				@Override
				public void populateOptions(SortedMap<String, String> itemValueMap) {
					for (String localeName : locales.keySet()) {
						itemValueMap.put(locales.get(localeName), localeName);
					}
				}
			};

		interfacePanel.add(langField);
		langField.setConnector(new FieldConnector() {
				public void storeValue(String value) {
					configuration.setLocale(value);
				}

				public String retrieveValue() {
					return configuration.getLocale();
				}
			});
		fields.add(langField);

		final Field overrideField = new BooleanField(configuration.isRedirectRoot(),
				MeshCMS.CONSTANTS.configOverrideLocale());
		interfacePanel.add(overrideField);
		overrideField.setConnector(new BooleanConnector() {
				@Override
				public void storeValue(boolean value) {
					configuration.setOverrideLocale(value);
				}

				@Override
				public boolean loadValue() {
					return configuration.isOverrideLocale();
				}
			});
		fields.add(overrideField);

		final BooleanField redirField = new BooleanField(MeshCMS.CONSTANTS
						.configRedirectRoot());
		redirField.getFieldWidget().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					overrideField.setRequired(redirField.getFieldWidget().getValue());
				}
			});
		interfacePanel.add(redirField);
		redirField.setConnector(new BooleanConnector() {
				@Override
				public void storeValue(boolean value) {
					configuration.setRedirectRoot(value);
				}

				@Override
				public boolean loadValue() {
					return configuration.isRedirectRoot();
				}
			});
		fields.add(redirField);

		final BooleanField searchField = new BooleanField(MeshCMS.CONSTANTS
						.configSearchMovedPages());
		interfacePanel.add(searchField);
		searchField.setConnector(new BooleanConnector() {
				@Override
				public void storeValue(boolean value) {
					configuration.setSearchMovedPages(value);
				}

				@Override
				public boolean loadValue() {
					return configuration.isSearchMovedPages();
				}
			});
		fields.add(searchField);

		Field thumbField = new BooleanField(MeshCMS.CONSTANTS
						.configReplaceThumbnails());
		interfacePanel.add(thumbField);
		thumbField.setConnector(new BooleanConnector() {
				@Override
				public void storeValue(boolean value) {
					configuration.setReplaceThumbnails(value);
				}

				@Override
				public boolean loadValue() {
					return configuration.isReplaceThumbnails();
				}
			});
		fields.add(thumbField);

		Field userCSSField = new PathField(false,
				MeshCMS.CONSTANTS.configUserCSS(), null);
		interfacePanel.add(userCSSField);
		userCSSField.setConnector(new FieldConnector() {
				public void storeValue(String value) {
					configuration.setUserCSS(Strings.isNullOrEmpty(value) ? null
						: new Path(value));
				}

				public String retrieveValue() {
					Path userCSS = configuration.getUserCSS();

					return userCSS == null ? "" : userCSS.asAbsolute();
				}
			});
		fields.add(userCSSField);

		CaptionPanel systemFieldset = new CaptionPanel(MeshCMS.CONSTANTS
						.configSystem());
		add(systemFieldset);

		FlowPanel systemPanel = new FlowPanel();
		systemFieldset.add(systemPanel);

		Field excerptField = new IntegerField(true,
				MeshCMS.CONSTANTS.excerptLength(), IntegerField.POSITIVE_ONLY);
		systemPanel.add(excerptField);
		excerptField.setConnector(new IntegerConnector() {
				@Override
				public void storeValue(int value) {
					configuration.setExcerptLength(value);
				}

				@Override
				public int loadValue() {
					return configuration.getExcerptLength();
				}
			});
		fields.add(excerptField);

		Field updateField = new IntegerField(true,
				MeshCMS.CONSTANTS.configMapMinutes(), IntegerField.POSITIVE_ONLY);
		systemPanel.add(updateField);
		updateField.setConnector(new IntegerConnector() {
				@Override
				public void storeValue(int value) {
					configuration.setUpdateInterval(value);
				}

				@Override
				public int loadValue() {
					return configuration.getUpdateInterval();
				}
			});
		fields.add(updateField);

		ButtonBar buttonBar = new ButtonBar();
		add(buttonBar);

		Button updateButton = new Button(MeshCMS.CONSTANTS.genericUpdate(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						if (fields.verifyAll()) {
							fields.storeAll();
							new AuthorizableServerCall<Null>() {
									@Override
									public void callServer() {
										MeshCMS.SERVER.saveSiteConfiguration(configuration,
											getAsyncCallback());
									}

									@Override
									public void onResult(Null result) {
										fileManager.refreshView();
									}
								}.run();
						}
					}
				});
		buttonBar.add(updateButton);
		fields.addActionButton(updateButton);
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public Widget asWidget() {
		return this;
	}

	public String getTabCaption() {
		return MeshCMS.CONSTANTS.homeConfigure();
	}
}

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
import com.cromoteca.meshcms.client.server.Module;
import com.cromoteca.meshcms.client.server.Module.Parameter;
import com.cromoteca.meshcms.client.server.SiteConfiguration;
import com.cromoteca.meshcms.client.toolbox.Null;
import com.cromoteca.meshcms.client.ui.fields.Field;
import com.cromoteca.meshcms.client.ui.fields.FieldConnector;
import com.cromoteca.meshcms.client.ui.fields.FieldList;
import com.cromoteca.meshcms.client.ui.filemanager.FileManager;
import com.cromoteca.meshcms.client.ui.pageeditor.ZoneEditorPanel;
import com.cromoteca.meshcms.client.ui.widgets.ButtonBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ModuleConfigEditor extends FlowPanel implements FileManagerPanel {
	private FileManager fileManager;

	public ModuleConfigEditor() {
		new AuthorizableServerCall<SiteConfiguration>() {
				@Override
				public void callServer() {
					MeshCMS.SERVER.getSiteConfiguration(getAsyncCallback());
				}

				@Override
				public void onResult(SiteConfiguration result) {
					buildLayout(result);
				}
			}.run();
	}

	private void buildLayout(final SiteConfiguration configuration) {
		final FieldList fields = new FieldList();
		Collection<Module> modules = MeshCMS.SITE_INFO.getModules().values();

		for (final Module module : modules) {
			List<Parameter> params = new ArrayList<Parameter>();

			for (Parameter param : module.getParameters()) {
				if (param.isSiteWide()) {
					params.add(param);
				}
			}

			if (params.size() > 0) {
				String moduleCaption = MeshCMS.getDynamicTranslation("modules_",
						module.getName());
				CaptionPanel fieldset = new CaptionPanel(MeshCMS.MESSAGES
								.moduleConfigModule(moduleCaption), true);
				add(fieldset);

				FlowPanel fieldPanel = new FlowPanel();
				fieldset.add(fieldPanel);

				for (final Parameter param : params) {
					// note: there's no current dir, so parameters of type
					// PATH and RICH_TEXT won't work
					Field field = ZoneEditorPanel.createField(param, module.getName(),
							null);

					if (field != null) {
						fieldPanel.add(field);

						FieldConnector connector = new FieldConnector() {
								public void storeValue(String value) {
									configuration.setModuleParameter(module.getName(),
										param.getName(), value);
								}

								public String retrieveValue() {
									return configuration.getModuleParameter(module.getName(),
										param.getName(), param.getDefaultValue());
								}
							};

						field.setConnector(connector);
						fields.add(field);
					}
				}
			}
		}

		ButtonBar buttonBar = new ButtonBar();
		add(buttonBar);

		Button saveButton = new Button(MeshCMS.CONSTANTS.genericUpdate(),
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
		buttonBar.add(saveButton);
		fields.setActionButton(saveButton);
		fields.verifyAll();
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public Widget asWidget() {
		return this;
	}

	public String getTabCaption() {
		return MeshCMS.CONSTANTS.moduleConfigTitle();
	}
}

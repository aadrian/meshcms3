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

import com.cromoteca.meshcms.client.core.AbstractAsyncCallback;
import com.cromoteca.meshcms.client.core.AuthorizableServerCall;
import com.cromoteca.meshcms.client.core.MeshCMS;
import com.cromoteca.meshcms.client.server.ServerInfo;
import com.cromoteca.meshcms.client.toolbox.Null;
import com.cromoteca.meshcms.client.ui.filemanager.FileManager;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ControlPanel extends FlowPanel implements FileManagerPanel {
	private static final NumberFormat PERCENT_FORMAT = NumberFormat.getFormat(
			"#0%");

	public ControlPanel() {
		buildLayout();
	}

	private void buildLayout() {
		Button logout = new Button(MeshCMS.CONSTANTS.homeLogout());
		logout.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					event.preventDefault();
					MeshCMS.SERVER.logout(new AbstractAsyncCallback<Null>() {
							@Override
							public void onResult(Null result) {
								MeshCMS.closeWindow(true);
							}
						});
				}
			});
		add(logout);
		new AuthorizableServerCall<ServerInfo>() {
				@Override
				public void callServer() {
					MeshCMS.SERVER.getServerInfo(getAsyncCallback());
				}

				@Override
				public void onResult(ServerInfo info) {
					add(new Label(MeshCMS.CONSTANTS.sysVersion() + info.getVersion()));
					add(new Label(MeshCMS.CONSTANTS.sysMemory()
							+ PERCENT_FORMAT.format(
								(float) info.getUsedMB() / (float) info.getTotalMB())));
					add(new Label(MeshCMS.CONSTANTS.sysPageCount() + info.getPageCount()));
				}
			}.run();
	}

	public void setFileManager(FileManager fileManager) {}

	public Widget asWidget() {
		return this;
	}

	public String getTabCaption() {
		return MeshCMS.CONSTANTS.homeTitle();
	}
}

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
package com.cromoteca.meshcms.client.ui.pageeditor;

import com.cromoteca.meshcms.client.core.AuthorizableServerCall;
import com.cromoteca.meshcms.client.core.MeshCMS;
import com.cromoteca.meshcms.client.server.Page;
import com.cromoteca.meshcms.client.server.Zone;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.client.ui.fields.FieldList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class ZoneEditor extends DockLayoutPanel {
	private Page page;
	private Path dirPath;
	private Path pagePath;

	public ZoneEditor(Path pagePath, Path dirPath, String zoneName) {
		super(Unit.PX);
		this.pagePath = pagePath;
		this.dirPath = dirPath;
		buildLayout(zoneName);
	}

	private void buildLayout(final String zoneName) {
		new AuthorizableServerCall<Page>() {
				@Override
				public void callServer() {
					MeshCMS.SERVER.getPage(pagePath, true, getAsyncCallback());
				}

				@Override
				public void onResult(Page result) {
					page = result;

					Zone zone = page.getPageConfiguration().getZone(zoneName);

					if (zone == null) {
						zone = new Zone(zoneName);
						page.getPageConfiguration().addZone(zone);
					}

					buildLayout(zone);
				}
			}.run();
	}

	private void buildLayout(Zone zone) {
		TabLayoutPanel tabs = new TabLayoutPanel(40, Unit.PX);
		final FieldList fields = new FieldList();
		final ZoneEditorPanel zoneEditorPanel = new ZoneEditorPanel(dirPath,
				fields, zone);
		tabs.add(new ScrollPanel(zoneEditorPanel), MeshCMS.CONSTANTS.editZoneTitle());

		EditorButtonBar editorButtonBar = new EditorButtonBar(pagePath, page,
				false, fields);
		editorButtonBar.add(zoneEditorPanel);
		addSouth(editorButtonBar, EditorButtonBar.HEIGHT);

		add(tabs);
	}
}

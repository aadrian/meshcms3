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
package com.cromoteca.meshcms.server.modules;

import com.cromoteca.meshcms.client.server.ZoneItem;
import com.cromoteca.meshcms.server.core.ServerModule;
import java.util.List;
import java.util.Map;

public class Blog extends ServerModule {
	private String path;
	private boolean showPageImages;
	private int bodyExcerptLength;
	private int numberOfEntries;

	public void setBodyExcerptLength(int bodyExcerptLength) {
		this.bodyExcerptLength = bodyExcerptLength;
	}

	public void setNumberOfEntries(int numberOfEntries) {
		this.numberOfEntries = numberOfEntries;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setShowPageImages(boolean showPageImages) {
		this.showPageImages = showPageImages;
	}

	@Override
	public String process() {
		super.process();

		List<ZoneItem> items = getZoneOutput().getZone().getItems();
		int idx = getPosition();
		ZoneItem item = new ZoneItem();
		item.setModuleName("pagelist");

		Map<String, String> parameters = item.getParameters();
		parameters.put("path", path);
		parameters.put("numberOfEntries", Integer.toString(numberOfEntries));
		parameters.put("sortByVisits", "false");
		parameters.put("showPageImages", Boolean.toString(showPageImages));
		parameters.put("showDates", "true");
		parameters.put("showBodies", "true");
		parameters.put("bodyExcerptLength", Integer.toString(bodyExcerptLength));
		parameters.put("showReadMoreLinks", "true");
		parameters.put("showTags", "true");
		parameters.put("showHistoryLinks", "true");
		items.add(++idx, item);

		return null;
	}
}

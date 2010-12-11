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
package com.cromoteca.meshcms.client.server;

import com.cromoteca.meshcms.client.toolbox.Path;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Information about a module zone;
 */
public class Zone implements Serializable {
	public static final String HEAD_ZONE = "head";
	public static final String BODY_ZONE = "body";
	private List<ZoneItem> items;
	private transient Path path;
	private String name;
	private transient ZoneItem currentItem;
	private ZoneAction action;
	private boolean inheritable;

	public Zone(String name) {
		this.name = name;
		items = new ArrayList<ZoneItem>();
	}

	private Zone() {
		name = "";
		items = new ArrayList<ZoneItem>();
	}

	public int getIndex(ZoneItem item) {
		int index = -1;

		for (int i = 0; index == -1 && i < items.size(); i++) {
			if (items.get(i).equals(item)) {
				index = i;
			}
		}

		return index;
	}

	public List<ZoneItem> getItems() {
		return items;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ZoneItem getCurrentItem() {
		return currentItem;
	}

	public void setCurrentItem(ZoneItem currentItem) {
		this.currentItem = currentItem;
	}

	public boolean isInheritable() {
		return inheritable;
	}

	public void setInheritable(boolean inheritable) {
		this.inheritable = inheritable;
	}

	public void setItems(List<ZoneItem> items) {
		this.items = items;
	}

	public ZoneAction getAction() {
		return action;
	}

	public void setAction(ZoneAction action) {
		this.action = action;
	}

	@Override
	public String toString() {
		return name;
	}

	public static boolean isInheritable(String zoneName) {
		return !(HEAD_ZONE.equals(zoneName) || BODY_ZONE.equals(zoneName));
	}
}

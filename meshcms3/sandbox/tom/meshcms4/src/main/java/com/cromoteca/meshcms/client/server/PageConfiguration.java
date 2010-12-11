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

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Maps to the JSON part of a MeshCMS web page.&nbsp;Contains all values that
 * cannot be expressed with common HTML page members.
 */
public class PageConfiguration implements Serializable {
	private Date creationDate;
	private MenuPolicy menuPolicy = MenuPolicy.INSERT;
	private Set<Zone> zones;
	private String draft;
	private String shortTitle;

	public PageConfiguration() {
		zones = new HashSet<Zone>();
	}

	public Zone getZone(String zoneName) {
		for (Zone zone : zones) {
			if (zoneName.equals(zone.getName())) {
				return zone;
			}
		}

		return null;
	}

	public void addZone(Zone zone) {
		Zone oldZone = getZone(zone.getName());

		if (oldZone != null) {
			zones.remove(oldZone);
		}

		zones.add(zone);
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public MenuPolicy getMenuPolicy() {
		return menuPolicy;
	}

	public void setMenuPolicy(MenuPolicy menuPolicy) {
		this.menuPolicy = menuPolicy;
	}

	public Set<Zone> getZones() {
		return zones;
	}

	public String getDraft() {
		return draft;
	}

	public void setDraft(String draft) {
		this.draft = draft;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}
}

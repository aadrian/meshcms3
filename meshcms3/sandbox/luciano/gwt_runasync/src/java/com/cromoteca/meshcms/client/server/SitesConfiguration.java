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
import java.util.HashSet;
import java.util.Set;

/**
 * Contain information about virtual websites.
 */
public class SitesConfiguration implements Serializable {
	private Set<VirtualWebSite> webSites;
	private String[] mainWebSiteDomains;
	private boolean manageTripleWs;
	private boolean useDirsAsDomains;

	public SitesConfiguration() {
		webSites = new HashSet<VirtualWebSite>();
		manageTripleWs = true;
		useDirsAsDomains = true;
	}

	public Set<VirtualWebSite> getWebSites() {
		return webSites;
	}

	public String[] getMainWebSiteDomains() {
		return mainWebSiteDomains;
	}

	public void setMainWebSiteDomains(String[] mainWebSiteDomains) {
		this.mainWebSiteDomains = mainWebSiteDomains;
	}

	public boolean isManageTripleWs() {
		return manageTripleWs;
	}

	public void setManageTripleWs(boolean manageTripleWs) {
		this.manageTripleWs = manageTripleWs;
	}

	public boolean isUseDirsAsDomains() {
		return useDirsAsDomains;
	}

	public void setUseDirsAsDomains(boolean useDirsAsDomains) {
		this.useDirsAsDomains = useDirsAsDomains;
	}
}

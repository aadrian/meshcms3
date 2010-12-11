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
package com.cromoteca.meshcms.server.core;

import com.cromoteca.meshcms.client.server.CMSDirectoryItem;
import com.cromoteca.meshcms.client.server.SitesConfiguration;
import com.cromoteca.meshcms.client.server.VirtualWebSite;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.storage.File;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

public class MultiSiteManager {
	private SitesConfiguration configuration;
	private SortedMap<String, WebSite> domainMap;
	private WebSite mainWebSite;

	private MultiSiteManager() {}

	public static MultiSiteManager load(WebSite mainWebSite) {
		MultiSiteManager m = new MultiSiteManager();
		m.mainWebSite = mainWebSite;

		try {
			m.configuration = Context.loadFromJSON(SitesConfiguration.class,
					mainWebSite.getCMSFile(CMSDirectoryItem.SITES_CONFIGURATION_FILE));
		} catch (Exception ex) {
			Context.log(ex);
		}

		if (m.configuration == null) {
			m.configuration = new SitesConfiguration();
		}

		return m;
	}

	public SitesConfiguration getSitesConfiguration() {
		return configuration;
	}

	public void setSitesConfiguration(SitesConfiguration configuration) {
		this.configuration = configuration;
	}

	public WebSite getWebSite(String domain) {
		WebSite webSite = domainMap.get(domain.toLowerCase());

		if (webSite == null && configuration.getMainWebSiteDomains() == null) {
			webSite = mainWebSite;
		}

		return webSite;
	}

	private WebSite searchWebSite(File file) {
		if (domainMap != null) {
			for (WebSite webSite : domainMap.values()) {
				if (webSite.getRootFile().equals(file)) {
					return webSite;
				}
			}
		}

		return null;
	}

	public File getRootFile(VirtualWebSite site) {
		return site.isExternal() ? Server.getStorage().getFile(site.getRoot())
		: mainWebSite.getFile(new Path(site.getRoot()));
	}

	public void initDomainMap() throws IOException {
		SortedMap<String, WebSite> newMap = new TreeMap<String, WebSite>();

		for (VirtualWebSite site : configuration.getWebSites()) {
			if (configuration.isUseDirsAsDomains()) {
				Path rootPath = new Path(site.getRoot());
				String dirName = rootPath.getLastElement();
				WebSite webSite = searchWebSite(getRootFile(site));

				if (webSite == null) {
					if (site.isExternal()) {
						webSite = new WebSite(Server.getStorage().getFile(site.getRoot()),
								null, site.isCMSEnabled());
					} else {
						webSite = new WebSite(mainWebSite.getFile(rootPath), rootPath,
								site.isCMSEnabled());
					}
				}

				parseDomains(newMap, webSite, site.getAliases());

				if (configuration.isUseDirsAsDomains()) {
					parseDomains(newMap, webSite, dirName);
				}
			}
		}

		if (!parseDomains(domainMap, mainWebSite,
						configuration.getMainWebSiteDomains())) {
			// no valid domains in mainWebSiteDomains, so set it to null for getWebSite() to work correctly
			configuration.setMainWebSiteDomains(null);
		}

		domainMap = newMap;
	}

	private boolean parseDomains(SortedMap<String, WebSite> map, WebSite webSite,
		String... domainNames) {
		boolean result = false;

		if (domainNames != null) {
			for (String token : domainNames) {
				map.put(token, webSite);
				result = true;

				if (configuration.isManageTripleWs() && !token.startsWith("www.")) {
					map.put("www." + token, webSite);
				}
			}
		}

		return result;
	}

	public void store() throws IOException {
		Context.storeToJSON(configuration,
			mainWebSite.getCMSFile(CMSDirectoryItem.SITES_CONFIGURATION_FILE));
	}
}

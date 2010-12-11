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

import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.core.ServerModule;
import com.cromoteca.meshcms.server.toolbox.Strings;

public class CanonicalURL extends ServerModule {
	private String root;

	public void setRoot(String root) {
		this.root = root;
	}

	public String getLink() {
		StringBuilder sb = new StringBuilder();

		if (Strings.isNullOrEmpty(root)) {
			sb.append("http://");
			sb.append(rc.getWebSite().getSiteConfiguration().getSiteHost());
			sb.append('/');
		} else {
			sb.append(root);

			if (!root.endsWith("/")) {
				sb.append('/');
			}
		}

		Path pagePath = rc.getPagePath();
		sb.append(pagePath);

		if (!pagePath.isRoot() && rc.getWebSite().isDirectory(pagePath)) {
			sb.append('/');
		}

		String query = rc.getURL().getQuery();

		if (!Strings.isNullOrEmpty(query)) {
			sb.append('?');
			sb.append(query);
		}

		return sb.toString();
	}
}

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

import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.core.Context.RequestContext;
import com.cromoteca.meshcms.server.webview.ServerPage;

public class BackingBean implements ServerPage {
	private transient RequestContext rc = Context.getRequestContext();

	public Object getBean() {
		return this;
	}

	public String process() {
		return null;
	}

	public String getPath() {
		return rc.getPagePath().asLink();
	}

	public String getContextPath() {
		return Context.getContextPath();
	}

	public Path getDir() {
		return rc.getDirectoryPath();
	}

	public String adjustPath(String path) {
		return rc.adjustPath(path).toString();
	}

	public Path getMeshPath() {
		return rc.getMeshPath();
	}

	public Path getAdminPath() {
		return rc.getAdminPath();
	}
}

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

import com.cromoteca.meshcms.client.server.Zone;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.webview.PageNameMapper;
import com.cromoteca.meshcms.server.webview.Scope;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;

public class ZoneOutput implements Serializable {
	private List<ModuleOutput> modules;
	private Zone zone;

	public ZoneOutput(Zone zone) {
		this.zone = zone;
		modules = new ArrayList<ModuleOutput>();
	}

	public Zone getZone() {
		return zone;
	}

	public List<ModuleOutput> getAll() {
		return modules;
	}

	public ModuleOutput add() {
		ModuleOutput output = new ModuleOutput();
		modules.add(output);

		return output;
	}

	public ModuleOutput getCurrent() {
		ModuleOutput output;

		if (modules.isEmpty()) {
			output = add();
		} else {
			output = modules.get(modules.size() - 1);
		}

		return output;
	}

	public void removeCurrent() {
		if (!modules.isEmpty()) {
			modules.remove(modules.size() - 1);
		}
	}

	public String runTemplate(Path template, Object templateBean)
		throws IOException, ServletException {
		Context.setScopedAttribute(PageNameMapper.REQUEST_ATTRIBUTE_NAME,
			templateBean, Scope.REQUEST);

		String html = WebUtils.requestPage(template.asLink()).getAsString();
		Context.removeScopedAttribute(PageNameMapper.REQUEST_ATTRIBUTE_NAME,
			Scope.REQUEST);

		return html;
	}
}

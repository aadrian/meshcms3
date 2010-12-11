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

import com.cromoteca.meshcms.client.server.Page;
import com.cromoteca.meshcms.server.core.ModuleOutput;
import com.cromoteca.meshcms.server.core.ServerModule;
import java.io.IOException;
import javax.servlet.ServletException;

public class Body extends ServerModule {
	@Override
	public void run() throws IOException, ServletException {
		Page page = getPageView().getPage();
		String html = page.getBody();
		ModuleOutput output = getZoneOutput().getCurrent();
		output.setTitle(page.getTitle());
		output.addContent(html);
	}
}

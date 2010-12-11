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
package com.cromoteca.meshcms.server.pages.meshcms.resources;

import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.core.Context;
import com.cromoteca.meshcms.server.core.WebSite;
import com.cromoteca.meshcms.server.modules.PageList;
import java.net.MalformedURLException;

public class Feed extends PageList {
	// TODO: add date using toolbox.Dates
	private String title;

	@Override
	public String process() {
		super.process();
		Context.getResponse().setContentType("text/xml");
		setShowDates(true);

		return null;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() throws MalformedURLException {
		return Context.getRequestContext().getURL(Path.ROOT);
	}

	public String getGenerator() {
		return WebSite.APP_FULL_NAME_WITH_URL;
	}
}

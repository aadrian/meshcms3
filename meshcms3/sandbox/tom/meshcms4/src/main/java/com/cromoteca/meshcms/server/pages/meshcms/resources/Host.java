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

import com.cromoteca.meshcms.client.core.MeshCMS;
import com.cromoteca.meshcms.client.server.CMSDirectoryItem;
import com.cromoteca.meshcms.client.server.SiteInfo;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.core.Context;
import com.cromoteca.meshcms.server.core.Context.RequestContext;
import com.cromoteca.meshcms.server.core.SessionUser;
import com.cromoteca.meshcms.server.toolbox.Strings;
import com.cromoteca.meshcms.server.toolbox.Web;
import com.cromoteca.meshcms.server.webview.ServerPage;

public class Host implements ServerPage {
	private Path path;
	private RequestContext rc = Context.getRequestContext();
	private SiteInfo siteInfo;
	private String mode;
	private String title;
	private boolean browserPopup;

	public Object getBean() {
		return this;
	}

	public String process() {
		siteInfo = rc.getSiteMap().getSiteInfo();

		SessionUser user = Context.getUser();
		String userLocale = user == null ? null : user.getUser().getLocale();
		String url = rc.getURL().toString();
		String urlLocale = Web.getURLParameter(url, "locale", false);
		String redirLocale = null;

		if (userLocale == null) {
			String[] langs = Web.getAcceptedLanguages(Context.getRequest());
			String browserLocale = langs.length == 0 ? null : langs[0];

			if (!Strings.equal(urlLocale, browserLocale, true)) {
				redirLocale = urlLocale == null ? browserLocale : urlLocale;
			}
		} else {
			if (!userLocale.equalsIgnoreCase(urlLocale)) {
				redirLocale = userLocale;
			}
		}

		return redirLocale == null ? null
		: Web.setURLParameter(url, "locale", redirLocale, false);
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;

		if (MeshCMS.FILE_MANAGER_MODE.equals(mode)) {
			title = Context.getConstants().getString("hostTitleFileManager");
		} else if (MeshCMS.FILE_BROWSER_MODE.equals(mode)) {
			title = Context.getConstants().getString("hostTitleFileBrowser");
			browserPopup = true;
		} else if (MeshCMS.IMAGE_BROWSER_MODE.equals(mode)) {
			title = Context.getConstants().getString("hostTitleImageBrowser");
			browserPopup = true;
		} else if (MeshCMS.PAGE_EDITOR_MODE.equals(mode)) {
			title = Context.getConstants().getString("hostTitlePageEditor");
		} else if (MeshCMS.ZONE_EDITOR_MODE.equals(mode)) {
			title = Context.getConstants().getString("hostTitleZoneEditor");
		} else if (MeshCMS.NEW_PAGE_MODE.equals(mode)) {
			title = Context.getConstants().getString("hostTitleNewPage");
		}
	}

	public boolean isBrowserPopup() {
		return browserPopup;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = new Path(path);
	}

	public boolean isTinyMCE() {
		return siteInfo.isTinyMCE();
	}

	public boolean isCkEditor() {
		return siteInfo.isCKEditor();
	}

	public String getTitle() {
		return title;
	}

	public String getEditorDir() {
		Path editorsPath = rc.getWebSite().getCMSPath(CMSDirectoryItem.EDITORS_DIR);
		Path currentPath = Context.MESHCMS_PATH.add("resources");

		return editorsPath.getRelativeTo(currentPath).toString();
	}
}

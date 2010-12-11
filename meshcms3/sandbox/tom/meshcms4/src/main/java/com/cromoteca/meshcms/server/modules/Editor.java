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
import com.cromoteca.meshcms.client.server.Zone;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.core.Context;
import com.cromoteca.meshcms.server.core.ServerModule;
import com.cromoteca.meshcms.server.toolbox.Web;
import com.cromoteca.meshcms.server.webview.Scope;
import java.util.ResourceBundle;

public class Editor extends ServerModule {
	private Path resPath;
	private ResourceBundle bundle;
	private boolean editBodyZone;

	public Editor() {
		bundle = Context.getConstantsInUserLocale();
		resPath = rc.adjustPath(Context.MESHCMS_PATH.add("resources").asAbsolute());
	}

	public String getZoneName() {
		return getZoneOutput().getZone().getName();
	}

	public boolean isEditBodyZone() {
		return editBodyZone;
	}

	public void setEditBodyZone(boolean editBodyZone) {
		this.editBodyZone = editBodyZone;
	}

	public boolean isBody() {
		return Zone.BODY_ZONE.equals(getZoneName());
	}

	public boolean isEditable() {
		return rc.isEditable();
	}

	public Path getResPath() {
		return resPath;
	}

	public boolean isNoDraft() {
		return rc.isNoDraft();
	}

	public String getDraftLink() {
		return Web.setURLParameter(rc.getURL().toString(), Context.ACTION_NAME,
			null, false);
	}

	public String getViewDraftLabel() {
		return bundle.getString("editPageViewDraft");
	}

	public boolean isHasDraft() {
		boolean hasDraft = false;
		Page page = Context.getScopedSingleton(Page.class, Scope.REQUEST);

		if (page != null) {
			hasDraft = page.isDraft()
						|| page.getPageConfiguration().getDraft() != null;
		}

		return hasDraft;
	}

	public String getNoDraftLink() {
		return Web.setURLParameter(rc.getURL().toString(), Context.ACTION_NAME,
			Context.ACTION_NO_DRAFT, false);
	}

	public String getViewPublishedLabel() {
		return bundle.getString("editPageViewPublished");
	}

	public String getEditPageLabel() {
		return bundle.getString("editPageEdit");
	}

	public String getCreatePageLabel() {
		return bundle.getString("editPageCreate");
	}

	public String getFileManagerLabel() {
		return bundle.getString("editPageFileManager");
	}

	public String getPopupsLabel() {
		return bundle.getString("editPageUsePopups");
	}

	public String getEditZoneLabel() {
		return bundle.getString("editZoneTitle");
	}

	public boolean isUser() {
		return Context.getUser() != null;
	}
}

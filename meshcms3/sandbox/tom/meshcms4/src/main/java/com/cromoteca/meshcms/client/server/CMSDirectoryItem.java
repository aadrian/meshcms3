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

import com.cromoteca.meshcms.client.core.MeshCMS;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.client.ui.controlpanel.ControlPanel;
import com.cromoteca.meshcms.client.ui.controlpanel.FileManagerPanel;
import com.cromoteca.meshcms.client.ui.controlpanel.ModuleConfigEditor;
import com.cromoteca.meshcms.client.ui.controlpanel.ServerConfigEditor;
import com.cromoteca.meshcms.client.ui.controlpanel.SiteConfigEditor;
import com.cromoteca.meshcms.client.ui.controlpanel.UserProfileEditor;
import com.cromoteca.meshcms.client.ui.controlpanel.VirtualSitesEditor;
import com.google.gwt.resources.client.ImageResource;
import java.io.Serializable;

/**
 * Stores info about CMS directories and files.
 */
public class CMSDirectoryItem implements Serializable {
	public static final int NORMAL_DIR = 0;
	public static final int NORMAL_DIR_WITH_PAGES = -1;
	public static final int ROOT_DIR = -2;
	public static final int CMS_DIR = 1;
	public static final int USER_PROFILE_DIR = 2;
	public static final int SITE_DIR = 3;
	public static final int SITE_CONFIGURATION_DIR = 4;
	public static final int SITE_CONFIGURATION_FILE = 5;
	public static final int SITE_PAGES_DATA_FILE = 6;
	public static final int SITE_THEMES_DIR = 7;
	public static final int SITE_MODULES_DIR = 8;
	public static final int SITE_REPOSITORY_DIR = 9;
	public static final int SITE_USERS_DIR = 10;
	public static final int SERVER_DIR = 11;
	public static final int EDITORS_DIR = 12;
	public static final int SERVER_CONFIGURATION_DIR = 13;
	public static final int SERVER_CONFIGURATION_FILE = 14;
	public static final int SITES_DIR = 15;
	public static final int SITES_CONFIGURATION_FILE = 16;
	public static final int SERVER_THEMES_DIR = 17;
	public static final int SERVER_MODULES_DIR = 18;
	private Path path;
	private String name;
	private boolean admin;
	private boolean directory;
	private boolean hidden; // TODO: use it
	private int code;

	public CMSDirectoryItem(FileInfo fileInfo) {
		path = fileInfo.getPath();
		directory = fileInfo.isDirectory();

		if (path.isRoot()) {
			name = "root";
			code = ROOT_DIR;
		} else {
			code = fileInfo.getPageInfo() == null ? NORMAL_DIR : NORMAL_DIR_WITH_PAGES;
		}
	}

	public CMSDirectoryItem(int code, String name, Path path, boolean directory,
		boolean hidden, boolean admin) {
		this.code = code;
		this.name = name;
		this.path = path;
		this.directory = directory;
		this.hidden = hidden;
		this.admin = admin;
	}

	private CMSDirectoryItem() {}

/**
 * Gets the icon related to this CMS item.
 * @return
 */
	public ImageResource getIcon() {
		ImageResource icon;

		switch (code) {
			case NORMAL_DIR:
				icon = MeshCMS.ICONS_BUNDLE.folderOpen();

				break;

			case NORMAL_DIR_WITH_PAGES:
				icon = MeshCMS.ICONS_BUNDLE.folderOpenImage();

				break;

			case ROOT_DIR:
				icon = MeshCMS.ICONS_BUNDLE.home();

				break;

			case CMS_DIR:
				icon = MeshCMS.ICONS_BUNDLE.bookOpenBookmark();

				break;

			case USER_PROFILE_DIR:
				icon = MeshCMS.ICONS_BUNDLE.user();

				break;

			case SITE_DIR:
				icon = MeshCMS.ICONS_BUNDLE.globe();

				break;

			case SITE_CONFIGURATION_DIR:
				icon = MeshCMS.ICONS_BUNDLE.equalizer();

				break;

			case SITE_THEMES_DIR:
				icon = MeshCMS.ICONS_BUNDLE.blogsStack();

				break;

			case SITE_MODULES_DIR:
				icon = MeshCMS.ICONS_BUNDLE.block();

				break;

			case SITE_REPOSITORY_DIR:
				icon = MeshCMS.ICONS_BUNDLE.drawerOpen();

				break;

			case SITE_USERS_DIR:
				icon = MeshCMS.ICONS_BUNDLE.users();

				break;

			case SERVER_DIR:
				icon = MeshCMS.ICONS_BUNDLE.server();

				break;

			case EDITORS_DIR:
				icon = MeshCMS.ICONS_BUNDLE.pencil();

				break;

			case SERVER_CONFIGURATION_DIR:
				icon = MeshCMS.ICONS_BUNDLE.equalizer();

				break;

			case SITES_DIR:
				icon = MeshCMS.ICONS_BUNDLE.globeNetwork();

				break;

			case SERVER_THEMES_DIR:
				icon = MeshCMS.ICONS_BUNDLE.blogsStack();

				break;

			case SERVER_MODULES_DIR:
				icon = MeshCMS.ICONS_BUNDLE.block();

				break;

			default:
				icon = MeshCMS.ICONS_BUNDLE.question();
		}

		return icon;
	}

	/**
	 * Gets the (translated) label for this CMS item.
	 * @return
	 */
	public String getLabel() {
		return name == null ? getPath().getLastElement()
		: MeshCMS.getDynamicTranslation("filesystem_", name);
	}

	public FileManagerPanel getFileManagerPanel() {
		FileManagerPanel panel;

		switch (code) {
			case CMS_DIR:
				panel = new ControlPanel();

				break;

			case USER_PROFILE_DIR:
				panel = new UserProfileEditor(false);

				break;

			case SITE_CONFIGURATION_DIR:
				panel = new SiteConfigEditor();

				break;

			case SITE_MODULES_DIR:
				panel = new ModuleConfigEditor();

				break;

			case SITE_USERS_DIR:
				panel = new UserProfileEditor(true);

				break;

			case SERVER_CONFIGURATION_DIR:
				panel = new ServerConfigEditor();

				break;

			case SITES_DIR:
				panel = new VirtualSitesEditor();

				break;

			default:
				panel = null;
		}

		return panel;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public boolean isDirectory() {
		return directory;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}

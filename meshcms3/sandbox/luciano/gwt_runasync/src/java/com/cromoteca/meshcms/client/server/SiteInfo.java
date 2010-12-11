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

import com.cromoteca.meshcms.client.toolbox.Path;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Contains information needed by the client GWT application.&nbsp;Fetched at
 * startup.
 */
public class SiteInfo implements Serializable {
	private transient Map<Path, CMSDirectoryItem> cmsDirectoryItemMap;
	private Map<String, Module> modules;
	private Map<String, Theme> themes;
	private Path cmsPath;
	private Path userCSS;
	private Set<CMSDirectoryItem> cmsDirectoryItems;
	private String contextPath;
	private boolean ckEditor;
	private boolean editArea;
	private boolean tinyMCE;

	public CMSDirectoryItem getCMSDirectoryItem(FileInfo fileInfo) {
		CMSDirectoryItem cmsDirectoryItem = null;
		Path path = fileInfo.getPath();

		if (path.isContainedIn(cmsPath)) {
			cmsDirectoryItem = getCMSDirectoryItem(path);
		}

		if (cmsDirectoryItem == null) {
			cmsDirectoryItem = new CMSDirectoryItem(fileInfo);
		}

		return cmsDirectoryItem;
	}

	public CMSDirectoryItem getCMSDirectoryItem(Path path) {
		if (cmsDirectoryItemMap == null) {
			cmsDirectoryItemMap = new HashMap<Path, CMSDirectoryItem>();

			for (CMSDirectoryItem cmsDirectoryItem : cmsDirectoryItems) {
				cmsDirectoryItemMap.put(cmsDirectoryItem.getPath(), cmsDirectoryItem);
			}
		}

		return cmsDirectoryItemMap.get(path);
	}

	public Map<Path, CMSDirectoryItem> getCMSDirectoryItemMap() {
		return cmsDirectoryItemMap;
	}

	public Map<String, Module> getModules() {
		return modules;
	}

	public Map<String, Theme> getThemes() {
		return themes;
	}

	public Path getCMSPath() {
		return cmsPath;
	}

	public void setCMSPath(Path cmsPath) {
		this.cmsPath = cmsPath;
	}

	public Set<CMSDirectoryItem> getCMSDirectoryItems() {
		return cmsDirectoryItems;
	}

	public void setCMSDirectoryItems(Set<CMSDirectoryItem> cmsDirectoryItems) {
		this.cmsDirectoryItems = cmsDirectoryItems;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public void setModules(Map<String, Module> modules) {
		this.modules = modules;
	}

	public void setThemes(Map<String, Theme> themes) {
		this.themes = themes;
	}

	public boolean isCKEditor() {
		return ckEditor;
	}

	public void setCKEditor(boolean ckEditor) {
		this.ckEditor = ckEditor;
	}

	public boolean isEditArea() {
		return editArea;
	}

	public void setEditArea(boolean editArea) {
		this.editArea = editArea;
	}

	public boolean isTinyMCE() {
		return tinyMCE;
	}

	public void setTinyMCE(boolean tinyMCE) {
		this.tinyMCE = tinyMCE;
	}

	public Path getUserCSS() {
		return userCSS;
	}

	public void setUserCSS(Path userCSS) {
		this.userCSS = userCSS;
	}
}

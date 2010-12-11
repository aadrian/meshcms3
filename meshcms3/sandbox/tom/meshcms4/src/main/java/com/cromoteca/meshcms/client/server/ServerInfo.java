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

import java.io.Serializable;

/**
 * Contains some information about the MeshCMS installation that is good to
 * show in the Control Panel.
 */
public class ServerInfo implements Serializable {
	private String charset;
	private String version;
	private UserProfile user;
	private int pageCount;
	private int totalMB;
	private int usedMB;

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public UserProfile getUser() {
		return user;
	}

	public void setUser(UserProfile user) {
		this.user = user;
	}

	public int getTotalMB() {
		return totalMB;
	}

	public void setTotalMB(int totalMB) {
		this.totalMB = totalMB;
	}

	public int getUsedMB() {
		return usedMB;
	}

	public void setUsedMB(int usedMB) {
		this.usedMB = usedMB;
	}
}

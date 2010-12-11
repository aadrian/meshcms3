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

import com.cromoteca.meshcms.client.server.CMSDirectoryItem;
import com.cromoteca.meshcms.client.server.UserProfile;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.toolbox.Strings;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class SessionUser implements Serializable {
	private String username;
	private User user;
	private boolean global;

	public SessionUser(UserProfile profile) {
		user = new User(profile);
		username = profile.getUsername();
	}

	private SessionUser() {}

	public static SessionUser load(String username, String unencryptedPassword)
		throws IOException {
		String encryptedPassword = User.encryptPassword(unencryptedPassword);
		WebSite webSite = Context.getRequestContext().getWebSite();
		SessionUser sessionUser = new SessionUser();
		sessionUser.username = username;

		User loadedUser = User.load(webSite, username, encryptedPassword);

		if (loadedUser == null) {
			boolean virtual = webSite.getType() != WebSiteType.MAIN;
			WebSite mainWebSite = Context.getServer().getMainWebSite();

			if (virtual) {
				loadedUser = User.load(mainWebSite, username, encryptedPassword);

				if (loadedUser != null
							&& loadedUser.isAdmin()
							&& loadedUser.getHome().isRoot()) {
					sessionUser.global = true;
				}
			}

			if (loadedUser == null) {
				File file = mainWebSite.getCMSFile(CMSDirectoryItem.SITE_USERS_DIR);

				if (file.list().length == 0
							&& User.DEFAULT_ADMIN_USERNAME.equals(username)
							&& User.DEFAULT_ADMIN_PASSWORD.equals(unencryptedPassword)) {
					loadedUser = new User(unencryptedPassword, true, true,
							Server.DEFAULT_LOCALE, Path.ROOT);
					sessionUser.global = virtual;
					loadedUser.store(mainWebSite, User.DEFAULT_ADMIN_USERNAME);
				}
			}
		}

		if (loadedUser == null) {
			return null;
		}

		sessionUser.user = loadedUser;

		return sessionUser;
	}

	public boolean isGlobal() {
		return global;
	}

	public User getUser() {
		return user;
	}

	public String getUsername() {
		return username;
	}

	public String getDisplayName() {
		String displayName = user.getName();

		if (Strings.isNullOrEmpty(displayName)) {
			displayName = username;
		}

		return displayName;
	}

	@Override
	public String toString() {
		return username;
	}

	public UserProfile getUserProfile() {
		return user.getUserProfile(username);
	}

	public void setUserProfile(UserProfile profile) {
		user.setUserProfile(profile);
	}

	public void store() throws IOException {
		WebSite webSite = global ? Context.getServer().getMainWebSite()
			: Context.getRequestContext().getWebSite();
		user.store(webSite, username);
	}

	public boolean isEditor() {
		return user.isEditor();
	}

	public boolean isAdmin() {
		return user.isAdmin();
	}

	public String getName() {
		return user.getName();
	}

	public String getLocale() {
		return user.getLocale();
	}

	public Path getHome() {
		return user.getHome();
	}

	public String getEmail() {
		return user.getEmail();
	}
}

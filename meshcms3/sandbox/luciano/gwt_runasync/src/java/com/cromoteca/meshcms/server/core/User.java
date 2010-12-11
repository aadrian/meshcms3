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
import com.cromoteca.meshcms.server.storage.File;
import com.cromoteca.meshcms.server.toolbox.Strings;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
	public static final String DEFAULT_ADMIN_USERNAME = "admin";
	public static final String DEFAULT_ADMIN_PASSWORD = "admin";
	private Map<String, String> preferences;
	private Path home;
	private String email;
	private String encryptedPassword;
	private String locale;
	private String name;
	private boolean admin;
	private boolean editor;

	public User(UserProfile profile) {
		this();
		encryptedPassword = encryptPassword(profile.getUnencryptedPassword());
		name = profile.getName();
		email = profile.getEmail();
		admin = profile.isAdmin();
		editor = profile.isEditor();
		locale = profile.getLocale();
		home = profile.getHome();
	}

	protected User(String unencryptedPassword, boolean admin, boolean editor,
		String locale, Path home) {
		this();
		setUnencryptedPassword(unencryptedPassword);
		this.admin = admin;
		this.editor = editor;
		this.locale = locale;
		this.home = home;
	}

	private User() {
		preferences = new HashMap<String, String>();
	}

	public Map<String, String> getPreferences() {
		return preferences;
	}

	public static File getFile(WebSite webSite, String username) {
		return webSite.getCMSFile(CMSDirectoryItem.SITE_USERS_DIR)
				.getDescendant(username + Server.JSON_EXTENSION);
	}

	static User load(WebSite webSite, String username, String encryptedPassword)
		throws IOException {
		User loadedUser = null;
		File userFile = getFile(webSite, username);

		if (userFile.exists()) {
			loadedUser = Context.loadFromJSON(User.class, userFile);

			if (!encryptedPassword.equals(loadedUser.encryptedPassword)) {
				loadedUser = null;
			}
		}

		return loadedUser;
	}

	protected void store(WebSite webSite, String username)
		throws IOException {
		File file = getFile(webSite, username);
		Context.storeToJSON(this, file);
	}

	public Path getHome() {
		return home;
	}

	public void setHome(Path home) {
		this.home = home;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public boolean isEditor() {
		return editor;
	}

	public void setEditor(boolean editor) {
		this.editor = editor;
	}

	public void setUnencryptedPassword(String unencryptedPassword) {
		encryptedPassword = encryptPassword(unencryptedPassword);
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	static String encryptPassword(String unencryptedPassword) {
		return Strings.getMD5(unencryptedPassword);
	}

	public boolean canWrite(Path path) {
		return editor && path.isContainedIn(home);
	}

	public UserProfile getUserProfile(String username) {
		UserProfile profile = new UserProfile();
		profile.setUsername(username);
		profile.setName(name);
		profile.setEmail(email);
		profile.setLocale(locale);
		profile.setHome(home);
		profile.setAdmin(admin);
		profile.setEditor(editor);

		return profile;
	}

	public void setUserProfile(UserProfile profile) {
		name = profile.getName();
		email = profile.getEmail();
		locale = profile.getLocale();

		String unencryptedPassword = profile.getUnencryptedPassword();

		if (!Strings.isNullOrEmpty(unencryptedPassword)) {
			setUnencryptedPassword(unencryptedPassword);
		}
	}
}

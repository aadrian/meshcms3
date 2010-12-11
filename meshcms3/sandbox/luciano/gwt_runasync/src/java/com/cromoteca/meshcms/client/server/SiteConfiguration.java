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
import com.cromoteca.meshcms.client.toolbox.Time;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the configuration parameters of a webSite.
 */
public class SiteConfiguration implements Serializable {
	public static final int TIDY_NO = 0;
	public static final int TIDY_YES = 1;
	public static final int TIDY_ASK = 2;
	private Map<String, String> moduleParameters;
	private Path userCSS;
	private String locale;
	private String siteHost;
	private String siteName;
	private String siteOwner;
	private String siteOwnerURL;
	private String siteSlogan;
	private boolean hideExceptions;
	private boolean overrideLocale;
	private boolean redirectRoot;
	private boolean replaceThumbnails;
	private boolean searchMovedPages;
	private int excerptLength;
	private int tidy;
	private int updateInterval;

	public SiteConfiguration() {
		setHideExceptions(false);
		setReplaceThumbnails(true);
		setSearchMovedPages(false);
		setRedirectRoot(true);
		setUpdateInterval(60);
		setTidy(TIDY_NO);
		setSiteOwner("your name");
		setSiteOwnerURL("http://www.yoursite.com/yourpage");
		setSiteName("Site name");
		setSiteSlogan("Change this text in the site configuration");
		setSiteHost("www.thissite.com");
		setLocale("en");
		setOverrideLocale(true);
		setExcerptLength(400);
		moduleParameters = new HashMap<String, String>();
	}

	/**
	 * Returns the minimum interval between two updates of the site map, measured
	 * in milliseconds.
	 */
	public long getUpdateIntervalMillis() {
		return updateInterval * (long) Time.MINUTE;
	}

	public String getModuleParameter(String moduleName, String propertyName,
		String defaultValue) {
		String value = moduleParameters.get(moduleName + ':' + propertyName);

		return value == null ? defaultValue : value;
	}

	public Map<String, String> getModuleParameteres(String moduleName) {
		Map<String, String> params = new HashMap<String, String>();
		moduleName += ':';

		for (String key : moduleParameters.keySet()) {
			if (key.startsWith(moduleName)) {
				params.put(key.substring(moduleName.length()), moduleParameters.get(key));
			}
		}

		return params;
	}

	public void setModuleParameter(String moduleName, String propertyName,
		String value) {
		moduleParameters.put(moduleName + ':' + propertyName, value);
	}

	public Path getUserCSS() {
		return userCSS;
	}

	public void setUserCSS(Path userCSS) {
		this.userCSS = userCSS;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getSiteSlogan() {
		return siteSlogan;
	}

	public void setSiteSlogan(String siteSlogan) {
		this.siteSlogan = siteSlogan;
	}

	public String getSiteOwner() {
		return siteOwner;
	}

	public void setSiteOwner(String siteOwner) {
		this.siteOwner = siteOwner;
	}

	public String getSiteOwnerURL() {
		return siteOwnerURL;
	}

	public void setSiteOwnerURL(String siteOwnerURL) {
		this.siteOwnerURL = siteOwnerURL;
	}

	public String getSiteHost() {
		return siteHost;
	}

	public void setSiteHost(String siteHost) {
		this.siteHost = siteHost;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public boolean isHideExceptions() {
		return hideExceptions;
	}

	public void setHideExceptions(boolean hideExceptions) {
		this.hideExceptions = hideExceptions;
	}

	public boolean isOverrideLocale() {
		return overrideLocale;
	}

	public void setOverrideLocale(boolean overrideLocale) {
		this.overrideLocale = overrideLocale;
	}

	public boolean isRedirectRoot() {
		return redirectRoot;
	}

	public void setRedirectRoot(boolean redirectRoot) {
		this.redirectRoot = redirectRoot;
	}

	public boolean isReplaceThumbnails() {
		return replaceThumbnails;
	}

	public void setReplaceThumbnails(boolean replaceThumbnails) {
		this.replaceThumbnails = replaceThumbnails;
	}

	public boolean isSearchMovedPages() {
		return searchMovedPages;
	}

	public void setSearchMovedPages(boolean searchMovedPages) {
		this.searchMovedPages = searchMovedPages;
	}

	public int getExcerptLength() {
		return excerptLength;
	}

	public void setExcerptLength(int excerptLength) {
		this.excerptLength = excerptLength;
	}

	public int getTidy() {
		return tidy;
	}

	public void setTidy(int tidy) {
		this.tidy = tidy;
	}

	public int getUpdateInterval() {
		return updateInterval;
	}

	public void setUpdateInterval(int updateInterval) {
		this.updateInterval = updateInterval;
	}
}

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

import com.cromoteca.meshcms.client.server.MenuPolicy;
import com.cromoteca.meshcms.client.server.Zone;
import com.cromoteca.meshcms.client.toolbox.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class describes a web page with all the related info (path, title, hits
 * and so on).
 */
public final class PageInfo {
	private List<String> keywords;
	private Map<String, String> headProperties;
	private Map<String, Zone> inheritableZones;
	private MenuPolicy menuPolicy;
	private Path path;
	private String excerpt;
	private String menuTitle;
	private String title;
	private int[] stats;
	private int lastStatsIndex;
	private int statSum;
	private long creationDate;
	private long lastModified;

	/**
	 * Creates a page info in the specified {@link WebSite} to describe the page
	 * available at the specified path.
	 */
	public PageInfo(Path path) {
		this.path = path;
		stats = new int[Context.getServer().getServerConfiguration().getStatsLength()];
		headProperties = new HashMap<String, String>();
		inheritableZones = new HashMap<String, Zone>();
	}

	public Map<String, Zone> getInheritableZones() {
		return inheritableZones;
	}

	public Map<String, String> getHeadProperties() {
		return headProperties;
	}

	/**
	 * Adds a hit to the count.
	 */
	public synchronized void addHit() {
		stats[getIndex()]++;
		statSum++;
	}

	protected synchronized void copyStatsFrom(PageInfo other) {
		getIndex(); // to update index before copying
		statSum = other.getTotalHits(); // this one calls other.getIndex()
		stats = other.getStats();
		lastStatsIndex = other.getLastStatsIndex();
	}

	/**
	 * Returns the hit count for the last day.
	 */
	public synchronized int getHits() {
		return stats[getIndex()];
	}

	/**
	 * Returns the hit count for a previous day.
	 */
	public synchronized int getHits(int daysBefore) {
		int index = getIndex() - daysBefore;

		return stats[index < 0 ? index + stats.length : index];
	}

	private synchronized int getIndex() {
		int index = Context.getServer().getStatsIndex();

		if (index != lastStatsIndex) {
			lastStatsIndex = index;
			statSum -= stats[index];
			stats[index] = 0;
		}

		return index;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	/**
	 * Returns the time of the last modification made to the page.
	 */
	public long getLastModified() {
		return lastModified;
	}

	protected synchronized int getLastStatsIndex() {
		return lastStatsIndex;
	}

	/**
	 * Returns the depth level of the page.
	 */
	public int getLevel() {
		return path.getElementCount();
	}

	/**
	 * Returns the file name of the page.
	 */
	public String getName() {
		return path.getLastElement();
	}

	/**
	 * Returns the path of the page.
	 */
	public Path getPath() {
		return path;
	}

	protected synchronized int[] getStats() {
		return stats;
	}

	/**
	 * Returns the total hit count.
	 */
	public synchronized int getTotalHits() {
		getIndex(); // to update index before sum

		return statSum;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	/**
	 * Sets the time of the last modification made to the page. This value should
	 * be set equal to the value of <code>java.io.File.lastModified()</code>.
	 */
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public String getMenuTitle() {
		return menuTitle;
	}

	public void setMenuTitle(String menuTitle) {
		this.menuTitle = menuTitle;
	}

	public String getExcerpt() {
		return excerpt;
	}

	public void setExcerpt(String excerpt) {
		this.excerpt = excerpt;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "Title: " + title + ", Path: " + path;
	}

	public MenuPolicy getMenuPolicy() {
		return menuPolicy;
	}

	public void setMenuPolicy(MenuPolicy menuPolicy) {
		this.menuPolicy = menuPolicy;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}
}

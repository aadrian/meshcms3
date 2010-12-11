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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Stores information about files and directories.
 */
public class FileInfo implements Serializable, Comparable<FileInfo> {
	public static final FileInfo ROOT;

	static {
		ROOT = new FileInfo(Path.ROOT);
	}

	private Date lastModified;
	private PageInfo pageInfo;
	private Path path;
	private String length;
	private String name;
	private boolean directory;

	public FileInfo() {}

	public FileInfo(Path path) {
		setPath(path);
	}

	/**
	 * Sets all page info values at once.
	 * @param menuTitle
	 * @param score
	 * @param welcome
	 * @param theme
	 */
	public void setPageInfoValues(String menuTitle, int score, boolean welcome,
		String theme) {
		setPageInfo(new PageInfo(menuTitle, score, welcome, theme));
	}

	public void setPath(Path path) {
		this.path = path;
		name = path.getLastElement();
	}

	/**
	 * Translates a list of FileInfo in a list of Paths.
	 * @param list
	 * @return
	 */
	public static List<Path> getPaths(Iterable<FileInfo> list) {
		List<Path> paths = new ArrayList<Path>();

		for (FileInfo fileInfo : list) {
			paths.add(fileInfo.path);
		}

		return paths;
	}

	public int compareTo(FileInfo other) {
		int result = path.getParent().compareTo(other.path.getParent());

		if (result == 0) {
			if (directory && !other.directory) {
				result = -1;
			} else if (!directory && other.directory) {
				result = 1;
			} else {
				result = name.toLowerCase().compareTo(other.name.toLowerCase());
			}
		}

		return result;
	}

	/**
	 * @return the lastModified
	 */
	public Date getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * @return the pageInfo
	 */
	public PageInfo getPageInfo() {
		return pageInfo;
	}

	/**
	 * @param pageInfo the pageInfo to set
	 */
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	/**
	 * @return the path
	 */
	public Path getPath() {
		return path;
	}

	/**
	 * @return the length
	 */
	public String getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(String length) {
		this.length = length;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the directory
	 */
	public boolean isDirectory() {
		return directory;
	}

	/**
	 * @param directory the directory to set
	 */
	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	/**
	 * Stores additional data for files that are pages.
	 */
	public static class PageInfo implements Serializable {
		private String theme;
		private String title;
		private boolean welcome;
		private int score;

		public PageInfo() {}

		public PageInfo(String title, int score, boolean welcome, String theme) {
			this.title = title;
			this.score = score;
			this.welcome = welcome;
			this.theme = theme;
		}

		public String getTheme() {
			return theme;
		}

		public void setTheme(String theme) {
			this.theme = theme;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public boolean isWelcome() {
			return welcome;
		}

		public void setWelcome(boolean welcome) {
			this.welcome = welcome;
		}

		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}
	}
}

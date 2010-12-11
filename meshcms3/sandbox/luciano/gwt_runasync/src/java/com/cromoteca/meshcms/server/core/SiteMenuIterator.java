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

import com.cromoteca.meshcms.client.toolbox.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SiteMenuIterator implements Iterator {
	private PageInfo nextPage;
	private Path pagePath;
	private Path rootPath;
	private SiteMapIterator iter;
	private boolean allItems;
	private boolean childItems;
	private boolean firstLevelItems;
	private boolean intermediateLevelItems;
	private boolean nextPageChecked;
	private boolean onPathItems;
	private boolean sameLevelItems;

	/**
	 * Creates an iterator for the full a submap of the given webSite, starting
	 * from the specified root path.
	 */
	public SiteMenuIterator(Path rootPath, Path pagePath) {
		this.rootPath = rootPath;
		this.pagePath = pagePath;
		iter = new SiteMapIterator(rootPath);
		iter.setSkipHiddenPages(true);
	}

	private boolean findNextPage() {
		if (!nextPageChecked) {
			nextPage = iter.getNextPage();

			if (nextPage != null) {
				if (!allItems) {
					boolean drop = true;
					Path itemPath = nextPage.getPath();

					if (drop && onPathItems) {
						drop = !pagePath.isContainedIn(itemPath);
					}

					if (drop && childItems) {
						drop = !itemPath.getParent().equals(pagePath);
					}

					if (drop && sameLevelItems) {
						drop = !itemPath.getParent().equals(pagePath.getParent());
					}

					if (drop && firstLevelItems) {
						drop = !(itemPath.getElementCount() == rootPath.getElementCount()
									+ 1 || itemPath.equals(rootPath));
					}

					if (drop
								&& intermediateLevelItems
								&& itemPath.getElementCount() < pagePath.getElementCount()
								&& itemPath.getElementCount() > rootPath.getElementCount()
								&& !pagePath.isContainedIn(itemPath)) {
						Path p = pagePath.getPartial(itemPath.getElementCount() - 1);
						drop = !itemPath.isChildOf(p);
					}

					if (drop) {
						nextPage = getNextPage();
					}
				}
			}
		}

		nextPageChecked = true;

		return nextPage != null;
	}

	/**
	 * Returns the next page (same as {@link #next}, but returns null when there
	 * are no more pages.
	 */
	public PageInfo getNextPage() {
		findNextPage();
		nextPageChecked = false;

		return nextPage;
	}

	public boolean hasNext() {
		return findNextPage();
	}

	public Object next() {
		if (!findNextPage()) {
			throw new NoSuchElementException();
		}

		return getNextPage();
	}

	public void remove() {
		throw new UnsupportedOperationException("Site map is readonly");
	}

	public void setAllItems(boolean allItems) {
		this.allItems = allItems;
	}

	public void setChildItems(boolean childItems) {
		this.childItems = childItems;
	}

	public void setFirstLevelItems(boolean firstLevelItems) {
		this.firstLevelItems = firstLevelItems;
	}

	public void setIntermediateLevelItems(boolean intermediateLevelItems) {
		this.intermediateLevelItems = intermediateLevelItems;
	}

	public void setSameLevelItems(boolean sameLevelItems) {
		this.sameLevelItems = sameLevelItems;
	}

	public void setOnPathItems(boolean onPathItems) {
		this.onPathItems = onPathItems;
	}
}

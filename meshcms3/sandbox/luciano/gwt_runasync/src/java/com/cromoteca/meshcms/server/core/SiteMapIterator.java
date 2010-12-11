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

/**
 * Iterator for the site map.
 */
public class SiteMapIterator implements Iterator {
	private Iterator iter;
	private PageInfo nextPage;
	private SiteMap siteMap;
	private boolean nextPageChecked;
	private boolean skipHiddenPages;

	/**
	 * Creates an iterator for the full site map of the given webSite.
	 */
	public SiteMapIterator() {
		this(Path.ROOT);
	}

	/**
	 * Creates an iterator for the full a submap of the given webSite, starting
	 * from the specified root path.
	 */
	public SiteMapIterator(Path root) {
		siteMap = Context.getRequestContext().getSiteMap();
		iter = siteMap.getPageList(root).iterator();
	}

	private boolean findNextPage() {
		if (!nextPageChecked) {
			nextPage = null;

			while (nextPage == null && iter.hasNext()) {
				nextPage = (PageInfo) iter.next();

				if (nextPage != null
							&& skipHiddenPages
							&& siteMap.isHidden(nextPage.getPath())) {
					nextPage = null;
				}
			}

			nextPageChecked = true;
		}

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

	public boolean isSkipHiddenPages() {
		return skipHiddenPages;
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

	public void setSkipHiddenPages(boolean skipHiddenPages) {
		this.skipHiddenPages = skipHiddenPages;
	}
}

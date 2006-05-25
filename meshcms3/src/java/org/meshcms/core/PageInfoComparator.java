/*
 * MeshCMS - A simple CMS based on SiteMesh
 * Copyright (C) 2004-2006 Luciano Vernaschi
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * You can contact the author at http://www.cromoteca.com
 * and at info@cromoteca.com
 */

package org.meshcms.core;

import java.util.*;
import org.meshcms.util.*;

/**
 * Compares two object of type {@link PageInfo} by comparing paths, scores and
 * hits. This comparator can <em>only</em> be used with pages contained in the
 * site map.
 *
 * @see SiteMap
 */
public final class PageInfoComparator implements Comparator {
  private SiteInfo siteInfo;
  private SiteMap siteMap;

  public PageInfoComparator(WebSite webSite) {
    siteInfo = webSite.getSiteInfo();
    siteMap = webSite.getSiteMap();
  }

  /**
   * Calls {@link #compare(PageInfo, PageInfo)}.
   */
  public int compare(Object o1, Object o2) {
    try {
      return compare((PageInfo) o1, (PageInfo) o2);
    } catch (ClassCastException ex) {}

    return 0;
  }

  /**
   * Compares two pages. The first criterion is the path of the page: pages
   * are sorted comparing the first different element of the path.
   */
  public int compare(PageInfo pageInfo1, PageInfo pageInfo2) {
    Path path1 = pageInfo1.getPath();
    Path path2 = pageInfo2.getPath();

    if (path1.equals(path2)) { // same path == same page
      return 0;
    }

    if (path1.isRoot()) { // root always come first
      return -1;
    }

    if (path2.isRoot()) { // root always come first
      return 1;
    }

    Path commonPath = path1.getCommonPath(path2);

    if (commonPath.equals(path1)) { // path2 is contained in path1
      return -1;
    }

    if (commonPath.equals(path2)) { // path1 is contained in path2
      return 1;
    }

    // compare the paths up to the first different element. For example, if
    // path1 == /home/subdir/otherdir/page.html, and
    // path2 == /home/subdir/otherpage.html,
    // we compare /home/subdir/otherdir to /home/subdir/otherpage.html
    Path subPath1 = path1.getPartial(commonPath.getElementCount() + 1);
    Path subPath2 = path2.getPartial(commonPath.getElementCount() + 1);
    return compareSameLevel(subPath1, subPath2);
  }

  private int compareSameLevel(Path path1, Path path2) {
    // compare scores
    int score1 = siteInfo.getPageScore(path1);
    int score2 = siteInfo.getPageScore(path2);

    if (score1 > score2) {
      return -1;
    }

    if (score1 < score2) {
      return 1;
    }

    // Get page infos and compares them by hit counts.
    PageInfo pageInfo1 = siteMap.getPageInfo(path1);
    PageInfo pageInfo2 = siteMap.getPageInfo(path2);

    if (pageInfo1.getTotalHits() > pageInfo2.getTotalHits()) {
      return -1;
    }

    if (pageInfo1.getTotalHits() < pageInfo2.getTotalHits()) {
      return 1;
    }

    return 0;
  }
}

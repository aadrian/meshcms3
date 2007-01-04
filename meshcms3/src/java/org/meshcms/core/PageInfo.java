/*
 * MeshCMS - A simple CMS based on SiteMesh
 * Copyright (C) 2004-2007 Luciano Vernaschi
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

import org.meshcms.util.*;

/**
 * This class describes a web page with all the related info (path, title, hits
 * and so on).
 */
public final class PageInfo {
  private Path path;
  private String title;
  private int[] stats;
  private int statSum;
  private int lastStatsIndex;
  private WebSite webSite;
  private long lastModified;
  private String charset;

  /**
   * Creates a page info in the specified {@link WebSite} to describe the page
   * available at the specified path.
   */
  public PageInfo(WebSite webSite, Path path) {
    this.webSite = webSite;
    this.path = path;
    stats = new int[webSite.getStatsLength()];
  }

  /**
   * @return the path of the page.
   */
  public Path getPath() {
    return path;
  }

  /**
   * Sets the title of the page.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return the title of the page.
   */
  public String getTitle() {
    return title;
  }

  /**
   * @return the file name of the page.
   */
  public String getName() {
    return path.getLastElement();
  }

  /**
   * Adds a hit to the count.
   */
  public synchronized void addHit() {
    stats[getIndex()]++;
    statSum++;
  }

  /**
   * @return the hit count for the last day.
   */
  public synchronized int getHits() {
    return stats[getIndex()];
  }

  /**
   * @return the hit count for a previous day.
   */
  public synchronized int getHits(int daysBefore) {
    int index = getIndex() - daysBefore;
    return stats[index < 0 ? index + stats.length : index];
  }

  /**
   * @return the total hit count.
   */
  public synchronized int getTotalHits() {
    getIndex(); // to update index before sum
    return statSum;
  }

  protected synchronized int[] getStats() {
    return stats;
  }

  protected synchronized int getLastStatsIndex() {
    return lastStatsIndex;
  }

  protected synchronized void copyStatsFrom(PageInfo other) {
    getIndex(); // to update index before copying
    statSum = other.getTotalHits(); // this one calls other.getIndex()
    stats = other.getStats();
    lastStatsIndex = other.getLastStatsIndex();
  }

  private synchronized int getIndex() {
    int index = webSite.getStatsIndex();

    if (index != lastStatsIndex) {
      lastStatsIndex = index;
      statSum -= stats[index];
      stats[index] = 0;
    }

    return index;
  }

  /**
   * @return the depth level of the page.
   */
  public int getLevel() {
    return path.getElementCount();
  }

  /**
   * @return the title of the page (same as {@link #getTitle}).
   */
  public String toString() {
    return title;
  }

  /**
   * @return the time of the last modification made to the page.
   */
  public long getLastModified() {
    return lastModified;
  }

  /**
   * Sets the time of the last modification made to the page. This value
   * should be set equal to the value of <code>java.io.File.lastModified()</code>.
   */
  public void setLastModified(long lastModified) {
    this.lastModified = lastModified;
  }

  public String getCharset() {
    return charset;
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }
}

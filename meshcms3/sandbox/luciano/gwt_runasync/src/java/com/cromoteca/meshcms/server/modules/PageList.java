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
package com.cromoteca.meshcms.server.modules;

import com.cromoteca.meshcms.client.server.MenuPolicy;
import com.cromoteca.meshcms.client.server.Page;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.core.Context;
import com.cromoteca.meshcms.server.core.PageInfo;
import com.cromoteca.meshcms.server.core.PageParser;
import com.cromoteca.meshcms.server.core.ServerModule;
import com.cromoteca.meshcms.server.core.SiteMap;
import com.cromoteca.meshcms.server.core.WebUtils;
import com.cromoteca.meshcms.server.core.ZoneOutput;
import com.cromoteca.meshcms.server.core.ZoneOutput.ModuleOutput;
import com.cromoteca.meshcms.server.toolbox.IO;
import com.cromoteca.meshcms.server.toolbox.Numbers;
import com.cromoteca.meshcms.server.toolbox.Strings;
import com.cromoteca.meshcms.server.toolbox.Web;
import com.cromoteca.meshcms.server.webview.ServerPage;
import com.glaforge.i18n.io.SmartEncodingInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import net.htmlparser.jericho.Source;

public class PageList extends ServerModule {
	public static DateFormat RSS_DATE_FORMAT = new SimpleDateFormat(
			"EEE, d MMM yyyy HH:mm:ss z");
	public static final DateFormat YEAR_MONTH_DATE_FORMAT = new SimpleDateFormat(
			"yyyyMM");
	private SiteMap siteMap;
	private String date;
	private String newerLink;
	private String olderLink;
	private String path;
	private String tag;
	private boolean showBodies;
	private boolean showDates;
	private boolean showHistoryLinks;
	private boolean showPageImages;
	private boolean showReadMoreLinks;
	private boolean showTags;
	private boolean sortByVisits;
	private int bodyExcerptLength;
	private int firstEntry;
	private int numberOfEntries;
	private int pageImageHeight;
	private int pageImageWidth;

	@Override
	public void run() throws IOException, ServletException {
		ZoneOutput zo = getZoneOutput();

		for (Entry entry : getEntries()) {
			ModuleOutput mo = zo.add();
			mo.setTitle("<a href=\"" + entry.getLink() + "\">" + entry.getTitle()
				+ "</a>");

			String html = zo.runTemplate(getModule().getPath().add("item.jsp"), entry);
			mo.addContent(html);
			html = zo.runTemplate(getModule().getPath().add("item_info.jsp"), entry);
			mo.setNotes(html);
		}

		if (olderLink != null || newerLink != null) {
			String html = zo.runTemplate(getModule().getPath().add("navigation.jsp"),
					this);
			zo.add().addContent(html);
		}
	}

	public void setPageImageHeight(int pageImageHeight) {
		this.pageImageHeight = pageImageHeight;
	}

	public void setPageImageWidth(int pageImageWidth) {
		this.pageImageWidth = pageImageWidth;
	}

	public void setBodyExcerptLength(int bodyExcerptLength) {
		this.bodyExcerptLength = bodyExcerptLength;
	}

	public void setShowReadMoreLinks(boolean showReadMoreLinks) {
		this.showReadMoreLinks = showReadMoreLinks;
	}

	public void setShowDates(boolean showDates) {
		this.showDates = showDates;
	}

	public void setShowBodies(boolean showBodies) {
		this.showBodies = showBodies;
	}

	public void setShowPageImages(boolean showPageImages) {
		this.showPageImages = showPageImages;
	}

	public void setSortByVisits(boolean sortByVisits) {
		this.sortByVisits = sortByVisits;
	}

	public void setFirstEntry(int firstEntry) {
		this.firstEntry = firstEntry;
	}

	public void setNumberOfEntries(int numberOfEntries) {
		this.numberOfEntries = numberOfEntries;
	}

	public boolean isShowHistoryLinks() {
		return showHistoryLinks;
	}

	public void setShowHistoryLinks(boolean showHistoryLinks) {
		this.showHistoryLinks = showHistoryLinks;
	}

	public boolean isShowTags() {
		return showTags;
	}

	public void setShowTags(boolean showTags) {
		this.showTags = showTags;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<Entry> getEntries() throws IOException {
		siteMap = rc.getSiteMap();

		Path root = getAbsoluteDirPath(path);
		List<PageInfo> pageList = siteMap.getPageList(root);
		List<PageInfo> acceptedPages = new ArrayList<PageInfo>(pageList.size());

		for (PageInfo item : pageList) {
			boolean accept = item.getMenuPolicy() != MenuPolicy.SKIP;

			if (item.getPath().equals(root)) {
				accept = false;
			} else if (!Strings.isNullOrEmpty(tag)
						&& Strings.searchString(item.getKeywords(), tag, false) < 0) {
				accept = false;
			} else if (!Strings.isNullOrEmpty(date)
						&& !YEAR_MONTH_DATE_FORMAT.format(new Date(item.getCreationDate()))
						.equals(date)) {
				accept = false;
			}

			if (accept) {
				acceptedPages.add(item);
			}
		}

		if (sortByVisits) {
			Collections.sort(acceptedPages, new PageHitsComparator());
		} else {
			Collections.sort(acceptedPages, new PageDateComparator());
		}

		List<Entry> entryList = new ArrayList<Entry>();

		for (int i = firstEntry;
					i < firstEntry + numberOfEntries && i < acceptedPages.size(); i++) {
			PageInfo pageInfo = acceptedPages.get(i);
			entryList.add(new Entry(pageInfo));
		}

		if (showHistoryLinks && !sortByVisits) {
			boolean newer = firstEntry > 0;
			boolean older = firstEntry + numberOfEntries < pageList.size();
			String baseURL = rc.getURL().toString();
			newerLink = null;
			olderLink = null;

			if (newer || older) {
				if (!Strings.isNullOrEmpty(tag)) {
					baseURL = Web.setURLParameter(baseURL, "tag", tag, true);
				}

				if (!Strings.isNullOrEmpty(date)) {
					baseURL = Web.setURLParameter(baseURL, "date", date, false);
				}

				if (newer) {
					newerLink = firstEntry - numberOfEntries > 0
						? Web.setURLParameter(baseURL, "firstEntry",
							Integer.toString(firstEntry - numberOfEntries), false)
						: Web.setURLParameter(baseURL, "firstEntry", null, false);
				}

				if (older) {
					olderLink = Web.setURLParameter(baseURL, "firstEntry",
							Integer.toString(firstEntry + numberOfEntries), false);
				}
			}
		}

		return entryList;
	}

	public String getNewerMsg() {
		return Context.getConstants().getString("includeNewer");
	}

	public String getOlderMsg() {
		return Context.getConstants().getString("includeOlder");
	}

	public String getNewerLink() {
		return newerLink;
	}

	public String getOlderLink() {
		return olderLink;
	}

	public class Entry implements ServerPage {
		Path imgPath;
		private Date date;
		private PageInfo pageInfo;
		private String link;
		private String text;

		public Entry(PageInfo pageInfo) {
			this.pageInfo = pageInfo;

			if (showPageImages) {
				String imgParam = pageInfo.getHeadProperties().get("pageimage:path");

				if (imgParam != null) {
					imgPath = rc.getWebSite().getDirectory(pageInfo.getPath())
								.add(imgParam);

					if (!rc.getWebSite().getFile(imgPath).isFile()) {
						imgPath = null;
					}
				}
			}

			if (showReadMoreLinks) {
				link = getRelativeLink(pageInfo.getPath());
			}

			if (showBodies) {
				try {
					InputStream inputStream = siteMap.getServedFile(pageInfo.getPath())
								.getInputStream();
					SmartEncodingInputStream seis = new SmartEncodingInputStream(inputStream,
							SmartEncodingInputStream.BUFFER_LENGTH_8KB, IO.ISO_8859_1);
					Source source = new Source(seis.getReader());
					inputStream.close();

					PageParser pageParser = new PageParser();
					Page page = pageParser.parse(source, true);
					text = WebUtils.createExcerpt(page.getBody(), bodyExcerptLength,
							pageInfo.getPath(), rc.getPagePath());
				} catch (IOException ex) {
					Context.log(ex);
				}
			}

			if (Strings.isNullOrEmpty(text)) {
				text = pageInfo.getExcerpt();
			}

			if (showDates) {
				date = new Date(pageInfo.getCreationDate());
			}
		}

		public boolean isHasTags() {
			return showTags && pageInfo.getKeywords() != null
					&& pageInfo.getKeywords().size() > 0;
		}

		public String getTitle() {
			return pageInfo.getTitle();
		}

		public boolean isImage() {
			return imgPath != null;
		}

		public String getImageURL() {
			Path p = getThumbPath();

			return getRelativeLink(p);
		}

		public String getRssImageURL() throws MalformedURLException {
			Path p = getThumbPath();

			return rc.getURL(p);
		}

		private Path getThumbPath() {
			Path p = Context.MESHCMS_PATH.add("thumbnails",
					pageImageWidth + "_" + pageImageHeight + "_crop").add(imgPath);

			return p;
		}

		public Date getDate() {
			return date;
		}

		public String getRssDate() {
			return RSS_DATE_FORMAT.format(date);
		}

		public String getLink() {
			return link;
		}

		public String getRssLink() throws MalformedURLException {
			return rc.getURL(pageInfo.getPath());
		}

		public String getText() {
			return text;
		}

		public List<Tag> getTags() {
			List<String> keywords = pageInfo.getKeywords();
			List<Tag> tags = new ArrayList<Tag>(keywords.size());

			for (String keyword : keywords) {
				String tagLink = Web.setURLParameter(rc.getURL().toString(), "tag",
						keyword, true);
				tags.add(new Tag(tagLink, keyword));
			}

			return tags;
		}

		public String getReadMoreMsg() {
			return Context.getConstants().getString("readMore");
		}

		public String getModulePath() {
			return adjustPath(getModule().getPath().asAbsolute());
		}

		public String getDateMsg() {
			return Context.getConstants().getString("pageLinkDate");
		}

		public String getTagsMsg() {
			return Context.getConstants().getString("pageLinkTags");
		}

		public Object getBean() {
			return this;
		}

		public String process() {
			return null;
		}
	}

	public static class PageDateComparator implements Comparator<PageInfo> {
		public int compare(PageInfo p1, PageInfo p2) {
			return Numbers.comparisonSign(p2.getCreationDate(), p1.getCreationDate());
		}
	}

	public static class PageHitsComparator implements Comparator<PageInfo> {
		public int compare(PageInfo p1, PageInfo p2) {
			return p2.getTotalHits() - p1.getTotalHits();
		}
	}

	public static class Tag {
		private String link;
		private String value;

		public Tag(String link, String value) {
			this.link = link;
			this.value = value;
		}

		public String getLink() {
			return link;
		}

		public String getValue() {
			return value;
		}
	}
}

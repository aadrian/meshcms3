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

import com.cromoteca.meshcms.client.toolbox.Strings;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A web page after HTML parsing.
 */
public class Page implements Serializable {
	private List<String> keywords;
	private PageConfiguration pageConfiguration;
	private String body;
	private String bodyAttributes;
	private String description;
	private String doctype;
	private String encoding;
	private String head;
	private String headAttributes;
	private String htmlAttributes;
	private String title;
	private boolean draft;

	public Page() {
		keywords = new ArrayList<String>();
		pageConfiguration = new PageConfiguration();
	}

	public void setDescription(String description) {
		this.description = "".equals(description) ? null : description;
	}

	public void addKeyword(String keyword) {
		keywords.add(keyword);
	}

	public void setTitle(String title) {
		this.title = "".equals(title) ? null : title;
	}

	/**
	 * Returns the menu title, or the page title if menu title is empty.
	 */
	public String getMenuTitle() {
		String menuTitle = pageConfiguration.getShortTitle();

		if (Strings.isNullOrEmpty(menuTitle)) {
			menuTitle = title;
		}

		return menuTitle;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public PageConfiguration getPageConfiguration() {
		return pageConfiguration;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getBodyAttributes() {
		return bodyAttributes;
	}

	public void setBodyAttributes(String bodyAttributes) {
		this.bodyAttributes = bodyAttributes;
	}

	public String getDescription() {
		return description;
	}

	public String getDoctype() {
		return doctype;
	}

	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getHeadAttributes() {
		return headAttributes;
	}

	public void setHeadAttributes(String headAttributes) {
		this.headAttributes = headAttributes;
	}

	public String getHTMLAttributes() {
		return htmlAttributes;
	}

	public void setHTMLAttributes(String htmlAttributes) {
		this.htmlAttributes = htmlAttributes;
	}

	public String getTitle() {
		return title;
	}

	public boolean isDraft() {
		return draft;
	}

	public void setDraft(boolean draft) {
		this.draft = draft;
	}

	public void setPageConfiguration(PageConfiguration pageConfiguration) {
		this.pageConfiguration = pageConfiguration;
	}
}

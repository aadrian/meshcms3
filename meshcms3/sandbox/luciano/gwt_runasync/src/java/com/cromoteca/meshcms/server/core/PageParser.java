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

import com.cromoteca.meshcms.client.server.Page;
import com.cromoteca.meshcms.client.server.PageConfiguration;
import com.cromoteca.meshcms.server.toolbox.JSON;
import com.cromoteca.meshcms.server.toolbox.Strings;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StartTagType;

public class PageParser {
	public Page parse(Source source, boolean searchDraft) {
		Page page = new Page();
		page.setEncoding(source.getEncoding());

		StartTag doctype = source.getFirstStartTag(StartTagType.DOCTYPE_DECLARATION);

		if (doctype != null) {
			page.setDoctype(doctype.toString());
		}

		Element htmlElement = source.getFirstElement("html");

		if (htmlElement != null) {
			page.setHTMLAttributes(htmlElement.getAttributes().toString());
		}

		Element headElement = source.getFirstElement("head");

		if (headElement != null) {
			page.setHeadAttributes(headElement.getAttributes().toString());

			if (headElement != null) {
				List<Element> headChildren = headElement.getChildElements();
				Element titleElement = headElement.getFirstElement("title");

				if (titleElement != null) {
					page.setTitle(titleElement.getContent().toString());
					headChildren.remove(titleElement);
				}

				List<Element> metaElements = headElement.getAllElements("meta");

				if (metaElements != null) {
					for (Element metaElement : metaElements) {
						String name = metaElement.getAttributeValue("name");

						if ("keywords".equalsIgnoreCase(name)) {
							String keyList = metaElement.getAttributeValue("content");
							String[] keys = keyList.split("[,;:]");

							for (String key : keys) {
								page.addKeyword(key.trim());
							}

							headChildren.remove(metaElement);
						} else if ("description".equalsIgnoreCase(name)) {
							page.setDescription(metaElement.getAttributeValue("content"));
							headChildren.remove(metaElement);
						} else if ("content-type".equalsIgnoreCase(
										metaElement.getAttributeValue("http-equiv"))) {
							headChildren.remove(metaElement);
						}
					}
				}

				List<Element> scriptElements = headElement.getAllElements("script");

				if (scriptElements != null) {
					PageConfiguration map = null;

					for (Element scriptElement : scriptElements) {
						if ("application/javascript".equals(scriptElement.getAttributeValue(
											"type"))) {
							if (map == null) {
								String textContent = scriptElement.getContent().toString();

								if (!Strings.isNullOrEmpty(textContent)) {
									Matcher matcher = Pattern.compile(
											"(?s)meshcmsPageInfo\\s*=\\s*(\\{.*\\})")
												.matcher(textContent);

									if (matcher.find()) {
										String json = matcher.group(1);

										try {
											map = JSON.getGson()
														.fromJson(json, PageConfiguration.class);
											headChildren.remove(scriptElement);
										} catch (Exception ex) {
											Context.log(ex);
										}
									}
								}
							}
						}
					}

					if (map != null) {
						page.setPageConfiguration(map);
					}
				}

				StringBuilder sb = new StringBuilder();

				for (Element headChild : headChildren) {
					sb.append(headChild);
				}

				page.setHead(sb.toString().trim());
			}
		}

		Element bodyElement = source.getFirstElement("body");

		if (bodyElement == null) {
			bodyElement = source.getFirstElement();
		}

		if (bodyElement != null) {
			page.setBody(bodyElement.getContent().toString().trim());
			page.setBodyAttributes(bodyElement.getAttributes().toString());
		}

		if (searchDraft) {
			String draft = page.getPageConfiguration().getDraft();

			if (draft != null) {
				Source draftSource = new Source(draft);
				page = parse(draftSource, false);
				page.setDraft(true);
			}
		}

		return page;
	}
}

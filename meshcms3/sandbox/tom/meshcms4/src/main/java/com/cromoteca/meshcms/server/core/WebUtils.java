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

import com.cromoteca.meshcms.client.server.ServerConfiguration;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.toolbox.IO;
import com.cromoteca.meshcms.server.toolbox.InMemoryResponseWrapper;
import com.cromoteca.meshcms.server.toolbox.Strings;
import com.cromoteca.meshcms.server.webui.ResizedThumbnail;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * A collection of utilities related to a web application.
 */
public final class WebUtils {
	public static final DateFormat numericDateFormatter = new SimpleDateFormat(
			"yyyyMMddHHmmss");
	public static final Pattern BODY_REGEX = Pattern.compile(
			"(?s)<body[^>]*>(.*?)</body[^>]*>");
	public static final Pattern IMG_WIDTH_HEIGHT_REGEX = Pattern.compile(
			"width\\s*:\\s*(\\d+)\\s*px|width\\s*=[\"'](\\d+)[\"']|height\\s*:\\s*(\\d+)\\s*px|height\\s*=[\"'](\\d+)[\"']");
	public static final Pattern IMG_SRC_REGEX = Pattern.compile(
			"src\\s*=\\s*([\"'])(.*?)\\1");
	public static final Pattern IMG_REGEX = Pattern.compile("<img\\b[^>]*>");

	// Pattern fixTagsPattern = Pattern.compile("(?s)([^>]*>|[^<>]+)+");
	private WebUtils() {}

	public static String createExcerpt(String html, int length)
		throws IOException {
		StringBuilder sb = new StringBuilder(length + 20);
		StringReader sr = new StringReader(html);
		int n;
		char c;
		int count = 0;
		boolean text = true;
		StringBuilder tag = new StringBuilder();
		List<String> tags = new ArrayList<String>();

		while ((n = sr.read()) != -1) {
			c = (char) n;

			if (text) {
				if (c == '<') {
					text = false;
				} else {
					if (Character.isWhitespace(c)) {
						if (count >= length) {
							sb.append("&hellip;");

							break;
						}
					} else {
						count++;
					}
				}
			} else if (c == '>') {
				text = true;

				Matcher m = Pattern.compile("^(/?)([a-zA-Z0-9:]+)").matcher(tag);

				if (m.find()) {
					String t = m.group(2);

					if (m.group(1).length() == 0) {
						tags.add(t);
					} else {
						int i = tags.size() - 1;

						while (i >= 0) {
							if (t.equalsIgnoreCase(tags.get(i))) {
								tags.remove(i);

								break;
							}

							i--;
						}
					}
				}

				tag.setLength(0);
			} else {
				tag.append(c);
			}

			sb.append(c);
		}

		for (int i = tags.size() - 1; i >= 0; i--) {
			sb.append("</").append(tags.get(i)).append('>');
		}

		return sb.toString();
	}

	public static String createExcerpt(String html, int length, Path oldPage,
		Path newPage) throws IOException {
		String excerpt = createExcerpt(html, length);

		return fixLinks(excerpt, Context.getContextPath(), oldPage, newPage);
	}

	public static String fixLinks(String body, String contextPath, Path oldPage,
		Path newPage) {
		if (Strings.isNullOrEmpty(body)) {
			return body;
		}

		WebSite webSite = Context.getRequestContext().getWebSite();
		oldPage = webSite.getDirectory(oldPage);
		newPage = webSite.getDirectory(newPage);

		if (oldPage.equals(newPage)) {
			return body;
		}

		Pattern tagPattern = Pattern.compile("<(?:img|a)\\b[^>]*>");
		Pattern attrPattern = Pattern.compile("(src|href)\\s*=\\s*([\"'])(.*?)\\2");
		Matcher tagMatcher = tagPattern.matcher(body);
		StringBuffer sb = null;

		while (tagMatcher.find()) {
			if (sb == null) {
				sb = new StringBuffer(body.length());
			}

			String tag = tagMatcher.group();
			Matcher attrMatcher = attrPattern.matcher(tag);

			if (attrMatcher.find()) {
				String url = attrMatcher.group(3).trim();

				if (url.indexOf(':') < 0) {
					Path path;

					if (url.startsWith("/")) {
						if (contextPath.length() > 0 && url.startsWith(contextPath)) {
							url = url.substring(contextPath.length());
						}

						path = new Path(url);
					} else {
						path = oldPage.add(url);
					}

					if (path != null) {
						path = path.getRelativeTo(newPage);

						if (path.isRoot()) {
							path = webSite.getSiteMap().getCurrentWelcome(newPage);
						}

						String newTag = attrMatcher.replaceFirst(attrMatcher.group(1)
								+ "=\"" + path + "\"");
						tagMatcher.appendReplacement(sb,
							Strings.escapeRegexReplacement(newTag));
					}
				}
			}
		}

		if (sb != null) {
			tagMatcher.appendTail(sb);
			body = sb.toString();
		}

		return body;
	}

	/**
	 * Returns a nicer representation of the number as a file length. The number is
	 * returned as bytes, kilobytes or megabytes, with the unit attached.
	 */
	public static String formatFileLength(long length) {
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Context.getLocale());
		DecimalFormat format = (DecimalFormat) numberFormat;
		format.applyPattern("###0.##");

		double num = length;
		String unit;

		if (length < IO.KBYTE) {
			unit = "genericUnitBytes";
		} else if (length < IO.MBYTE) {
			num /= IO.KBYTE;
			unit = "genericUnitKilobytes";
		} else if (length < IO.GBYTE) {
			num /= IO.MBYTE;
			unit = "genericUnitMegabytes";
		} else {
			num /= IO.GBYTE;
			unit = "genericUnitGigabytes";
		}

		return format.format(num) + Context.getConstants().getString(unit);
	}

	public static long getLastModifiedTime(ServletRequest request) {
		long time = 0L;

		try {
			time = (Long) request.getAttribute(HitFilter.LAST_MODIFIED_ATTRIBUTE);
		} catch (Exception ex) {}

		return time;
	}

	public static javax.mail.Session getMailSession() {
		javax.mail.Session mailSession;
		Properties props = new Properties();
		ServerConfiguration serverConfiguration = Context.getServer()
					.getServerConfiguration();
		String mailServer = serverConfiguration.getMailServer();

		if (Strings.isNullOrEmpty(mailServer)) {
			mailSession = javax.mail.Session.getDefaultInstance(props, null);
		} else {
			props.put("mail.smtp.host", mailServer);

			final String smtpUsername = serverConfiguration.getSmtpUsername();
			final String smtpPassword = serverConfiguration.getSmtpPassword();

			if (!Strings.isNullOrEmpty(smtpUsername)) {
				props.put("mail.smtp.auth", "true");
			}

			mailSession = javax.mail.Session.getInstance(props,
					new javax.mail.Authenticator() {
						@Override
						protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
							return new javax.mail.PasswordAuthentication(smtpUsername,
								smtpPassword);
						}
					});
		}

		return mailSession;
	}

	public static String replaceThumbnails(String body, String contextPath,
		Path pagePath) {
		WebSite webSite = Context.getRequestContext().getWebSite();
		ResizedThumbnail thumbMaker = new ResizedThumbnail();
		thumbMaker.setHighQuality(Context.getServer().getServerConfiguration()
					.isHighQualityThumbnails());
		thumbMaker.setMode(ResizedThumbnail.MODE_STRETCH);

		Matcher imgMatcher = IMG_REGEX.matcher(body);
		StringBuffer sb = null;

		while (imgMatcher.find()) {
			if (sb == null) {
				sb = new StringBuffer(body.length());
			}

			String imgTag = imgMatcher.group();
			Matcher whMatcher = IMG_WIDTH_HEIGHT_REGEX.matcher(imgTag);
			int styleWidth = 0;
			int attrWidth = 0;
			int styleHeight = 0;
			int attrHeight = 0;

			while (whMatcher.find()) {
				styleWidth = Strings.parseInt(whMatcher.group(1), styleWidth);
				attrWidth = Strings.parseInt(whMatcher.group(2), attrWidth);
				styleHeight = Strings.parseInt(whMatcher.group(3), styleHeight);
				attrHeight = Strings.parseInt(whMatcher.group(4), attrHeight);
			}

			int w = styleWidth < 1 ? attrWidth : styleWidth;
			int h = styleHeight < 1 ? attrHeight : styleHeight;

			if (w > 0 || h > 0) {
				Matcher srcMatcher = IMG_SRC_REGEX.matcher(imgTag);

				if (srcMatcher.find()) {
					String imgURL = srcMatcher.group(2).trim();

					if (imgURL.indexOf("://") < 0) {
						Path imgPath;

						if (imgURL.startsWith("/")) {
							if (contextPath.length() > 0 && imgURL.startsWith(contextPath)) {
								imgURL = imgURL.substring(contextPath.length());
							}

							imgPath = new Path(imgURL);
						} else {
							imgPath = webSite.getDirectory(pagePath).add(imgURL);
						}

						if (imgPath != null) {
							thumbMaker.setWidth(w);
							thumbMaker.setHeight(h);

							Path thumbPath = thumbMaker.checkAndCreate(imgPath);

							if (thumbPath != null) {
								String newImgTag = srcMatcher.replaceAll("src=\""
										+ webSite.getLink(thumbPath, pagePath) + "\"");
								imgMatcher.appendReplacement(sb,
									Strings.escapeRegexReplacement(newImgTag));
							}
						}
					}
				}
			}
		}

		if (sb != null) {
			imgMatcher.appendTail(sb);
			body = sb.toString();
		}

		return body;
	}

	public static void updateLastModifiedTime(HttpServletRequest request,
		File file) {
		updateLastModifiedTime(request, file.lastModified());
	}

	public static void updateLastModifiedTime(HttpServletRequest request,
		long time) {
		if (time > getLastModifiedTime(request)) {
			request.setAttribute(HitFilter.LAST_MODIFIED_ATTRIBUTE, new Long(time));
		}
	}

	public static InMemoryResponseWrapper requestPage(String path)
		throws ServletException, IOException {
		HttpServletRequest request = Context.getRequest();
		InMemoryResponseWrapper responseWrapper = new InMemoryResponseWrapper(Context
						.getResponse());
		request.getRequestDispatcher(path).include(request, responseWrapper);

		return responseWrapper;
	}
}

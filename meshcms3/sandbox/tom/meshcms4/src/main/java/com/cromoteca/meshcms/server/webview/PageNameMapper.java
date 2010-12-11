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
package com.cromoteca.meshcms.server.webview;

import com.cromoteca.meshcms.server.toolbox.Strings;
import com.cromoteca.meshcms.server.toolbox.Web;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class PageNameMapper implements PageMapper {
	public static final String REQUEST_ATTRIBUTE_NAME = "mesh-page-mapper-object";
	private final Map<String, Class> cache;
	private String basePackage;

	public PageNameMapper() {
		cache = new HashMap<String, Class>();
	}

	public Object getPage() {
		HttpServletRequest request = Context.getRequest();
		String pagePath = Web.getPathInfo(request);

		if (Strings.isNullOrEmpty(pagePath)) {
			pagePath = Web.getServletPath(request);
		}

		return getPage(pagePath);
	}

	public Object getPage(String pagePath) {
		Object o = Context.getScopedAttribute(REQUEST_ATTRIBUTE_NAME, Object.class,
				Scope.REQUEST);

		if (o == null) {
			Class cls = null;

			if (cache.containsKey(pagePath)) {
				cls = cache.get(pagePath);
			} else {
				String className = getClassName(pagePath);

				if (className != null) {
					try {
						cls = Class.forName(className);
					} catch (ClassNotFoundException ex) {}

					cache.put(pagePath, cls);
				}
			}

			if (cls != null) {
				try {
					o = cls.newInstance();
				} catch (Exception ex) {
					Context.log(ex);
				}
			}
		}

		return o;
	}

	public String getClassName(String pagePath) {
		int dot = pagePath.lastIndexOf('.');

		if (dot >= 0) {
			pagePath = pagePath.substring(0, dot);
		}

		int slash = pagePath.lastIndexOf('/');
		boolean upper = true;
		StringBuffer sb = new StringBuffer();

		if (basePackage != null && basePackage.length() > 0) {
			sb.append(basePackage);
		}

		for (int i = 0; i < pagePath.length(); i++) {
			char c = pagePath.charAt(i);

			if (Character.isLetterOrDigit(c)) {
				sb.append(upper && i > slash ? Character.toUpperCase(c) : c);
				upper = false;
			} else {
				if (c == '/' && sb.length() > 0) {
					sb.append('.');
				}

				upper = true;
			}
		}

		return sb.toString();
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}
}

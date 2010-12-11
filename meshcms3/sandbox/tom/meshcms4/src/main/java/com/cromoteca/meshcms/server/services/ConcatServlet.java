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
package com.cromoteca.meshcms.server.services;

import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.core.Context;
import com.cromoteca.meshcms.server.core.Context.RequestContext;
import com.cromoteca.meshcms.server.core.WebSite;
import com.cromoteca.meshcms.server.toolbox.IO;
import com.cromoteca.meshcms.server.toolbox.Strings;
import java.io.IOException;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConcatServlet extends HttpServlet {
	public static final Pattern SRC_HREF_PATTERN = Pattern.compile(
			"(?i)url\\s*\\(\\s*[\"']?([^\\)\"']+)[\"']?\\s*\\)");
	private static final Path OWN_PATH = Context.MESHCMS_PATH.add("concat");

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		WebSite webSite = Context.getRequestContext().getWebSite();
		@SuppressWarnings("unchecked")
		TreeSet<String> names = new TreeSet<String>(request.getParameterMap()
						.keySet());

		for (String name : names) {
			Path path = new Path(request.getParameter(name));

			try {
				String content = IO.readFully(webSite.getFile(path));

				if ("css".equals(Strings.getExtension(path, false))) {
					content = adjustURLs(content, path.getParent());
				}

				response.getWriter().write(content);
			} catch (Exception ex) {
				Context.log("File " + path.asAbsolute() + " not found (ignored)");
			}
		}
	}

	private String adjustURLs(String css, Path cssDir) {
		Matcher tagMatcher = SRC_HREF_PATTERN.matcher(css);
		StringBuffer sb = new StringBuffer();
		RequestContext rc = Context.getRequestContext();

		while (tagMatcher.find()) {
			String url = tagMatcher.group(1);

			if (url.indexOf(':') < 0) {
				Path urlPath = cssDir.add(url);
				url = rc.adjustPath(urlPath.asAbsolute(), OWN_PATH).toString();
			}

			tagMatcher.appendReplacement(sb,
				"url('" + Strings.escapeRegexReplacement(url) + "')");
		}

		tagMatcher.appendTail(sb);

		return sb.toString();
	}
}

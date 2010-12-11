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
package com.cromoteca.meshcms.server.toolbox;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GWTDevFilter implements Filter {
	public static final String CODE_SERVER_PORT_PARAM = "codeServerPort";
	public static final String GWT_CODESVR = "gwt.codesvr";
	private String codeServerPort;
	private boolean isJetty;

	public void init(FilterConfig filterConfig) throws ServletException {
		codeServerPort = filterConfig.getInitParameter(CODE_SERVER_PORT_PARAM);
		isJetty = filterConfig.getServletContext().getServerInfo().contains("jetty");
	}

	public void doFilter(ServletRequest request, ServletResponse response,
		FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		if (isJetty && "get".equalsIgnoreCase(httpRequest.getMethod())) {
			String url = Web.getFullURL(httpRequest);

			if (!url.contains(GWT_CODESVR)) {
				httpResponse.sendRedirect(Web.setURLParameter(url, GWT_CODESVR,
						httpRequest.getServerName() + ':' + codeServerPort, true));

				return;
			}
		}

		chain.doFilter(request, response);
	}

	public void destroy() {}
}

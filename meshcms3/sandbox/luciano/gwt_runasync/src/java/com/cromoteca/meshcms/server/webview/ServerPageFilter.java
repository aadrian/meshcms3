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

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServerPageFilter implements Filter {
	public static final String BEAN_NAME = "bean";
	private PageMapper mapper;

	public void init(FilterConfig filterConfig) throws ServletException {
		PageNameMapper pageMapper = new PageNameMapper();
		pageMapper.setBasePackage(filterConfig.getInitParameter("basePackage"));
		mapper = pageMapper;
	}

	public void doFilter(ServletRequest req, ServletResponse resp,
		FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		Context.add(request, response);

		Object oldBean = request.getAttribute(BEAN_NAME);

		try {
			ServerPage page = (ServerPage) mapper.getPage();
			String redirect = null;

			if (page != null) {
				Object bean = page.getBean();

				if (bean != null) {
					Context.fillModel(bean);
					request.setAttribute(BEAN_NAME, bean);
				}

				redirect = page.process();
			}

			if (redirect != null) {
				Context.redirect(redirect, false);
			}

			chain.doFilter(request, response);
		} finally {
			if (oldBean == null) {
				request.removeAttribute(BEAN_NAME);
			} else {
				request.setAttribute(BEAN_NAME, oldBean);
			}

			Context.remove();
		}
	}

	public void destroy() {
		mapper = null;
	}
}

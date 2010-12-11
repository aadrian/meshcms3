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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.beanutils.BeanUtils;

public class Context {
	private static final String EXPIRING_PREFIX = "expiring:";
	private static final ThreadLocal<Local> threadLocal = new ThreadLocal<Local>();
	private static ServletContext servletContext;
	private HttpServletRequest request;
	private HttpServletResponse response;

	private static Context getContext() {
		List<Context> stack = threadLocal.get().contexts;

		return stack.get(stack.size() - 1);
	}

	public static HttpServletRequest getRequest() {
		return getContext().request;
	}

	public static HttpServletResponse getResponse() {
		return getContext().response;
	}

	public static ServletContext getServletContext() {
		return servletContext;
	}

	public static String getContextPath() {
		return getRequest().getContextPath();
	}

	public static URL getURL() {
		return threadLocal.get().url;
	}

	public static void setServletContext(ServletContext sc) {
		if (servletContext == null) {
			servletContext = sc;
		}
	}

	public static boolean isActive() {
		return threadLocal.get() != null;
	}

	public static void add(HttpServletRequest request,
		HttpServletResponse response) {
		Local local = threadLocal.get();

		if (local == null) {
			local = new Local();
			threadLocal.set(local);

			try {
				local.url = new URL(Web.getFullURL(request));
			} catch (MalformedURLException ex) {
				log(ex);
			}
		}

		Context c = new Context();
		c.request = request;
		c.response = response;
		local.contexts.add(c);
	}

	public static void remove() {
		List<Context> stack = threadLocal.get().contexts;
		stack.remove(stack.size() - 1);

		if (stack.isEmpty()) {
			threadLocal.remove();
		}
	}

	public static boolean hasScopedAttribute(String name, Scope scope) {
		return getScopedAttribute(name, Object.class, scope) != null;
	}

	public static <T> boolean hasScopedSingleton(Class<T> cls, Scope scope) {
		return getScopedSingleton(cls, scope) != null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getScopedAttribute(String name, Class<T> cls, Scope scope) {
		T result = null;

		switch (scope) {
			case APPLICATION:
				result = (T) servletContext.getAttribute(name);

				break;

			case REQUEST:
				result = (T) getRequest().getAttribute(name);

				break;

			case SESSION:

				HttpSession session = getRequest().getSession(false);

				if (session != null) {
					result = (T) session.getAttribute(name);
				}

				break;
		}

		return result;
	}

	public static void setScopedAttribute(String name, Object value, Scope scope) {
		switch (scope) {
			case APPLICATION:
				servletContext.setAttribute(name, value);

				break;

			case REQUEST:
				getRequest().setAttribute(name, value);

				break;

			case SESSION:
				getRequest().getSession(true).setAttribute(name, value);

				break;
		}
	}

	public static void removeScopedAttribute(String name, Scope scope) {
		switch (scope) {
			case APPLICATION:
				servletContext.removeAttribute(name);

				break;

			case REQUEST:
				getRequest().removeAttribute(name);

				break;

			case SESSION:

				HttpSession session = getRequest().getSession(false);

				if (session != null) {
					session.removeAttribute(name);
				}

				break;
		}
	}

	public static <T> T getScopedSingleton(Class<T> cls, Scope scope) {
		return getScopedAttribute(cls.getName(), cls, scope);
	}

	public static void setScopedSingleton(Object value, Scope scope) {
		Class<?> cls = value.getClass();

		do {
			setScopedAttribute(cls.getName(), value, scope);
			cls = cls.getSuperclass();
		} while (cls != null);
	}

	public static void setScopedSingleton(Class<?> cls, Object value, Scope scope) {
		if (value != null && !(cls.isAssignableFrom(value.getClass()))) {
			throw new IllegalArgumentException(value.getClass()
				+ " is not subclass of " + cls);
		}

		setScopedAttribute(cls.getName(), value, scope);
	}

	public static void removeScopedSingleton(Class cls, Scope scope) {
		removeScopedAttribute(cls.getName(), scope);
	}

	protected static <T extends Expiring> T getExpiringAttribute(String name,
		Class<T> cls, Scope scope) {
		name = EXPIRING_PREFIX + name;

		T t = getScopedAttribute(name, cls, scope);

		if (t != null) {
			removeScopedAttribute(name, scope);

			if (t.getExpirationTime() < System.currentTimeMillis()) {
				t = null;
			}
		}

		return t;
	}

	protected static void setExpiringAttribute(String name, Expiring value,
		Scope scope) {
		setScopedAttribute(EXPIRING_PREFIX + name, value, scope);
	}

	protected static void removeExpiringAttribute(String name, Scope scope) {
		removeScopedAttribute(EXPIRING_PREFIX + name, scope);
	}

	public static void fillModel(Object model) {
		HttpServletRequest request = getRequest();
		Enumeration names = request.getParameterNames();
		Map<String, String[]> map = new HashMap<String, String[]>();

		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			map.put(name, request.getParameterValues(name));
		}

		try {
			BeanUtils.populate(model, map);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		} catch (InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void redirect(String location, boolean permanently,
		boolean preserveQueryString) throws IOException {
		if (location.startsWith("/")) {
			location = getContextPath() + location;
		}

		if (preserveQueryString) {
			String q = getRequest().getQueryString();

			if (!Strings.isNullOrEmpty(q) && location.indexOf('?') < 0) {
				location += '?' + q;
			}
		}

		for (Context c : threadLocal.get().contexts) {
			if (c.response != null) {
				if (permanently) {
					c.response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
					c.response.setHeader("Location", location);
				} else {
					try {
						c.response.sendRedirect(location);
					} catch (IllegalStateException ex) {
						// log("Failed redirect (1 out of " + threadLocal.get().contexts.size() + " responses)", ex);
					}
				}
			}
		}
	}

	public static void log(String message) {
		servletContext.log(message);
	}

	public static void log(String message, Throwable t) {
		servletContext.log(message, t);
	}

	public static void log(Throwable t) {
		servletContext.log(t.getMessage(), t);
	}

	private static class Local {
		final List<Context> contexts = new ArrayList<Context>();
		URL url;
	}
}

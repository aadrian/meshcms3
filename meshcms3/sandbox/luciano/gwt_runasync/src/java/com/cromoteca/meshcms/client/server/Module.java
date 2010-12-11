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

import com.cromoteca.meshcms.client.toolbox.Path;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Module configuration (mappes to JSON module file).
 */
public class Module implements Serializable {
	private List<Parameter> parameters;
	private transient Path basePath;
	private String beanClass;
	private String name;
	private String template;
	private boolean body = true;
	private boolean head;

	private Module() {
		parameters = new ArrayList<Parameter>();
	}

	public Path getPath() {
		return basePath.add(name);
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public Path getBasePath() {
		return basePath;
	}

	public void setBasePath(Path basePath) {
		this.basePath = basePath;
	}

	public String getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(String beanClass) {
		this.beanClass = beanClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public Path getTemplatePath() {
		return getPath().add(template);
	}

	public boolean isBody() {
		return body;
	}

	public void setBody(boolean body) {
		this.body = body;
	}

	public boolean isHead() {
		return head;
	}

	public void setHead(boolean head) {
		this.head = head;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Module parameter (mappes to JSON module file).
	 */
	public static class Parameter implements Serializable {
		private List<String> values;
		private String defaultValue;
		private String name;
		private Type type;
		private boolean pageProperty;
		private boolean required;

		/**
		 * Indicates that this parameter must be set in the site configuration.
		 */
		private boolean siteWide;

		public Parameter() {
			values = new ArrayList<String>();
		}

		public List<String> getValues() {
			return values;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		public boolean isPageProperty() {
			return pageProperty;
		}

		public void setPageProperty(boolean pageProperty) {
			this.pageProperty = pageProperty;
		}

		public boolean isRequired() {
			return required;
		}

		public void setRequired(boolean required) {
			this.required = required;
		}

		public boolean isSiteWide() {
			return siteWide;
		}

		public void setSiteWide(boolean siteWide) {
			this.siteWide = siteWide;
		}

		@Override
		public String toString() {
			String s = name;

			if (defaultValue != null) {
				s += " (default=" + defaultValue + ")";
			}

			return s;
		}
	}

	/**
	 * Module parameter type (text, boolean etc.)
	 */
	public static enum Type {
		BOOLEAN,
		INTEGER,
		MULTILINE_TEXT,
		PATH,
		RICH_TEXT,
		SELECTION,
		TEXT;
	}
}

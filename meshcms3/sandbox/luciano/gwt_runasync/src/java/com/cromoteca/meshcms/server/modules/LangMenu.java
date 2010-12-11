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

import com.cromoteca.meshcms.client.toolbox.Pair;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.core.ServerModule;
import com.cromoteca.meshcms.server.toolbox.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LangMenu extends ServerModule {
	private List<Item> items;
	private String flagPath;
	private String style;
	private boolean flags;
	private boolean names;

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public boolean isFlags() {
		return flags;
	}

	public void setFlags(boolean flags) {
		this.flags = flags;
	}

	public boolean isNames() {
		return names;
	}

	public void setNames(boolean names) {
		this.names = names;
	}

	@Override
	public String process() {
		super.process();

		List<Pair<String, Locale>> languages = rc.getSiteMap().getLangList();
		items = new ArrayList<Item>(languages.size());

		ResourceBundle flagsBundle = null;

		if (flags) {
			flagsBundle = ResourceBundle.getBundle(
					"com.cromoteca.meshcms.server.pages.lib.flags.Flags");
			// TODO: remove /meshcms
			flagPath = getRelativeLink(new Path("/meshcms/resources/lib/flags"));
		}

		for (Pair<String, Locale> language : languages) {
			Locale locale = language.getSecondObject();
			Item item = new Item();
			Path pagePath = rc.getPagePath();
			String langCode = language.getFirstObject();
			item.current = !pagePath.isRoot()
						&& langCode.equals(pagePath.getElementAt(0));
			item.name = Strings.toTitleCase(locale.getDisplayName(locale));

			Path link;

			if (item.current || pagePath.isRoot()) {
				link = new Path(language.getFirstObject());
			} else {
				link = rc.getPagePath().replace(0, language.getFirstObject());

				if (rc.getSiteMap().getPageInfo(link) == null) {
					link = new Path(language.getFirstObject());
				}
			}

			item.link = getRelativeLink(link);

			if (flags) {
				try {
					item.flag = flagPath + '/'
						+ flagsBundle.getString(language.getFirstObject());
				} catch (MissingResourceException ex) {
					// OK, no flag
				}
			}

			items.add(item);
		}

		return super.process();
	}

	public boolean isHasItems() {
		return items != null && items.size() > 0;
	}

	public List<Item> getItems() {
		return items;
	}

	public class Item {
		private String flag;
		private String link;
		private String name;
		private boolean current;

		public boolean isCurrent() {
			return current;
		}

		public String getLink() {
			return link;
		}

		public String getName() {
			return name;
		}

		public String getFlag() {
			return flag;
		}
	}
}

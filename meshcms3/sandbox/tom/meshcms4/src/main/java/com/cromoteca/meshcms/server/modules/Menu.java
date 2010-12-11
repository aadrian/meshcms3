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
import com.cromoteca.meshcms.server.core.Context;
import com.cromoteca.meshcms.server.core.PageInfo;
import com.cromoteca.meshcms.server.core.ServerModule;
import com.cromoteca.meshcms.server.core.SiteMenuIterator;
import com.cromoteca.meshcms.server.toolbox.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Menu extends ServerModule {
	private List<Item> items;
	private Path rootPath;
	private String rootLabel;
	private String rootType;
	private String style;
	private boolean allItems;
	private boolean childItems;
	private boolean firstLevelItems;
	private boolean intermediateLevelItems;
	private boolean linkCurrent;
	private boolean onPathItems;
	private boolean sameLevelItems;
	private int lastSteps;

	public void setRootLabel(String rootLabel) {
		this.rootLabel = rootLabel;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setLinkCurrent(boolean linkCurrent) {
		this.linkCurrent = linkCurrent;
	}

	public void setAllItems(boolean allItems) {
		this.allItems = allItems;
	}

	public void setChildItems(boolean childItems) {
		this.childItems = childItems;
	}

	public void setFirstLevelItems(boolean firstLevelItems) {
		this.firstLevelItems = firstLevelItems;
	}

	public void setOnPathItems(boolean onPathItems) {
		this.onPathItems = onPathItems;
	}

	public void setIntermediateLevelItems(boolean intermediateLevelItems) {
		this.intermediateLevelItems = intermediateLevelItems;
	}

	public void setSameLevelItems(boolean sameLevelItems) {
		this.sameLevelItems = sameLevelItems;
	}

	public void setCustomRoot(String customRoot) {
		rootPath = getAbsoluteDirPath(customRoot);
	}

	public void setRootType(String rootType) {
		this.rootType = rootType;
	}

	public List<Item> getItems() {
		if (items == null) {
			items = new ArrayList<Item>();

			if ("themeRoot".equals(rootType)) {
				rootPath = rc.getSiteMap().getThemeRoot(rc.getPagePath());
			} else if ("siteRoot".equals(rootType)) {
				rootPath = Path.ROOT;
			} else if ("langRoot".equals(rootType)) {
				rootPath = Path.ROOT;

				if (!rc.getPagePath().isRoot()) {
					List<Pair<String, Locale>> languages = rc.getSiteMap().getLangList();

					for (Pair<String, Locale> language : languages) {
						if (language.getFirstObject()
									.equals(rc.getPagePath().getElementAt(0))) {
							rootPath = new Path(language.getFirstObject());
						}
					}
				}
			}

			SiteMenuIterator iter = new SiteMenuIterator(rootPath, rc.getPagePath());
			iter.setAllItems(allItems);
			iter.setChildItems(childItems);
			iter.setFirstLevelItems(firstLevelItems);
			iter.setOnPathItems(onPathItems);
			iter.setIntermediateLevelItems(intermediateLevelItems);
			iter.setSameLevelItems(sameLevelItems);

			int initialLevel = rootPath.getElementCount();
			int oldLevel = initialLevel;

			while (iter.hasNext()) {
				PageInfo nextPage = iter.getNextPage();
				int newLevel = Math.max(initialLevel + 1, nextPage.getLevel());
				Item item = new Item();
				item.path = nextPage.getPath();
				item.title = nextPage.getMenuTitle();
				item.stepsDown = Math.max(0, newLevel - oldLevel);
				item.stepsUp = Math.max(0, oldLevel - newLevel);
				item.link = getRelativeLink(item.path);
				item.level = item.path.getElementCount() - initialLevel;
				item.visits = nextPage.getTotalHits();

				if (Context.getUser() == null) {
					String url = nextPage.getHeadProperties().get("link:url");

					if (url != null) {
						item.link = url;
					}
				}

				items.add(item);
				oldLevel = newLevel;
			}

			lastSteps = oldLevel - initialLevel;
		}

		return items;
	}

	public int getLastSteps() {
		return lastSteps;
	}

	public class Item {
		Path path;
		String link;
		String title;
		int level;
		int stepsDown;
		int stepsUp;
		int visits;

		public int getLevel() {
			return level;
		}

		public Path getPath() {
			return path;
		}

		public String getTitle() {
			return isRoot() && !Strings.isNullOrEmpty(rootLabel) ? rootLabel : title;
		}

		public int getStepsDown() {
			return stepsDown;
		}

		public int getStepsUp() {
			return stepsUp;
		}

		public boolean isLi() {
			return stepsDown == 0;
		}

		public String getLink() {
			return link;
		}

		public boolean isRoot() {
			return path.equals(rootPath);
		}

		public boolean isCurrent() {
			return path.equals(rc.getPagePath());
		}

		public boolean isLinkCurrent() {
			return linkCurrent;
		}

		public boolean isOnPath() {
			return !(isCurrent() || isRoot()) && rc.getPagePath().isContainedIn(path);
		}

		public String getStyle() {
			if (isCurrent()) {
				return "active";
			}

			if (isOnPath()) {
				return "active onpath";
			}

			return null;
		}

		public int getVisits() {
			return visits;
		}
	}
}

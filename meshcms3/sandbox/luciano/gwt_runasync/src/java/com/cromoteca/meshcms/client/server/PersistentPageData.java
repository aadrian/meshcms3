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
import com.cromoteca.meshcms.client.toolbox.Strings;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains all page information that is not saved inside the page itself.
 */
public class PersistentPageData implements Serializable {
	private Map<Path, Integer> scoreMap;
	private Map<Path, String> themeMap;

	public PersistentPageData() {
		scoreMap = new HashMap<Path, Integer>();
		themeMap = new HashMap<Path, String>();
	}

	public int getScore(Path path) {
		Integer score = scoreMap.get(path);

		return score == null ? 0 : score;
	}

	public void setScore(Path path, int score) {
		if (score == 0) {
			scoreMap.remove(path);
		} else {
			scoreMap.put(path, score);
		}
	}

	public String getTheme(Path path) {
		return themeMap.get(path);
	}

	public void setTheme(Path path, String theme) {
		if (Strings.isNullOrEmpty(theme)) {
			themeMap.remove(path);
		} else {
			themeMap.put(path, theme);
		}
	}
}

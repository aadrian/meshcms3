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
package com.cromoteca.meshcms.client.core;

import com.cromoteca.meshcms.client.server.FileInfo;
import com.cromoteca.meshcms.client.server.FileTypes;
import com.cromoteca.meshcms.client.toolbox.Strings;
import com.google.gwt.resources.client.ImageResource;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains data about file types and extension.
 */
public final class FileTypesWithIcons extends FileTypes {
	private static final Map<TypeInfo, ImageResource> iconMap = new HashMap<TypeInfo, ImageResource>();

	static {
		iconMap.put(UNKNOWN, MeshCMS.ICONS_BUNDLE.document());
		iconMap.put(DIRECTORY, MeshCMS.ICONS_BUNDLE.folderOpen());
		iconMap.put(EXT_MAP.get("html"), MeshCMS.ICONS_BUNDLE.documentGlobe());
		iconMap.put(EXT_MAP.get("jsp"), MeshCMS.ICONS_BUNDLE.documentGlobe());
		iconMap.put(EXT_MAP.get("jpg"), MeshCMS.ICONS_BUNDLE.documentImage());
		iconMap.put(EXT_MAP.get("java"), MeshCMS.ICONS_BUNDLE.documentAttributeJ());
		iconMap.put(EXT_MAP.get("jar"), MeshCMS.ICONS_BUNDLE.documentAttributeJ());
		iconMap.put(EXT_MAP.get("xml"), MeshCMS.ICONS_BUNDLE.documentCode());
		iconMap.put(EXT_MAP.get("mp3"), MeshCMS.ICONS_BUNDLE.documentMusic());
		iconMap.put(EXT_MAP.get("flv"), MeshCMS.ICONS_BUNDLE.documentFilm());
		iconMap.put(EXT_MAP.get("zip"), MeshCMS.ICONS_BUNDLE.folderZipper());
		iconMap.put(EXT_MAP.get("txt"), MeshCMS.ICONS_BUNDLE.documentText());
		iconMap.put(EXT_MAP.get("css"), MeshCMS.ICONS_BUNDLE.documentCode());
		iconMap.put(EXT_MAP.get("js"), MeshCMS.ICONS_BUNDLE.documentCode());
		iconMap.put(EXT_MAP.get("exe"), MeshCMS.ICONS_BUNDLE.documentBlock());
		iconMap.put(EXT_MAP.get("doc"), MeshCMS.ICONS_BUNDLE.documentWord());
		iconMap.put(EXT_MAP.get("xls"), MeshCMS.ICONS_BUNDLE.documentExcel());
		iconMap.put(EXT_MAP.get("ppt"), MeshCMS.ICONS_BUNDLE.documentPowerpoint());
		iconMap.put(EXT_MAP.get("pdf"), MeshCMS.ICONS_BUNDLE.documentPdf());
		iconMap.put(EXT_MAP.get("tif"), MeshCMS.ICONS_BUNDLE.documentImage());
		iconMap.put(EXT_MAP.get("ico"), MeshCMS.ICONS_BUNDLE.documentImage());
		iconMap.put(EXT_MAP.get("swf"), MeshCMS.ICONS_BUNDLE.documentFlashMovie());
		iconMap.put(EXT_MAP.get("svg"), MeshCMS.ICONS_BUNDLE.documentPencil());
		iconMap.put(EXT_MAP.get("json"), MeshCMS.ICONS_BUNDLE.json());
	}

	private FileTypesWithIcons() {}

	/**
	 * Returns the name of the icon file for the type of the given file.
	 */
	public static ImageResource getIconFile(FileInfo fileInfo) {
		ImageResource icon;

		if (fileInfo.isDirectory()) {
			icon = iconMap.get(DIRECTORY);
		} else {
			TypeInfo info = getInfo(Strings.getExtension(fileInfo.getName(), false));
			icon = iconMap.get(info);
		}

		if (icon == null) {
			icon = iconMap.get(UNKNOWN);
		}

		return icon;
	}
}

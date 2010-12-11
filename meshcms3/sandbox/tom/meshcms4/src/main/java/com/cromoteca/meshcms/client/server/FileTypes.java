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

import com.cromoteca.meshcms.client.toolbox.Strings;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Stores information about file types.
 */
public class FileTypes {
	/**
	 * Denotes a directory.
	 */
	public static final TypeInfo DIRECTORY;
	public static final SortedMap<String, TypeInfo> EXT_MAP = new TreeMap<String, TypeInfo>();

	/**
	 * Id of static HTML files.
	 */
	public static final int HTML_ID = 1;

	/**
	 * Id of server-side HTML files (e.g. JSPs).
	 */
	public static final int SERVERSIDE_ID = 2;
	public static final int WEB_IMAGE_ID = 3;

	/**
	 * Denotes an unknown file type.
	 */
	public static final TypeInfo UNKNOWN;

	static {
		UNKNOWN = new TypeInfo();
		UNKNOWN.id = -1;
		UNKNOWN.description = "Unknown";
		UNKNOWN.compressible = false;
		DIRECTORY = new TypeInfo();
		DIRECTORY.id = 0;
		DIRECTORY.description = "Folder";
		DIRECTORY.compressible = false;

		TypeInfo info = new TypeInfo();
		info.id = HTML_ID;
		info.icon = "document-globe.png";
		info.description = "Static Page";
		info.compressible = true;
		EXT_MAP.put("htm", info);
		EXT_MAP.put("html", info);
		EXT_MAP.put("xhtml", info);
		info = new TypeInfo();
		info.id = SERVERSIDE_ID;
		info.icon = "document-globe.png";
		info.description = "Server-Side Page";
		info.compressible = false;
		EXT_MAP.put("asp", info);
		EXT_MAP.put("cgi", info);
		EXT_MAP.put("jsp", info);
		EXT_MAP.put("ftl", info);
		EXT_MAP.put("php", info);
		EXT_MAP.put("pl", info);
		EXT_MAP.put("shtml", info);
		info = new TypeInfo();
		info.id = WEB_IMAGE_ID;
		info.icon = "document-image.png";
		info.description = "Web Image";
		info.compressible = false;
		EXT_MAP.put("gif", info);
		EXT_MAP.put("jpeg", info);
		EXT_MAP.put("jpg", info);
		EXT_MAP.put("mng", info);
		EXT_MAP.put("png", info);
		info = new TypeInfo();
		info.id = 4;
		info.icon = "document-attribute-j.png";
		info.description = "Java File";
		info.compressible = true;
		EXT_MAP.put("class", info);
		EXT_MAP.put("java", info);
		info = new TypeInfo();
		info.id = 5;
		info.icon = "document-attribute-j.png";
		info.description = "Java Archive";
		info.compressible = false;
		EXT_MAP.put("ear", info);
		EXT_MAP.put("jar", info);
		EXT_MAP.put("war", info);
		info = new TypeInfo();
		info.id = 6;
		info.icon = "document-code.png";
		info.description = "XML File";
		info.compressible = true;
		EXT_MAP.put("dtd", info);
		EXT_MAP.put("tld", info);
		EXT_MAP.put("xml", info);
		EXT_MAP.put("xsd", info);
		EXT_MAP.put("xsl", info);
		info = new TypeInfo();
		info.id = 7;
		info.icon = "document-music.png";
		info.description = "Audio File";
		info.compressible = false;
		EXT_MAP.put("au", info);
		EXT_MAP.put("mp3", info);
		EXT_MAP.put("ogg", info);
		EXT_MAP.put("wav", info);
		EXT_MAP.put("wma", info);
		info = new TypeInfo();
		info.id = 8;
		info.icon = "document-film.png";
		info.description = "Video File";
		info.compressible = false;
		EXT_MAP.put("avi", info);
		EXT_MAP.put("flv", info);
		EXT_MAP.put("mov", info);
		EXT_MAP.put("mpeg", info);
		EXT_MAP.put("mpg", info);
		EXT_MAP.put("wmv", info);
		info = new TypeInfo();
		info.id = 9;
		info.icon = "folder-zipper.png";
		info.description = "Archive";
		info.compressible = false;
		EXT_MAP.put("7z", info);
		EXT_MAP.put("bz2", info);
		EXT_MAP.put("gz", info);
		EXT_MAP.put("rar", info);
		EXT_MAP.put("rpm", info);
		EXT_MAP.put("tar", info);
		EXT_MAP.put("tgz", info);
		EXT_MAP.put("z", info);
		EXT_MAP.put("zip", info);
		info = new TypeInfo();
		info.id = 10;
		info.icon = "document-text.png";
		info.description = "Plain Text File";
		info.compressible = true;
		EXT_MAP.put("log", info);
		EXT_MAP.put("txt", info);
		info = new TypeInfo();
		info.id = 11;
		info.icon = "document-code.png";
		info.description = "Style Sheet File";
		info.compressible = true;
		EXT_MAP.put("css", info);
		info = new TypeInfo();
		info.id = 12;
		info.icon = "document-code.png";
		info.description = "Script File";
		info.compressible = true;
		EXT_MAP.put("js", info);
		info = new TypeInfo();
		info.id = 13;
		info.icon = "document-block.png";
		info.description = "Executable File";
		info.compressible = false;
		EXT_MAP.put("bin", info);
		EXT_MAP.put("exe", info);
		info = new TypeInfo();
		info.id = 14;
		info.icon = "document-word.png";
		info.description = "Word Document";
		info.compressible = true;
		EXT_MAP.put("doc", info);
		EXT_MAP.put("docx", info);
		EXT_MAP.put("rtf", info);
		info = new TypeInfo();
		info.id = 15;
		info.icon = "document-excel.png";
		info.description = "Excel Document";
		info.compressible = true;
		EXT_MAP.put("xls", info);
		EXT_MAP.put("xlsx", info);
		info = new TypeInfo();
		info.id = 16;
		info.icon = "document-powerpoint.png";
		info.description = "PowerPoint Document";
		info.compressible = true;
		EXT_MAP.put("ppt", info);
		EXT_MAP.put("pptx", info);
		EXT_MAP.put("pps", info);
		info = new TypeInfo();
		info.id = 17;
		info.icon = "document-pdf.png";
		info.description = "PDF Document";
		info.compressible = false;
		EXT_MAP.put("pdf", info);
		info = new TypeInfo();
		info.id = 18;
		info.icon = "document-image.png";
		info.description = "Image";
		info.compressible = false;
		EXT_MAP.put("bmp", info);
		EXT_MAP.put("psd", info);
		EXT_MAP.put("tga", info);
		EXT_MAP.put("tif", info);
		EXT_MAP.put("tiff", info);
		info = new TypeInfo();
		info.id = 19;
		info.icon = "document-image.png";
		info.description = "Icon";
		info.compressible = true;
		EXT_MAP.put("ico", info);
		info = new TypeInfo();
		info.id = 20;
		info.icon = "document-flash-movie.png";
		info.description = "Flash File";
		info.compressible = false;
		EXT_MAP.put("swf", info);
		info = new TypeInfo();
		info.id = 21;
		info.icon = "document--pencil.png";
		info.description = "Vector Image File";
		info.compressible = true;
		EXT_MAP.put("svg", info);
		info = new TypeInfo();
		info.id = 22;
		info.icon = "json.png";
		info.description = "JSON File";
		info.compressible = true;
		EXT_MAP.put("json", info);
	}

	protected FileTypes() {}

	/**
	 * Returns the description of the type of the file.
	 */
	public static String getDescription(String fileName) {
		return getInfo(Strings.getExtension(fileName, false)).description;
	}

	/**
	 * Returns the file name of an icon for the given file.
	 * @param fileName
	 * @return
	 */
	public static String getIcon(String fileName) {
		return getInfo(Strings.getExtension(fileName, false)).icon;
	}

	public static TypeInfo getInfo(String extension) {
		TypeInfo info = EXT_MAP.get(extension);

		return info == null ? UNKNOWN : info;
	}

	/**
	 * Returns true if the file is supposed to be compressible. For example, text
	 * files are compressible, while ZIP files are not.
	 */
	public static boolean isCompressible(String fileName) {
		return getInfo(Strings.getExtension(fileName, false)).compressible;
	}

	public static boolean isWebImage(String fileName) {
		int id = getInfo(Strings.getExtension(fileName, false)).id;

		return id == WEB_IMAGE_ID;
	}

	public static class TypeInfo {
		String description;
		String icon;
		boolean compressible;
		int id;
	}
}

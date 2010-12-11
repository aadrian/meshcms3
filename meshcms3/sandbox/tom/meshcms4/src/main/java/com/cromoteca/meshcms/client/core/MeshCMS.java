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

import com.cromoteca.meshcms.client.i18n.Constants;
import com.cromoteca.meshcms.client.i18n.LookupConstants;
import com.cromoteca.meshcms.client.i18n.Messages;
import com.cromoteca.meshcms.client.icons.IconsBundle;
import com.cromoteca.meshcms.client.server.SiteInfo;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.client.toolbox.Strings;
import com.cromoteca.meshcms.client.ui.filemanager.DetailedList;
import com.cromoteca.meshcms.client.ui.filemanager.FileBrowser;
import com.cromoteca.meshcms.client.ui.filemanager.FileManager;
import com.cromoteca.meshcms.client.ui.pageeditor.PageCreator;
import com.cromoteca.meshcms.client.ui.pageeditor.PageEditor;
import com.cromoteca.meshcms.client.ui.pageeditor.ZoneEditor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import java.util.MissingResourceException;

/**
 * Entry point of the GWT application.
 */
public class MeshCMS implements EntryPoint {
	public static final String FILE_MANAGER_MODE = "file_manager";
	public static final String FILE_BROWSER_MODE = "file_browser";
	public static final String IMAGE_BROWSER_MODE = "image_browser";
	public static final String PAGE_EDITOR_MODE = "page_editor";
	public static final String ZONE_EDITOR_MODE = "zone_editor";
	public static final String NEW_PAGE_MODE = "new_page";
	public static final Constants CONSTANTS = GWT.create(Constants.class);
	public static final LookupConstants LOOKUP_CONSTANTS = GWT.create(LookupConstants.class);
	public static final Messages MESSAGES = GWT.create(Messages.class);
	public static final IconsBundle ICONS_BUNDLE = GWT.create(IconsBundle.class);
	public static String CONTEXT_PATH;
	public static SiteInfo SITE_INFO;
	public static ServicesAsync SERVER;
	public static Path CURRENT_DIR;
	public static Path CURRENT_PATH;
	private static FlowPanel statusPanel;
	public static String RELOAD_URL;
	private static Path currentPath;
	private static Path currentDir;

	public void onModuleLoad() {
		SERVER = GWT.create(Services.class);
		new AuthorizableServerCall<SiteInfo>() {
				@Override
				public void callServer() {
					SERVER.getSiteInfo(false, getAsyncCallback());
				}

				@Override
				public void onResult(SiteInfo result) {
					CONTEXT_PATH = result.getContextPath();
					changeSiteInfo(result);

					// root panel
					RootLayoutPanel rootPanel = RootLayoutPanel.get();
					String mode = Window.Location.getParameter("mode");
					CURRENT_PATH = new Path(Window.Location.getParameter("path"));
					CURRENT_DIR = new Path(Window.Location.getParameter("dir"));

					boolean editBody = Boolean.parseBoolean(Window.Location.getParameter(
								"editBodyZone"));

					if (FILE_MANAGER_MODE.equals(mode)) {
						rootPanel.add(new FileManager(new DetailedList()));
					} else if (FILE_BROWSER_MODE.equals(mode)) {
						rootPanel.add(FileBrowser.getFileBrowser());
					} else if (IMAGE_BROWSER_MODE.equals(mode)) {
						rootPanel.add(FileBrowser.getImageBrowser());
					} else if (PAGE_EDITOR_MODE.equals(mode)) {
						rootPanel.add(new PageEditor(editBody));
					} else if (ZONE_EDITOR_MODE.equals(mode)) {
						String zoneName = Window.Location.getParameter("zone");
						rootPanel.add(new ZoneEditor(zoneName));
					} else if (NEW_PAGE_MODE.equals(mode)) {
						rootPanel.add(new PageCreator(editBody));
					}
				}
			}.run();
	}

	public static void changeSiteInfo(SiteInfo siteInfo) {
		SITE_INFO = siteInfo;
	}

	public static void changeCurrentDir(Path currentDir) {
		CURRENT_DIR = currentDir;
	}

	public static void changeCurrentPath(Path currentPath) {
		CURRENT_PATH = currentPath;
	}

	/**
	 * Returns the status panel that contains loading icons.
	 * @return
	 */
	public static FlowPanel getStatusPanel() {
		if (statusPanel == null) {
			statusPanel = new FlowPanel();
			statusPanel.setStylePrimaryName("mesh-status-panel");
			RootPanel.get().add(statusPanel);
		}

		return statusPanel;
	}

	public static Path getCurrentDir() {
		return currentDir;
	}

	public static Path getCurrentPath() {
		return currentPath;
	}

	/**
	 * Searches for a translation of strings like module names and parameter
	 * names.
	 * @param prefix
	 * @param name
	 * @return
	 */
	public static String getDynamicTranslation(String prefix, String name) {
		String value;
		String key = prefix == null ? name : prefix + name;

		try {
			value = LOOKUP_CONSTANTS.getString(key);
		} catch (MissingResourceException ex) {
			// TODO: remove log to console when all strings have been localized
			System.out.println(key + "=???");
			value = Strings.beautify(name, false);
		}

		return value;
	}

	/**
	 * Closes the application window and reloads the underlying page.
	 * @param reload
	 */
	public static void closeWindow(boolean reload, Path path) {
		if (path == null) {
			path = CURRENT_PATH;
		}

		if (!nativeCloseWindow(reload, new Path(CONTEXT_PATH).add(path).asAbsolute())) {
			Window.Location.assign(new Path("../..").add(path).toString());
		}
	}

	/**
	 * Checks if the given locale is used as locale in the query string.
	 * @param locale
	 * @return
	 */
	public static boolean isQueryStringLocale(String locale) {
		String qs = Window.Location.getQueryString();

		return qs != null && qs.indexOf("locale=" + locale) >= 0;
	}

	/**
	 * Native function to close the application window.
	 * @param reload whether to reload the calling page or not
	 * @param url a redirect url
	 * @return
	 */
	private static native boolean nativeCloseWindow(boolean reload, String url) /*-{
	if ($wnd.parent && $wnd.parent != $wnd) {
	if ($wnd.parent.jQuery && $wnd.parent.jQuery.fn.colorbox) {
	$wnd.parent.jQuery.fn.colorbox.close();
	}
	
	if (reload) {
	$wnd.parent.location.reload();
	} else if (url) {
	$wnd.parent.location.assign(url);
	}
	
	return true;
	} else if ($wnd.opener) {
	if (reload) {
	$wnd.opener.location.reload();
	} else if (url) {
	$wnd.opener.location.assign(url);
	}
	
	$wnd.close();
	
	return true;
	} else {
	return false;
	}
	}-*/;

	public static native void setSelectedRichTextEditor(String editor) /*-{
	if(editor=="tiny_mce"){
	$wnd.MESHCMS_EDITOR = $wnd.MESHCMS_TINY_MCE;
	}else if (editor=="ckeditor"){
	$wnd.MESHCMS_EDITOR = $wnd.MESHCMS_CKEDITOR;
	}
	}-*/;
}

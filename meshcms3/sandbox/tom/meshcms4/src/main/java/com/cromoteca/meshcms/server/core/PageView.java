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
package com.cromoteca.meshcms.server.core;

import com.cromoteca.meshcms.client.server.Module;
import com.cromoteca.meshcms.client.server.Module.Parameter;
import com.cromoteca.meshcms.client.server.Page;
import com.cromoteca.meshcms.client.server.SiteConfiguration;
import com.cromoteca.meshcms.client.server.Zone;
import com.cromoteca.meshcms.client.server.ZoneAction;
import com.cromoteca.meshcms.client.server.ZoneItem;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.core.Context.RequestContext;
import com.cromoteca.meshcms.server.toolbox.IO;
import com.cromoteca.meshcms.server.toolbox.InMemoryResponseWrapper;
import com.cromoteca.meshcms.server.toolbox.JSON;
import com.cromoteca.meshcms.server.toolbox.Strings;
import com.cromoteca.meshcms.server.toolbox.Web;
import com.cromoteca.meshcms.server.webview.Scope;
import com.glaforge.i18n.io.SmartEncodingInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import net.htmlparser.jericho.Source;

public class PageView {
	private ConfigurationView configurationView;
	private List<String> files;
	private Map<String, String> parameters;
	private Page page;
	private Path themePath;
	private String ext;
	private String module;
	private String zone;
	private ZoneAction action;

	public PageView(Page page) {
		this.page = page;
		configurationView = new ConfigurationView();
		files = new ArrayList<String>();

		Path themeDir = Context.getScopedAttribute(HitFilter.THEME_DIR_ATTRIBUTE,
				Path.class, Scope.REQUEST);

		if (themeDir != null) {
			themePath = Context.getRequestContext().adjustPath(themeDir.asAbsolute());
		}
	}

	public void setAction(String action) {
		this.action = ZoneAction.valueOf(action.toUpperCase());
	}

	public void setModule(String module) {
		this.module = module;
	}

	public void setZone(String zone) {
		this.zone = zone;
		parameters = new HashMap<String, String>();
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setFile(String filePath) {
		files.add(filePath);
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public Path getAdminPath() {
		return Context.getRequestContext().getAdminPath();
	}

	public Path getMeshPath() {
		return Context.getRequestContext().getMeshPath();
	}

	public Path getPagePath() {
		return Context.getRequestContext().getPagePath();
	}

	public Path getThemePath() {
		return themePath;
	}

	public Page getPage() {
		return page;
	}

	public String getTitle() {
		return Web.convertToHTMLEntities(page.getTitle());
	}

	public String getDoctype() {
		return page.getDoctype();
	}

	public String getKeywords() {
		String keywords = Strings.generateList(page.getKeywords(), ", ");

		if (keywords != null && keywords.length() == 0) {
			keywords = null;
		}

		return keywords;
	}

	public String getConcatFiles() {
		StringBuilder sb = new StringBuilder();

		if (!files.isEmpty()) {
			if (ext == null) {
				ext = Strings.getExtension(files.get(0), false);
			}

			sb.append(Context.getRequestContext()
						.adjustPath(Context.MESHCMS_PATH.asAbsolute()));
			sb.append("/concat/concat.");

			if (Context.getUser() != null) {
				sb.append("nocache.");
			}

			sb.append(ext);

			char sep = '?';
			int n = 0;

			for (String file : files) {
				sb.append(sep).append('f').append(++n).append('=');
				sb.append(Context.getRequestContext().getDirectoryPath().add(file));
				sep = '&';
			}
		}

		files.clear();
		ext = null;

		return sb.toString();
	}

	public String getHead() {
		return Web.convertToHTMLEntities(page.getHead());
	}

	public String getHeadAttributes() {
		return page.getHeadAttributes();
	}

	public String getHtmlAttributes() {
		return page.getHTMLAttributes();
	}

	public String getPageInfo() {
		return JSON.getGson().toJson(page.getPageConfiguration());
	}

	public String getEncoding() {
		return IO.SYSTEM_CHARSET;
	}

	public String getFileEncoding() {
		return page.getEncoding();
	}

	public String getDescription() {
		return Web.convertToHTMLEntities(page.getDescription());
	}

	public String getBody() {
		String html = page.getBody();
		RequestContext rc = Context.getRequestContext();

		if (rc.getWebSite().getSiteConfiguration().isReplaceThumbnails()) {
			html = WebUtils.replaceThumbnails(html, Context.getContextPath(),
					rc.getPagePath());
		}

		return Web.convertToHTMLEntities(html);
	}

	public String getBodyAttributes() {
		return page.getBodyAttributes();
	}

	public String getMenuTitle() {
		return Web.convertToHTMLEntities(page.getMenuTitle());
	}

	public String getZone() throws ServletException, IOException {
		StringBuilder sb = new StringBuilder();

		for (ModuleOutput moduleOutput : getModules()) {
			String content = moduleOutput.getContent();

			if (content != null) {
				sb.append(content);
			}
		}

		return sb.toString();
	}

	public ConfigurationView getSite() {
		return configurationView;
	}

	public String getLang() {
		return Context.getLocale().getLanguage();
	}

	public SessionUser getUser() {
		return Context.getUser();
	}

	public String getCms() {
		return WebSite.APP_FULL_NAME;
	}

	public List<ModuleOutput> getModules() throws ServletException, IOException {
		return zone().getAll();
	}

	private ZoneOutput zone() throws ServletException, IOException {
		if (Zone.BODY_ZONE.equals(zone)) {
			if (module == null) {
				module = "body";

				if (action == null) {
					action = ZoneAction.ADD_AFTER;
				}
			}
		}

		if (action == null) {
			action = module == null ? ZoneAction.REPLACE : ZoneAction.NONE;
		}

		Zone z = page.getPageConfiguration().getZone(zone);
		RequestContext rc = Context.getRequestContext();

		if ((z == null || z.getItems().isEmpty()) && Zone.isInheritable(zone)) {
			List<PageInfo> ancestors = rc.getSiteMap().getBreadcrumbs(rc.getPagePath());

			if (ancestors != null) {
				Collections.reverse(ancestors);

				for (PageInfo pageInfo : ancestors) {
					z = pageInfo.getInheritableZones().get(zone);

					if (z != null) {
						z.setPath(pageInfo.getPath());

						break;
					}
				}
			}
		}

		if (z == null) {
			z = new Zone(zone);
		}

		if (z.getPath() == null) {
			z.setPath(rc.getPagePath());
		}

		ZoneItem item;

		if (module == null) {
			item = null;
		} else {
			item = new ZoneItem();
			item.setModuleName(module);

			Module m = rc.getSiteMap().getSiteInfo().getModules().get(module);

			for (Parameter p : m.getParameters()) {
				item.getParameters().put(p.getName(), p.getDefaultValue());
			}

			for (String s : parameters.keySet()) {
				item.getParameters().put(s, parameters.get(s));
			}
		}

		switch (action) {
			case NONE:
				z.getItems().clear();

				if (item != null) {
					z.getItems().add(item);
				}

				break;

			case ADD_AFTER:

				if (item != null) {
					z.getItems().add(0, item);
				}

				break;

			case ADD_BEFORE:

				if (item != null) {
					z.getItems().add(item);
				}

				break;

			case REPLACE:

				if (item != null && z.getItems().isEmpty()) {
					z.getItems().add(item);
				}

				break;
		}

		// TODO: is this needed?
		ZoneOutput output = new ZoneOutput(z);
		Context.setZoneOutput(output);

		if (action != ZoneAction.NONE
					&& !Zone.HEAD_ZONE.equals(zone)
					&& getUser() != null) {
			ZoneItem editor = new ZoneItem();
			editor.setModuleName("editor");
			editor.getParameters()
					.put("editBodyZone", Boolean.toString(action != ZoneAction.NONE));
			z.getItems().add(0, editor);
		}

		// can't use foreach since some modules (e.g. comments) add other modules dynamically
		for (int i = 0; i < z.getItems().size(); i++) {
			ZoneItem zoneItem = z.getItems().get(i);
			z.setCurrentItem(zoneItem);

			Module m = Context.getRequestContext().getSiteMap().getSiteInfo()
						.getModules().get(zoneItem.getModuleName());
			ServerModule serverModule;

			if (m.getBeanClass() == null) {
				serverModule = null;
			} else {
				try {
					serverModule = (ServerModule) Class.forName(m.getBeanClass())
								.newInstance();
					serverModule.setModule(m);
					serverModule.setZoneOutput(output);
					serverModule.setPageView(this);
				} catch (InstantiationException ex) {
					throw new ServletException(ex);
				} catch (IllegalAccessException ex) {
					throw new ServletException(ex);
				} catch (ClassNotFoundException ex) {
					throw new ServletException(ex);
				}
			}

			if (m.getTemplate() == null) {
				if (serverModule != null) {
					serverModule.process();
					serverModule.run();
				}
			} else {
				String html = output.runTemplate(m.getTemplatePath(), serverModule);
				output.getCurrent().addContent(html);
			}
		}

		z.setCurrentItem(null);
		action = null;
		module = null;
		parameters.clear();

		return output;
	}

	public void save(boolean asDraft) throws ServletException, IOException {
		save(Context.getRequestContext().getFilePath(), asDraft);
	}

	public void save(Path path, boolean asDraft)
		throws ServletException, IOException {
		HttpServletRequest request = Context.getRequest();
		Context.setScopedAttribute(HitFilter.PAGE_VIEW_ATTRIBUTE, this,
			Scope.REQUEST);

		InMemoryResponseWrapper responseWrapper = new InMemoryResponseWrapper(Context
						.getResponse());
		// TODO: remove static path /meshcms
		request.getRequestDispatcher("/meshcms/resources/save.jsp")
				.include(request, responseWrapper);

		Source source = responseWrapper.getSource();
		RequestContext rc = Context.getRequestContext();

		if (source != null) {
			String html = source.toString();
			File file = rc.getWebSite().getFile(path);

			if (asDraft && file.exists()) {
				InputStream inputStream = new FileInputStream(file);
				SmartEncodingInputStream seis = new SmartEncodingInputStream(inputStream,
						SmartEncodingInputStream.BUFFER_LENGTH_8KB, IO.ISO_8859_1);
				Source s = new Source(seis.getReader());
				inputStream.close();

				PageParser pageParser = new PageParser();
				Page p = pageParser.parse(s, false);
				p.getPageConfiguration().setDraft(html);
				p.getPageConfiguration()
						.setMenuPolicy(page.getPageConfiguration().getMenuPolicy());
				new PageView(p).save(path, false);
			} else {
				String charset = source.getEncoding();
				rc.getWebSite().saveToFile(html.getBytes(charset), path);
			}
		}
	}

	public static class ConfigurationView {
		private SiteConfiguration configuration;

		public ConfigurationView() {
			configuration = Context.getRequestContext().getWebSite()
						.getSiteConfiguration();
		}

		public String getSlogan() {
			return Web.convertToHTMLEntities(configuration.getSiteSlogan());
		}

		public String getOwnerURL() {
			return configuration.getSiteOwnerURL();
		}

		public String getOwner() {
			return Web.convertToHTMLEntities(configuration.getSiteOwner());
		}

		public String getName() {
			return Web.convertToHTMLEntities(configuration.getSiteName());
		}

		public String getHost() {
			return configuration.getSiteHost();
		}

		public String getUserCSS() {
			Path userCSS = configuration.getUserCSS();

			return userCSS == null ? null
			: Context.getRequestContext().adjustPath(userCSS.asAbsolute()).toString();
		}
	}
}

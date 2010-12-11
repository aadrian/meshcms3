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
import com.cromoteca.meshcms.client.server.SiteConfiguration;
import com.cromoteca.meshcms.client.server.Zone;
import com.cromoteca.meshcms.client.server.ZoneItem;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.client.toolbox.Strings;
import com.cromoteca.meshcms.client.ui.fields.StringArrayConnector;
import com.cromoteca.meshcms.server.core.Context.RequestContext;
import com.cromoteca.meshcms.server.toolbox.Exceptions;
import com.cromoteca.meshcms.server.toolbox.Numbers;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.ServletException;
import org.apache.commons.beanutils.BeanUtils;

public class ServerModule extends BackingBean {
	protected final RequestContext rc = Context.getRequestContext();
	private Module module;
	private PageView pageView;
	private ZoneItem zoneItem;
	private ZoneOutput zoneOutput;
	private int position;

	public int getPosition() {
		return position;
	}

	public String getParameter(String name) {
		return Strings.noNull(zoneItem.getParameters().get(name));
	}

	public String getId() {
		return "mesh_" + zoneOutput.getZone().getName() + '_' + position;
	}

	public Path getZoneDir() {
		return rc.getWebSite().getDirectory(zoneOutput.getZone().getPath());
	}

	public Path getAbsoluteDirPath(String path) {
		Path zoneDir = zoneOutput == null ? Path.ROOT : getZoneDir();

		return path == null ? zoneDir : zoneDir.add(path);
	}

	public String getRelativeLink(Path path) {
		String link = path.equals(rc.getDirectoryPath()) ? "."
			: path.getRelativeTo(rc.getDirectoryPath()).toString();

		if (rc.getWebSite().isDirectory(path)) {
			link += '/';
		}

		return link;
	}

	public String getFullPageURL() {
		return rc.getURL().toString();
	}

	public List<File> collectFiles(String dirParamName, String extensions,
		String orderParamName) {
		Path directory = new Path(getParameter(dirParamName));
		String[] exts = Strings.split(extensions,
				StringArrayConnector.DEFAULT_SPACERS_REGEX, true);
		String order = getParameter(orderParamName);

		return collectFiles(directory, exts, order);
	}

	public List<File> collectFiles(Path directory, String[] extensions,
		String order) {
		Arrays.sort(extensions);

		List<File> items = new ArrayList<File>();
		File dirFile = rc.getWebSite().getFile(directory);

		if (dirFile != null) {
			boolean check = extensions != null && extensions.length > 0;

			for (File file : dirFile.listFiles()) {
				if (file.isFile()) {
					if (check
								&& Arrays.binarySearch(extensions,
									Strings.getExtension(file.getName(), false)) < 0) {
						continue;
					}

					items.add(file);
				}
			}
		}

		if ("newestfirst".equals(order)) {
			Collections.sort(items,
				new Comparator<File>() {
					public int compare(File item1, File item2) {
						return Numbers.comparisonSign(item2.lastModified(),
							item1.lastModified());
					}
				});
		} else if ("oldestfirst".equals(order)) {
			Collections.sort(items,
				new Comparator<File>() {
					public int compare(File item1, File item2) {
						return Numbers.comparisonSign(item1.lastModified(),
							item2.lastModified());
					}
				});
		} else if ("random".equals(order)) {
			Collections.shuffle(items);
		} else if (order != null) {
			Collections.sort(items,
				new Comparator<File>() {
					public int compare(File item1, File item2) {
						return item1.getName().compareTo(item2.getName());
					}
				});
		}

		return items;
	}

	public ZoneOutput getZoneOutput() {
		return zoneOutput;
	}

	public void setZoneOutput(ZoneOutput zoneOutput) {
		this.zoneOutput = zoneOutput;
	}

	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	public PageView getPageView() {
		return pageView;
	}

	public void setPageView(PageView pageView) {
		this.pageView = pageView;
	}

	public ZoneItem getZoneItem() {
		return zoneItem;
	}

	@Override
	public String process() {
		if (zoneOutput != null) {
			Zone zone = zoneOutput.getZone();

			if (zone != null) {
				zoneItem = zone.getCurrentItem();
				position = zone.getIndex(zoneItem);

				try {
					Context.fillModel(getBean());

					SiteConfiguration configuration = Context.getRequestContext()
								.getWebSite().getSiteConfiguration();
					Module m = Context.getRequestContext().getSiteMap().getSiteInfo()
								.getModules().get(zoneItem.getModuleName());

					for (Parameter parameter : m.getParameters()) {
						if (parameter.getDefaultValue() != null) {
							BeanUtils.setProperty(getBean(), parameter.getName(),
								parameter.getDefaultValue());
						}
					}

					BeanUtils.populate(getBean(),
						configuration.getModuleParameters(zoneItem.getModuleName()));
					BeanUtils.populate(getBean(), zoneItem.getParameters());
				} catch (Exception ex) {
					Exceptions.throwAsRuntime(ex);
				}
			}
		}

		return null;
	}

	public void run() throws IOException, ServletException {}
}

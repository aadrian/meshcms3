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

import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.core.ServerModule;
import com.cromoteca.meshcms.server.toolbox.IO;
import com.cromoteca.meshcms.server.toolbox.Strings;
import com.cromoteca.meshcms.server.toolbox.Web;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import javax.servlet.ServletException;

public class Include extends ServerModule {
	private String encoding;
	private String file;
	private boolean preformatted;

	public void setFile(String file) {
		this.file = file;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isPreformatted() {
		return preformatted;
	}

	public void setPreformatted(boolean preformatted) {
		this.preformatted = preformatted;
	}

	public String getContent() throws ServletException, IOException {
		if (Strings.isNullOrEmpty(encoding)) {
			encoding = IO.SYSTEM_CHARSET;
		}

		String s;

		try {
			Charset charset = Charset.forName(encoding);

			if (file.indexOf(':') < 0) {
				Path path = getZoneDir().add(file);
				s = IO.readFully(rc.getWebSite().getFile(path), charset);
			} else {
				URLConnection connection = new URL(file).openConnection();
				s = IO.readFully(new InputStreamReader(connection.getInputStream(),
							charset));
			}
		} catch (IllegalArgumentException ex) {
			s = "Unsupported encoding: \"" + encoding + '"';
		} catch (IOException ex) {
			s = "File not found or not readable";
		}

		return Web.convertToHTMLEntities(s, IO.SYSTEM_CHARSET, preformatted);
	}
}

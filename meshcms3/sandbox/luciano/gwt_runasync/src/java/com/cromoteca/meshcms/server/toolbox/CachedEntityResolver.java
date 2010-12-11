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
package com.cromoteca.meshcms.server.toolbox;

import com.cromoteca.meshcms.server.storage.File;
import com.cromoteca.meshcms.server.webview.Context;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CachedEntityResolver implements EntityResolver {
	private File cacheDir;

	public File getCacheDir() {
		return cacheDir;
	}

	public void setCacheDir(File cacheDir) {
		this.cacheDir = cacheDir;
	}

	public InputSource resolveEntity(String publicId, String systemId)
		throws SAXException, IOException {
		cacheDir.create(true);

		File cache = cacheDir.getDescendant(IO.fixFileName(publicId, true));

		if (!cache.exists()) {
			try {
				URL url = new URL(systemId);
				URLConnection connection = url.openConnection();
				connection.setRequestProperty("User-Agent",
					"Cromoteca Cached Entity Resolver");

				InputStream is = connection.getInputStream();
				OutputStream os = cache.getOutputStream();
				IO.copyStream(is, os, true);
			} catch (IOException ex) {
				Context.log("Error while fetching entity", ex);

				return null;
			}
		}

		InputSource inputSource = new InputSource(systemId);
		inputSource.setPublicId(publicId);
		inputSource.setByteStream(cache.getInputStream());

		return inputSource;
	}
}

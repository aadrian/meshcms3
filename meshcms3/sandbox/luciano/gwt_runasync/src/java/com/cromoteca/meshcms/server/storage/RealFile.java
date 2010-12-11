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
package com.cromoteca.meshcms.server.storage;

import com.cromoteca.meshcms.server.webview.Context;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RealFile implements File {
	protected final java.io.File file;

	public RealFile(java.io.File file) {
		this.file = file;
	}

	public void setLastModified(long time) {
		file.setLastModified(time);
	}

	public boolean renameTo(File dest) {
		RealFile fileItem = (RealFile) dest;

		return file.renameTo(fileItem.file);
	}

	public List<String> getChildNames() {
		return Arrays.asList(file.list());
	}

	public List<File> getChildren() {
		return getChildren(AcceptAllItemFilter.get());
	}

	public List<File> getChildren(ItemFilter filter) {
		List<File> files = new ArrayList<File>();
		java.io.File[] listFiles = file.listFiles();

		if (listFiles == null) {
			return files;
		}

		for (java.io.File f : listFiles) {
			RealFile fileItem = new RealFile(f);

			if (filter.accept(fileItem)) {
				files.add(fileItem);
			}
		}

		return files;
	}

	public long getLength() {
		return file.length();
	}

	public long getLastModified() {
		return file.lastModified();
	}

	public boolean isDirectory() {
		try {
			return file.isDirectory();
		} catch (Exception ex) {
			Context.log("Returning true due to exception", ex);

			return true;
		}
	}

	public boolean isFile() {
		try {
			return file.isFile();
		} catch (Exception ex) {
			Context.log("Returning true due to exception", ex);

			return false;
		}
	}

	public RealFile getParent() {
		java.io.File parentFile = file.getParentFile();

		return parentFile == null ? null : new RealFile(parentFile);
	}

	public String getName() {
		return file.getName();
	}

	public String getAbsolutePath() {
		return getAbsolutePath(file);
	}

	public boolean exists() {
		try {
			return file.exists();
		} catch (Exception ex) {
			Context.log("Returning true due to exception", ex);

			return true;
		}
	}

	public boolean delete() {
		return file.delete();
	}

	public InputStream getInputStream() throws FileNotFoundException {
		return new FileInputStream(file);
	}

	public OutputStream getOutputStream() throws FileNotFoundException {
		return new FileOutputStream(file);
	}

	public RealFile getDescendant(String relativePath) {
		return new RealFile(new java.io.File(file, relativePath));
	}

	public boolean create(boolean asDirectory) {
		if (asDirectory) {
			file.mkdirs();

			return file.isDirectory();
		} else {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				Context.log(ex);
			}

			return file.isFile();
		}
	}

	@Override
	public String toString() {
		return "file:" + file;
	}

	public static String getAbsolutePath(java.io.File file) {
		try {
			return file.getCanonicalPath();
		} catch (IOException ex) {
			return file.getAbsolutePath();
		}
	}
}

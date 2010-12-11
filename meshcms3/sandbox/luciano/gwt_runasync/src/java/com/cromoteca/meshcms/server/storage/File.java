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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface File {
	boolean delete(); // removes files and empty dirs

	boolean exists();

	String getAbsolutePath();

	String getName();

	File getParent(); // be careful with the returned object -> getDescendant("..")

	boolean isDirectory();

	boolean isFile();

	long getLastModified();

	long getLength();

	List<String> getChildNames(); // be careful with the returned object

	List<File> getChildren(); // be careful with the returned object

	List<File> getChildren(ItemFilter filter); // be careful with the returned object

	boolean renameTo(File dest); // be careful with the operation

	void setLastModified(long time);

	InputStream getInputStream() throws FileNotFoundException;

	OutputStream getOutputStream() throws FileNotFoundException;

	File getDescendant(String relativePath); // be careful with the returned object

	boolean create(boolean asDirectory); // be careful with the created object
}

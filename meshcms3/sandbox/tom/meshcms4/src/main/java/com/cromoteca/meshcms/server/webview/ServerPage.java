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
package com.cromoteca.meshcms.server.webview;


/**
 * Contract to manage a web request.
 */
public interface ServerPage {
	/**
	 * Can return a bean to assign fields from the request.
	 *
	 * @return the bean object
	 */
	Object getBean();

	/**
	 * Processes the request.
	 *
	 * @param isPost true if it is a post request, false if it is a get
	 *
	 * @return an optional string to redirect the request
	 */
	String process();
}

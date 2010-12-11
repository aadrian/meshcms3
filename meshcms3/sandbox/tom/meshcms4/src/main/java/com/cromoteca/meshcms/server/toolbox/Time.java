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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class Time extends com.cromoteca.meshcms.client.toolbox.Time {
	public static final long YEAR = DAY * 365L;

	// TODO: this is used for RSS too - see http://asg.web.cmu.edu/rfc/rfc822.html#sec-5
	public static final DateFormat RSS_DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z",
			new Locale("en", "US"));

	static {
		RSS_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
}

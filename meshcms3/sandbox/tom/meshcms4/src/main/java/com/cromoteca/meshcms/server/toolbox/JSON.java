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

import com.cromoteca.meshcms.client.toolbox.Path;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;

public class JSON {
	private static Gson gson;

	static {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setVerbosePrinting();
		gsonBuilder.registerTypeAdapter(Path.class, new PathTypeAdapter());
		gsonBuilder.registerTypeAdapter(Date.class, new DateTypeAdapter());
		gson = gsonBuilder.create();
	}

	public static Gson getGson() {
		return gson;
	}

	public static class DateTypeAdapter implements JsonSerializer<Date>,
		JsonDeserializer<Date> {
		public JsonElement serialize(Date src, Type typeOfSrc,
			JsonSerializationContext context) {
			return new JsonPrimitive(Time.RSS_DATE_FORMAT.format(src));
		}

		public Date deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
			try {
				return Time.RSS_DATE_FORMAT.parse(json.getAsString());
			} catch (ParseException ex) {
				throw new JsonParseException(ex);
			}
		}
	}

	public static class PathTypeAdapter implements JsonSerializer<Path>,
		JsonDeserializer<Path> {
		public JsonElement serialize(Path src, Type typeOfSrc,
			JsonSerializationContext context) {
			return new JsonPrimitive(src.toString());
		}

		public Path deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
			return new Path(json.getAsString());
		}
	}
}

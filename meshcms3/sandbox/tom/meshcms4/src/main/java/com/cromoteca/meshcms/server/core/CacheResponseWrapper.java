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

import com.cromoteca.meshcms.server.toolbox.IO;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Modifies the response to write the page to both the browser and the cache.
 * This class is a slightly modified version of the GZIPResponseWrapper found
 * in <a href="http://www.onjava.com/pub/a/onjava/2003/11/19/filters.html">this
 * article</a>.
 *
 * @see CacheResponseStream
 */
public class CacheResponseWrapper extends HttpServletResponseWrapper {
	HttpServletResponse response;
	OutputStream cacheOutput;
	PrintWriter writer;
	ServletOutputStream stream;
	private boolean gzipResponse;

	/**
	 * Creates a new wrapper.
	 *
	 * @param response the original response
	 * @param cacheOutput the output stream to write the cached page to
	 */
	public CacheResponseWrapper(HttpServletResponse response,
		OutputStream cacheOutput, boolean gzipResponse) {
		super(response);
		this.response = response;
		this.cacheOutput = cacheOutput;
		this.gzipResponse = gzipResponse;
	}

	/**
	 * Creates the output stream.
	 *
	 * @see CacheResponseStream
	 */
	public ServletOutputStream createOutputStream()
		throws IOException {
		OutputStream outputStream;

		if (gzipResponse) {
			if (cacheOutput == null) {
				outputStream = new GZIPOutputStream(response.getOutputStream());
			} else {
				outputStream = new GZIPOutputStream(new TeeOutputStream(
							response.getOutputStream(),
							cacheOutput));
			}
		} else {
			if (cacheOutput == null) {
				outputStream = response.getOutputStream();
			} else {
				outputStream = new TeeOutputStream(response.getOutputStream(),
						new GZIPOutputStream(cacheOutput));
			}
		}

		return new WrapperServletOutputStream(outputStream);
	}

	/**
	 * Closes the stream.
	 */
	public void finishResponse() {
		try {
			if (writer != null) {
				writer.close();
			}

			if (stream != null) {
				stream.close();
			}
		} catch (IOException ex) {}
	}

	@Override
	public void flushBuffer() throws IOException {
		if (writer != null) {
			writer.flush();
		}

		if (stream != null) {
			stream.flush();
		}
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (writer != null) {
			throw new IllegalStateException("getWriter() has already been called!");
		}

		if (stream == null) {
			stream = createOutputStream();
		}

		return stream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (writer != null) {
			return writer;
		}

		if (stream != null) {
			throw new IllegalStateException(
				"getOutputStream() has already been called!");
		}

		stream = createOutputStream();
		writer = new PrintWriter(new OutputStreamWriter(stream, IO.SYSTEM_CHARSET));

		return writer;
	}

	@Override
	public void setContentLength(int len) {
		if (!gzipResponse) {
			super.setContentLength(len);
		}
	}
}

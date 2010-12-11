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

import com.cromoteca.meshcms.server.webview.Context;
import com.glaforge.i18n.io.SmartEncodingInputStream;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import net.htmlparser.jericho.Source;

@SuppressWarnings("deprecation")
public class InMemoryResponseWrapper extends HttpServletResponseWrapper {
	private CharArrayWriter charArrayWriter;
	private InMemoryServletOutputStream inMemoryStream;
	private PrintWriter printWriter;
	private int status = SC_OK;

	public InMemoryResponseWrapper(HttpServletResponse httpResponse) {
		super(httpResponse);
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (printWriter != null) {
			throw new IllegalStateException("getWriter has already been called");
		}

		if (inMemoryStream == null) {
			inMemoryStream = new InMemoryServletOutputStream();
		}

		return inMemoryStream;
	}

	public Source getSource() throws IOException {
		Source source = null;

		if (printWriter != null && charArrayWriter != null) {
			printWriter.flush();
			source = new Source(charArrayWriter.toString());
		} else if (inMemoryStream != null) {
			inMemoryStream.flush();

			ByteArrayInputStream input = new ByteArrayInputStream(inMemoryStream
							.toByteArray());
			SmartEncodingInputStream seis = new SmartEncodingInputStream(input,
					SmartEncodingInputStream.BUFFER_LENGTH_8KB, IO.ISO_8859_1);
			source = new Source(seis.getReader());

			//		} else {
			//			throw new IllegalStateException(
			//				"Neither getOutputStream nor getWriter have been called yet");
		}

		return source;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (inMemoryStream != null) {
			throw new IllegalStateException("getOutputStream has already been called");
		}

		if (printWriter == null) {
			charArrayWriter = new CharArrayWriter();
			printWriter = new PrintWriter(charArrayWriter);
		}

		return printWriter;
	}

	@Override
	public void setContentLength(int len) {}

	public void writeAll() throws IOException {
		if (inMemoryStream != null) {
			byte[] bytes = inMemoryStream.toByteArray();
			super.setContentLength(bytes.length);
			IO.copyStream(new ByteArrayInputStream(bytes), super.getOutputStream(),
				false);
		} else if (charArrayWriter != null) {
			char[] chars = charArrayWriter.toCharArray();
			IO.copyReaderToWriter(new CharArrayReader(chars), super.getWriter(), false);
		}
	}

	public byte[] getAsBytes() {
		if (inMemoryStream != null) {
			return inMemoryStream.toByteArray();
		} else if (charArrayWriter != null) {
			char[] chars = charArrayWriter.toCharArray();

			try {
				return new String(chars).getBytes(IO.SYSTEM_CHARSET);
			} catch (UnsupportedEncodingException ex) {
				Context.log(ex);
			}
		}

		return null;
	}

	public String getAsString() {
		if (inMemoryStream != null) {
			byte[] bytes = inMemoryStream.toByteArray();

			return new String(bytes);
		} else if (charArrayWriter != null) {
			char[] chars = charArrayWriter.toCharArray();

			return new String(chars);
		}

		return null;
	}

	public int getStatus() {
		return status;
	}

	@Override
	public void sendError(int status, String message)
		throws IOException {
		this.status = status;
		super.sendError(status, message);
	}

	@Override
	public void sendError(int status) throws IOException {
		this.status = status;
		super.sendError(status);
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		status = SC_MOVED_TEMPORARILY;
		super.sendRedirect(location);
	}

	@Override
	public void setStatus(int status) {
		this.status = status;
		super.setStatus(status);
	}

	@Override
	public void setStatus(int status, String message) {
		this.status = status;
		super.setStatus(status, message);
	}

	@Override
	public void flushBuffer() throws IOException {}
}

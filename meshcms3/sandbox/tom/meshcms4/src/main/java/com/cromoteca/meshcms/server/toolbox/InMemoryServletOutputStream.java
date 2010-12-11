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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletOutputStream;

public class InMemoryServletOutputStream extends ServletOutputStream {
	private ByteArrayOutputStream bufferStream;

	public InMemoryServletOutputStream() {
		bufferStream = new ByteArrayOutputStream();
	}

	@Override
	public void close() throws IOException {
		bufferStream.close();
	}

	@Override
	public void flush() throws IOException {
		bufferStream.flush();
	}

	public synchronized void reset() {
		bufferStream.reset();
	}

	public int size() {
		return bufferStream.size();
	}

	public synchronized byte[] toByteArray() {
		return bufferStream.toByteArray();
	}

	@Override
	public String toString() {
		return bufferStream.toString();
	}

	public String toString(String encoding) throws UnsupportedEncodingException {
		return bufferStream.toString(encoding);
	}

	@Override
	public void write(byte[] b) throws IOException {
		bufferStream.write(b);
	}

	@Override
	public synchronized void write(byte[] b, int off, int len) {
		bufferStream.write(b, off, len);
	}

	@Override
	public void write(int b) throws IOException {
		bufferStream.write(b);
	}
}

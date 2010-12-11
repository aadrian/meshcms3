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

import java.io.IOException;
import java.io.OutputStream;

public class TeeOutputStream extends OutputStream {
	private OutputStream primaryStream;
	private OutputStream secondaryStream;

	public TeeOutputStream(OutputStream primaryStream,
		OutputStream secondaryStream) {
		this.primaryStream = primaryStream;
		this.secondaryStream = secondaryStream;
	}

	@Override
	public void close() throws IOException {
		primaryStream.close();

		try {
			secondaryStream.close();
		} catch (IOException ex) {}
	}

	@Override
	public void flush() throws IOException {
		primaryStream.flush();

		try {
			secondaryStream.flush();
		} catch (IOException ex) {}
	}

	@Override
	public void write(byte[] b) throws IOException {
		primaryStream.write(b);

		try {
			secondaryStream.write(b);
		} catch (IOException ex) {}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		primaryStream.write(b, off, len);

		try {
			secondaryStream.write(b, off, len);
		} catch (IOException ex) {}
	}

	@Override
	public void write(int b) throws IOException {
		primaryStream.write(b);

		try {
			secondaryStream.write(b);
		} catch (IOException ex) {}
	}
}

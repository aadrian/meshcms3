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
import com.cromoteca.meshcms.server.storage.File;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class IO {
	public static final Charset ISO_8859_1 = Charset.forName("iso-8859-1");

	/**
	 * A standard size for buffers.
	 */
	public static final int BUFFER_SIZE = 4096;

	/**
	 * The number of bytes in a kilobyte (2^10).
	 */
	public static final int KBYTE = 1024;

	/**
	 * The number of bytes in a megabyte (2^20).
	 */
	public static final int MBYTE = KBYTE * KBYTE;

	/**
	 * The number of bytes in a gigabyte (2^30).
	 */
	public static final int GBYTE = MBYTE * KBYTE;

	/**
	 * Initialized with the name of the default system charset.
	 */
	public static final String SYSTEM_CHARSET;

	/**
	 * Initialized to report if the default system charset is multibyte.
	 */
	public static final boolean IS_MULTIBYTE_SYSTEM_CHARSET;

	/**
	 * Mapping of the ISO-8859-1 characters to characters included in FN_CHARS.
	 */
	public static final String FN_CHARMAP = "________________________________"
		+ "__'____'()..--._0123456789..(-)." + "_ABCDEFGHIJKLMNOPQRSTUVWXYZ(_)__"
		+ "'abcdefghijklmnopqrstuvwxyz(_)-_" + "________________________________"
		+ "__cL.Y_P_Ca(__R-o-23'mP._10)423_" + "AAAAAAACEEEEIIIIENOOOOOxOUUUUYTS"
		+ "aaaaaaaceeeeiiiienooooo-ouuuuyty";

	/**
	 * Characters that are considered spacers in a file name.
	 */
	public static final String FN_SPACERS = "_!'()-";

	static {
		String systemCharset = System.getProperty("file.encoding", ISO_8859_1.name());
		boolean multibyte = false;

		try {
			Charset c = Charset.forName(systemCharset);
			systemCharset = c.toString();
			multibyte = c.newEncoder().maxBytesPerChar() > 1.0F;
		} catch (Exception ex) {}

		SYSTEM_CHARSET = systemCharset;
		IS_MULTIBYTE_SYSTEM_CHARSET = multibyte;
	}

	/**
	 * Reads an <code>InputStream</code> and copy all data to an
	 * <code>OutputStream</code>.
	 *
	 * @param in       the Input Stream
	 * @param out      the Output Stream
	 * @param closeOut if true, out is closed when the copy has finished
	 *
	 * @throws IOException if an I/O error occurs.
	 */
	public static void copyStream(InputStream in, OutputStream out,
		boolean closeOut) throws IOException {
		byte[] b = new byte[BUFFER_SIZE];
		int n;

		try {
			while ((n = in.read(b)) != -1) {
				out.write(b, 0, n);
			}
		} finally {
			try {
				in.close();
			} finally {
				if (closeOut) {
					out.close();
				}
			}
		}
	}

	/**
	 * Copies the Reader to the Writer until there are no data left.
	 *
	 * @param reader      the Reader
	 * @param writer      the Writer
	 * @param closeWriter if true, closes the Writer at the end
	 *
	 * @throws IOException if an I/O error occurs
	 */
	public static void copyReaderToWriter(Reader reader, Writer writer,
		boolean closeWriter) throws IOException {
		char[] c = new char[BUFFER_SIZE];
		int n;

		while ((n = reader.read(c)) != -1) {
			writer.write(c, 0, n);
		}

		reader.close();
		writer.flush();

		if (closeWriter) {
			writer.close();
		}
	}

	/**
	 * Returns a modified name that does not contain characters not recommended in
	 * a file name.
	 */
	public static String fixFileName(String text, boolean spacers) {
		char[] chars = text.toCharArray();
		StringBuilder sb = new StringBuilder(chars.length);
		boolean needSeparator = false;

		for (char c : chars) {
			if (c < 256) {
				c = FN_CHARMAP.charAt(c);
			}

			if (Character.isLetterOrDigit(c)) {
				if (needSeparator) {
					if (spacers && sb.length() > 0) {
						sb.append('-');
					}

					needSeparator = false;
				}

				sb.append(Character.toLowerCase(c));
			} else {
				needSeparator = true;
			}
		}

		return sb.toString();
	}

	/**
	 * Writes a String to a File.
	 *
	 * @param s    the String to be written.
	 * @param file the destination file where to write
	 *
	 * @throws IOException If an I/O error occurs
	 */
	public static void write(String s, File file) throws IOException {
		write(s, file, SYSTEM_CHARSET);
	}

	public static void write(String s, File file, String charset)
		throws IOException {
		Writer writer = new BufferedWriter(new OutputStreamWriter(
					file.getOutputStream(),
					charset));
		writer.write(s);
		writer.close();
	}

	/**
	 * Copies the content of a file to another file.
	 *
	 * @param file            the file to be copied
	 * @param newFile         the file to copy to
	 * @param overwrite       if false, the file is not copied if newFile exists
	 * @param setLastModified if true, newFile gets the same date of file
	 *
	 * @return true if copied successfully, false otherwise
	 *
	 * @throws IOException if the content can't be copied due to an I/O error
	 */
	public static boolean copyFile(File file, File newFile, boolean overwrite,
		boolean setLastModified) throws IOException {
		if (newFile.exists() && !overwrite) {
			return false;
		}

		newFile.getParent().create(true);

		InputStream fis = null;

		try {
			fis = file.getInputStream();
			copyStream(fis, newFile.getOutputStream(), true);

			if (setLastModified) {
				newFile.setLastModified(file.getLastModified());
			}
		} finally {
			if (fis != null) {
				fis.close();
			}
		}

		return true;
	}

	/**
	 * Reads a file and puts all data into a String.
	 *
	 * @param file the file to read from.
	 *
	 * @return the content of the file as a string.
	 *
	 * @throws IOException If an I/O error occurs
	 */
	public static String readFully(File file) throws IOException {
		Reader reader = new InputStreamReader(file.getInputStream());
		String s = readFully(reader);
		reader.close();

		return s;
	}

	public static byte[] readAllBytes(File file) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		copyStream(file.getInputStream(), baos, true);

		return baos.toByteArray();
	}

	public static void writeAllBytes(byte[] b, File file)
		throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		copyStream(bais, file.getOutputStream(), true);
	}

	public static String readFully(File file, Charset charset)
		throws IOException {
		Reader reader = new InputStreamReader(file.getInputStream(), charset);
		String s = readFully(reader);
		reader.close();

		return s;
	}

/**
   * Reads from a Reader and puts all available data into a String.
   *
   * @param reader the reader to read from.
   *
   * @return the content of the <code>reader</code> as a string.
   *
   * @throws IOException If an I/O error occurs
   */
	public static String readFully(Reader reader) throws IOException {
		CharArrayWriter caw = new CharArrayWriter();
		char[] cbuf = new char[BUFFER_SIZE];
		int n;

		while ((n = reader.read(cbuf)) != -1) {
			caw.write(cbuf, 0, n);
		}

		return caw.toString();
	}

/**
   * A quick and dirty method to unzip an archive into a directory.
   *
   * @param zip source archive to be processed
   * @param dir destination directory where to unzip the archive.
   *
   * @throws IOException If an I/O error occurs
   */
	public static void unzip(File zip, File dir) throws IOException {
		dir.create(true);

		InputStream in = new BufferedInputStream(zip.getInputStream());
		ZipInputStream zin = new ZipInputStream(in);
		ZipEntry e;

		while ((e = zin.getNextEntry()) != null) {
			File f = dir.getDescendant(e.getName());

			if (e.isDirectory()) {
				f.create(true);
			} else {
				f.getParent().create(true);

				OutputStream out = f.getOutputStream();
				byte[] b = new byte[BUFFER_SIZE];
				int len;

				while ((len = zin.read(b)) != -1) {
					out.write(b, 0, len);
				}

				out.close();
			}
		}

		zin.close();
	}

/**
   * Copies a directory with its contents. See {@link DirectoryCopier} for
   * details on options.
   */
	public static boolean copyDirectory(File dir, File newDir,
		boolean overwriteDir, boolean overwriteFiles, boolean setLastModified) {
		DirectoryCopier dc = new DirectoryCopier(dir, newDir, overwriteDir,
				overwriteFiles, setLastModified);
		dc.process();

		return dc.getResult();
	}

	/**
	 * A "brute force" method to delete a file that might be in use by another
	 * thread or application. This method simply tries again and again for 20
	 * seconds then gives up.
	 *
	 * @param file the file to be deleted
	 *
	 * @return <code>true</code> if the delete operation succeded, and
	 *         <code>false<code> otherwise.
	 */
	public static boolean forceDelete(File file) {
		if (!file.exists()) {
			return true;
		}

		/* do not force on directories */
		if (file.isDirectory()) {
			return file.delete();
		}

		for (int i = 1; i < 20; i++) {
			if (file.delete()) {
				return true;
			}

			try {
				Thread.sleep(i * 100L);
			} catch (InterruptedException ex) {}
		}

		return false;
	}

	/**
	 * A "brute force" method to move or rename a file that might be in use by
	 * another thread or application. This method simply tries again and again for
	 * 20 seconds then gives up.
	 *
	 * @param oldFile   the old(source) File
	 * @param newFile   the new(destination) File
	 * @param overwrite if true, tries to delete newFile before renaming oldFile
	 *
	 * @return the result of the operation
	 */
	public static boolean forceRenameTo(File oldFile, File newFile,
		boolean overwrite) {
		if (newFile.exists()) {
			if (overwrite) {
				if (!forceDelete(newFile)) {
					return false;
				}
			} else {
				return false;
			}
		}

		for (int i = 0; i < 20; i++) {
			if (oldFile.renameTo(newFile)) {
				return true;
			}

			try {
				Thread.sleep(i * 100L);
			} catch (InterruptedException ex) {}
		}

		return false;
	}

	/**
	 * Returns a file name similar to <code>fileName</code>, but different from the
	 * names of the files in the directory.
	 */
	public static String generateUniqueName(String fileName, File directory) {
		if (directory.isDirectory()) {
			return generateUniqueName(fileName, directory.getChildNames());
		}

		return null;
	}

	/**
	 * Generates a unique (but similar to the original) file name, based on an
	 * exclusion list. <p/> E.g. <code>product.html</code> would be
	 * <code>product1.html</code> if the exclusion list already contains
	 * <code>product.html</code>
	 *
	 * @param fileName the source file name
	 * @param files    exlusion list of file names.
	 *
	 * @return a file name similar to <code>fileName</code>, but different from the
	 *         strings in the <code>files</code> array.
	 */
	public static String generateUniqueName(String fileName, List<String> files) {
		if (Strings.searchString(files, fileName, true) == -1) {
			return fileName;
		}

		// fileName = fileName.toLowerCase();
		String ext = "";
		int idx = fileName.lastIndexOf('.');

		if (idx != -1) {
			ext = fileName.substring(idx);
			fileName = fileName.substring(0, idx);
		}

		int d = 0;

		for (int i = fileName.length() - 1; i >= 0; i--) {
			if (!Character.isDigit(fileName.charAt(i))) {
				d = i + 1;

				break;
			}
		}

		int number = Strings.parseInt(fileName.substring(d), 1);
		fileName = fileName.substring(0, d);

		String temp;

		do {
			temp = fileName + ++number + ext;
		} while (Strings.searchString(files, temp, true) != -1);

		return temp;
	}

	public static File getFileFromPath(File parent, Path path) {
		return path.getElementCount() == 0 ? parent
		: parent.getDescendant(path.toString());
	}
}

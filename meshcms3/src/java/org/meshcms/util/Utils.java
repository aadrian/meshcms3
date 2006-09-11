/*
 * MeshCMS - A simple CMS based on SiteMesh
 * Copyright (C) 2004-2006 Luciano Vernaschi
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * You can contact the author at http://www.cromoteca.com
 * and at info@cromoteca.com
 */

package org.meshcms.util;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.zip.*;

/**
 * A collection of static utilities.
 *
 * @author Luciano Vernaschi
 */
public final class Utils {
  private Utils() {
  }

  /**
   * A string of characters to be used for random strings.
   */
  public static final String VALID_CHARS = "abcdefghijkmnpqrstuvwxyz23456789";

  /**
   * Characters that should not be found in a file name.
   */
  public static final String INVALID_FILENAME_CHARS = "\"\\/:*?<>| ,\t\n\r";

  /**
   * Characters that are always acceptable in a filename.
   */
  public static final String VALID_FILENAME_CHARS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'._-";

  /**
   * A standard size for buffers.
   */
  public static final int BUFFER_SIZE = 2048;

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
   * Repeatedly adds a character to the beginning of a number until the desired
   * length has been reached. Tipically used to add spaces or zeros.
   */
  public static String addDigits(int num, char space, int len) {
    return addDigits(Integer.toString(num), space, len);
  }

  /**
   * Repeatedly adds a character to the beginning of a string until the desired
   * length has been reached. Tipically used to add spaces or zeros.
   */
  public static String addDigits(String s, char space, int len) {
    if (s == null) {
      s = "";
    }

    while (s.length() < len) {
      s = space + s;
    }

    return s;
  }

  /**
   * Checks if an object is null or if its string representation is empty.
   */
  public static boolean isNullOrEmpty(Object s) {
    return s == null || s.toString().equals("");
  }

  /**
   * Checks if a string is null, empty or made of whitespaces only.
   */
  public static boolean isNullOrWhitespace(String s) {
    if (s == null) {
      return true;
    }

    for (int i = 0; i < s.length(); i++) {
      if (!Character.isWhitespace(s.charAt(i))) {
        return false;
      }
    }

    return true;
  }

  /**
   * Returns <code>s.trim()</code>, or null if <code>s</code> is null.
   */
  public static String trim(String s) {
    return (s == null) ? null : s.trim();
  }

  /**
   * Returns a non-null version of an object.
   *
   * @return s.toString(), or the empty string if s is null
   */
  public static String noNull(Object s) {
    return s == null ? "" : s.toString();
  }

  /**
   * Returns a non-null version of a string.
   *
   * @return s.toString(), or the default string provided if s is null
   */
  public static String noNull(String s, String def) {
    return s == null ? def : s;
  }

  /**
   * Compares two strings.&nbsp;Null values are accepted. If
   * <code>ignoreCase</code> is true, case is ignored.
   *
   * @return true if both strings are null or if they are equal, false
   * otherwise.
   */
  public static boolean compareStrings(String s1, String s2, boolean ignoreCase) {
    if (s1 == null && s2 == null) {
      return true;
    }

    if (s1 != null && s2 != null) {
      return ignoreCase ? s1.equalsIgnoreCase(s2) : s1.equals(s2);
    }

    return false;
  }

  /**
   * Checks if a string contains a value that is supposed to mean
   * &quot;true&quot;.
   *
   * @return true if the string is "true", "1", "yes", "ok", "checked",
   * "selected" or "on" (case insensitive),
   * false otherwise (null included)
   */
  public static boolean isTrue(String s) {
    if (s == null) {
      return false;
    }

    s = s.trim().toLowerCase();
    return s.equals("true") || s.equals("1") || s.equals("yes") ||
        s.equals("ok") || s.equals("checked") || s.equals("selected") ||
        s.equals("on");
  }

  /**
   * Shortens a string by cutting it and adding ellipses at the end. The string
   * is returned unmodified if its length is less or equal than len.
   */
  public static String limitedLength(String s, int len) {
    String s1;

    if (isNullOrEmpty(s)) {
      s1 = "";
    } else if (s.length() <= len) {
      s1 = s;
    } else if (len < 5) {
      s1 = "...";
    } else {
      s1 = s.substring(0, len - 4) + " ...";
    }

    return s1;
  }

  /**
   * Replaces ' with \' (useful for JavaScript).
   */
  public static String escapeSingleQuotes(Object o) {
    return replace(o, '\'', "\\'");
  }

  /**
   * Replaces ' with '' (useful for SQL).
   */
  public static String fixSingleQuotes(Object o) {
    return replace(o, '\'', "''");
  }

  /**
   * Returns an SQL string that represents the object.
   */
  public static String sqlString(Object o) {
    if (o == null) {
      return "NULL";
    }

    return "'" + replace(o, '\'', "''") + "'";
  }

  /**
   * Replaces all the occurrences of a character in a string.
   *
   * @param o the object that will be converted into the string to be analized
   * @param c the character to be replaced
   * @param n the string used in place of the character
   *
   * Return the empty string if s is null; the modified string otherwise.
   */
  public static String replace(Object o, char c, String n) {
    if (o == null) {
      return "";
    }

    String s = o.toString();
    StringBuffer sb = new StringBuffer();

    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == c) {
        sb.append(n);
      } else {
        sb.append(s.charAt(i));
      }
    }

    return sb.toString();
  }

  /**
   * Replaces some characters with their HTML entities. Only replaces &quot;,
   * &amp;, &#39;, &lt; and &gt;.
   */
  public static String encodeHTML(String s) {
    if (isNullOrEmpty(s)) {
      return "";
    }

    StringBuffer sb = new StringBuffer(s.length());

    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);

      switch (c) {
        case '\"':
          sb.append("&quot;");
          break;
        case '&':
          sb.append("&amp;");
          break;
        case '\'':
          sb.append("&#39;");
          break;
        case '<':
          sb.append("&lt;");
          break;
        case '>':
          sb.append("&gt;");
          break;
        default:
          sb.append(c);
      }
    }

    return sb.toString();
  }

  /**
   * Replaces some HTML entities with the corresponding characters. Only replaces
   * &quot;, &amp;, &#39;, &lt; and &gt;.
   */
  public static String decodeHTML(String s) {
    if (isNullOrEmpty(s)) {
      return "";
    }

    int sl = s.length();
    StringBuffer sb = new StringBuffer(sl);
    int i = 0;
    String s0;

    while (i < sl - 3) {
      s0 = s.substring(i, i + 4);

      if (s0.equals("&gt;")) {
        sb.append('>');
        i += 4;
      } else if (s0.equals("&lt;")) {
        sb.append('<');
        i += 4;
      } else if (s0.equals("&amp")) {
        if (i < sl - 4 && s.charAt(i + 4) == ';') {
          sb.append('&');
          i += 5;
        }
      } else if (s0.equals("&#39")) {
        if (i < sl - 4 && s.charAt(i + 4) == ';') {
          sb.append('\'');
          i += 5;
        }
      } else if (s0.equals("&quo")) {
        if (i < sl - 5 && s.charAt(i + 4) == 't' && s.charAt(i + 5) == ';') {
          sb.append('\"');
          i += 6;
        }
      } else {
        sb.append(s.charAt(i++));
      }
    }

    return sb.append(s.substring(i)).toString();
  }

  /**
   * Strips the HTML tags from a string.
   */
  public static String stripHTMLTags(String s) {
    return (s!=null) ? s.replaceAll("</?\\S+?[\\s\\S+]*?>", " ") : null;
  }

  /**
   * Copies the content of a file to another file.
   *
   * @param file the file to be copied
   * @param newFile the file to copy to
   * @param overwrite if false, the file is not copied if newFile exists
   * @param setLastModified if true, newFile gets the same date of file
   *
   * @return true if copied successfully, false otherwise
   */
  public static boolean copyFile(File file, File newFile, boolean overwrite,
                                 boolean setLastModified) throws IOException {
    if (!file.exists() || (newFile.exists() && !overwrite)) {
      return false;
    }

    FileInputStream fis = null;

    try {
      fis = new FileInputStream(file);
      copyStream(fis, new FileOutputStream(newFile), true);

      if (setLastModified) {
        newFile.setLastModified(file.lastModified());
      }
    } finally {
      if (fis != null) {
        fis.close();
      }
    }

    return true;
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
   * Reads an <code>InputStream</code> and copy all data to an
   * <code>OutputStream</code>.
   *
   * @param closeOut if true, out is closed when the copy has finished
   */
  public static void copyStream(InputStream in, OutputStream out,
                                boolean closeOut) throws IOException {
    byte b[] = new byte[BUFFER_SIZE];
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
   * @param closeWriter if true, closes the Writer at the end
   */
  public static void copyReaderToWriter(Reader reader, Writer writer,
      boolean closeWriter) throws IOException {
    char c[] = new char[BUFFER_SIZE];
    int n;

    while ((n = reader.read(c)) != -1) {
      writer.write(c, 0, n);
    }

    reader.close();

    if (closeWriter) {
      writer.close();
    }
  }

  /**
   * Writes the whole String to the File using a buffered FileWriter.
   */
  public static void writeFully(File file, String s) throws IOException {
    writeFully(file, s, null);
  }
  
  /**
   * Writes the whole String to the File using a buffered FileWriter and the
   * given charset.
   */
  public static void writeFully(File file, String s, String charset) throws IOException {
    Writer writer = isNullOrEmpty(charset) ? new BufferedWriter(new FileWriter(file)) :
        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
    writer.write(s);
    writer.close();
  }

  /**
   * Writes the whole byte array to the File using a FileOutputStream.
   */
  public static void writeFully(File file, byte[] b) throws IOException {
    FileOutputStream fos = new FileOutputStream(file);
    fos.write(b);
    fos.close();
  }

  /**
   * Reads a file and puts all data into a String.
   */
  public static String readFully(File file) throws IOException {
    return readFully(file, null);
  }

  /**
   * Reads a file and puts all data into a String.
   */
  public static String readFully(File file, String charset) throws IOException {
    Reader reader;

    try {
      reader = new InputStreamReader(new FileInputStream(file), charset);
    } catch (Exception ex) {
      reader = new FileReader(file);
    }

    reader = new BufferedReader(reader);
    String s = readFully(reader);
    reader.close();
    return s;
  }

  /**
   * Reads from a Reader and puts all available data into a String.
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
   * Reads from an InputStream and puts all available data into an array of
   * bytes.
   */
  public static byte[] readFully(InputStream in) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buf = new byte[BUFFER_SIZE];
    int n;

    while ((n = in.read(buf)) != -1) {
      baos.write(buf, 0, n);
    }

    return baos.toByteArray();
  }

  /**
   * Reads a file and put all lines into an array of Strings.
   */
  public static String[] readAllLines(File file)
      throws FileNotFoundException, IOException {
    BufferedReader reader = new BufferedReader(new FileReader(file));
    List list = new ArrayList();
    String line;

    while ((line = reader.readLine()) != null) {
      list.add(line);
    }

    reader.close();
    return (String[]) list.toArray(new String[list.size()]);
  }

  /**
   * A quick and dirty method to unzip an archive into a directory.
   */
  public static void unzip(File zip, File dir) throws IOException {
    dir.mkdirs();
    InputStream in = new BufferedInputStream(new FileInputStream(zip));
    ZipInputStream zin = new ZipInputStream(in);
    ZipEntry e;

    while ((e = zin.getNextEntry()) != null) {
      File f = new File(dir, e.getName());

      if (e.isDirectory()) {
        f.mkdirs();
      } else {
        f.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(f);
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
   * Creates a random string of a given length. Characters are picked from
   * {@link #VALID_CHARS}.
   */
  public static String generateRandomString(int len) {
    StringBuffer sb = new StringBuffer(len);

    for (int i = 0; i < len; i++) {
      sb.append(Utils.VALID_CHARS.charAt((int) (Math.random() *
                                                Utils.VALID_CHARS.length())));
    }

    return sb.toString();
  }

  /**
   * Creates a <code>String</code> containing the string representations of the
   * objects in the array, separated by <code>sep</code>. It can be seen as
   * somewhat opposite of <code>java.util.StringTokenizer</code>.
   */
  public static String generateList(Object[] list, String sep) {
    if (list == null) {
      return null;
    }

    if (list.length == 0) {
      return "";
    }

    StringBuffer sb = new StringBuffer();
    sb.append(list[0]);

    for (int i = 1; i < list.length; i++) {
      sb.append(sep).append(list[i]);
    }

    return sb.toString();
  }

  /**
   * Appends all properties into a single string using the given separator.
   */
  public static String listProperties(Properties p, String sep) {
    if (p == null) {
      return null;
    }

    Enumeration names = p.propertyNames();
    StringBuffer sb = new StringBuffer();

    if (names.hasMoreElements()) {
      String name = (String) names.nextElement();
      sb.append(name).append('=').append(p.getProperty(name));
    }

    while (names.hasMoreElements()) {
      String name = (String) names.nextElement();
      sb.append(sep).append(name).append('=').append(p.getProperty(name));
    }

    return sb.toString();
  }

  /**
   * Creates a <code>String</code> containing the string representations of the
   * objects in the collection, separated by <code>sep</code>. It can be seen as
   * somewhat opposite of <code>java.util.StringTokenizer</code>.
   */
  public static String generateList(Collection c, String sep) {
    if (c == null) {
      return null;
    }

    if (c.size() == 0) {
      return "";
    }

    Iterator iter = c.iterator();
    StringBuffer sb = new StringBuffer();

    if (iter.hasNext()) {
      sb.append(iter.next().toString());
    }

    while (iter.hasNext()) {
      sb.append(sep).append(iter.next());
    }

    return sb.toString();
  }

  /**
   * Creates a <code>String</code> containing the string representations of the
   * objects in the enumeration, separated by <code>sep</code>. It can be seen as
   * somewhat opposite of <code>java.util.StringTokenizer</code>.
   */
  public static String generateList(Enumeration e, String sep) {
    if (e == null) {
      return null;
    }

    if (!e.hasMoreElements()) {
      return "";
    }

    StringBuffer sb = new StringBuffer();

    if (e.hasMoreElements()) {
      sb.append(e.nextElement().toString());
    }

    while (e.hasMoreElements()) {
      sb.append(sep).append(e.nextElement());
    }

    return sb.toString();
  }

  /**
   * Creates a <code>String</code> containing the string representations of the
   * numbers in the array, separated by <code>sep</code>. It can be seen as
   * somewhat opposite of <code>java.util.StringTokenizer</code>.
   */
  public static String generateList(int[] list, String sep) {
    if (list == null) {
      return null;
    }

    if (list.length == 0) {
      return "";
    }

    StringBuffer sb = new StringBuffer(Integer.toString(list[0]));

    for (int i = 1; i < list.length; i++) {
      sb.append(sep).append(list[i]);
    }

    return sb.toString();
  }

  /**
   * Returns the sum of the numbers stored in the array.
   */
  public static int sum(int[] ints) {
    int s = 0;

    for (int i = 0; i < ints.length; i++) {
      s += ints[i];
    }

    return s;
  }

  /**
   * Returns a file name similar to <code>fileName</code>, but different from
   * the names of the files in the directory.
   */
  public static String generateUniqueName(String fileName, File directory) {
    if (directory.isDirectory()) {
      return generateUniqueName(fileName, directory.list());
    }

    return null;
  }

  /**
   * Returns a file name similar to <code>fileName</code>, but different from
   * the strings in the <code>files</code> array.
   *
   * @return a file name in the 8+3 DOS format
   */
  public static String generateUniqueDosName(String fileName, String[] files) {
    fileName = fileName.toLowerCase();
    String ext = "";
    int idx = fileName.lastIndexOf('.');

    if (idx != -1) {
      ext = fileName.substring(idx);

      if (ext.length() > 4) {
        ext = ext.substring(0, 4);
      }

      fileName = fileName.substring(0, idx);
    }

    String name = "";

    for (int i = 0; i < fileName.length(); i++) {
      char c = fileName.charAt(i);

      if (Character.isLetterOrDigit(c)) {
        name += c;

        if (name.length() == 8) {
          break;
        }
      }
    }

    if (name.length() == 0) {
      name = "file";
    }

    if (searchString(files, name + ext, true) < 0) {
      return name + ext;
    }

    int limit = 1;

    for (int i = 1; i <= 8; i++) {
      int first = limit;
      limit *= 10;

      String base = (name.length() <= 7 - i) ? name : name.substring(0, 7 - i);
      base += "_";

      for (int j = first; j < limit; j++) {
        String temp = base + j + ext;

        if (searchString(files, temp, true) < 0) {
          return temp;
        }
      }
    }

    return null;
  }

  /**
   * Returns a file name similar to <code>fileName</code>, but different from
   * the strings in the <code>files</code> array.
   */
  public static String generateUniqueName(String fileName, String[] files) {
    fileName = fileName.toLowerCase();
    String ext = "";
    int idx = fileName.lastIndexOf('.');

    if (idx != -1) {
      ext = fileName.substring(idx);
      fileName = fileName.substring(0, idx);
    }

    if (searchString(files, fileName + ext, true) == -1) {
      return fileName + ext;
    }

    int d = 0;

    for (int i = fileName.length() - 1; i >= 0; i--) {
      if (!Character.isDigit(fileName.charAt(i))) {
        d = i + 1;
        break;
      }
    }

    int number = parseInt(fileName.substring(d), 1);
    fileName = fileName.substring(0, d);
    String temp;

    do {
      temp = fileName + ++number + ext;
    } while (searchString(files, temp, true) != -1);

    return temp;
  }

  /**
   * Parses the string argument as an integer, but without returning
   * exception. If that would be the case, the default value provided is
   * returned instead.
   */
  public static int parseInt(String s, int def) {
    try {
      def = Integer.parseInt(s);
    } catch (Exception ex) {}

    return def;
  }

  /**
   * Parses the string argument as an long, but without returning
   * exception. If that would be the case, the default value provided is
   * returned instead.
   */
  public static long parseLong(String s, long def) {
    try {
      def = Long.parseLong(s);
    } catch (Exception ex) {}

    return def;
  }

  /**
   * Returns the tokens of a string. The default delimiter characters of
   * <code>java.util.StringTokenizer</code> are used.
   */
  public static String[] tokenize(String s) {
    return tokenize(s, null);
  }

  /**
   * Returns the tokens of a string using the specified delimiter characters.
   */
  public static String[] tokenize(String s, String delim) {
    if (s == null) {
      return null;
    }

    StringTokenizer st;

    if (isNullOrEmpty(delim)) {
      st = new StringTokenizer(s);
    } else {
      st = new StringTokenizer(s, delim);
    }

    String[] res = new String[st.countTokens()];

    for (int i = 0; i < res.length; i++) {
      res[i] = st.nextToken();
    }

    return res;
  }

  /**
   * Searches a string in an array of strings.
   *
   * @param array the array of strings
   * @param s the string to be searched
   * @param ignoreCase used to ask for a case insensitive search
   *
   * @return the index of the first occurrence, or -1 if not found
   */
  public static int searchString(String[] array, String s, boolean ignoreCase) {
    if (array == null || array.length == 0 || s == null) {
      return -1;
    }

    if (ignoreCase) {
      for (int i = 0; i < array.length; i++) {
        if (s.equalsIgnoreCase(array[i])) {
          return i;
        }
      }
    } else {
      for (int i = 0; i < array.length; i++) {
        if (s.equals(array[i])) {
          return i;
        }
      }
    }

    return -1;
  }

  /**
   * Tries to verify an e-mail address.
   */
  public static boolean checkAddress(String email) {
    if (isNullOrEmpty(email) || email.indexOf(' ') >= 0) {
      return false;
    }

    int dot = email.lastIndexOf('.');
    int at = email.lastIndexOf('@');
    return !(dot < 0 || at < 0 || at > dot);
  }

  /**
   * Generates a random integer between 0 (included) and max (excluded).
   */
  public static int getRandomInt(int max) {
    return (int) (Math.random() * max);
  }

  /**
   * Picks a random element from the array and returns it.
   */
  public static Object getRandomElement(Object[] array) {
    if (array == null || array.length == 0) {
      return null;
    }

    return array[getRandomInt(array.length)];
  }

  /**
   * Returns the decimal part of a float, regardless of its sign.
   */
  public static float decimalPart(float f) {
    return f - (int) f;
  }

  /**
   * Returns the decimal part of a double, regardless of its sign.
   */
  public static double decimalPart(double d) {
    return d - (long) d;
  }

  /**
   * Returns the sign of an integer.
   *
   * @return -1 if negative, 0 if zero, 1 if positive
   */
  public static int sign(int n) {
    if (n < 0) {
      return -1;
    }
    if (n > 0) {
      return 1;
    }
    return 0;
  }

  /**
   * Returns the closest number to n that does not exceed the interval between
   * min and max.
   */
  public static int constrain(int min, int max, int n) {
    if (n < min) {
      return min;
    }
    if (n > max) {
      return max;
    }
    return n;
  }

  /**
   * Adds a string at the end of another string, but only if the latter doesn't
   * end with the former.
   */
  public static String addAtEnd(String base, String what) {
    if (base == null) {
      base = what;
    } else if (!base.endsWith(what)) {
      base += what;
    }

    return base;
  }

  /**
   * Removes a string at the end of another string, if latter ends with the
   * former.
   */
  public static String removeAtEnd(String base, String what) {
    if (base != null && base.endsWith(what)) {
      base = base.substring(0, base.length() - what.length());
    }

    return base;
  }

  /**
   * Adds a string at the beginning of another string, but only if the latter
   * doesn't begin with the former.
   */
  public static String addAtBeginning(String base, String what) {
    if (base == null) {
      base = what;
    } else if (!base.startsWith(what)) {
      base = what + base;
    }

    return base;
  }

  /**
   * Removes a string at the beginning of another string, if the latter
   * begin with the former.
   */
  public static String removeAtBeginning(String base, String what) {
    if (base != null && base.startsWith(what)) {
      base = base.substring(what.length());
    }

    return base;
  }

  /**
   * Returns a relative path from folder to file using the separator provided.
   * In general, using {@link Path} provides better management of relative
   * paths.
   */
  public static String getRelativePath(File folder, File file, String separator) {
    return getRelativePath(getFilePath(folder), getFilePath(file), separator);
  }

  /**
   * Returns a relative path from folder to file using the separator provided.
   * In general, using {@link Path} provides better management of relative
   * paths.
   */
  public static String getRelativePath(String folder, String file,
                                       String separator) {
    String[] a0 = Utils.tokenize(folder, "/\\");
    String[] a1 = Utils.tokenize(file, "/\\");

    int i0 = 0;
    int i1 = 0;

    String result = "";

    while (i0 < a0.length && i1 < a1.length && a0[i0].equals(a1[i1])) {
      i0++;
      i1++;
    }

    while (i0++ < a0.length) {
      result += ".." + separator;
    }

    while (i1 < a1.length - 1) {
      result += a1[i1++] + separator;
    }

    if (i1 == a1.length - 1) {
      result += a1[i1];
    }

    return result;
  }

  /**
   * Adds the file path to the folder path. As an example,
   * <code>getCombinedPath("home/user/docs", "../myfile.txt", "/")</code>
   * returns "home/user/myfile.txt".
   * In general, using {@link Path} provides better management of paths.
   */
  public static String getCombinedPath(String folder, String file,
                                       String separator) {
    String[] a0 = Utils.tokenize(folder, "/\\");
    String[] a1 = Utils.tokenize(file, "/\\");

    int i0 = a0.length;
    int i1 = 0;

    while (i1 < a1.length && a1[i1].equals("..")) {
      i0--;
      i1++;
    }

    if (i0 < 0) {
      throw new IllegalArgumentException("Not enough levels");
    }

    String result = null;

    for (int i = 0; i < i0; i++) {
      if (!a0[i].equals(".")) {
        result = (result == null) ? a0[i] : result + separator + a0[i];
      }
    }

    for (int i = i1; i < a1.length; i++) {
      if (!a1[i].equals(".")) {
        result = (result == null) ? a1[i] : result + separator + a1[i];
      }
    }

    return noNull(result);
  }

  /**
   * Returns the full path of the file without having to catch exceptions.
   *
   * @return <code>f.getCanonicalPath()</code>, or
   * <code>f.getAbsolutePath()</code> if an exception is thrown
   */
  public static String getFilePath(File f) {
    try {
      return f.getCanonicalPath();
    } catch (IOException ex) {}

    return f.getAbsolutePath();
  }

  /**
   * Returns the extension of the file represented by the given object. The
   * object can be an instance of <code>java.io.File</code>,
   * <code>Path</code> or <code>java.lang.String</code>. Other objects are
   * converted to <code>String</code>s.
   *
   * @param includeDot if true, the dot is returned with the extension
   */
  public static String getExtension(Object o, boolean includeDot) {
    String fileName = null;

    if (o instanceof File) {
      fileName = ((File) o).getName();
    } else if (o instanceof Path) {
      fileName = ((Path) o).getLastElement();
    } else if (o != null) {
      fileName = o.toString();
    }

    if (fileName == null) {
      return null;
    }

    int dot = fileName.lastIndexOf('.');
    return (dot == -1) ? "" :
      fileName.substring(includeDot ? dot : dot + 1).toLowerCase();
  }

  /**
   * Removes the extension from a file name. The dot is removed too.
   */
  public static String removeExtension(Object o) {
    String fileName = null;

    if (o instanceof File) {
      fileName = ((File) o).getName();
    } else if (o instanceof Path) {
      fileName = ((Path) o).getLastElement();
    } else if (o != null) {
      fileName = o.toString();
    }

    if (fileName == null) {
      return null;
    }

    int dot = fileName.lastIndexOf('.');
    return (dot == -1) ? fileName : fileName.substring(0, dot);
  }

  /**
   * Returns the common part at the beginning of two strings.
   *
   * @return null if at least one of the strings is null, otherwise the common
   * part is returned, that can be empty
   */
  public static String getCommonPart(String s1, String s2) {
    if (s1 == null || s2 == null) {
      return null;
    }

    int len = Math.min(s1.length(), s2.length());

    for (int i = 0; i < len; i++) {
      if (s1.charAt(i) != s2.charAt(i)) {
        return s1.substring(0, i);
      }
    }

    return s1.length() < s2.length() ? s1 : s2;
  }

  /**
   * Converts the underscores to spaces and, if requested, applies the title case
   * to a string.
   */
  public static String beautify(String s, boolean titleCase) {
    StringBuffer sb = new StringBuffer(s.length());
    boolean nextUpper = true;

    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);

      if (c == '_') {
        c = ' ';
      }

      if (c == ' ') {
        nextUpper = true;
      } else {
        if (titleCase && nextUpper) {
          c = Character.toTitleCase(c);
          nextUpper = false;
        }
      }

      sb.append(c);
    }

    return sb.toString();
  }

  /**
   * Modifies a string to make it more suitable as a filename. "Dangerous"
   * characters are converted to underscores and lower case is applied if
   * requested. Same as {@link #uglify(String, boolean, String)} with an
   * underscore as spacer.
   */
  public static String uglify(String s, boolean lowerCase) {
    return uglify(s, lowerCase, "_");
  }

  /**
   * Modifies a string to make it more suitable as a filename. "Dangerous"
   * characters are converted to underscores and lower case is applied if
   * requested.
   *
   * @param spacer a safe String (usually a single character) to be used in
   * place of spaces
   */
  public static String uglify(String s, boolean lowerCase, String spacer) {
    s = s.trim();
    StringBuffer sb = new StringBuffer(s.length());
    char last = '?';

    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);

      if (VALID_FILENAME_CHARS.indexOf(c) != -1) {
        if (lowerCase) {
          c = Character.toLowerCase(c);
        }

        last = c;
        sb.append(c);
      } else if (last != ' ') {
        last = ' ';
        sb.append(' ');
      }
    }

    return replace(sb.toString().trim(), ' ', spacer);
  }

  /**
   * A "brute force" method to delete a file that might be in use by
   * another thread or application. This method simply tries again and again
   * for 20 seconds then gives up.
   *
   * @return the result of the operation
   */
  public static boolean forceDelete(File file) {
    if (!file.exists()) {
      return true;
    }

    for (int i = 0; i < 100; i++) {
      if (file.delete()) {
        return true;
      }

      try {
        Thread.sleep(200L);
      } catch (InterruptedException ex) {}
    }

    return false;
  }

  /**
   * A "brute force" method to move or rename a file that might be in use by
   * another thread or application. This method simply tries again and again
   * for 20 seconds then gives up.
   *
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

    for (int i = 0; i < 100; i++) {
      if (oldFile.renameTo(newFile)) {
        return true;
      }

      try {
        Thread.sleep(200L);
      } catch (InterruptedException ex) {}
    }

    return false;
  }

  /**
   * Returns a nicer representation of the length of the file. The file length
   * is returned as bytes, kilobytes or megabytes, with the unit attached.
   */
  public static String formatFileLength(File file) {
    return formatFileLength(file.length());
  }

  /**
   * Returns a nicer representation of the number as a file length. The number
   * is returned as bytes, kilobytes or megabytes, with the unit attached.
   */
  public static String formatFileLength(long length) {
    DecimalFormat format = new DecimalFormat("###0.##");
    double num = length;
    String unit;

    if (length < KBYTE) {
      unit = "B";
    } else if (length < MBYTE) {
      num /= KBYTE;
      unit = "KB";
    } else if (length < GBYTE) {
      num /= MBYTE;
      unit = "MB";
    } else {
      num /= GBYTE;
      unit = "GB";
    }

    return format.format(num) + unit;
  }

  /**
   * Encodes a path as a URL, using UTF-8.
   */
  public static String encodeURL(Path path) {
    return encodeURL(path.toString());
  }

  /**
   * Encodes a URL using UTF-8.
   */
  public static String encodeURL(String url) {
    try {
      url = URLEncoder.encode(url, "UTF-8");
    } catch (UnsupportedEncodingException ex) {
      ex.printStackTrace();
    }

    return url;
  }

  /**
   * Decodes a URL using UTF-8.
   */
  public static String decodeURL(String url) {
    try {
      url = URLDecoder.decode(url, "UTF-8");
    } catch (UnsupportedEncodingException ex) {
      ex.printStackTrace();
    }

    return url;
  }

  /**
   * Returns the java.util.Locale object for a given locale name (e.g. en_US).
   */
  public static Locale getLocale(String localeName) {
    if (!isNullOrEmpty(localeName)) {
      Locale[] locales = Locale.getAvailableLocales();

      for (int i = 0; i < locales.length; i++) {
        if (localeName.equals(locales[i].toString())) {
          return locales[i];
        }
      }
    }

    return null;
  }

  /**
   * Returns all the locales that have no country and no variant.
   */
  public static Locale[] getLanguageLocales() {
    Locale[] all = Locale.getAvailableLocales();
    List list = new ArrayList();

    for (int i = 0; i < all.length; i++) {
      if (all[i].toString().length() == 2) {
        list.add(all[i]);
      }
    }

    return (Locale[]) list.toArray(new Locale[list.size()]);
  }
}

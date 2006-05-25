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
import java.util.*;

/**
 * An abstract representation of a file path. The root of the path is
 * undefined, and the path can be relative (i.e. can start with '..').
 *
 * Example of paths are:
 *
 * <ul>
 *  <li>(the empty path)<br>This is meant as the (relative) root;</li>
 *  <li>filename.txt</li>
 *  <li>home/user/document.html</li>
 *  <li>../../directoryname</li>
 * </ul>
 *
 * A <code>Path</code> can be created from any object. When you call a
 * constructor, the path is initialized as empty, then the objects passed to
 * the constructor are added to it.
 *
 * When all objects have been added, the path is simplified by removing
 * redundant elements. For example, "home/user/../otheruser"
 * is reduced to "home/otheruser".
 *
 * After the constructor returns, the
 * <code>Path</code> object is immutable. When you call a method to modify
 * it (like one of the <code>add()</code> methods), it returns a new
 * <code>Path</code> that is the result of the requested operation.
 *
 * The objects are added as follows:
 *
 * <ul>
 *  <li>if the object is null, nothing is added;</li>
 *  <li>if it is another <code>Path</code>, its elements are added;</li>
 *  <li>if it is a <code>String</code>, it is split in tokens (divided by
 *   slashes or backslashes) and these tokens are added as elements;</li>
 *  <li>if it is a <code>Collection</code>, any member of the
 *   <code>Collection</code> is added as a separate object;</li>
 *  <li>if it is an array of <code>String</code>s, any member of the array is
 *   added as a separate <code>String</code> (to be tokenized);</li>
 *  <li>if it is another kind of object, its <code>toString()</code> method is
 *   called and the returned <code>String</code> is tokenized and added.</li>
 * </ul>
 *
 * @author Luciano Vernaschi
 */
public class Path implements Comparable, Serializable, Cloneable {
  protected String pathName;
  protected String[] elements;

  public static Path ROOT = new Path();

  /**
   * Creates an empty path.
   */
  public Path() {
    this(null, null, null);
  }

  /**
   * Creates a path and adds an object to it.
   */
  public Path(Object o) {
    this(o, null, null);
  }

  /**
   * Creates a path and adds two objects to it.
   */
  public Path(Object o1, Object o2) {
    this(o1, o2, null);
  }

  /**
   * Creates a path and adds three objects to it.
   */
  public Path(Object o1, Object o2, Object o3) {
    List list = new ArrayList();
    addObjectToList(list, o1);
    addObjectToList(list, o2);
    addObjectToList(list, o3);

    for (int i = 0; i < list.size(); i++) {
      String s = (String) list.get(i);

      if (s.equals("") || s.equals(".")) {
        list.remove(i--);
      } else if (s.equals("..")) {
        if (i > 0 && !"..".equals(list.get(i - 1))) {
          list.remove(i--);
          list.remove(i--);
        }
      }
    }

    elements = (String[]) list.toArray(new String[list.size()]);
    pathName = Utils.generateList(elements, "/");
  }

  protected void addObjectToList(List list, Object o) {
    if (o == null) {
      //
    } else if (o instanceof Path) {
      Path p = (Path) o;

      for (int i = 0; i < p.getElementCount(); i++) {
        list.add(p.getElementAt(i));
      }
    } else if (o instanceof String[]) {
      String[] s = (String[]) o;

      for (int i = 0; i < s.length; i++) {
        addObjectToList(list, s[i]);
      }
    } else if (o instanceof Collection) {
      Iterator i = ((Collection) o).iterator();

      while (i.hasNext()) {
        addObjectToList(list, i.next());
      }
    } else { // also works for java.io.File objects
      StringTokenizer st = new StringTokenizer(o.toString(), "\\/");

      while (st.hasMoreTokens()) {
        list.add(st.nextToken());
      }
    }
  }

  /**
   * Adds an object to the current path.
   *
   * @return a new <code>Path</code> which is the combination of the current
   * path and the added object
   */
  public Path add(Object o) {
    return new Path(this, o);
  }

  /**
   * Adds two objects to the current path.
   *
   * @return a new <code>Path</code> which is the combination of the current
   * path and the added objects
   */
  public Path add(Object o1, Object o2) {
    return new Path(this, o1, o2);
  }

  /**
   * Returns the parent of the current path. The parent of the root path is
   * '..' (a <code>Path</code> with one element whose value is "..").
   */
  public Path getParent() {
    return new Path(this, "..");
  }

  /**
   * Returns a parent of the current path, whose element count is equal to the
   * passed value.
   */
  public Path getPartial(int count) {
    if (count < 0) {
      throw new IllegalArgumentException("Negative level not allowed");
    }

    if (count == 0) {
      return ROOT;
    }

    if (count >= elements.length) {
      return this;
    }

    String[] partial = new String[count];
    System.arraycopy(elements, 0, partial, 0, count);
    return new Path(partial);
  }

  /**
   * Returns the common part between the two Paths.
   */
  public Path getCommonPath(Path other) {
    return commonPart(this, other);
  }

  /**
   * Returns <code>true</code> when the first element of the current path is
   * "..".
   */
  public boolean isRelative() {
    return elements.length > 0 && elements[0].equals("..");
  }

  /**
   * Returns <code>true</code> if the path is empty.
   */
  public boolean isRoot() {
    return elements.length == 0;
  }

  /**
   * Returns <code>true</code> if the path current path is contained in the
   * given path directly. Example:
   * <pre>
   * Path myPath = new Path("home/user/myfile.txt");
   * myPath.isChildOf(new Path("nohome")); // returns false
   * myPath.isChildOf(new Path("home")); // returns false
   * myPath.isChildOf(new Path("home/user")); // returns true
   * </pre>
   */
  public boolean isChildOf(Path parent) {
    if (parent == null) {
      return false;
    }

    int level = parent.getElementCount();

    if (elements.length != level + 1) {
      return false;
    }

    for (int i = 0; i < level; i++) {
      if (!elements[i].equals(parent.getElementAt(i))) {
        return false;
      }
    }

    return true;
  }

  /**
   * Returns <code>true</code> if the current path is contained in the
   * given path (at any depth). Example:
   * <pre>
   * Path myPath = new Path("home/user/myfile.txt");
   * myPath.isContainedIn(new Path("nohome")); // returns false
   * myPath.isContainedIn(new Path("home")); // returns true
   * myPath.isContainedIn(new Path("home/user")); // returns true
   * </pre>
   */
  public boolean isContainedIn(Path root) {
    if (root == null) {
      return false;
    }

    if (root.isRelative()) {
      throw new IllegalArgumentException("Root path can't be negative");
    }

    int level = root.getElementCount();

    if (level > elements.length) {
      return false;
    }

    for (int i = 0; i < level; i++) {
      if (!elements[i].equals(root.getElementAt(i))) {
        return false;
      }
    }

    return true;
  }

  /**
   * Returns the current path as relative to the given root. Example:
   * <pre>
   * Path myPath = new Path("home/user/myfile.txt");
   * myPath.getRelativeTo(new Path("home")); // returns "user/myfile.txt"
   * </pre>
   */
  public Path getRelativeTo(Object root) {
    Path rootPath = (root instanceof Path) ? (Path) root : new Path(root);

    if (rootPath.isRelative()) {
      throw new IllegalArgumentException("Root path can't be negative");
    }

    int i0 = 0;
    int i1 = 0;

    List list = new ArrayList();

    while (i0 < rootPath.getElementCount() && i1 < elements.length &&
           rootPath.getElementAt(i0).equals(elements[i1])) {
      i0++;
      i1++;
    }

    while (i0++ < rootPath.getElementCount()) {
      list.add("..");
    }

    while (i1 < elements.length - 1) {
      list.add(elements[i1++]);
    }

    if (i1 == elements.length - 1) {
      list.add(elements[i1]);
    }

    return new Path(list);
  }

  /**
   * Returns a <code>File</code> object relative to the given file.
   */
  public File getFile(File parent) {
    return elements.length == 0 ? parent : new File(parent, pathName);
  }

  /**
   * Returns the number of elements of the current path. Example:
   * <pre>
   * new Path().getElementCount(); // returns 0
   * new Path("home/user").getElementCount(); // returns 2
   * new Path("../user").getElementCount(); // returns 2
   */
  public int getElementCount() {
    return elements.length;
  }

  /**
   * Returns the element at the given index. There is no check for the index
   * value, so an <code>ArrayIndexOutOfBoundsException</code> might be thrown.
   */
  public String getElementAt(int index) {
    return elements[index];
  }

  /**
   * Returns the last element of the current path (usually the file name). For
   * the root path the empty <code>String</code> is returned.
   */
  public String getLastElement() {
    return elements.length == 0 ? "" : elements[elements.length - 1];
  }

  /**
   * Returns the <code>String</code> representation of the current path. The
   * separator between elements is always a slash, regardless of the platform.
   */
  public String toString() {
    return pathName;
  }

  /**
   * Returns the path encoded As a link: if the path is not empty, adds a
   * slash at the beginning.
   */
  public String getAsLink() {
    return elements.length == 0 ? "" : '/' + pathName;
  }

  /**
   * Compares this path to a new <code>Path</code> built by calling
   * <code>new Path(o)</code>
   */
  public int compareTo(Object o) {
    return compareTo(new Path(o));
  }

  /**
   * Compares two paths. Please note that <code>path1.compareTo(path2)</code>
   * is different from
   * <code>path1.toString().compareTo(path2.toString())</code>, since this
   * method compares the single elements of the paths.
   */
  public int compareTo(Path other) {
    int level = other.getElementCount();
    int result;

    for (int i = 0; i < elements.length; i++) {
      if (i >= level) {
        return 1;
      }

      result = elements[i].compareTo(other.getElementAt(i));

      if (result != 0) {
        return result;
      }
    }

    return level > elements.length ? -1 : 0;
  }

  /**
   * Returns the hash code of the <code>String</code> that representes this path.
   */
  public int hashCode() {
    return pathName.hashCode();
  }

  /**
   * Checks the two paths for equality. They are equal when their string
   * representations are equal.
   */
  public boolean equals(Object o) {
    if (o == null || !(o instanceof Path)) {
      return false;
    }

    return pathName.equals(o.toString());
  }

  /**
   * Returns the common part between the two Paths.
   */
  public static Path commonPart(Path p1, Path p2) {
    int n = Math.min(p1.getElementCount(), p2.getElementCount());

    for (int i = 0; i < n; i++) {
      if (!p1.getElementAt(i).equals(p2.getElementAt(i))) {
        return p1.getPartial(i);
      }
    }

    return p1;
  }

  /**
   * Returns the successor of this <code>Path</code>, as defined in the Javadoc
   * <code>of java.util.TreeMap.subMap(...)</code>. This is useful when you need
   * to use that method to get a <em>closed range</em> submap (or headmap, or
   * tailmap) of <code>Path</code>s.
   */
  public Path successor() {
    return (elements.length == 0) ? new Path("\0") : getParent().add(getLastElement() + '\0');
  }

  protected Object clone() throws CloneNotSupportedException {
    //return super.clone();
    return new Path(this);
  }
}

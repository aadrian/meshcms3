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
package com.cromoteca.meshcms.client.toolbox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An abstract representation of a file path. The root of the path is undefined,
 * and the path can be relative (i.e. can start with '..'). Example of paths
 * are: <ul> <li>(the empty path)<br> This is meant as the (relative) root;</li>
 * <li>filename.txt</li> <li>home/user/document.html</li>
 * <li>../../directoryname</li> </ul> A <code>Path</code> can be created from
 * any object. When you call a constructor, the path is initialized as empty,
 * then the objects passed to the constructor are added to it. When all objects
 * have been added, the path is simplified by removing redundant elements. For
 * example, "home/user/../otheruser" is reduced to "home/otheruser". After the
 * constructor returns, the <code>Path</code> object is immutable. When you call
 * a method to modify it (like one of the <code>add()</code> methods), it
 * returns a new <code>Path</code> that is the result of the requested
 * operation. The objects are added as follows: <ul> <li>if the object is null,
 * nothing is added;</li> <li>if it is another <code>Path</code>, its elements
 * are added;</li> <li>if it is a <code>String</code>, it is split in tokens
 * (divided by slashes or backslashes) and these tokens are added as
 * elements;</li> <li>if it is a <code>Collection</code>, any member of the
 * <code>Collection</code> is added as a separate object;</li> <li>if it is an
 * array of <code>String</code>s, any member of the array is added as a separate
 * <code>String</code> (to be tokenized);</li> <li>if it is another kind of
 * object, its <code>toString()</code> method is called and the returned
 * <code>String</code> is tokenized and added.</li> </ul>
 *
 * @author Luciano Vernaschi
 */
public final class Path implements Comparable<Path>, Serializable {
	private static final String[] EMPTY_ELEMENTS = new String[0];
	public static final Path ROOT = new Path();
	private String toStringCache;
	private String[] elements;

	/**
	 * Creates a path and adds some objects to it.
	 *
	 * @param pathItems the strings to be added
	 */
	public Path(String... pathItems) {
		List<String> list = new ArrayList<String>();

		for (String e : pathItems) {
			if (e != null) {
				list.addAll(Arrays.asList(e.split("[\\\\/]")));
			}
		}

		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i);

			if ("".equals(s) || ".".equals(s)) {
				list.remove(i--);
			} else if (i > 0 && "..".equals(s) && !"..".equals(list.get(i - 1))) {
				list.remove(i--);
				list.remove(i--);
			}
		}

		elements = list.toArray(new String[list.size()]);
	}

	public Path() {
		elements = EMPTY_ELEMENTS;
	}

	/**
	 * Returns the common part between the two Paths.
	 *
	 * @param p1 the Path 1
	 * @param p2 the Path 2
	 *
	 * @return a Path representing the common part between <code>p1</code> and
	 *         <code>p2</code>
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

	public Path add(Path path) {
		return add(path.getElements());
	}

	/**
	 * Adds some objects to the current path.
	 *
	 * @param pathItems the strings to be added to the current path
	 *
	 * @return a new <code>Path</code> which is the combination of the current path
	 *         and the added objects
	 */
	public Path add(String... pathItems) {
		String[] items = new String[elements.length + pathItems.length];
		System.arraycopy(elements, 0, items, 0, elements.length);
		System.arraycopy(pathItems, 0, items, elements.length, pathItems.length);

		return new Path(items);
	}

	/**
	 * Compares two paths. Please note that <code>path1.compareTo(path2)</code> is
	 * different from <code>path1.toString().compareTo(path2.toString())</code>,
	 * since this method compares the single elements of the paths.
	 *
	 * @param other the path to compare to this path
	 *
	 * @return -1, 0 or 1 as a compare result.
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
	 * Checks the two paths for equality. They are equal when their string
	 * representations are equal.
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Path)) {
			return false;
		}

		return toString().equals(o.toString());
	}

	/**
	 * Returns this path object encoded As a link: if the path is not empty, adds a
	 * slash at the beginning.
	 *
	 * @return a link represenation of this path.
	 */
	public String asLink() {
		String s = toString();

		if (elements.length == 0) {
			s = "";
		} else if (!isRelative()) {
			s = '/' + s;
		}

		return s;
	}

	public String asAbsolute() {
		return isRelative() ? toString() : '/' + toString();
	}

	/**
	 * Returns the common part between the two Paths (between <code>this</code>
	 * path and the <code>other</code> path).
	 *
	 * @param other the second path
	 *
	 * @return the common path
	 */
	public Path getCommonPath(Path other) {
		return commonPart(this, other);
	}

	/**
	 * Returns the element at the given index. There is no check for the index
	 * value, so an <code>ArrayIndexOutOfBoundsException</code> might be thrown.
	 *
	 * @param index the index for the searched element.
	 *
	 * @return element at the given <code>index</code>
	 */
	public String getElementAt(int index) {
		return elements[index];
	}

	/**
	 * Returns the number of elements of the current path. Example:
	 * <pre>
	 * new Path().getElementCount(); // returns 0
	 * new Path(&quot;home/user&quot;).getElementCount(); // returns 2
	 * new Path(&quot;../user&quot;).getElementCount(); // returns 2
	 *
	 * @return the number of elements the current path has.
	 */
	public int getElementCount() {
		return elements.length;
	}

	/**
	 * Returns a copy of the elements array.
	 */
	public String[] getElements() {
		String[] result = new String[elements.length];
		System.arraycopy(elements, 0, result, 0, elements.length);

		return result;
	}

	/**
	 * Returns the last element of the current path (usually the file name). For
	 * the root path the empty <code>String</code> is returned.
	 *
	 * @return the last element of the Path
	 */
	public String getLastElement() {
		return elements.length == 0 ? "" : elements[elements.length - 1];
	}

	/**
	 * Return the parent of the current path. The parent of the root path is '..'
	 * (a <code>Path</code> with one element whose value is "..").
	 *
	 * @return the parent of the current path.
	 */
	public Path getParent() {
		return new Path(toString(), "..");
	}

	/**
	 * Returns a parent of the current path, whose element count is equal to the
	 * passed value.
	 *
	 * @param count the count
	 *
	 * @return the parent of he current path
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

		String[] items = new String[count];

		for (int i = 0; i < count; i++) {
			items[i] = elements[i];
		}

		return new Path(items);
	}

	/**
	 * Returns the current path as relative to the given root. Example:
	 * <pre>
	 * Path myPath = new Path(&quot;home/user/myfile.txt&quot;);
	 * myPath.getRelativeTo(new Path(&quot;home&quot;)); // returns
	 * &quot;user/myfile.txt&quot;
	 * </pre>
	 *
	 * @param rootPath the root to relate this path to.
	 *
	 * @return the current path as relative to the given root.
	 */
	public Path getRelativeTo(Path rootPath) {
		int i0 = 0;
		int i1 = 0;
		List<String> list = new ArrayList<String>();

		while (i0 < rootPath.getElementCount()
					&& i1 < elements.length
					&& rootPath.getElementAt(i0).equals(elements[i1])) {
			i0++;
			i1++;
		}

		while (i0++ < rootPath.getElementCount()) {
			list.add("..");
		}

		while (i1 <= elements.length - 1) {
			list.add(elements[i1++]);
		}

		return new Path(list.toArray(new String[list.size()]));
	}

	/**
	 * Returns the hash code of the <code>String</code> that representes this
	 * path.
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * Checks if <code>this</code> path is a child of the <code>parent</code>
	 * path.
	 *
	 * @param parent the parent path
	 *
	 * @return <code>true</code> if the path current path is contained in the given
	 *         path directly. Example:
	 *         <pre>
	 *                 Path myPath = new Path(&quot;home/user/myfile.txt&quot;);
	 *                 myPath.isChildOf(new Path(&quot;nohome&quot;)); // returns
	 *         false
	 *                 myPath.isChildOf(new Path(&quot;home&quot;)); // returns
	 *         false
	 *                 myPath.isChildOf(new Path(&quot;home/user&quot;));
	 *         returns true
	 *                 </pre>
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
	 * Checkes if the current path is contained in another path.
	 *
	 * @param root the other path where to check if the current path is contained.
	 *
	 * @return <code>true</code> if the current path is contained in the given path
	 *         (at any depth). Example:
	 *         <pre>
	 *                 Path myPath = new Path(&quot;home/user/myfile.txt&quot;);
	 *                 myPath.isContainedIn(new Path(&quot;nohome&quot;));
	 *         returns
	 *                 false
	 *                 myPath.isContainedIn(new Path(&quot;home&quot;)); // returns
	 *         true
	 *                 myPath.isContainedIn(new Path(&quot;home/user&quot;));
	 *         returns
	 *                 true
	 *                 </pre>
	 */
	public boolean isContainedIn(Path root) {
		return root == null ? false : !getRelativeTo(root).isRelative();
	}

	/**
	 * Cheks if this path is relative (when the first element of this path is
	 * "..")
	 *
	 * @return <code>true</code> when the first element of the current path is
	 *         "..".
	 */
	public boolean isRelative() {
		return elements.length > 0 && "..".equals(elements[0]);
	}

	/**
	 * Returns true if this path is a ROOT path (when the path is empty)
	 *
	 * @return <code>true</code> if the path is empty.
	 */
	public boolean isRoot() {
		return elements.length == 0;
	}

	public Path replace(int index, String element) {
		int n = elements.length;

		if (index < 0 || index >= n) {
			throw new IllegalArgumentException("index out of range");
		}

		if (Strings.isNullOrEmpty(element)) {
			throw new IllegalArgumentException("element value is missing or empty");
		}

		String[] items = getElements();
		items[index] = element;

		return new Path(items);
	}

	public Path replaceLast(String newName) {
		return replace(elements.length - 1, newName);
	}

	/**
	 * Returns the successor of this <code>Path</code>, as defined in the Javadoc
	 * <code>of java.util.TreeMap.subMap(...)</code>. This is useful when you need
	 * to use that method to get a <em>closed range</em> submap (or headmap, or
	 * tailmap) of <code>Path</code>s.
	 *
	 * @return the successor path
	 */
	public Path successor() {
		return elements.length == 0 ? new Path("\u0000")
		: getParent().add(getLastElement() + '\u0000');
	}

	/**
	 * Returns the <code>String</code> representation of the current path. The
	 * separator between elements is always a slash, regardless of the platform.
	 */
	@Override
	public String toString() {
		if (toStringCache == null) {
			toStringCache = Strings.generateList(elements, "/");
		}

		return toStringCache;
	}

	public boolean contains(String element) {
		return Strings.searchString(elements, element, false) >= 0;
	}
}

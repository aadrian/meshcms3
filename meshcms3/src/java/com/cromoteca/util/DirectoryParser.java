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

package com.cromoteca.util;

import java.io.*;
import java.util.*;

/**
 * Abstract base class to perform operations on the contents of a directory.
 *
 * <p>Override the abstract method <code>processDirectory</code> and
 * <code>processFile</code> to define the actions to be taken for files and
 * directories included in the processed directory. You can also override
 * <code>preProcess</code> and <code>postProcess</code> to do additional
 * operations before and after the directory parsing.</p>
 *
 * <p>The directory to be parsed must be specified before starting by calling
 * on of the <code>setInitialDir</code> methods. Then you can start the parsing
 * by calling <code>process</code> or asinchronously by creating a new thread
 * and starting it.</p>
 *
 * <p>Please note that this class is <em>not</em> recursive by default. You must
 * call <code>setRecursive(true)</code> before processing if you want it to
 * process directory contents too.</p>
 *
 * @author Luciano Vernaschi
 */
public abstract class DirectoryParser extends Thread {
  protected File initialDir;

  protected boolean recursive = false;
  protected boolean processStartDir = false;
  protected boolean processDirBeforeContent = true;

  private Comparator comparator;

  /**
   * If true, directories will be processed recursively (default false).
   */
  public void setRecursive(boolean recursive) {
    this.recursive = recursive;
  }

  /**
   * If true, <code>processDirectory</code> will be called for the
   * base directory too (default false).
   *
   * @see #processDirectory
   */
  public void setProcessStartDir(boolean processStartDir) {
    this.processStartDir = processStartDir;
  }

  /**
   * If true, <code>processDirectory</code> will be called
   * for a directory before processing its contents (default true).
   *
   * @see #processDirectory
   */
  public void setProcessDirBeforeContent(boolean processDirBeforeContent) {
    this.processDirBeforeContent = processDirBeforeContent;
  }

  /**
   * Returns whether directories will be processed recursively or not.
   *
   * @see #setRecursive
   */
  public boolean isRecursive() {
    return recursive;
  }

  /**
   * Returns whether <code>processDirectory</code> will be called for the
   * base directory too.
   *
   * @see #processDirectory
   * @see #setProcessStartDir
   */
  public boolean isProcessStartDir() {
    return processStartDir;
  }

  /**
   * Returns whether <code>processDirectory</code> will be called
   * for a directory before processing its contents.
   *
   * @see #processDirectory
   * @see #setProcessDirBeforeContent
   */
  public boolean isProcessDirBeforeContent() {
    return processDirBeforeContent;
  }

  /**
   * If true, files and directories will be sorted used a
   * <code>FileNameComparator</code>.
   *
   * @see FileNameComparator
   */
  public void setSorted(boolean sorted) {
    if (sorted) {
      comparator = new FileNameComparator();
    } else {
      comparator = null;
    }
  }

  /**
   * Returns whether files and directories will be sorted used a
   * <code>FileNameComparator</code> or not.
   *
   * @see FileNameComparator
   * @see #setSorted
   */
  public boolean isSorted() {
    return comparator != null;
  }

  /**
   * Sets the directory to be processed. An istance of <code>File</code> is
   * created and {@link #setInitialDir(File)} is called.
   *
   * @param dir the file path as a <code>String</code>
   */
  public void setInitialDir(String dir) {
    setInitialDir(Utils.isNullOrEmpty(dir) ? null : new File(dir));
  }

  /**
   * Sets the directory to be processed.
   *
   * @param dir the directory path as a <code>File</code>
   */
  public void setInitialDir(File dir) {
    initialDir = dir;
  }

  /**
   * Returns the directory to be processed.
   */
  public File getInitialDir() {
    return initialDir;
  }

  /**
   * Starts processing (in a separate thread if instantiated properly).
   */
  public void run() {
    process();
  }

  /**
   * Starts processing.
   */
  public void process() {
    if (initialDir == null || !initialDir.exists()) {
      return;
    }

    if (preProcess()) {
      parse(initialDir, Path.ROOT);
    }

    postProcess();
  }

  private void parse(File file, Path path) {
    if (file.isDirectory()) {
      if (recursive || path.getElementCount() == 0) {
        boolean ok = true;

        if (processDirBeforeContent) {
          ok = processCurrentDir(file, path);
        }

        if (ok) {
          File[] list = file.listFiles();

          if (comparator != null) {
            Arrays.sort(list, comparator);
          }

          for (int i = 0; i < list.length; i++) {
            parse(list[i], path.add(list[i].getName()));
          }
        }
      }

      if (!processDirBeforeContent) {
        processCurrentDir(file, path);
      }
    } else if (file.isFile()) {
      processFile(file, path);
    }
  }

  private boolean processCurrentDir(File file, Path path) {
    return (processStartDir || path.getElementCount() != 0) ?
      processDirectory(file, path) : true;
  }

  /**
   * This method is called during the process, but before any element has been
   * processed. If it returns false, no processing will take place.
   *
   * <p>The base implementation does nothing and returns true.</p>
   */
  protected boolean preProcess() {
    return true;
  }

  /**
   * This method is called at the end of the processing. It is called even if
   * {@link #preProcess} returned false.
   *
   * <p>The base implementation does nothing.</p>
   */
  protected void postProcess() {
  }

  /**
   * This method will be called for any directory found while parsing the base
   * directory. You can return false to block processing the contents of the
   * directory (provided that {@link #isProcessDirBeforeContent} returns true,
   * otherwise contents have been processed already).
   *
   * @param file the directory to be processed
   * @param path the path of the directory (relative to the base directory)
   */
  protected abstract boolean processDirectory(File file, Path path);

  /**
   * This method will be called for any file found while parsing the base
   * directory.
   *
   * @param file the file to be processed
   * @param path the path of the file (relative to the base directory)
   */
  protected abstract void processFile(File file, Path path);
}

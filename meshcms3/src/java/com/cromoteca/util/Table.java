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
 * Creates an in-memory representation of a data structure similar to a table.
 * It has n named colums and m rows of data contained in an array of arrays.
 */
public class Table implements Serializable {
  protected String[] columnNames;
  protected String[][] data;

  /**
   * Creates a new Table fetching the data from a file using the tabulation
   * character as separator.
   */
  public Table(String tableFileName) throws FileNotFoundException, IOException {
    this(new File(tableFileName), "\t");
  }

  /**
   * Creates a new Table fetching the data from a file using the given separator.
   */
  public Table(String tableFileName, String sep) throws FileNotFoundException,
      IOException {
    this(new File(tableFileName), sep);
  }

  /**
   * Creates a new Table fetching the data from a file using the tabulation
   * character as separator.
   */
  public Table(File tableFile) throws FileNotFoundException, IOException {
    this(tableFile, "\t");
  }

  /**
   * Creates a new Table fetching the data from a file using the given separator.
   * The file must contain a line with the column headers, then a line for each
   * data row.
   */
  public Table(File tableFile, String sep) throws FileNotFoundException,
      IOException {
    if (!tableFile.isFile()) {
      throw new FileNotFoundException(Utils.getFilePath(tableFile));
    } else {
      BufferedReader br = new BufferedReader(new FileReader(tableFile));
      String line = br.readLine();

      if (line.length() == 1) {
        sep = line;
        line = br.readLine();
      }

      columnNames = Utils.tokenize(line, sep);
      List lines = new ArrayList();

      while ((line = br.readLine()) != null) {
        String[] lineData = Utils.tokenize(line, sep);

        if (lineData != null && lineData.length == columnNames.length) {
          lines.add(lineData);
        }
      }

      br.close();
      data = new String[lines.size()][];

      for (int i = 0; i < data.length; i++) {
        data[i] = (String[]) lines.get(i);
      }
    }
  }

  protected Table(Table parent) {
    columnNames = parent.columnNames;
  }

  /**
   * Returns an array of values for the <code>keyName</code> column, picking
   * them from rows where the <code>keyValue</code> column has value
   * <code>valueName</code>.
   */
  public String[] getValues(String keyName, String keyValue, String valueName) {
    int keyIndex = getColumnIndex(keyName);
    int valueIndex = getColumnIndex(valueName);

    if (keyIndex != -1 && valueIndex != -1) {
      List values = new ArrayList();

      for (int i = 0; i < data.length; i++) {
        if (data[i][keyIndex].equalsIgnoreCase(keyValue)) {
          values.add(data[i][valueIndex]);
        }
      }

      if (values.size() > 0) {
        String[] results = new String[values.size()];

        for (int i = 0; i < results.length; i++) {
          results[i] = (String) values.get(i);
        }

        return results;
      }
    }

    return null;
  }

  /**
   * Returns the the value for the <code>keyName</code> column, picking
   * it from the first row where the <code>keyValue</code> column has value
   * <code>valueName</code>.
   */
  public String getValue(String keyName, String keyValue, String valueName) {
    String[] values = getValues(keyName, keyValue, valueName);

    if (values == null || values.length == 0) {
      return null;
    }

    return values[0];
  }

  /**
   * Returns the value for the <code>keyName</code> column, as in
   * {@link #getValue(String, String, String)}, but here <code>keyValue</code>
   * is returned if no value has been found.
   */
  public String getValueOrDefault(String keyName, String keyValue,
      String valueName) {
    return Utils.noNull(getValue(keyName, keyValue, valueName), keyValue);
  }

  /**
   * Returns the value for the <code>keyName</code> column, interpreted as a
   * boolean.
   */
  public boolean getBoolean(String keyName, String keyValue, String valueName) {
    return Utils.isTrue(getValue(keyName, keyValue, valueName));
  }

  /**
   * Finds the first row where <code>keyName</code> has value
   * <code>keyValue</code>, then returns all values of that row as a
   * java.util.Properties instance, where the key names are the column names
   * from the table itself.
   */
  public Properties getProperties(String keyName, String keyValue) {
    int keyIndex = getColumnIndex(keyName);

    if (keyIndex != -1) {
      for (int i = 0; i < data.length; i++) {
        if (data[i][keyIndex].equalsIgnoreCase(keyValue)) {
          Properties props = new Properties();

          for (int j = 0; j < columnNames.length; j++) {
            props.put(columnNames[j], data[i][j]);
          }

          return props;
        }
      }
    }

    return null;
  }

  /**
   * Returns a new Table containing only the rows where <code>keyName</code> has
   * value <code>keyValue</code>.
   */
  public Table select(String keyName, String keyValue) {
    int keyIndex = getColumnIndex(keyName);

    if (keyIndex != -1) {
      Table result = new Table(this);
      List lines = new ArrayList();

      for (int i = 0; i < data.length; i++) {
        if (data[i][keyIndex].equalsIgnoreCase(keyValue)) {
          lines.add(data[i]);
        }
      }

      if (lines.size() > 0) {
        result.data = new String[lines.size()][];

        for (int i = 0; i < result.data.length; i++) {
          result.data[i] = (String[]) lines.get(i);
        }

        return result;
      }
    }

    return null;
  }

  /**
   * Returns true if there is at least one row where <code>keyName</code> has
   * value <code>keyValue</code>.
   */
  public boolean exists(String keyName, String keyValue) {
    int keyIndex = getColumnIndex(keyName);

    if (keyIndex != -1) {
      for (int i = 0; i < data.length; i++) {
        if (data[i][keyIndex].equalsIgnoreCase(keyValue)) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Returns the number of columns.
   */
  public int getColumnCount() {
    return columnNames.length;
  }

  /**
   * Returns the number of rows.
   */
  public int getRowCount() {
    try {
      return data.length;
    } catch (Exception ex) {}

    return 0;
  }

  /**
   * Returns all the values in the <code>keyName</code> column (duplicate values
   * are removed).
   */
  public String[] getValues(String keyName) {
    int keyIndex = getColumnIndex(keyName);
    List values = new ArrayList();

    if (keyIndex != -1) {
      for (int i = 0; i < data.length; i++) {
        if (!values.contains(data[i][keyIndex])) {
          values.add(data[i][keyIndex]);
        }
      }

      if (values.size() > 0) {
        String[] results = new String[values.size()];

        for (int i = 0; i < results.length; i++) {
          results[i] = (String) values.get(i);
        }

        return results;
      }
    }

    return null;
  }

  /**
   * Returns the names of the columns.
   */
  public String[] getColumnNames() {
    return columnNames;
  }

  /**
   * Dumps the table to a String using the tabulation character as separator.
   */
  public String dump() {
    String sep = "\t";

    String all = Utils.generateList(columnNames, sep) + '\n';

    for (int i = 0; i < data.length; i++) {
      all += Utils.generateList(data[i], sep) + '\n';
    }

    return all;
  }

  protected int getColumnIndex(String columnName) {
    for (int i = 0; i < columnNames.length; i++) {
      if (columnNames[i].equalsIgnoreCase(columnName)) {
        return i;
      }
    }

    return -1;
  }

  /**
   * Returns a cursor for the table.
   */
  public TableCursor getCursor() {
    return new TableCursor(this);
  }

  /**
   * Returns the value of <code>keyName</code> at the given cursor position.
   */
  public String getValueAt(TableCursor cursor, String keyName) {
    int row = cursor.getValue();

    if (row < 0 || getRowCount() < 1 || row >= getRowCount()) {
      throw new IllegalArgumentException("Invalid cursor");
    }

    return data[row][getColumnIndex(keyName)];
  }
}

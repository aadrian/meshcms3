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

package org.meshcms.core;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Writes the page to both the browser and the cache.
 *
 * @see CacheResponseWrapper
 */
public class CacheResponseStream extends ServletOutputStream {
  ServletOutputStream output;
  OutputStream cacheOutput;

  /**
   * Creates a new Stream to write to the original output stream of the response
   * and to the passed output stream.
   *
   * @param response the original response
   * @param cacheOutput the output for the cache
   */
  public CacheResponseStream(HttpServletResponse response,
      OutputStream cacheOutput) throws IOException {
    super();
    this.cacheOutput = cacheOutput;
    output = response.getOutputStream();
  }
  
  /**
   * Writes to both streams.
   */
  public void write(int b) throws IOException {
    output.write(b);
    
    try {
      cacheOutput.write(b);
    } catch (IOException ex) {}
  }

  /**
   * Flushes both streams.
   */
  public void flush() throws IOException {
    output.flush();
    
    try {
      cacheOutput.flush();
    } catch (IOException ex) {}
  }

  /**
   * Closes both streams.
   */
  public void close() throws IOException {
    output.close();
    
    try {
      cacheOutput.close();
    } catch (IOException ex) {}
  }
}

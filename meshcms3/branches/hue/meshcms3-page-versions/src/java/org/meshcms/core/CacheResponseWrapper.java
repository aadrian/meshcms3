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
 * Modifies the response to write the page to both the browser and the cache.
 * This class is a slightly modified version of the GZIPResponseWrapper found
 * in <a href="http://www.onjava.com/pub/a/onjava/2003/11/19/filters.html">this
 * article</a>.
 *
 * @see CacheResponseStream
 */
public class CacheResponseWrapper extends HttpServletResponseWrapper {
  HttpServletResponse response;
  ServletOutputStream stream;
  PrintWriter writer;
  OutputStream cacheOutput;
  String charset;
  
  /**
   * Creates a new wrapper.
   *
   * @param response the original response
   * @param cacheOutput the output stream to write the cached page to
   */
  public CacheResponseWrapper(HttpServletResponse response,
      OutputStream cacheOutput, String charset) {
    super(response);
    this.response = response;
    this.cacheOutput = cacheOutput;
    this.charset = charset;
    //response.setHeader("Content-Encoding", "gzip");
  }

  /**
   * Creates the output stream.
   *
   * @see CacheResponseStream
   */
  public ServletOutputStream createOutputStream() throws IOException {
    return new CacheResponseStream(response, cacheOutput);
  }
  
  /**
   * Closes the stream.
   */
  public void finishResponse() {
    try {
      if (writer != null) {
        writer.close();
      } else if (stream != null) {
        stream.close();
      }
    } catch (IOException e) {}
  }

  public void flushBuffer() throws IOException {
    stream.flush();
  }

  public ServletOutputStream getOutputStream() throws IOException {
    if (writer != null) {
      throw new IllegalStateException("getWriter() has already been called!");
    }
    
    if (stream == null) {
      stream = createOutputStream();
    }
    
    return stream;
  }
  
  public PrintWriter getWriter() throws IOException {
    if (writer != null) {
      return writer;
    }
    
    if (stream != null) {
      throw new IllegalStateException("getOutputStream() has already been called!");
    }
    
    stream = createOutputStream();
    writer = new PrintWriter(new OutputStreamWriter(stream,
        charset == null ? response.getCharacterEncoding() : charset));
    return writer;
  }
}

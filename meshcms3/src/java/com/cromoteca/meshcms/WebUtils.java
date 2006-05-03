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

package com.cromoteca.meshcms;

import java.io.*;
import java.nio.charset.*;
import java.net.*;
import java.text.*;
import java.util.*;
import javax.activation.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import com.cromoteca.meshcms.taglib.*;
import com.cromoteca.util.*;

/**
 * A collection of utilities related to a web application.
 */
public final class WebUtils implements Finals {
  public static SimpleDateFormat numericDateFormatter =
      new SimpleDateFormat("yyyyMMddHHmmss");

  /**
   * Characters allowed in a file name.
   */
  public static final String FN_CHARS =
      "'()-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz";
  
  /**
   * Mapping of the ISO-8859-1 characters to characters included in FN_CHARS.
   */
  public static final String FN_CHARMAP =
      "________________________________" +
      "__'____'()..--._0123456789..(-)." +
      "_ABCDEFGHIJKLMNOPQRSTUVWXYZ(_)__" +
      "'abcdefghijklmnopqrstuvwxyz(_)-_" +
      "________________________________" +
      "__cL.Y_P_Ca(__R-o-23'mP._10)423_" +
      "AAAAAAACEEEEIIIIENOOOOOxOUUUUYTS" +
      "aaaaaaaceeeeiiiienooooo-ouuuuyty";

  /**
   * Characters that are considered spacers in a file name.
   */
  public static final String FN_SPACERS = "_!'()-";
  
  /**
   * The default array of welcome file names. This array is used if the welcome
   * file names can't be found in the web.xml configuration file.
   */
  public static final String[] DEFAULT_WELCOME_FILES =
    {"index.html", "index.htm", "index.jsp"};
    
  /**
   * Parses the web.xml configuration file and returns an array of welcome file
   * names. If the names can't be found, {@link #DEFAULT_WELCOME_FILES} is
   * returned.
   */
  public static String[] getWelcomeFiles(ServletContext sc) {
    File webXml = new File(sc.getRealPath("/WEB-INF/web.xml"));
    String[] welcomeFiles = null;

    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder parser = factory.newDocumentBuilder();
      Document document = parser.parse(webXml);
      Element root = document.getDocumentElement();
      Element wfl = (Element)
        (root.getElementsByTagName("welcome-file-list").item(0));
      NodeList wfnl = wfl.getElementsByTagName("welcome-file");
      welcomeFiles = new String[wfnl.getLength()];

      for (int i = 0; i < welcomeFiles.length; i++) {
        welcomeFiles[i] = wfnl.item(i).getFirstChild().getNodeValue();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    if (welcomeFiles == null || welcomeFiles.length == 0) {
      welcomeFiles = DEFAULT_WELCOME_FILES;
    }

    return welcomeFiles;
  }

  /**
   * Returns a request parameter in the form <code>&name=value</code>. Returns
   * null if the value has not been found.
   */
  public static String getFullParameter(HttpServletRequest request, String name) {
    String value = request.getParameter(name);
    return (value == null) ? "" : "&" + name + "=" + value;
  }

  /**
   * Returns the full context path of the given path.
   */
  public static String getPathInContext(HttpServletRequest request, Path path) {
    return request.getContextPath() + "/" + path;
  }

  /**
   * Reconstructs the full URL of the request.
   */
  public static String getFullRequestURL(HttpServletRequest request) {
    StringBuffer sb = request.getRequestURL();
    String qs = request.getQueryString();

    if (!Utils.isNullOrEmpty(qs)) {
      sb.append('?').append(qs);
    }

    return sb.toString();
  }

  /**
   * Reconstructs the full URL of the context home. The URL is returned as a
   * StringBuffer so other elements can be added easily.
   */
  public static StringBuffer getContextHomeURL(HttpServletRequest request) {
    StringBuffer sb = new StringBuffer();
    sb.append(request.getScheme()).append("://");
    sb.append(request.getServerName());
    int port = request.getServerPort();

    if (port != 80) {
      sb.append(':').append(port);
    }

    return sb.append(request.getContextPath()).append('/');
  }
  
  /**
   * Returns the complete path of the folder of current theme (context
   * path included).
   *
   * @param request used to get the theme name
   */
  public static String getFullThemeFolder(HttpServletRequest request) {
    Path themePath = (Path) request.getAttribute(THEME_PATH_ATTRIBUTE);

    return (themePath == null) ? "" :
        request.getContextPath() + "/" + themePath;
  }
  
  /**
   * Returns the complete path of the <code>main.jsp</code> file (context
   * path included).
   *
   * @param request used to get the theme name
   */
  public static String getFullThemeFile(HttpServletRequest request) {
    return getFullThemeFolder(request) + "/" + THEME_DECORATOR;
  }
  
  /**
   * Returns the complete path of the <code>main.css</code> file (context
   * path included).
   *
   * @param request used to get the theme name
   */
  public static String getFullThemeCSS(HttpServletRequest request) {
    return getFullThemeFolder(request) + "/" + THEME_CSS;
  }
  
  /**
   * Returns the complete path of the <code>meshcms.css</code> file (context
   * path included). If <code>meshcms.css</code> is not found in the theme
   * folder, the default from the admin theme is returned.
   *
   * @param request used to get the theme name
   */
  public static String getFullMeshCSS(WebApp webApp, HttpServletRequest request) {
    Path themePath = (Path) request.getAttribute(THEME_PATH_ATTRIBUTE);
    Path cssPath = themePath.add(MESHCMS_CSS);
    
    if (!webApp.getFile(cssPath).exists()) {
      cssPath = webApp.getAdminPath().add(ADMIN_THEME, MESHCMS_CSS);
    }
    
    return request.getContextPath() + "/" + cssPath;
  }
  
  /**
   * Returns a numeric code for an object. This code is used in the menu, but
   * can be used elsewhere if needed. It is equal to the hash code of the
   * object, but never negative, since the minus sign creates problems when
   * converted to a string and used as part of a variable name in JavaScript.
   */
  public static int getMenuCode(Object o) {
    return o.hashCode() & 0x7FFFFFFF;
  }
  
  /**
   * Returns the folder path to be used as argument for the module. This method
   * must be called from module JSP files only. It makes no sense in
   * other cases.
   *
   * @param allowCurrentPath if true and the argument parameter is null, the
   * path of the page is returned
   */
  public static Path getModuleArgumentDirectoryPath(WebApp webApp,
      HttpServletRequest request, boolean allowCurrentPath) {
    String arg = request.getParameter("argument");
    Path argPath = null;
    
    if (arg != null) {
      argPath = new Path(arg);
    } else if (allowCurrentPath) {
      argPath = new Path(request.getParameter("pagepath"));
    }
    
    if (argPath != null && !webApp.isSystem(argPath, true)) {
      return webApp.getDirectory(argPath);
    }
    
    return null;
  }
  
  /**
   * Returns the files to be passed to the module. This method
   * must be called from module JSP files only. It makes no sense in
   * other cases.
   *
   * @param allowCurrentDir if true and the argument parameter is null, the
   * files included in the same folder of the page are returned
   */
  public static File[] getModuleFiles(WebApp webApp, HttpServletRequest request,
      boolean allowCurrentDir) {
    String arg = request.getParameter("argument");
    Path argPath = null;
    
    if (arg != null) {
      argPath = new Path(arg);
    } else if (allowCurrentDir) {
      argPath = webApp.getDirectory(new Path(request.getParameter("pagepath")));
    }
    
    if (argPath != null && !webApp.isSystem(argPath, true)) {
      File moduleFile = webApp.getFile(argPath);
      File[] files = null;

      if (moduleFile.isDirectory()) {
        files = moduleFile.listFiles();
      } else if (moduleFile.exists()) {
        files = new File[1];
        files[0] = moduleFile;
      }

      return files;
    }
    
    return null;
  }
  
  /**
   * Returns format to be used to display the date (can be null). This method
   * must be called from module JSP files only. It makes no sense in
   * other cases.
   */
  public static DateFormat getModuleDateFormat(PageContext pageContext) {
    Locale locale = getPageLocale(pageContext);
    DateFormat df = null;
    String dateFormatParam =
        Utils.noNull(pageContext.getRequest().getParameter("dateformat"));

    if (dateFormatParam.equals(Module.DATE_NORMAL)) {
      df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
    } else if (dateFormatParam.equals(Module.DATE_FULL)) {
      df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT,
          locale);
    }
    
    return df;
  }
  
  /**
   * Returns the 2nd level domain from which the request comes.
   */
  public static String get2ndLevelDomain(HttpServletRequest request) {
    return get2ndLevelDomain(request.getRequestURL().toString());
  }
  
  /**
   * Returns the 2nd level domain from the URL.
   */
  public static String get2ndLevelDomain(String urlString) {
    try {
      URL url = new URL(urlString);
      String host = url.getHost();
      int dot = host.lastIndexOf('.');
      
      if (dot != -1) {
        String partial = host.substring(0, dot);
        dot = partial.lastIndexOf('.');
        
        if (dot != -1) {
          host = host.substring(dot + 1);
        }
        
        return host;
      }
    } catch (Exception ex) {}
    
    return null;
  }
  
  /**
   * This method must be called to avoid the current page to be cached. For
   * example, some modules will call this method to be sure that they
   * are parsed again when the page is called another time.
   */
  public static void setBlockCache(HttpServletRequest request) {
    request.setAttribute(BLOCK_CACHE_ATTR, BLOCK_CACHE_ATTR);
  }

  /**
   * Checks the current Request scope to see if some class has called the
   * {@link #setBlockCache} method.
   */
  public static boolean isCacheBlocked(HttpServletRequest request) {
    return request.getAttribute(BLOCK_CACHE_ATTR) != null;
  }
  
  /**
   * Tries to locate a Locale stored in the Page scope or in the Request scope.
   * If none is found, returns the Locale of the request, or at least the
   * system default Locale.
   */
  public static Locale getPageLocale(PageContext pageContext) {
    Locale locale = null;

    UserInfo userInfo = (UserInfo) pageContext.getAttribute("userInfo",
      PageContext.SESSION_SCOPE);
    
    if (userInfo != null) {
      locale = Utils.getLocale(userInfo.getPreferredLocaleCode());
    }
    
    if (locale == null) {
      locale = (Locale) pageContext.getAttribute(LOCALE_ATTRIBUTE, PageContext.PAGE_SCOPE);
    }

    Enumeration en = pageContext.getAttributeNamesInScope(PageContext.PAGE_SCOPE);

    while (locale == null && en.hasMoreElements()) {
      Object obj = pageContext.getAttribute((String) en.nextElement());

      if (obj instanceof Locale) {
        locale = (Locale) obj;
      }
    }
    
    if (locale == null) {
      locale = (Locale) pageContext.getAttribute(LOCALE_ATTRIBUTE, PageContext.REQUEST_SCOPE);
    }

    en = pageContext.getAttributeNamesInScope(PageContext.REQUEST_SCOPE);

    while (locale == null && en.hasMoreElements()) {
      Object obj = pageContext.getAttribute((String) en.nextElement(),
          PageContext.REQUEST_SCOPE);

      if (obj instanceof Locale) {
        locale = (Locale) obj;
      }
    }
    
    if (locale == null) {
      locale = pageContext.getRequest().getLocale();
    }
    
    if (locale == null) {
      locale = Locale.getDefault();
    }
    
    return locale;
  }
  
  /**
   * Tries to locate a ResourceBundle stored in the Page scope or in the Request
   * scope using the JSTL.
   *
   * @return the ResourceBundle, or null if not found
   */
  public static ResourceBundle getPageResourceBundle(PageContext pageContext) {
    Enumeration en = pageContext.getAttributeNamesInScope(PageContext.PAGE_SCOPE);

    while (en.hasMoreElements()) {
      Object obj = pageContext.getAttribute((String) en.nextElement());
      
      if (obj instanceof ResourceBundle) {
        return (ResourceBundle) obj;
      } else if (obj instanceof javax.servlet.jsp.jstl.fmt.LocalizationContext) {
        return ((javax.servlet.jsp.jstl.fmt.LocalizationContext) obj).getResourceBundle();
      }
    }
    
    en = pageContext.getAttributeNamesInScope(PageContext.REQUEST_SCOPE);

    while (en.hasMoreElements()) {
      Object obj = pageContext.getAttribute((String) en.nextElement(),
          PageContext.REQUEST_SCOPE);

      if (obj instanceof ResourceBundle) {
        return (ResourceBundle) obj;
      } else if (obj instanceof javax.servlet.jsp.jstl.fmt.LocalizationContext) {
        return ((javax.servlet.jsp.jstl.fmt.LocalizationContext) obj).getResourceBundle();
      }
    }
    
    return null;
  }

  /**
   * Returns a modified name that does not contain characters not recommended
   * in a file name.
   */
  public static String fixFileName(String name) {
    name = name.trim();
    StringBuffer sb = new StringBuffer(name.length());
    boolean addSpacer = false;
    char spacer = FN_SPACERS.charAt(0);
    char c;

    for (int i = 0; i < name.length(); i++) {
      c = name.charAt(i);
      
      if (c < 256) {
        c = FN_CHARMAP.charAt(c);
      } else {
        c = spacer;
      }
      
      if (FN_SPACERS.indexOf(c) < 0) { // not a spacer
        if (addSpacer) { // needs to add a spacer due to previous characters
          if (sb.length() > 0) { // add a spacer only if not as first char
            sb.append(spacer);
          }
          
          addSpacer = false;
        }
        
        sb.append(c);
      } else {
        addSpacer = true; // it's a spacer, will be added next if needed
      }
    }

    return sb.toString();
  }

  /**
   * Returns a nicer representation of the number as a file length. The number
   * is returned as bytes, kilobytes or megabytes, with the unit attached.
   */
  public static String formatFileLength(long length, Locale locale,
      ResourceBundle bundle) {
    NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
    DecimalFormat format = (DecimalFormat) numberFormat;
    format.applyPattern("###0.##");
    double num = length;
    String unit;

    if (length < Utils.KBYTE) {
      unit = "genericUnitBytes";
    } else if (length < Utils.MBYTE) {
      num /= Utils.KBYTE;
      unit = "genericUnitKilobytes";
    } else if (length < Utils.GBYTE) {
      num /= Utils.MBYTE;
      unit = "genericUnitMegabytes";
    } else {
      num /= Utils.GBYTE;
      unit = "genericUnitGigabytes";
    }

    return format.format(num) + bundle.getString(unit);
  }
  
  public static String getCharsetCanonicalName(String charsetName) {
    Charset suggestedCharset = null;
    
    try {
      suggestedCharset = Charset.forName(charsetName);
      charsetName = suggestedCharset.name();
    } catch (Exception ex) {}
    
    return charsetName;
  }
  
  public static String parseCharset(String fullValue) {
    try {
      return new MimeType(fullValue).getParameter("charset");
    } catch (MimeTypeParseException ex) {
      return null;
    }
  }
  
  public static void updateLastModifiedTime(HttpServletRequest request, File file) {
    updateLastModifiedTime(request, file.lastModified());
  }
  
  public static void updateLastModifiedTime(HttpServletRequest request, long time) {
    if (time > getLastModifiedTime(request)) {
      request.setAttribute(LAST_MODIFIED_ATTRIBUTE, new Long(time));
    }
  }
  
  public static long getLastModifiedTime(HttpServletRequest request) {
    long time = 0L;
    
    try {
      time = ((Long) request.getAttribute(LAST_MODIFIED_ATTRIBUTE)).longValue();
    } catch (Exception ex) {}
    
    return time;
  }
}

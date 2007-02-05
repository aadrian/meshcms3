/*
 * MeshCMS - A simple CMS based on SiteMesh
 * Copyright (C) 2004-2007 Luciano Vernaschi
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
import java.nio.charset.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import javax.activation.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.meshcms.util.*;

/**
 * A collection of utilities related to a web application.
 */
public final class WebUtils {
  public static final SimpleDateFormat numericDateFormatter =
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

  public static Properties NUMBER_TO_ENTITY;
  public static Properties ENTITY_TO_NUMBER;

  static {
    String[] entities = {
      "39", "#39", "160", "nbsp", "161", "iexcl", "162", "cent", "163", "pound",
      "164", "curren", "165", "yen", "166", "brvbar", "167", "sect",
      "168", "uml", "169", "copy", "170", "ordf", "171", "laquo", "172", "not",
      "173", "shy", "174", "reg", "175", "macr", "176", "deg", "177", "plusmn",
      "178", "sup2", "179", "sup3", "180", "acute", "181", "micro",
      "182", "para", "183", "middot", "184", "cedil", "185", "sup1",
      "186", "ordm", "187", "raquo", "188", "frac14", "189", "frac12",
      "190", "frac34", "191", "iquest", "192", "Agrave", "193", "Aacute",
      "194", "Acirc", "195", "Atilde", "196", "Auml", "197", "Aring",
      "198", "AElig", "199", "Ccedil", "200", "Egrave", "201", "Eacute",
      "202", "Ecirc", "203", "Euml", "204", "Igrave", "205", "Iacute",
      "206", "Icirc", "207", "Iuml", "208", "ETH", "209", "Ntilde",
      "210", "Ograve", "211", "Oacute", "212", "Ocirc", "213", "Otilde",
      "214", "Ouml", "215", "times", "216", "Oslash", "217", "Ugrave",
      "218", "Uacute", "219", "Ucirc", "220", "Uuml", "221", "Yacute",
      "222", "THORN", "223", "szlig", "224", "agrave", "225", "aacute",
      "226", "acirc", "227", "atilde", "228", "auml", "229", "aring",
      "230", "aelig", "231", "ccedil", "232", "egrave", "233", "eacute",
      "234", "ecirc", "235", "euml", "236", "igrave", "237", "iacute",
      "238", "icirc", "239", "iuml", "240", "eth", "241", "ntilde",
      "242", "ograve", "243", "oacute", "244", "ocirc", "245", "otilde",
      "246", "ouml", "247", "divide", "248", "oslash", "249", "ugrave",
      "250", "uacute", "251", "ucirc", "252", "uuml", "253", "yacute",
      "254", "thorn", "255", "yuml", "402", "fnof", "913", "Alpha",
      "914", "Beta", "915", "Gamma", "916", "Delta", "917", "Epsilon",
      "918", "Zeta", "919", "Eta", "920", "Theta", "921", "Iota",
      "922", "Kappa", "923", "Lambda", "924", "Mu", "925", "Nu", "926", "Xi",
      "927", "Omicron", "928", "Pi", "929", "Rho", "931", "Sigma", "932", "Tau",
      "933", "Upsilon", "934", "Phi", "935", "Chi", "936", "Psi",
      "937", "Omega", "945", "alpha", "946", "beta", "947", "gamma",
      "948", "delta", "949", "epsilon", "950", "zeta", "951", "eta",
      "952", "theta", "953", "iota", "954", "kappa", "955", "lambda",
      "956", "mu", "957", "nu", "958", "xi", "959", "omicron", "960", "pi",
      "961", "rho", "962", "sigmaf", "963", "sigma", "964", "tau",
      "965", "upsilon", "966", "phi", "967", "chi", "968", "psi",
      "969", "omega", "977", "thetasym", "978", "upsih", "982", "piv",
      "8226", "bull", "8230", "hellip", "8242", "prime", "8243", "Prime",
      "8254", "oline", "8260", "frasl", "8472", "weierp", "8465", "image",
      "8476", "real", "8482", "trade", "8501", "alefsym", "8592", "larr",
      "8593", "uarr", "8594", "rarr", "8595", "darr", "8596", "harr",
      "8629", "crarr", "8656", "lArr", "8657", "uArr", "8658", "rArr",
      "8659", "dArr", "8660", "hArr", "8704", "forall", "8706", "part",
      "8707", "exist", "8709", "empty", "8711", "nabla", "8712", "isin",
      "8713", "notin", "8715", "ni", "8719", "prod", "8721", "sum",
      "8722", "minus", "8727", "lowast", "8730", "radic", "8733", "prop",
      "8734", "infin", "8736", "ang", "8743", "and", "8744", "or",
      "8745", "cap", "8746", "cup", "8747", "int", "8756", "there4",
      "8764", "sim", "8773", "cong", "8776", "asymp", "8800", "ne",
      "8801", "equiv", "8804", "le", "8805", "ge", "8834", "sub", "8835", "sup",
      "8836", "nsub", "8838", "sube", "8839", "supe", "8853", "oplus",
      "8855", "otimes", "8869", "perp", "8901", "sdot", "8968", "lceil",
      "8969", "rceil", "8970", "lfloor", "8971", "rfloor", "9001", "lang",
      "9002", "rang", "9674", "loz", "9824", "spades", "9827", "clubs",
      "9829", "hearts", "9830", "diams", "34", "quot", "38", "amp", "60", "lt",
      "62", "gt", "338", "OElig", "339", "oelig", "352", "Scaron",
      "353", "scaron", "376", "Yuml", "710", "circ", "732", "tilde",
      "8194", "ensp", "8195", "emsp", "8201", "thinsp", "8204", "zwnj",
      "8205", "zwj", "8206", "lrm", "8207", "rlm", "8211", "ndash",
      "8212", "mdash", "8216", "lsquo", "8217", "rsquo", "8218", "sbquo",
      "8220", "ldquo", "8221", "rdquo", "8222", "bdquo", "8224", "dagger",
      "8225", "Dagger", "8240", "permil", "8249", "lsaquo", "8250", "rsaquo",
      "8364", "euro"
    };

    NUMBER_TO_ENTITY = new Properties();
    ENTITY_TO_NUMBER = new Properties();

    for (int i = 0; i < entities.length; i += 2) {
      NUMBER_TO_ENTITY.setProperty(entities[i], entities[i + 1]);
      ENTITY_TO_NUMBER.setProperty(entities[i + 1], entities[i]);
    }
  }

  public static String convertToHTMLEntities(String s, boolean encodeTags) {
    StringBuffer sb = new StringBuffer(s.length());

    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      int n = ((int) c) & 0xFFFF;

      if (n > 127) {
        String ent = NUMBER_TO_ENTITY.getProperty(Integer.toString(c));

        if (ent == null) {
          sb.append("&#").append(n).append(';');
        } else {
          sb.append('&').append(ent).append(';');
        }
      } else if (encodeTags && (n == 34 || /* n == 38 || */ n == 39 || n == 60 || n == 62)) {
        sb.append('&').append(NUMBER_TO_ENTITY.getProperty(Integer.toString(c))).append(';');
      } else {
        sb.append(c);
      }
    }

    return sb.toString();
  }

  /**
   * Parses the web.xml configuration file and returns an array of welcome file
   * names. If the names can't be found, {@link #DEFAULT_WELCOME_FILES} is
   * returned.
   *
   * @param sc ServletContext required to access the <code>web.xml</code> file
   * @return an array of welcome file names
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
   * @return a request parameter in the form <code>&name=value</code>. Returns
   * null if the value has not been found.
   */
  public static String getFullParameter(HttpServletRequest request, String name) {
    String value = request.getParameter(name);
    return (value == null) ? "" : "&" + name + "=" + value;
  }

  /**
   * @return the full context path of the given path.
   */
  public static String getPathInContext(HttpServletRequest request, Path path) {
    return request.getContextPath() + "/" + path;
  }

  /**
   * Reconstructs the full URL of the request.
   *
   * @deprecated with no replacement since it uses request.getRequestURL()
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
    String scheme = request.getScheme();
    sb.append(scheme).append("://");
    sb.append(request.getServerName());
    int port = request.getServerPort();

    if (!(("http".equals(scheme) && port == 80) ||
          ("https".equals(scheme) && port == 443))) {
      sb.append(':').append(port);
    }

    return sb.append(request.getContextPath());
  }

  /**
   * @return the complete path of the folder of current theme (context
   * path included).
   *
   * @param request used to get the theme name
   */
  public static String getFullThemeFolder(HttpServletRequest request) {
    Path themePath = (Path) request.getAttribute(HitFilter.THEME_PATH_ATTRIBUTE);

    return (themePath == null) ? "" :
        request.getContextPath() + "/" + themePath;
  }

  /**
   * @return the complete path of the <code>main.jsp</code> file (context
   * path included).
   *
   * @param request used to get the theme name
   */
  public static String getFullThemeFile(HttpServletRequest request) {
    return getFullThemeFolder(request) + "/" + SiteMap.THEME_DECORATOR;
  }

  /**
   * @return the complete path of the <code>main.css</code> file (context
   * path included).
   *
   * @param request used to get the theme name
   */
  public static String getFullThemeCSS(HttpServletRequest request) {
    return getFullThemeFolder(request) + "/" + SiteMap.THEME_CSS;
  }

  /**
   * @return the complete path of the <code>meshcms.css</code> file (context
   * path included). If <code>meshcms.css</code> is not found in the theme
   * folder, the default from the admin theme is returned.
   *
   * @param request used to get the theme name
   */
  public static String getFullMeshCSS(WebSite webSite, HttpServletRequest request) {
    Path themePath = (Path) request.getAttribute(HitFilter.THEME_PATH_ATTRIBUTE);
    Path cssPath = themePath.add(SiteMap.MESHCMS_CSS);

    if (!webSite.getFile(cssPath).exists()) {
      cssPath = webSite.getAdminThemePath().add(SiteMap.MESHCMS_CSS);
    }

    return request.getContextPath() + "/" + cssPath;
  }

  /**
   * @return a numeric code for an object. This code is used in the menu, but
   * can be used elsewhere if needed. It is equal to the hash code of the
   * object, but never negative, since the minus sign creates problems when
   * converted to a string and used as part of a variable name in JavaScript.
   */
  public static int getMenuCode(Object o) {
    return o.hashCode() & 0x7FFFFFFF;
  }

  /**
   * @return the 2nd level domain from which the request comes.
   */
  public static String get2ndLevelDomain(HttpServletRequest request) {
    return get2ndLevelDomain(request.getRequestURL().toString());
  }

  /**
   * @return the 2nd level domain from the URL.
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
    request.setAttribute(HitFilter.BLOCK_CACHE_ATTRIBUTE,
        HitFilter.BLOCK_CACHE_ATTRIBUTE);
  }

  /**
   * Checks the current Request scope to see if some class has called the
   * {@link #setBlockCache} method.
   */
  public static boolean isCacheBlocked(HttpServletRequest request) {
    return request.getAttribute(HitFilter.BLOCK_CACHE_ATTRIBUTE) != null;
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
      locale = (Locale) pageContext.getAttribute(HitFilter.LOCALE_ATTRIBUTE,
          PageContext.PAGE_SCOPE);
    }

    Enumeration en = pageContext.getAttributeNamesInScope(PageContext.PAGE_SCOPE);

    while (locale == null && en.hasMoreElements()) {
      Object obj = pageContext.getAttribute((String) en.nextElement());

      if (obj instanceof Locale) {
        locale = (Locale) obj;
      }
    }

    if (locale == null) {
      locale = (Locale) pageContext.getAttribute(HitFilter.LOCALE_ATTRIBUTE,
          PageContext.REQUEST_SCOPE);
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
   * @return a modified name that does not contain characters not recommended
   * in a file name.
   */
  public static String fixFileName(String name, boolean useSpacers) {
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
          if (useSpacers && sb.length() > 0) { // add a spacer only if not as first char
            sb.append(spacer);
          }

          addSpacer = false;
        }

        sb.append(c);
      } else {
        addSpacer = true; // it's a spacer, will be added next if needed
      }
    }

    while(sb.length() > 0 && sb.charAt(sb.length() - 1) == '.') {
      sb.deleteCharAt(sb.length() - 1);
    }

    if (sb.length() == 0) {
      sb.append(spacer);
    }

    return sb.toString();
  }

  /**
   * @return a nicer representation of the number as a file length. The number
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
    Charset suggestedCharset;

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
      request.setAttribute(HitFilter.LAST_MODIFIED_ATTRIBUTE, new Long(time));
    }
  }

  public static long getLastModifiedTime(HttpServletRequest request) {
    long time = 0L;

    try {
      time = ((Long) request.getAttribute(HitFilter.LAST_MODIFIED_ATTRIBUTE)).longValue();
    } catch (Exception ex) {}

    return time;
  }

  public static javax.mail.Session getMailSession(WebSite webSite) {
    Properties props = new Properties();
    props.put("mail.smtp.host", webSite.getConfiguration().getMailServer());
    final String smtpUsername = webSite.getConfiguration().getSmtpUsername();
    final String smtpPassword = webSite.getConfiguration().getSmtpPassword();

    if (!Utils.isNullOrWhitespace(smtpUsername)) {
      props.put("mail.smtp.auth", "true");
    }

    javax.mail.Session mailSession = javax.mail.Session.getInstance(props,
        new javax.mail.Authenticator() {
      protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
        return new javax.mail.PasswordAuthentication(smtpUsername, smtpPassword);
      }
    });

    return mailSession;
  }

  public static File getCacheFile(WebSite webSite, SiteMap siteMap, Path pagePath) {
    if (siteMap == null) {
      siteMap = webSite.getSiteMap();
    }

    pagePath = siteMap.getServedPath(pagePath);
    File cacheFile = webSite.getRepositoryFile(pagePath, HitFilter.CACHE_FILE_NAME);
    
    // a cached page too small is suspicious
    if (cacheFile.exists() && cacheFile.length() > 256 &&
        cacheFile.lastModified() > siteMap.getLastModified()) {
      return cacheFile;
    }

    return null;
  }

  public static boolean isCached(WebSite webSite, SiteMap siteMap, Path pagePath) {
    if (siteMap == null) {
      siteMap = webSite.getSiteMap();
    }

    int cacheType = webSite.getConfiguration().getCacheType();
    pagePath = siteMap.getServedPath(pagePath);

    if (cacheType == Configuration.IN_MEMORY_CACHE) {
      return siteMap.isCached(pagePath);
    } else if (cacheType == Configuration.ON_DISK_CACHE ||
        cacheType == Configuration.MIXED_CACHE) {
      return getCacheFile(webSite, siteMap, pagePath) != null;
    }

    return false;
  }

  public static void removeFromCache(WebSite webSite, SiteMap siteMap, Path pagePath) {
    if (siteMap == null) {
      siteMap = webSite.getSiteMap();
    }

    int cacheType = webSite.getConfiguration().getCacheType();
    pagePath = siteMap.getServedPath(pagePath);

    if (cacheType == Configuration.IN_MEMORY_CACHE ||
        cacheType == Configuration.MIXED_CACHE) {
      siteMap.removeFromCache(pagePath);
    }
    
    if (cacheType == Configuration.ON_DISK_CACHE ||
        cacheType == Configuration.MIXED_CACHE) {
      File cacheFile = getCacheFile(webSite, siteMap, pagePath);

      if (cacheFile != null && cacheFile.exists()) {
        Utils.forceDelete(cacheFile);
      }
    }
  }

  /**
   * @deprecated use {@link Utils#encodeHTML(java.lang.String)} instead.
   */
  public static String encodeHTML(String html) {
    return Utils.encodeHTML(html);
  }

  public static Path getCorrespondingPath(WebSite webSite, Path path,
      String otherRoot) {
    Path cPath = path.replace(0, otherRoot);
    return webSite.getFile(cPath).exists() ? cPath : new Path(otherRoot);
  }

  public static String[] getAcceptedLanguages(HttpServletRequest request) {
    Enumeration alEnum = request.getHeaders("Accept-Language");
    List list = new ArrayList();

    while (alEnum.hasMoreElements()) {
      String s = (String) alEnum.nextElement();
      StringTokenizer st = new StringTokenizer(s, ",");

      while (st.hasMoreTokens()) {
        String token = st.nextToken();
        int sc = token.indexOf(';');

        if (sc >= 0) {
          token = token.substring(0, sc);
        }

        token = token.trim();
        token = Utils.replace(token, '-', "_");

        if (!list.contains(token)) {
          list.add(token);
        }
      }
    }

    // add main language codes as fallback (e.g. it_IT, en, en_US becomes
    // it_IT, en, en_US, it
    int n = list.size();

    for (int i = 0; i < n; i++) {
      String token = (String) list.get(i);
      int us = token.indexOf('_');

      if (us >= 0) {
        token = token.substring(0, us);

        if (!list.contains(token)) {
          list.add(token);
        }
      }
    }

    return (String[]) list.toArray(new String[list.size()]);
  }
  
  public static String fixRelativeURLs(String html, Path root) {
    Pattern p = Pattern.compile("(<(?:a|img)(?:[^>]*?)(?:src|href)=\")([^\"]*)(\"[^>]*?>)",
        Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(html);
    StringBuffer sb = new StringBuffer(html.length());
    
    while(m.find()) {
      String s = m.group(2);
      
      if (s.indexOf(':') < 0) {
        s = new Path(s).getRelativeTo(root).getAsLink();
      }
      
      m.appendReplacement(sb, m.group(1) + s + m.group(3));
    }
    
    m.appendTail(sb);
    return sb.toString();
  }
}

package com.cromoteca.meshcms;

import javax.servlet.http.*;
import com.cromoteca.util.*;

public class MultiSiteRequestWrapper extends HttpServletRequestWrapper {
  Path requestedPath;
  Path servedPath;
  
  public MultiSiteRequestWrapper(HttpServletRequest request,
      VirtualWebSite webSite) {
    super(request);
    requestedPath = new Path(request.getServletPath());
    servedPath = webSite.getServedPath(requestedPath);
  }
  
  public String getServletPath() {
    return servedPath.getAsLink();
  }
  
  public Path getRequestedPath() {
    return requestedPath;
  }

  public Path getServedPath() {
    return servedPath;
  }
}

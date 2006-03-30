package com.cromoteca.meshcms;

import javax.servlet.http.*;

public class MultiSiteRequestWrapper extends HttpServletRequestWrapper {
  HttpServletRequest request;
  
  public MultiSiteRequestWrapper(HttpServletRequest request) {
    super(request);
    this.request = request;
  }
}

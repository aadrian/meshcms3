/*
 * This file is nearly identical to the SessionDecoratorMapper available in the
 * SiteMesh CVS and written by Ricardo Lecheta. It has been kept out of the
 * SiteMesh package to point out that this file is not part of the current
 * SiteMesh official release.
 */
package org.meshcms.core;

import java.util.*;
import javax.servlet.http.*;
import com.opensymphony.module.sitemesh.*;
import com.opensymphony.module.sitemesh.mapper.*;

/**
 * <p>Will look at a request attribute to find the name of an appropriate decorator to use. If the
 * request attribute is not present, the mapper will not do anything and allow the next mapper in the chain
 * to select a decorator.</p>
 * 
 * <p>By default, it will look at the 'decorator' session attribute, however this can be overriden by
 * configuring the mapper with a 'decorator.parameter' property.</p>
 *
 * @author Ricardo Lecheta
 */
public class RequestDecoratorMapper extends AbstractDecoratorMapper {
  private String decoratorParameter = null;

  public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
    super.init(config, properties, parent);
    decoratorParameter = properties.getProperty("decorator.parameter", "decorator");
  }

  public Decorator getDecorator(HttpServletRequest request, Page page) {
    Decorator result = null;
    String decorator = (String) request.getAttribute(decoratorParameter);

    if (decorator != null) {
      result = getNamedDecorator(request, decorator);
    }
        
    return result == null ? super.getDecorator(request, page) : result;
  }
}

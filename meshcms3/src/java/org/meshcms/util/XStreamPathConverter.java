package org.meshcms.util;

import com.thoughtworks.xstream.converters.basic.*;

public class XStreamPathConverter extends AbstractBasicConverter {
  private boolean prependSlash;
  
  protected Object fromString(String string) {
    return new Path(string);
  }

  public boolean canConvert(Class aClass) {
    return aClass.equals(Path.class);
  }

  protected String toString(Object obj) {
    return prependSlash ? ((Path) obj).getAsLink() : obj.toString();
  }

  public boolean isPrependSlash() {
    return prependSlash;
  }

  public void setPrependSlash(boolean prependSlash) {
    this.prependSlash = prependSlash;
  }
}

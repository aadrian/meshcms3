package org.meshcms.util;

import com.thoughtworks.xstream.converters.basic.*;

/**
 * Allows to save instances of {@link Path} using XStream.
 */
public class XStreamPathConverter extends AbstractSingleValueConverter {
  private boolean prependSlash;
  
  public Object fromString(String string) {
    return new Path(string);
  }

  public boolean canConvert(Class aClass) {
    return aClass.equals(Path.class);
  }

  public String toString(Object obj) {
    return prependSlash ? ((Path) obj).getAsLink() : obj.toString();
  }

  /**
   * Returns the current type of string (with or without prepended slash).
   */
  public boolean isPrependSlash() {
    return prependSlash;
  }

  /**
   * Defines the type of string that will be used to save (with or without
   * prepended slash).
   */
  public void setPrependSlash(boolean prependSlash) {
    this.prependSlash = prependSlash;
  }
}

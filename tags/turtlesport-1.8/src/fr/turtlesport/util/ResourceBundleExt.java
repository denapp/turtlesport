package fr.turtlesport.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.PropertyResourceBundle;

/**
 * @author Denis Apparicio
 * 
 */
public class ResourceBundleExt extends PropertyResourceBundle {

  public ResourceBundleExt(InputStream stream) throws IOException {
    super(stream);
  }

  public ResourceBundleExt(Reader reader) throws IOException {
    super(reader);
  }

  public String getStringLib(String key) {
    return getString(key) + " :";
  }

}

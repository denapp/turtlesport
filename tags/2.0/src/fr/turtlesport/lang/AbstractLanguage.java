package fr.turtlesport.lang;

import java.net.URL;

import javax.swing.ImageIcon;

import fr.turtlesport.ui.swing.img.ImagesRepository;

public abstract class AbstractLanguage implements ILanguage {

  /* (non-Javadoc)
  * @see fr.turtlesport.lang.ILanguage#getEncoding()
  */
  public String getEncoding() {
    return "UTF-8";
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#getFlag()
   */
  public ImageIcon getFlag() {
    String name = "flag/" + getLocale().getLanguage() + ".png";
    URL url = ImagesRepository.class.getResource(name);
    return (url == null) ? null : new ImageIcon(url);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.ILanguage#getName()
   */
  public String getName() {
    return getLocale().getDisplayLanguage(LanguageManager.getManager()
        .getCurrentLang().getLocale());
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getLocale().getLanguage() + ";" + getLocale().getCountry();
  }

}

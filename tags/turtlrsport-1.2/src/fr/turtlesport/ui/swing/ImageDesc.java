package fr.turtlesport.ui.swing;

import java.io.InputStream;

/**
 * @author denis
 * 
 */
public class ImageDesc {

  private Class<?>  clazz;

  private String name;

  /**
   * Creation d'une instance en fonction du path.
   * 
   * @param path
   *          le path.
   * @return
   * @throws ClassNotFoundException
   */
  public static ImageDesc createInstance(String path) throws ClassNotFoundException {
    if (path == null || path.charAt(0) != '@') {
      return null;
    }

    int index = path.indexOf('#');
    String className = path.substring(1, index);
    String name = path.substring(index+1, path.length());

    return new ImageDesc(Class.forName(className), name);
  }

  /**
   * D&eacute;termine si ce path est valide.
   * 
   * @param path
   *          le path
   * @return <code>true</code> si ce path est valide, <code>false</code>
   *         sinon.
   */
  public static final boolean isValidPath(String path) {
    if (path == null || path.charAt(0) != '@') {
      return false;
    }

    int index = path.indexOf('#');
    return (index != -1 && (index != path.length() - 1));
  }

  /**
   * Construit la description d'une image en pr&eacute;cisant sa classe et son
   * image.
   * 
   * @param clazz
   *          la classe.
   * @param name
   *          le nom de l'image.
   */
  public ImageDesc(Class<?> clazz, String name) {
    super();
    this.clazz = clazz;
    this.name = name;
  }

  /**
   * Restitue l'<code>InputStream</code> de cette image.
   * 
   * @return l'<code>InputStream</code> de cette image.
   */
  public InputStream getInputStream() {
    return clazz.getResourceAsStream(name);
  }

  /**
   * Restitue la classe.
   * 
   * @return la classe.
   */
  public Class<?> getClazz() {
    return clazz;
  }

  /**
   * Valorise la classe.
   * 
   * @param la
   *          nouvelle valeur.
   */
  public void setClazz(Class<?> clazz) {
    this.clazz = clazz;
  }

  /**
   * Restitue le nom de l'image.
   * 
   * @return le nom de l'image.
   */
  public String getName() {
    return name;
  }

  /**
   * Valorise le nom de l'image.
   * 
   * @param name
   *          la nouvelle valeur.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Restitue le path de cette classe.
   * 
   * @return
   */
  public String getPath() {
    return "@" + clazz.getName() + "#" + name;
  }

}

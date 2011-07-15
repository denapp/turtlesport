package fr.turtlesport.ui.swing.component;

import java.io.File;

import fr.turtlesport.ui.swing.ImageDesc;

/**
 * @author Denis Apparicio
 * 
 */
public class PhotoEvent {

  private String path;

  /**
   * @param file
   */
  public PhotoEvent(File file) {
    super();
    this.path = file.getAbsolutePath();
  }

  /**
   * @param file
   */
  public PhotoEvent(ImageDesc img) {
    super();
    this.path = img.getPath();    
  }

  /**
   * Restitue le path de la photo.
   * 
   * @return le path de la photo.
   */
  public String getPath() {
    return path;
  }

}

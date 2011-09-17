package fr.turtlesport.log;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Layout;
import org.apache.log4j.RollingFileAppender;

import fr.turtlesport.util.Location;

/**
 * @author Denis Apparicio
 * 
 */
public class UserRollingFileAppender extends RollingFileAppender {

  private static final String USER_FILE_NAME = new File(Location.userLocation(),
                                                        "turtle.log").getPath();

  /**
   * 
   */
  public UserRollingFileAppender() {
    super();
  }

  /**
   * @param layout
   * @param filename
   * @param append
   * @throws IOException
   */
  public UserRollingFileAppender(Layout layout, String filename, boolean append) throws IOException {
    super(layout, USER_FILE_NAME, append);
    setFile(filename);
  }

  /**
   * @param layout
   * @param filename
   * @throws IOException
   */
  public UserRollingFileAppender(Layout layout, String filename) throws IOException {
    super(layout, USER_FILE_NAME);
    setFile(filename);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.log4j.FileAppender#setFile(java.lang.String)
   */
  @Override
  public void setFile(String filename) {
    super.setFile(new File(Location.userLocation(), new File(filename)
        .getName()).getPath());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.log4j.FileAppender#getFile()
   */
  @Override
  public String getFile() {
    return USER_FILE_NAME;
  }

}

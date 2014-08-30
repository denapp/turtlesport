package fr.turtlesport.mail.linux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.mail.AbstractMailClientThunderbird;
import fr.turtlesport.util.Location;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public class MailClientLinuxThunderbird extends AbstractMailClientThunderbird {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger
        .getLogger(MailClientLinuxThunderbird.class);
  }

  protected MailClientLinuxThunderbird() {
    if (!isAvailable()) {
      throw new IllegalAccessError();
    }
  }
  
  /**
   * D&eacute;termine si le client mail est valable.
   * 
   * @return <code>true</code> si le client mail est valable, <code>false</code>
   *         sinon.
   */
  protected static boolean isAvailable() {
    return Location.isInPath("thunderbird");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.AbstractMailClientThunderbird#getLocation()
   */
  @Override
  public String getLocation() {
    return "thunderbird";
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.mail.AbstractMailClientThunderbird#isRunning()
   */
  @Override
  public boolean isRunning() {
    // Check running mozilla instance using: mozilla -remote ping().
    // If there is no running Mozilla instance. The complete output is: 'No
    // running
    // window found.'

     try {
      Process proc = Runtime.getRuntime().exec(new String[] { getLocation(),
          "-remote",
          "ping()" });

      InputStream stderr = proc.getErrorStream();
      InputStreamReader isr = new InputStreamReader(stderr);
      BufferedReader br = new BufferedReader(isr);
      String line = null;
      while ((line = br.readLine()) != null) {
        if (line.indexOf("No running window") != -1) {
          br.close();
          return false;
        }
      }
      br.close();
    }
    catch (IOException e) {
      log.error("", e);
      return false;
    }

    return true;
  }

}

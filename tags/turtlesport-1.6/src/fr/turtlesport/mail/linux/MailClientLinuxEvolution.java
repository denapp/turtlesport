package fr.turtlesport.mail.linux;

import fr.turtlesport.mail.AbstractMailClientEvolution;
import fr.turtlesport.util.Location;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public class MailClientLinuxEvolution extends AbstractMailClientEvolution {
  
  protected MailClientLinuxEvolution() {
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
    return Location.isInPath("evolution");
  }

  @Override
  public String getLocation() {
    return "evolution";
  }
  
}

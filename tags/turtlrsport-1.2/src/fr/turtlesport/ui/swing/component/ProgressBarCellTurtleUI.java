package fr.turtlesport.ui.swing.component;

import javax.swing.plaf.basic.BasicProgressBarUI;

/**
 * @author Denis Apparicio
 * 
 */
public class ProgressBarCellTurtleUI extends BasicProgressBarUI {

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.plaf.basic.BasicProgressBarUI#installDefaults()
   */
  @Override
  protected void installDefaults() {
    super.installDefaults();
    setCellLength(5);
    setCellSpacing(2);
  }

}

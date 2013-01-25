package fr.turtlesport.ui.swing.component;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.JDialogMap;
import fr.turtlesport.unit.event.UnitEvent;
import fr.turtlesport.unit.event.UnitListener;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelMap extends JPanel implements  UnitListener {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JPanelMap.class);
  }

  private JTurtleMapKit       jMapKit;

  /**
   * 
   */
  public JPanelMap() {
    super();

    log.debug(">>JPanelMap");
    initialize();
    log.debug("<<JPanelMap");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.unit.event.UnitListener#completedRemoveUnitListener()
   */
  public void completedRemoveUnitListener() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.unit.event.UnitListener#unitChanged(fr.turtlesport.unit.
   * event.UnitEvent)
   */
  public void unitChanged(UnitEvent event) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.LanguageListener#completedRemoveLanguageListener()
   */
  public void completedRemoveLanguageListener() {
  }

  public void fireActionGrow() {
    if (SwingUtilities.isEventDispatchThread()) {
      doFireActionGrow();
    }
    else {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          doFireActionGrow();
        }
      });
    }
  }

  private void doFireActionGrow() {
    JDialogMap.prompt(jMapKit);
  }

  private void initialize() {
    this.setLayout(new BorderLayout());
    this.add(getJMapKit(), BorderLayout.CENTER);

    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    // Evenement
    jMapKit.getMainMap().addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          fireActionGrow();
        }
      }
    });

  }

  private JTurtleMapKit getJMapKit() {
    if (jMapKit == null) {
      jMapKit = new JTurtleMapKit(true);
    }
    return jMapKit;
  }


}

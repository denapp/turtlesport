package fr.turtlesport.ui.swing.component;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.JDialogMap;
import fr.turtlesport.ui.swing.JPanelRun;
import fr.turtlesport.ui.swing.component.JTurtleMapKit.TablePointsModel;
import fr.turtlesport.unit.event.UnitEvent;
import fr.turtlesport.unit.event.UnitListener;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelMap extends JPanel implements LanguageListener, UnitListener {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JPanelMap.class);
  }

  private JLabel              jLabelOpenDialog;

  private JTurtleMapKit       jMapKit;

  /**
   * 
   */
  public JPanelMap(boolean isMap) {
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.lang.LanguageListener#languageChanged(fr.turtlesport.lang
   * .LanguageEvent)
   */
  public void languageChanged(final LanguageEvent event) {
    if (SwingUtilities.isEventDispatchThread()) {
      performedLanguage(event.getLang());
    }
    else {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          performedLanguage(event.getLang());
        }
      });
    }
  }

  private void performedLanguage(ILanguage lang) {
    ResourceBundle rb = ResourceBundleUtility.getBundle(lang, getClass());
    jLabelOpenDialog.setText(rb.getString("jLabelOpenDialog"));
  }

  private void initialize() {
    jLabelOpenDialog = new JLabel();
    jLabelOpenDialog.setHorizontalAlignment(SwingConstants.CENTER);
    jLabelOpenDialog.setFont(GuiFont.FONT_PLAIN_SMALL);

    this.setLayout(new BorderLayout());
    this.add(getJMapKit(), BorderLayout.CENTER);
    this.add(jLabelOpenDialog, BorderLayout.SOUTH);

    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    // Evenement
    jMapKit.getMainMap().addMouseListener(new MouseAdapter() {

      public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          Container parent = JPanelMap.this.getParent();
          while (parent != null) {
            if (parent instanceof JPanelRun) {
              break;
            }
            parent = parent.getParent();
          }

          DataRun data = null;
          if (parent instanceof JPanelRun) {
            data = ((JPanelRun) parent).getModel().getDataRun();
          }
          JDialogMap.prompt(jMapKit, data);
        }
      }

    });

    // Language
    LanguageManager.getManager().addLanguageListener(this);
    performedLanguage(LanguageManager.getManager().getCurrentLang());
  }

  private JTurtleMapKit getJMapKit() {
    if (jMapKit == null) {
      jMapKit = new JTurtleMapKit(true);
    }
    return jMapKit;
  }

  public TablePointsModel getModelMap() {
    return jMapKit.getModelMap();
  }

}

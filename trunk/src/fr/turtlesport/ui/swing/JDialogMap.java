package fr.turtlesport.ui.swing;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.component.JTurtleMapKit;
import fr.turtlesport.ui.swing.model.ModelDialogMap;
import fr.turtlesport.ui.swing.model.ModelMapkitManager;
import fr.turtlesport.ui.swing.model.ModelPointsManager;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JDialogMap extends JDialog {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JDialogMap.class);
  }

  private JSplitPane     jSplitPane;

  private JTurtleMapKit       mapKit;

  private JLabel              jLabelTitle;

  private JPanelRunLap        jPanelRight;

  private ResourceBundle      rb;

  // Model
  private ModelDialogMap      model;

  /**
   * @param frame
   */
  private JDialogMap(Frame frame) {
    super(frame, true);
    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), getClass());

    initialize();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.Window#dispose()
   */
  @Override
  public void dispose() {
    super.dispose();
    ModelMapkitManager.getInstance()
        .removeChangeListener(mapKit.getMapListener());
  }

  /**
   * Affiche la boite de dialogue.
   * 
   * @param mapKit
   */
  public static void prompt(JTurtleMapKit mapKit) {
    // mis a jour du model et affichage de l'IHM
    JDialogMap view = new JDialogMap(MainGui.getWindow());

    // MapKit
    view.mapKit.getMainMap().setCenterPosition(mapKit.getMainMap()
        .getCenterPosition());
    int zoom = mapKit.getMainMap().getZoom();
    if (zoom != 1) {
      zoom--;
    }
    view.mapKit.getMainMap().setZoom(zoom);

    // original zoom et position
    zoom = mapKit.getOriginalZoom();
    if (zoom != 1) {
      zoom--;
    }
    view.mapKit.setOriginalZoom(zoom);
    view.mapKit.setOriginalPosition(mapKit.getOriginalPosition());

    // model
    ModelDialogMap model = new ModelDialogMap();
    try {
      model.updateView(view);
      view.model = model;
      int indexLap = ModelPointsManager.getInstance().getLapIndex();
      if (indexLap != -1) {
        view.getJPanelRight().getJComboBoxLap().setSelectedIndex(indexLap + 1);
      }
    }
    catch (SQLException e) {
      JShowMessage.error(view.rb.getString("errorDatabase"));
      return;
    }

    view.setSize(920, 690);
    view.setLocationRelativeTo(MainGui.getWindow());
    view.setVisible(true);
  }

  public JLabel getJLabelTitle() {
    return jLabelTitle;
  }

  private void initialize() {
    jLabelTitle = new JLabel();
    jLabelTitle.setHorizontalAlignment(SwingConstants.CENTER);
    jLabelTitle.setFont(GuiFont.FONT_PLAIN);

    this.setContentPane(getJSplitPane());
    this.setTitle(rb.getString("title"));

    // Evenement
    getJPanelRight().getJComboBoxLap().addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (model != null
            && getJPanelRight().getJComboBoxLap().getSelectedIndex() > 0) {
          model.updateViewLap(JDialogMap.this, getJPanelRight()
              .getJComboBoxLap().getSelectedIndex() - 1);
        }
      }
    });

    jPanelRight.getJSwitchBox().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        try {
          if (model != null) {
            model.correctAltitude(JDialogMap.this);
            if (getJPanelRight().getJComboBoxLap().getSelectedIndex() > 0) {
              model.updateViewLap(JDialogMap.this, getJPanelRight()
                  .getJComboBoxLap().getSelectedIndex() - 1);
            }
          }
        }
        catch (SQLException e) {
          log.error("", e);
        }
      }
    });

  }

  public JPanelRunLap getJPanelRight() {
    if (jPanelRight == null) {
      jPanelRight = new JPanelRunLap();
      jPanelRight.setPreferredSize(new Dimension(300, 300));
      jPanelRight.setMinimumSize(new Dimension(300, 300));
    }
    return jPanelRight;
  }

  public JTurtleMapKit getJPanelMap() {
    if (mapKit == null) {
      mapKit = new JTurtleMapKit(false);
      mapKit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      Dimension dim = new Dimension(600, 600);
      mapKit.setPreferredSize(dim);
      mapKit.setMinimumSize(dim);
      mapKit.setGeoPositionVisible(true);
      mapKit.setTimeVisible(true);
    }
    return mapKit;
  }
  
  /**
   * This method initializes jSplitPane
   * 
   * @return javax.swing.JSplitPane
   */
  private JSplitPane getJSplitPane() {
    if (jSplitPane == null) {
      jSplitPane = new JSplitPane();
//      jSplitPane.setOpaque(false);
      jSplitPane.setContinuousLayout( true );
      jSplitPane.setOneTouchExpandable(true);
      jSplitPane.setLeftComponent(getJPanelMap());
      jSplitPane.setRightComponent(getJPanelRight());
      jSplitPane.setDividerLocation(600);
      jSplitPane.setResizeWeight(1.0);
    }
    return jSplitPane;
  }

}

package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.plaf.ComboBoxUI;

import fr.turtlesport.Configuration;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.JDiagramComponent;
import fr.turtlesport.ui.swing.component.JDiagramOneComponent;
import fr.turtlesport.ui.swing.component.JPanelGraphOne;
import fr.turtlesport.ui.swing.model.ModelDialogDiagramComponents;
import fr.turtlesport.ui.swing.model.ModelMapkitManager;
import fr.turtlesport.ui.swing.model.ModelPointsManager;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.util.OperatingSystem;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JDialogDiagramComponents extends JDialog {
  private static TurtleLogger          log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JDialogDiagramComponents.class);
  }

  private JPanelRunLap                 jPanelRight;

  private JPanelGraphOne[]             jPanelTabGraph;

  private JPanel                       jPanelGraphs;

  private ResourceBundle               rb;

  // Model
  private ModelDialogDiagramComponents model = new ModelDialogDiagramComponents();

  private JPanel                       jPanelX;

  private JComboboxUIlistener          jComboBoxX;

  public JDialogDiagramComponents(Frame owner) {
    super(owner, true);
    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), JDialogDiagramComponents.class);
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
    for (JPanelGraphOne panel : jPanelTabGraph) {
      ModelMapkitManager.getInstance().removeChangeListener(panel.getJDiagram()
          .getModel());
    }
  }

  /**
   * @throws SQLException
   * 
   */
  public static void prompt() {
    JDialogDiagramComponents view = new JDialogDiagramComponents(MainGui.getWindow());

    try {
      view.model.updateView(view);
      int indexLap = ModelPointsManager.getInstance().getLapIndex();
      if (indexLap != -1) {
        view.getJPanelRight().getJComboBoxLap().setSelectedIndex(indexLap + 1);
      }
    }
    catch (SQLException e) {
      return;
    }

    // view.pack();
    view.setLocationRelativeTo(MainGui.getWindow());
    view.setVisible(true);
  }

  private void initialize() {
    setTitle(rb.getString("title"));
    JPanel contentPane = new JPanel();
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

    contentPane.add(getjPanelGraphs());
    contentPane.add(Box.createRigidArea(new Dimension(5, 0)));
    contentPane.add(getJPanelRight());

    setContentPane(contentPane);

    this.setSize(940, (jPanelTabGraph.length > 4) ? 740 : 690);

    boolean isAxisXDistance = Configuration.getConfig()
        .getPropertyAsBoolean("Diagram", "isAxisXDistance", true);
    jComboBoxX.setSelectedIndex(isAxisXDistance ? 0 : 1);

    // Evenement
    getJPanelRight().getJComboBoxLap().addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (model != null
            && getJPanelRight().getJComboBoxLap().getSelectedIndex() > 0) {
          model.updateViewLap(JDialogDiagramComponents.this, getJPanelRight()
              .getJComboBoxLap().getSelectedIndex() - 1);
        }
      }
    });
    jComboBoxX.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        for (JPanelGraphOne panel : jPanelTabGraph) {
          panel.getJDiagram().getModel()
              .setAxisX(jComboBoxX.getSelectedIndex() == 0);
        }
      }
    });

    // Denivele
    jPanelRight.getJSwitchBox().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
      }
    });

    jPanelRight.getJSwitchBox().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        try {
          if (model != null) {
            model.correctAltitude(JDialogDiagramComponents.this);
            if (getJPanelRight().getJComboBoxLap().getSelectedIndex() > 0) {
              model.updateViewLap(JDialogDiagramComponents.this,
                                  getJPanelRight().getJComboBoxLap()
                                      .getSelectedIndex() - 1);
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
    }
    return jPanelRight;
  }

  private JPanel getjPanelGraphs() {
    if (jPanelGraphs == null) {
      JPanel jPanelGraphsCenter = new JPanel();
      jPanelGraphsCenter.setLayout(new BoxLayout(jPanelGraphsCenter,
                                                 BoxLayout.Y_AXIS));

      int lenTab = 3;
      boolean hasCadence = ModelPointsManager.getInstance().hasCadencePoints();
      if (hasCadence) {
        lenTab++;
      }
      boolean hasTemp = ModelPointsManager.getInstance().hasTemperaturePoints();
      if (hasTemp) {
        lenTab++;
      }

      int ipos = -1;
      jPanelTabGraph = new JPanelGraphOne[lenTab];
      jPanelTabGraph[++ipos] = new JPanelGraphOne(JDiagramOneComponent.Type.HEART);
      jPanelTabGraph[++ipos] = new JPanelGraphOne(JDiagramOneComponent.Type.ALTITUDE);
      jPanelTabGraph[++ipos] = new JPanelGraphOne(JDiagramOneComponent.Type.SPEED);

      if (hasCadence) {
        jPanelTabGraph[++ipos] = new JPanelGraphOne(JDiagramOneComponent.Type.CADENCE);
      }
      if (hasTemp) {
        jPanelTabGraph[++ipos] = new JPanelGraphOne(JDiagramOneComponent.Type.TEMPERATURE);
      }

      for (int i = 0; i < jPanelTabGraph.length; i++) {
        for (int j = 0; j < jPanelTabGraph.length; j++) {
          if (i != j) {
            jPanelTabGraph[i].getJDiagram()
                .addDiagram(jPanelTabGraph[j].getJDiagram());
          }
        }
      }

      Dimension dim = new Dimension(0, 5);
      for (int i = 0; i < jPanelTabGraph.length - 1; i++) {
        jPanelGraphsCenter.add(jPanelTabGraph[i]);
        jPanelGraphsCenter.add(Box.createRigidArea(dim));
      }
      jPanelGraphsCenter.add(jPanelTabGraph[jPanelTabGraph.length - 1]);

      if (jPanelTabGraph.length > 4) {
        Dimension dimPref = new Dimension(550, 190);
        for (int i = 0; i < jPanelTabGraph.length; i++) {
          jPanelTabGraph[i].setPreferredSize(dimPref);
        }
      }
      JScrollPane scrollPane = new JScrollPane();
      scrollPane.getViewport().add(jPanelGraphsCenter);

      jPanelGraphs = new JPanel(new BorderLayout());
      jPanelGraphs.add(scrollPane, BorderLayout.CENTER);
      jPanelGraphs.add(getJPanelX(), BorderLayout.SOUTH);

    }
    return jPanelGraphs;
  }

  private JPanel getJPanelX() {
    if (jPanelX == null) {
      jPanelX = new JPanel();
      jPanelX.setLayout(new FlowLayout(FlowLayout.RIGHT));
      jPanelX.add(getJComboBoxX());
      Dimension dim = new Dimension(JDiagramComponent.WIDTH_LEFT / 2, 20);
      jPanelX.add(getJComboBoxX());
      jPanelX.add(new Box.Filler(dim, dim, dim));
    }
    return jPanelX;
  }

  private JComboBox getJComboBoxX() {
    if (jComboBoxX == null) {
      jComboBoxX = new JComboboxUIlistener();
      jComboBoxX.setFont(GuiFont.FONT_PLAIN_VERY_SMALL);
      int width = (OperatingSystem.isMacOSX()) ? 120 : 110;
      jComboBoxX.setPreferredSize(new Dimension(width, jComboBoxX
          .getPreferredSize().height));
      jComboBoxX.removeAllItems();
      ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
          .getManager().getCurrentLang(), JDiagramComponent.class);
      jComboBoxX.addItem(MessageFormat.format(rb.getString("unitX"),
                                              DistanceUnit.getDefaultUnit()));
      jComboBoxX.addItem(rb.getString("time"));
    }
    return jComboBoxX;
  }

  private class JComboboxUIlistener extends JComboBox {
    public JComboboxUIlistener() {
      super();
    }

    @Override
    public void setUI(ComboBoxUI ui) {
      super.setUI(ui);
      try {
        Dimension dim = getPreferredSize();

        // recuperation de la hauteur
        String[] items = { "item" };
        JComboBox cb = new JComboBox(items);
        int height = ui.getPreferredSize(cb).height;

        dim = new Dimension(dim.width, height);
        setPreferredSize(dim);
      }
      catch (Throwable e) {
      }
    }
  }

}

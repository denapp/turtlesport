package fr.turtlesport.ui.swing.component;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import fr.turtlesport.Configuration;
import fr.turtlesport.db.DataRunTrk;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.img.diagram.ImagesDiagramRepository;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelGraph extends JPanel implements LanguageListener {

  private JDiagramComponent jDiagram;

  private JPanel            jPanelTitle;

  private JCheckBox         jCheckBox1;

  private JCheckBox         jCheckBox2;

  private JCheckBox         jCheckBoxFilter;

  private JCheckBox         jCheckBoxTime;

  private JButton           jButtonZoomMoins;

  private JButton           jButtonZoomPlus;

  private static boolean    isVisibleY1;

  private static boolean    isVisibleY2;

  private static boolean    isVisibleTime;

  private static boolean    isFilter;

  private Border            raisedBorder  = BorderFactory
                                              .createLoweredBevelBorder();

  private Border            loweredBorder = BorderFactory
                                              .createRaisedBevelBorder();

  private JButton           jButtonReload;

  static {
    isVisibleY1 = Configuration.getConfig().getPropertyAsBoolean("Diagram",
                                                                 "isVisibleY1",
                                                                 true);
    isVisibleY2 = Configuration.getConfig().getPropertyAsBoolean("Diagram",
                                                                 "isVisibleY2",
                                                                 false);

    isVisibleTime = Configuration.getConfig()
        .getPropertyAsBoolean("Diagram", "isVisibleTime", true);

    isFilter = Configuration.getConfig().getPropertyAsBoolean("Diagram",
                                                              "isFilter",
                                                              false);
  }

  /**
   * 
   */
  public JPanelGraph() {
    super();
    initialize();
  }

  public static boolean isVisibleY1() {
    return isVisibleY1;
  }

  public static boolean isVisibleY2() {
    return isVisibleY2;
  }

  public static boolean isFilter() {
    return isFilter;
  }

  public static boolean isVisibleTime() {
    return isVisibleTime;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.LanguageListener#languageChanged(fr.turtlesport.lang.LanguageEvent)
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

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.LanguageListener#completedRemoveLanguageListener()
   */
  public void completedRemoveLanguageListener() {
  }

  private void performedLanguage(ILanguage lang) {
    ResourceBundle rb = ResourceBundleUtility
        .getBundle(lang, JDiagramComponent.class);

    jDiagram.getModel().setUnitX(MessageFormat.format(rb.getString("unitX"),
                                                      DistanceUnit
                                                          .getDefaultUnit()));
    jCheckBox1.setText("<html><font color=red>" + rb.getString("unitY1")
                       + "</font></html>");
    jCheckBox2.setText("<html><font color=blue>" + rb.getString("unitY2")
                       + "</font></html>");
    jCheckBoxTime.setText(rb.getString("time"));
    jCheckBoxFilter.setText(rb.getString("filter"));
  }

  /**
   * @param trks
   */
  public void updatePoints(DataRunTrk[] trks, String unit) {
    jDiagram.getModel().updateData(trks, unit);
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    jDiagram = new JDiagramComponent();
    this.setSize(300, 200);
    this.setLayout(new BorderLayout());
    this.add(jDiagram, BorderLayout.CENTER);
    this.add(getJPanelTitle(), BorderLayout.NORTH);

    LanguageManager.getManager().addLanguageListener(this);
    performedLanguage(LanguageManager.getManager().getCurrentLang());

    // Evenement
    jCheckBox1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        isVisibleY1 = jCheckBox1.isSelected();
        Configuration.getConfig().addProperty("Diagram",
                                              "isVisibleY1",
                                              Boolean.toString(isVisibleY1));
        jDiagram.getModel().changeVisible();
      }
    });
    jCheckBox2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        isVisibleY2 = jCheckBox2.isSelected();
        Configuration.getConfig().addProperty("Diagram",
                                              "isVisibleY2",
                                              Boolean.toString(isVisibleY2));
        jDiagram.getModel().changeVisible();
      }
    });
    jCheckBoxTime.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        isVisibleTime = jCheckBoxTime.isSelected();
        Configuration.getConfig().addProperty("Diagram",
                                              "isVisibleTime",
                                              Boolean.toString(isVisibleTime));
        jDiagram.getModel().changeVisible();
      }
    });
    jCheckBoxFilter.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        isFilter = jCheckBoxFilter.isSelected();
        Configuration.getConfig().addProperty("Diagram",
                                              "isFilter",
                                              Boolean.toString(isFilter));

        jDiagram.getModel().changeFilter();
      }
    });
    jButtonZoomMoins.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButtonZoomMoins.setBorder(raisedBorder);
        jButtonZoomPlus.setBorder(loweredBorder);
        jDiagram.getModel().zoomMoins();
      }
    });
    jButtonZoomPlus.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButtonZoomPlus.setBorder(raisedBorder);
        jButtonZoomMoins.setBorder(loweredBorder);
        jDiagram.getModel().zoomPlus();
      }
    });
    jButtonReload.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jDiagram.getModel().reload();
      }
    });

  }

  /**
   * @return the jDiagram
   */
  public JDiagramComponent getJDiagram() {
    return jDiagram;
  }

  /**
   * This method initializes jPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelTitle() {
    if (jPanelTitle == null) {
      jPanelTitle = new JPanel();
      jPanelTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
      jPanelTitle.add(getJCheckBox1());
      jPanelTitle.add(getJCheckBox2());
      jPanelTitle.add(getJCheckBoxTime());
      jPanelTitle.add(getJCheckBoxFilter());
      jPanelTitle.add(new JLabel("   "));
      jPanelTitle.add(getJButtonZoomPlus());
      jPanelTitle.add(getJButtonZoomMoins());
      jPanelTitle.add(getJButtonReload());
    }
    return jPanelTitle;
  }

  /**
   * This method initializes jCheckBox
   * 
   * @return javax.swing.JCheckBox
   */
  private JCheckBox getJCheckBox1() {
    if (jCheckBox1 == null) {
      jCheckBox1 = new JCheckBox();
      jCheckBox1.setFont(GuiFont.FONT_PLAIN_SMALL);
      jCheckBox1.setSelected(isVisibleY1);
    }
    return jCheckBox1;
  }

  /**
   * This method initializes jCheckBox
   * 
   * @return javax.swing.JCheckBox
   */
  private JCheckBox getJCheckBox2() {
    if (jCheckBox2 == null) {
      jCheckBox2 = new JCheckBox();
      jCheckBox2.setSelected(isVisibleY2);
      jCheckBox2.setFont(GuiFont.FONT_PLAIN_SMALL);
    }
    return jCheckBox2;
  }

  /**
   * This method initializes jCheckBox
   * 
   * @return javax.swing.JCheckBox
   */
  private JCheckBox getJCheckBoxTime() {
    if (jCheckBoxTime == null) {
      jCheckBoxTime = new JCheckBox();
      jCheckBoxTime.setSelected(isVisibleTime);
      jCheckBoxTime.setFont(GuiFont.FONT_PLAIN_SMALL);
      jCheckBoxTime.setForeground(JDiagramComponent.COLOR_TIME);
    }
    return jCheckBoxTime;
  }

  /**
   * This method initializes jCheckBoxFilter
   * 
   * @return javax.swing.JCheckBox
   */
  private JCheckBox getJCheckBoxFilter() {
    if (jCheckBoxFilter == null) {
      jCheckBoxFilter = new JCheckBox();
      jCheckBoxFilter.setSelected(isFilter);
      jCheckBoxFilter.setFont(GuiFont.FONT_PLAIN_SMALL);
    }
    return jCheckBoxFilter;
  }

  /**
   * This method initializes jButtonZoomPlus
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonZoomMoins() {
    if (jButtonZoomMoins == null) {
      jButtonZoomMoins = new JButton();
      jButtonZoomMoins.setIcon(ImagesDiagramRepository
          .getImageIcon("minus.png"));
      jButtonZoomMoins.setText("");
      jButtonZoomMoins.setMargin(new Insets(2, 2, 2, 2));
      jButtonZoomMoins.setMargin(new Insets(2, 2, 2, 2));
      jButtonZoomMoins.setMaximumSize(new Dimension(20, 20));
      jButtonZoomMoins.setMinimumSize(new Dimension(20, 20));
      jButtonZoomMoins.setPreferredSize(new Dimension(20, 20));
      jButtonZoomMoins.setOpaque(false);
      jButtonZoomMoins.setBorder(loweredBorder);
    }
    return jButtonZoomMoins;
  }

  /**
   * This method initializes jButtonZoomMoins
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonZoomPlus() {
    if (jButtonZoomPlus == null) {
      jButtonZoomPlus = new JButton();
      jButtonZoomPlus.setIcon(ImagesDiagramRepository.getImageIcon("plus.png"));
      jButtonZoomPlus.setText("");
      jButtonZoomPlus.setMargin(new Insets(2, 2, 2, 2));
      jButtonZoomPlus.setMaximumSize(new Dimension(20, 20));
      jButtonZoomPlus.setMinimumSize(new Dimension(20, 20));
      jButtonZoomPlus.setPreferredSize(new Dimension(20, 20));
      jButtonZoomPlus.setOpaque(false);
      jButtonZoomPlus.setBorder(raisedBorder);
    }
    return jButtonZoomPlus;
  }

  /**
   * This method initializes jButtonReload
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonReload() {
    if (jButtonReload == null) {
      jButtonReload = new JButton();
      jButtonReload.setIcon(ImagesDiagramRepository
          .getImageIcon("view-fullscreen.png"));
      jButtonReload.setText("");
      jButtonReload.setMargin(new Insets(2, 2, 2, 2));
      jButtonReload.setMaximumSize(new Dimension(20, 20));
      jButtonReload.setMinimumSize(new Dimension(20, 20));
      jButtonReload.setPreferredSize(new Dimension(20, 20));
      jButtonReload.setOpaque(false);
    }
    return jButtonReload;
  }

}

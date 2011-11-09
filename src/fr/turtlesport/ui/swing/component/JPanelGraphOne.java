package fr.turtlesport.ui.swing.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ComboBoxUI;

import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.img.diagram.ImagesDiagramRepository;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.PaceUnit;
import fr.turtlesport.unit.SpeedUnit;
import fr.turtlesport.unit.event.UnitEvent;
import fr.turtlesport.util.OperatingSystem;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelGraphOne extends JPanel {

  private JDiagramOneComponent jDiagram;

  private JPanel               jPanelTitle;

  private JCheckBox            jCheckBoxFilter;

  private JComboboxUIlistener  jComboBoxY3;

  private JComboboxUIlistener  jComboBoxX;

  private JButton              jButtonZoomMoins;

  private JButton              jButtonZoomPlus;

  private Border               raisedBorder  = BorderFactory
                                                 .createLoweredBevelBorder();

  private Border               loweredBorder = BorderFactory
                                                 .createRaisedBevelBorder();

  private JButton              jButtonReload;

  private JPanel               jPanelX;

  private JLabel               jLabelTitle;

  private int                  type;

  /**
   * 
   */
  public JPanelGraphOne(int type) {
    super();
    this.type = type;
    initialize();
  }

  public JDiagramOneComponent getjDiagram() {
    return jDiagram;
  }

  private void performedLanguage(ILanguage lang) {
    ResourceBundle rb = ResourceBundleUtility
        .getBundle(lang, JDiagramComponent.class);

    jDiagram.getModel().setUnitX(MessageFormat.format(rb.getString("unitX"),
                                                      DistanceUnit
                                                          .getDefaultUnit()));

    switch (type) {
      case JDiagramOneComponent.HEART:
        getJLabelTitle().setText("<html><font color=red>"
                                 + rb.getString("unitY1") + "</font></html>");
        break;

      case JDiagramOneComponent.ALTITUDE:
        getJLabelTitle().setText("<html><font color=blue>"
                                 + rb.getString("unitY2") + "</font></html>");
        break;

      default:
        getJComboBoxY3().removeAllItems();
        getJComboBoxY3().addItem(rb.getString("speed") + "("
                                 + SpeedUnit.getDefaultUnit() + ")");
        getJComboBoxY3().addItem(rb.getString("allure") + "("
                                 + PaceUnit.getDefaultUnit() + ")");
        jComboBoxY3.setSelectedIndex(jDiagram.getModel().isVisibleSpeed() ? 0
            : 1);

    }

    if (type != JDiagramOneComponent.SPEED) {
      jCheckBoxFilter.setText(rb.getString("filter"));
    }
    jComboBoxX.removeAllItems();
    jComboBoxX.addItem(MessageFormat.format(rb.getString("unitX"),
                                            DistanceUnit.getDefaultUnit()));
    jComboBoxX.addItem(rb.getString("time"));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.unit.event.UnitListener#unitChanged(fr.turtlesport.unit.
   * event.UnitEvent)
   */
  public void unitChanged(UnitEvent e) {
    if (e.isEventSpeed() || e.isEventSpeedAndPace() || e.isEventPace()
        || e.isEventDistance()) {
      ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
          .getManager().getCurrentLang(), JDiagramComponent.class);

      int index = jComboBoxY3.getSelectedIndex();
      jComboBoxY3.removeAllItems();
      jComboBoxY3.addItem(rb.getString("speed") + "("
                          + SpeedUnit.getDefaultUnit() + ")");
      jComboBoxY3.addItem(rb.getString("allure") + "("
                          + PaceUnit.getDefaultUnit() + ")");
      if (index != -1) {
        jComboBoxY3.setSelectedIndex(index);
      }

      index = jComboBoxX.getSelectedIndex();
      jComboBoxX.removeAllItems();
      jComboBoxX.addItem(MessageFormat.format(rb.getString("unitX"),
                                              DistanceUnit.getDefaultUnit()));
      jComboBoxX.addItem(rb.getString("time"));
      if (index != -1) {
        jComboBoxX.setSelectedIndex(index);
      }
    }
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    jDiagram = new JDiagramOneComponent(type);
    setSize(300, 230);
    setLayout(new BorderLayout(0, 0));
    add(jDiagram, BorderLayout.CENTER);
    add(getJPanelTitle(), BorderLayout.NORTH);
    add(getJPanelX(), BorderLayout.SOUTH);
    setOpaque(true);

    // setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    setBorder(BorderFactory
        .createTitledBorder(null,
                            "",
                            TitledBorder.DEFAULT_JUSTIFICATION,
                            TitledBorder.DEFAULT_POSITION,
                            GuiFont.FONT_PLAIN,
                            null));
    performedLanguage(LanguageManager.getManager().getCurrentLang());

    jComboBoxX.setSelectedIndex(jDiagram.getModel().isAxisXDistance() ? 0 : 1);

    // Evenement
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
    jComboBoxX.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jDiagram.getModel().setAxisX(jComboBoxX.getSelectedIndex() == 0);
      }
    });

    if (type == JDiagramOneComponent.SPEED) {
      jComboBoxY3.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          jDiagram.getModel().setVisibleY3(jComboBoxY3.getSelectedIndex());
        }
      });
    }
    else {
      jCheckBoxFilter.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          jDiagram.getModel().setFilter(jCheckBoxFilter.isSelected());
        }
      });
    }

  }

  /**
   * @return the jDiagram
   */
  public JDiagramOneComponent getJDiagram() {
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
      jPanelTitle.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
      if (type == JDiagramOneComponent.SPEED) {
        jPanelTitle.add(getJComboBoxY3());
        Dimension dim = new Dimension(50, 10);
        jPanelTitle.add(new Box.Filler(dim, dim, dim));
      }
      else {
        jPanelTitle.add(getJLabelTitle());
        Dimension dim = new Dimension(300, 10);
        jPanelTitle.add(new Box.Filler(dim, dim, dim));
        jPanelTitle.add(getJCheckBoxFilter());
        jPanelTitle.add(new JLabel("   "));
        }
      jPanelTitle.add(getJButtonZoomPlus());
      jPanelTitle.add(getJButtonZoomMoins());
      jPanelTitle.add(getJButtonReload());
      Dimension dim = new Dimension(JDiagramOneComponent.WIDTH_TITLE_2, 10);
      jPanelTitle.add(new Box.Filler(dim, dim, dim));
    }
    return jPanelTitle;
  }

  private JPanel getJPanelX() {
    if (jPanelX == null) {
      jPanelX = new JPanel();
      jPanelX.setLayout(new FlowLayout(FlowLayout.RIGHT));
      jPanelX.add(getJComboBoxX());
      Dimension dim = new Dimension(JDiagramComponent.WIDTH_TITLE_2 / 2, 20);
      jPanelX.add(getJComboBoxX());
      jPanelX.add(new Box.Filler(dim, dim, dim));
    }
    return jPanelX;
  }

  private JComboBox getJComboBoxY3() {
    if (jComboBoxY3 == null) {
      jComboBoxY3 = new JComboboxUIlistener();
      jComboBoxY3.setFont(GuiFont.FONT_PLAIN_VERY_SMALL);
      jComboBoxY3.setForeground(JDiagramComponent.COLORY3);
      jComboBoxY3.setRenderer(new MyDefaultListCellRenderer());
      jComboBoxY3.setOpaque(true);
      int width = (OperatingSystem.isMacOSX()) ? 125 : 115;
      jComboBoxY3.setPreferredSize(new Dimension(width, jComboBoxY3
          .getPreferredSize().height));
    }
    return jComboBoxY3;
  }

  private JLabel getJLabelTitle() {
    if (jLabelTitle == null) {
      jLabelTitle = new JLabel();
      jLabelTitle.setFont(GuiFont.FONT_PLAIN);
    }
    return jLabelTitle;
  }

  private JComboBox getJComboBoxX() {
    if (jComboBoxX == null) {
      jComboBoxX = new JComboboxUIlistener();
      jComboBoxX.setFont(GuiFont.FONT_PLAIN_VERY_SMALL);
      int width = (OperatingSystem.isMacOSX()) ? 120 : 110;
      jComboBoxX.setPreferredSize(new Dimension(width, jComboBoxX
          .getPreferredSize().height));
    }
    return jComboBoxX;
  }

  private class MyDefaultListCellRenderer extends DefaultListCellRenderer {

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax
     * .swing.JList, java.lang.Object, int, boolean, boolean)
     */
    @Override
    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
      JLabel renderer = (JLabel) super
          .getListCellRendererComponent(list,
                                        value,
                                        index,
                                        isSelected,
                                        cellHasFocus);
      renderer.setFont(GuiFont.FONT_PLAIN_VERY_SMALL);
      renderer.setForeground(JDiagramComponent.COLORY3);

      return renderer;
    }

  }

  /**
   * This method initializes jCheckBoxFilter
   * 
   * @return javax.swing.JCheckBox
   */
  private JCheckBox getJCheckBoxFilter() {
    if (jCheckBoxFilter == null) {
      jCheckBoxFilter = new JCheckBox();
      jCheckBoxFilter.setSelected(jDiagram.getModel().isFilter());
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

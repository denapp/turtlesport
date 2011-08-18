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
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.ComboBoxUI;

import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.img.diagram.ImagesDiagramRepository;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.PaceUnit;
import fr.turtlesport.unit.SpeedUnit;
import fr.turtlesport.unit.event.UnitEvent;
import fr.turtlesport.unit.event.UnitListener;
import fr.turtlesport.unit.event.UnitManager;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelGraph extends JPanel implements LanguageListener,
                                       UnitListener {

  private JDiagramComponent   jDiagram;

  private JPanel              jPanelTitle;

  private JCheckBox           jCheckBox1;

  private JCheckBox           jCheckBox2;

  private JCheckBox           jCheckBox3;

  private JCheckBox           jCheckBoxFilter;

  private JCheckBox           jCheckBoxTime;

  private JComboboxUIlistener jComboBoxY3;

  private JComboboxUIlistener jComboBoxX;

  private JButton             jButtonZoomMoins;

  private JButton             jButtonZoomPlus;

  private Border              raisedBorder  = BorderFactory
                                                .createLoweredBevelBorder();

  private Border              loweredBorder = BorderFactory
                                                .createRaisedBevelBorder();

  private JButton             jButtonReload;

  private JPanel              jPanelX;

  /**
   * 
   */
  public JPanelGraph() {
    super();
    initialize();
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

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.lang.LanguageListener#completedRemoveLanguageListener()
   */
  public void completedRemoveLanguageListener() {
    LanguageManager.getManager().removeLanguageListener(jDiagram);
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

    jComboBoxY3.removeAllItems();
    jComboBoxY3.addItem(rb.getString("speed") + "("
                        + SpeedUnit.getDefaultUnit() + ")");
    jComboBoxY3.addItem(rb.getString("allure") + "("
                        + PaceUnit.getDefaultUnit() + ")");

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
    if (e.isEventSpeed() || e.isEventSpeedAndPace() || e.isEventPace() || e.isEventDistance()) {
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

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.unit.event.UnitListener#completedRemoveUnitListener()
   */
  public void completedRemoveUnitListener() {
    UnitManager.getManager().removeUnitListener(jDiagram);
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    jDiagram = new JDiagramComponent();
    setSize(300, 230);
    setLayout(new BorderLayout(0, 0));
    add(jDiagram, BorderLayout.CENTER);
    add(getJPanelTitle(), BorderLayout.NORTH);
    add(getJPanelX(), BorderLayout.SOUTH);
    setOpaque(true);

    LanguageManager.getManager().addLanguageListener(this);
    performedLanguage(LanguageManager.getManager().getCurrentLang());
    UnitManager.getManager().addUnitListener(this);

    if (jDiagram.getModel().isVisibleY3()) {
      jComboBoxY3
          .setSelectedIndex(jDiagram.getModel().isVisibleSpeed() ? 0 : 1);
    }
    jComboBoxX.setSelectedIndex(jDiagram.getModel().isAxisXDistance() ? 0 : 1);

    // Evenement
    jCheckBox1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jDiagram.getModel().setVisibleY1(jCheckBox1.isSelected());
      }
    });
    jCheckBox2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jDiagram.getModel().setVisibleY2(jCheckBox2.isSelected());
      }
    });
    jCheckBox3.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int value = (jCheckBox3.isSelected()) ? jComboBoxY3.getSelectedIndex()
            : -1;
        jDiagram.getModel().setVisibleY3(value);
      }
    });
    jCheckBoxTime.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jDiagram.getModel().setVisibleTime(jCheckBoxTime.isSelected());
      }
    });
    jCheckBoxFilter.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jDiagram.getModel().setFilter(jCheckBoxFilter.isSelected());
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

    jComboBoxY3.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jDiagram.getModel().setVisibleY3(jComboBoxY3.getSelectedIndex());
      }
    });
    jComboBoxX.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jDiagram.getModel().setAxisX(jComboBoxX.getSelectedIndex() == 0);
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
      jPanelTitle.add(getJCheckBox3());
      jPanelTitle.add(getJComboBoxY3());
      jPanelTitle.add(getJCheckBoxTime());
      jPanelTitle.add(getJCheckBoxFilter());
      jPanelTitle.add(new JLabel("   "));
      jPanelTitle.add(getJButtonZoomPlus());
      jPanelTitle.add(getJButtonZoomMoins());
      jPanelTitle.add(getJButtonReload());
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

  /**
   * This method initializes jCheckBox
   * 
   * @return javax.swing.JCheckBox
   */
  private JCheckBox getJCheckBox1() {
    if (jCheckBox1 == null) {
      jCheckBox1 = new JCheckBox();
      jCheckBox1.setFont(GuiFont.FONT_PLAIN_SMALL);
      jCheckBox1.setSelected(jDiagram.getModel().isVisibleY1());
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
      jCheckBox2.setSelected(jDiagram.getModel().isVisibleY2());
      jCheckBox2.setFont(GuiFont.FONT_PLAIN_SMALL);
    }
    return jCheckBox2;
  }

  private JCheckBox getJCheckBox3() {
    if (jCheckBox3 == null) {
      jCheckBox3 = new JCheckBox();
      jCheckBox3.setSelected(jDiagram.getModel().isVisibleY3());
      jCheckBox3.setFont(GuiFont.FONT_PLAIN_SMALL);
    }
    return jCheckBox3;
  }

  private JComboBox getJComboBoxY3() {
    if (jComboBoxY3 == null) {
      jComboBoxY3 = new JComboboxUIlistener();
      jComboBoxY3.setFont(GuiFont.FONT_PLAIN_VERY_SMALL);
      jComboBoxY3.setForeground(JDiagramComponent.COLORY3);
      jComboBoxY3.setRenderer(new MyDefaultListCellRenderer());
      jComboBoxY3.setOpaque(true);
      jComboBoxY3.setPreferredSize(new Dimension(110, jComboBoxY3
          .getPreferredSize().height));
    }
    return jComboBoxY3;
  }

  private JComboBox getJComboBoxX() {
    if (jComboBoxX == null) {
      jComboBoxX = new JComboboxUIlistener();
      jComboBoxX.setFont(GuiFont.FONT_PLAIN_VERY_SMALL);
      jComboBoxX.setPreferredSize(new Dimension(110, jComboBoxX
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
   * This method initializes jCheckBox
   * 
   * @return javax.swing.JCheckBox
   */
  private JCheckBox getJCheckBoxTime() {
    if (jCheckBoxTime == null) {
      jCheckBoxTime = new JCheckBox();
      jCheckBoxTime.setSelected(jDiagram.getModel().isVisibleTime());
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

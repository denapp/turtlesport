package fr.turtlesport.ui.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.component.JTextAreaLength;
import fr.turtlesport.ui.swing.component.JTurtleMapKit;
import fr.turtlesport.ui.swing.component.JTurtleMapKit.JCheckBoxMenuItemMap;
import fr.turtlesport.ui.swing.img.ImagesRepository;
import fr.turtlesport.ui.swing.model.ModelDialogMap;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JDialogMap extends JDialog {

  private JTurtleMapKit   jPanelMap;

  private JLabel          jLabelTitle;

  private JPanel          jPanelRunLap;

  private ResourceBundle  rb;

  private JLabel          jLabelLibDayLap;

  private JLabel          jLabelValDayLap;

  private JLabel          jLabelLibHourLap;

  private JLabel          jLabelValHourLap;

  private JLabel          jLabelLibDistanceLap;

  private JLabel          jLabelValDistanceLap;

  private JLabel          jLabelValTimeLap;

  private JLabel          jLabelLibTimeLap;

  private JLabel          jLabelLibPaceLap;

  private JLabel          jLabelValPaceLap;

  private JLabel          jLabelLibSpeedLap;

  private JLabel          jLabelValSpeedLap;

  private JLabel          jLabelLibCaloriesLap;

  private JLabel          jLabelValCaloriesLap;

  private JLabel          jLabelValAltitudeLap;

  private JLabel          jLabelLibHeartLap;

  private JLabel          jLabelValHeartLap;

  private JPanel          jPanelRunSummary;

  private TitledBorder    borderPanelRunSummary;

  private JLabel          jLabelLibDistTot;

  private JLabel          jLabelValDistTot;

  private JLabel          jLabelLibTimeTot;

  private JLabel          jLabelValTimeTot;

  private JLabel          jLabelLibAllureTot;

  private JLabel          jLabelValAllureTot;

  private JLabel          jLabelLibSpeedMoyTot;

  private JLabel          jLabelValSpeedMoyTot;

  private JLabel          jLabelLibCaloriesTot;

  private JLabel          jLabelValCaloriesTot;

  private JLabel          jLabelLibHeartTot;

  private JLabel          jLabelValHeartTot;

  private JLabel          jLabelLibCategory;

  private JLabel          jLabelValCategory;

  private JLabel          jLabelLibEquipment;

  private JLabel          jLabelValEquipment;

  private JLabel          jLabelLibNotes;

  private JLabel          jLabelLibAltitudeTot;

  private JLabel          jLabelValAltitudeTot;

  private JTextAreaLength jTextFieldNotes;

  private JScrollPane     jScrollPaneTextArea;

  private JPanel          jPanelLeft;

  private JLabel          jLabelLibAltitudeLap;

  private JLabel          jLabelLibLap;

  private JComboBox       jComboBoxLap;

  private JTurtleMapKit   owner;

  // Model
  private ModelDialogMap  model;

  /**
   * @param owner
   * @param modal
   * @param isMap
   */
  public JDialogMap(Frame frame, JTurtleMapKit owner) {
    super(frame, true);
    this.owner = owner;
    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), getClass());

    initialize();
  }

  @Override
  public void dispose() {
    super.dispose();
    owner = null;
  }

  public static void prompt(JTurtleMapKit mapKit, DataRun data) {
    // mis a jour du model et affichage de l'IHM
    JDialogMap view = new JDialogMap(MainGui.getWindow(), mapKit);

    ModelDialogMap model = new ModelDialogMap(mapKit.getModelMap().getListGeo(),
                                              data);
    try {
      model.updateView(view);
      view.model = model;
    }
    catch (SQLException e) {
      JShowMessage.error(view.rb.getString("errorDatabase"));
      return;
    }

    view.jPanelMap.getMainMap().setCenterPosition(mapKit.getMainMap()
        .getCenterPosition());
    int zoom = mapKit.getMainMap().getZoom();
    if (zoom != 1) {
      zoom--;
    }
    view.jPanelMap.getMainMap().setZoom(zoom);

    //original zoom et position
     zoom = mapKit.getOriginalZoom();
    if (zoom != 1) {
      zoom--;
    }
    view.jPanelMap.setOriginalZoom(zoom);
    view.jPanelMap.setOriginalPosition(mapKit.getOriginalPosition());

    view.pack();
    view.setLocationRelativeTo(MainGui.getWindow());
    view.setVisible(true);
  }

  public JLabel getJLabelTitle() {
    return jLabelTitle;
  }

  public JLabel getJLabelValTimeTot() {
    return jLabelValTimeTot;
  }

  public JLabel getJLabelValCaloriesTot() {
    return jLabelValCaloriesTot;
  }

  public JLabel getJLabelValHeartTot() {
    return jLabelValHeartTot;
  }

  public JLabel getJLabelValAltitudeTot() {
    return jLabelValAltitudeTot;
  }

  public JLabel getJLabelValDistTot() {
    return jLabelValDistTot;
  }

  public JLabel getJLabelValAllureTot() {
    return jLabelValAllureTot;
  }

  public JLabel getJLabelValSpeedMoyTot() {
    return jLabelValSpeedMoyTot;
  }

  public JLabel getJLabelValEquipment() {
    return jLabelValEquipment;
  }

  public JTextAreaLength getJTextFieldNotes() {
    return jTextFieldNotes;
  }

  public JLabel getJLabelValCategory() {
    return jLabelValCategory;
  }

  public JComboBox getJComboBoxLap() {
    return jComboBoxLap;
  }

  public JLabel getJLabelValDayLap() {
    return jLabelValDayLap;
  }

  public JLabel getJLabelValHourLap() {
    return jLabelValHourLap;
  }

  public JLabel getJLabelValDistanceLap() {
    return jLabelValDistanceLap;
  }

  public JLabel getJLabelValTimeLap() {
    return jLabelValTimeLap;
  }

  public JLabel getJLabelValPaceLap() {
    return jLabelValPaceLap;
  }

  public JLabel getJLabelValSpeedLap() {
    return jLabelValSpeedLap;
  }

  public JLabel getJLabelValCaloriesLap() {
    return jLabelValCaloriesLap;
  }

  public JLabel getJLabelValAltitudeLap() {
    return jLabelValAltitudeLap;
  }

  public JLabel getJLabelValHeartLap() {
    return jLabelValHeartLap;
  }

  private void initialize() {
    ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
        .getManager().getCurrentLang(), getClass());

    jLabelTitle = new JLabel();
    jLabelTitle.setHorizontalAlignment(SwingConstants.CENTER);
    jLabelTitle.setFont(GuiFont.FONT_PLAIN);

    JPanel contentPane = new JPanel();
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
    contentPane.add(getJPanelMap());
    contentPane.add(Box.createRigidArea(new Dimension(5, 0)));
    contentPane.add(getJPanelLeft());

    this.setContentPane(contentPane);
    this.setTitle(rb.getString("title"));

    // Evenement
    jComboBoxLap.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (model != null) {
          model.updateViewLap(JDialogMap.this, ((JComboBox) e.getSource())
              .getSelectedItem());
        }
      }

    });

    JPopupMenu popupMenu = jPanelMap.getJXSplitButtonMap().getDropDownMenu();
    for (int i = 0; i < popupMenu.getComponentCount(); i++) {
      Component c = popupMenu.getComponent(i);
      if (c instanceof JCheckBoxMenuItemMap) {
        final JCheckBoxMenuItemMap mi = ((JCheckBoxMenuItemMap) c);
        mi.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            owner.setTileFactory(mi.getTileFactory());
          }
        });
      }
    }
  }

  private JPanel getJPanelLeft() {
    if (jPanelLeft == null) {
      jPanelLeft = new JPanel();
      Dimension dim = new Dimension(260, 600);
      jPanelLeft.setPreferredSize(dim);
      jPanelLeft.setLayout(new BoxLayout(jPanelLeft, BoxLayout.Y_AXIS));
      jPanelLeft.add(getJPanelRunSummary());
      jPanelLeft.add(Box.createRigidArea(new Dimension(5, 0)));
      jPanelLeft.add(getJPanelRunLap());
    }
    return jPanelLeft;
  }

  public JTurtleMapKit getJPanelMap() {
    if (jPanelMap == null) {
      jPanelMap = new JTurtleMapKit(false);
      jPanelMap.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      jPanelMap.setBorder(BorderFactory
          .createTitledBorder(null,
                              "",
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null));
      Dimension dim = new Dimension(600, 600);
      jPanelMap.setPreferredSize(dim);
    }
    return jPanelMap;
  }

  /**
   * This method initializes jPanelRunSummary.
   * 
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelRunSummary() {
    if (jPanelRunSummary == null) {
      jPanelRunSummary = new JPanel();
      jPanelRunSummary.setLayout(new GridBagLayout());
      borderPanelRunSummary = BorderFactory
          .createTitledBorder(null,
                              "Course",
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null);
      jPanelRunSummary.setBorder(borderPanelRunSummary);
      jPanelRunSummary.setPreferredSize(new Dimension(260, 300));

      Insets insets = new Insets(0, 0, 5, 10);
      GridBagConstraints g = new GridBagConstraints();

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibDistTot = new JLabel(rb.getString("jLabelLibDistTot"));
      jLabelLibDistTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibDistTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibDistTot, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValDistTot = new JLabel();
      jLabelValDistTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibDistTot.setLabelFor(jLabelValDistTot);
      jPanelRunSummary.add(jLabelValDistTot, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibTimeTot = new JLabel(rb.getString("jLabelLibTimeTot"));
      jLabelLibTimeTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibTimeTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibTimeTot, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValTimeTot = new JLabel();
      jLabelValTimeTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibTimeTot.setLabelFor(jLabelValTimeTot);
      jPanelRunSummary.add(jLabelValTimeTot, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibAllureTot = new JLabel(rb.getString("jLabelLibAllure"));
      jLabelLibAllureTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibAllureTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibAllureTot, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValAllureTot = new JLabel();
      jLabelValAllureTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibAllureTot.setLabelFor(jLabelValAllureTot);
      jPanelRunSummary.add(jLabelValAllureTot, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibSpeedMoyTot = new JLabel(rb.getString("jLabelLibSpeedMoy"));
      jLabelLibSpeedMoyTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibSpeedMoyTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibSpeedMoyTot, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValSpeedMoyTot = new JLabel();
      jLabelValSpeedMoyTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibSpeedMoyTot.setLabelFor(jLabelValSpeedMoyTot);
      jPanelRunSummary.add(jLabelValSpeedMoyTot, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibCaloriesTot = new JLabel(rb.getString("jLabelLibCaloriesTot"));
      jLabelLibCaloriesTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibCaloriesTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibCaloriesTot, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValCaloriesTot = new JLabel();
      jLabelValCaloriesTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibCaloriesTot.setLabelFor(jLabelValCaloriesTot);
      jPanelRunSummary.add(jLabelValCaloriesTot, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibHeartTot = new JLabel();
      jLabelLibHeartTot.setIcon(ImagesRepository.getImageIcon("heart.gif"));
      jLabelLibHeartTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibHeartTot.setText(rb.getString("jLabelLibHeartTot"));
      jLabelLibHeartTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibHeartTot, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValHeartTot = new JLabel();
      jLabelValHeartTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibHeartTot.setLabelFor(jLabelValHeartTot);
      jPanelRunSummary.add(jLabelValHeartTot, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibAltitudeTot = new JLabel();
      jLabelLibAltitudeTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibAltitudeTot.setText(rb.getString("jLabelLibAltitudeTot"));
      jLabelLibAltitudeTot.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibAltitudeTot, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValAltitudeTot = new JLabel();
      jLabelValAltitudeTot.setFont(GuiFont.FONT_PLAIN);
      jLabelLibAltitudeTot.setLabelFor(jLabelValAltitudeTot);
      jPanelRunSummary.add(jLabelValAltitudeTot, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibCategory = new JLabel("Categorie :");
      jLabelLibCategory.setFont(GuiFont.FONT_PLAIN);
      jLabelLibCategory.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibCategory, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValCategory = new JLabel();
      jLabelValCategory.setFont(GuiFont.FONT_PLAIN);
      jLabelLibCategory.setLabelFor(jLabelValCategory);
      jPanelRunSummary.add(jLabelValCategory, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibEquipment = new JLabel("Equipement :");
      jLabelLibEquipment.setFont(GuiFont.FONT_PLAIN);
      jLabelLibEquipment.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunSummary.add(jLabelLibEquipment, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValEquipment = new JLabel();
      jLabelValEquipment.setFont(GuiFont.FONT_PLAIN);
      jLabelValCategory.setLabelFor(jLabelValEquipment);
      jPanelRunSummary.add(jLabelValEquipment, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibNotes = new JLabel("Notes :");
      jLabelLibNotes.setFont(GuiFont.FONT_PLAIN);
      jLabelLibNotes.setHorizontalAlignment(SwingConstants.TRAILING);
      jLabelLibNotes.setVerticalAlignment(SwingConstants.TOP);
      jPanelRunSummary.add(jLabelLibNotes, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.gridheight = 3;
      g.anchor = GridBagConstraints.NORTHWEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelLibNotes.setLabelFor(getJScrollPaneTextArea());
      jPanelRunSummary.add(getJScrollPaneTextArea(), g);

    }
    return jPanelRunSummary;
  }

  /**
   * This method initializes jScrollPaneTextArea.
   * 
   * @return javax.swing.JTextField
   */
  private JScrollPane getJScrollPaneTextArea() {
    if (jScrollPaneTextArea == null) {
      jTextFieldNotes = new JTextAreaLength(5, 20);
      jTextFieldNotes.setMaxiMumCharacters(100);
      jTextFieldNotes.setFont(GuiFont.FONT_PLAIN);
      jTextFieldNotes.setWrapStyleWord(true);
      jTextFieldNotes.setLineWrap(true);
      jScrollPaneTextArea = new JScrollPane(jTextFieldNotes);
      jScrollPaneTextArea
          .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      jScrollPaneTextArea
          .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    }
    return jScrollPaneTextArea;
  }

  /**
   * This method initializes jPanelRunLap.
   * 
   * @return javax.swing.JPanel
   */
  public JPanel getJPanelRunLap() {
    if (jPanelRunLap == null) {
      jPanelRunLap = new JPanel();
      jPanelRunLap.setOpaque(true);

      TitledBorder borderPanelRunLap = BorderFactory
          .createTitledBorder(null,
                              rb.getString("borderPanelRunLap"),
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null);
      jPanelRunLap.setBorder(borderPanelRunLap);
      jPanelRunLap.setPreferredSize(new Dimension(260, 300));
      jPanelRunLap.setLayout(new GridBagLayout());

      Insets insets = new Insets(0, 0, 5, 10);
      GridBagConstraints g = new GridBagConstraints();

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibLap = new JLabel();
      jLabelLibLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibLap.setText(rb.getString("jLabelLibLap"));
      jLabelLibLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jComboBoxLap = new JComboBox();
      jComboBoxLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibLap.setLabelFor(jComboBoxLap);
      jPanelRunLap.add(jComboBoxLap, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibDayLap = new JLabel();
      jLabelLibDayLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibDayLap.setText(rb.getString("JLabelLibDayLap"));
      jLabelLibDayLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibDayLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValDayLap = new JLabel();
      jLabelValDayLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibDayLap.setLabelFor(jLabelValDayLap);
      jPanelRunLap.add(jLabelValDayLap, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibHourLap = new JLabel();
      jLabelLibHourLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibHourLap.setText(rb.getString("jLabelLibHourLap"));
      jLabelLibHourLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibHourLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValHourLap = new JLabel();
      jLabelValHourLap.setFont(GuiFont.FONT_PLAIN);
      jLabelValHourLap.setLabelFor(jLabelLibDayLap);
      jLabelLibHourLap.setLabelFor(jLabelValHourLap);
      jPanelRunLap.add(jLabelValHourLap, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibDistanceLap = new JLabel();
      jLabelLibDistanceLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibDistanceLap.setText(rb.getString("jLabelLibDistanceLap"));
      jLabelLibDistanceLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibDistanceLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValDistanceLap = new JLabel();
      jLabelValDistanceLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibDistanceLap.setLabelFor(jLabelValDistanceLap);
      jPanelRunLap.add(jLabelValDistanceLap, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibTimeLap = new JLabel();
      jLabelLibTimeLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibTimeLap.setText(rb.getString("jLabelLibTimeLap"));
      jLabelLibTimeLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibTimeLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValTimeLap = new JLabel();
      jLabelValTimeLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibTimeLap.setLabelFor(jLabelValTimeLap);
      jPanelRunLap.add(jLabelValTimeLap, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibPaceLap = new JLabel();
      jLabelLibPaceLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibPaceLap.setText(rb.getString("jLabelLibPaceLap"));
      jLabelLibPaceLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibPaceLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValPaceLap = new JLabel();
      jLabelValPaceLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibPaceLap.setLabelFor(jLabelValPaceLap);
      jPanelRunLap.add(jLabelValPaceLap, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibSpeedLap = new JLabel();
      jLabelLibSpeedLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibSpeedLap.setText(rb.getString("jLabelLibSpeedLap"));
      jLabelLibSpeedLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibSpeedLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValSpeedLap = new JLabel();
      jLabelValSpeedLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibSpeedLap.setLabelFor(jLabelValSpeedLap);
      jPanelRunLap.add(jLabelValSpeedLap, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibHeartLap = new JLabel();
      jLabelLibHeartLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibHeartLap.setIcon(ImagesRepository.getImageIcon("heart.gif"));
      jLabelLibHeartLap.setText(rb.getString("jLabelLibHeartLap"));
      jLabelLibHeartLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibHeartLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValHeartLap = new JLabel();
      jLabelValHeartLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibHeartLap.setLabelFor(jLabelValHeartLap);
      jPanelRunLap.add(jLabelValHeartLap, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibCaloriesLap = new JLabel();
      jLabelLibCaloriesLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibCaloriesLap.setText(rb.getString("jLabelLibCaloriesLap"));
      jLabelLibCaloriesLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jPanelRunLap.add(jLabelLibCaloriesLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValCaloriesLap = new JLabel();
      jLabelValCaloriesLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibCaloriesLap.setLabelFor(jLabelValCaloriesLap);
      jPanelRunLap.add(jLabelValCaloriesLap, g);

      g = new GridBagConstraints();
      g.weightx = 0.0;
      g.anchor = GridBagConstraints.EAST;
      g.fill = GridBagConstraints.BOTH;
      g.insets = insets;
      jLabelLibAltitudeLap = new JLabel();
      jLabelLibAltitudeLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibAltitudeLap.setText(rb.getString("jLabelLibAltitudeLap"));
      jLabelLibAltitudeLap.setHorizontalAlignment(SwingConstants.TRAILING);
      jLabelLibAltitudeLap.setVerticalAlignment(SwingConstants.TOP);
      jPanelRunLap.add(jLabelLibAltitudeLap, g);
      g = new GridBagConstraints();
      g.weightx = 1.0;
      g.weighty = 1.0;
      g.anchor = GridBagConstraints.WEST;
      g.fill = GridBagConstraints.BOTH;
      g.gridwidth = GridBagConstraints.REMAINDER;
      g.insets = insets;
      jLabelValAltitudeLap = new JLabel();
      jLabelValAltitudeLap.setVerticalAlignment(SwingConstants.TOP);
      jLabelValAltitudeLap.setFont(GuiFont.FONT_PLAIN);
      jLabelLibAltitudeLap.setLabelFor(jLabelValAltitudeLap);
      jPanelRunLap.add(jLabelValAltitudeLap, g);
    }
    return jPanelRunLap;
  }

}
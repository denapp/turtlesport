package fr.turtlesport.ui.swing;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import fr.turtlesport.Configuration;
import fr.turtlesport.db.EquipementTableManager;
import fr.turtlesport.db.RunTableManager;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.ui.swing.component.JTextFieldTime;
import fr.turtlesport.ui.swing.component.JXDatePickerLocale;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.util.Location;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelSearch extends JPanel {

  private JLabel                  jLabelDistance;

  private JFormattedTextField     jTextFielDistanceMin;

  private JFormattedTextField     jTextFielDistanceMax;

  private JLabel                  jLabelDistanceTo;

  private JLabel                  jLabelDuration;

  private JTextFieldTime          jTextFielDurationMin;

  private JLabel                  jLabelDurationTo;

  private JTextFieldTime          jTextFielDurationMax;

  private JLabel                  jLabelLibEquipment;

  private JComboBox               jComboBoxEquipment;

  // Model
  private EquipementComboBoxModel modelEquipements;

  private JLabel                  jLabelLibLocation;

  private JComboBox               jComboBoxLocation;

  private JLabel                  jLabelLibPeriod;

  private JXDatePickerLocale      jXDatePickerMin;

  private JLabel                  jLabelPeriodTo;

  private JXDatePickerLocale      jXDatePickerMax;

  private JComboBox               jComboBoxDistanceUnits;

  private LocationComboBoxModel modelLocations;

  /**
   * Create the panel.
   * 
   * @throws SQLException
   */
  public JPanelSearch() throws SQLException {
    super();
    modelEquipements = new EquipementComboBoxModel();
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] { 100, 75, 20, 75, 70 };
    gridBagLayout.rowHeights = new int[] { 30, 30, 30, 30, 30 };
    gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
    gridBagLayout.rowWeights = new double[] { 0.0, 0.0 };
    setLayout(gridBagLayout);

    Insets insets = new Insets(0, 0, 5, 5);

    // Row 1
    jLabelDistance = new JLabel("Distance :");
    jLabelDistance.setFont(GuiFont.FONT_PLAIN);
    jLabelDistance.setHorizontalAlignment(SwingConstants.RIGHT);
    GridBagConstraints gbc_jLabelDistance = new GridBagConstraints();
    gbc_jLabelDistance.insets = insets;
    gbc_jLabelDistance.gridx = 0;
    gbc_jLabelDistance.gridy = 0;
    gbc_jLabelDistance.fill = GridBagConstraints.EAST;
    add(jLabelDistance, gbc_jLabelDistance);

    jTextFielDistanceMin = new JFormattedTextField();
    jTextFielDistanceMin.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jTextFielDistanceMin = new GridBagConstraints();
    gbc_jTextFielDistanceMin.insets = insets;
    gbc_jTextFielDistanceMin.gridx = 1;
    gbc_jTextFielDistanceMin.gridy = 0;
    gbc_jTextFielDistanceMin.fill = GridBagConstraints.HORIZONTAL;
    add(jTextFielDistanceMin, gbc_jTextFielDistanceMin);

    jLabelDistanceTo = new JLabel("à");
    jLabelDistanceTo.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jLabelDistanceTo = new GridBagConstraints();
    gbc_jLabelDistanceTo.insets = insets;
    gbc_jLabelDistanceTo.gridx = 2;
    gbc_jLabelDistanceTo.gridy = 0;
    gbc_jLabelDistanceTo.fill = GridBagConstraints.CENTER;
    add(jLabelDistanceTo, gbc_jLabelDistanceTo);

    jTextFielDistanceMax = new JFormattedTextField();
    jTextFielDistanceMax.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jTextFielDistanceMax = new GridBagConstraints();
    gbc_jTextFielDistanceMax.insets = insets;
    gbc_jTextFielDistanceMax.gridx = 3;
    gbc_jTextFielDistanceMax.gridy = 0;
    gbc_jTextFielDistanceMax.fill = GridBagConstraints.HORIZONTAL;
    add(jTextFielDistanceMax, gbc_jTextFielDistanceMax);

    jComboBoxDistanceUnits = new JComboBox();
    jComboBoxDistanceUnits.setFont(GuiFont.FONT_PLAIN);
    jComboBoxDistanceUnits.setModel(new DefaultComboBoxModel(DistanceUnit
        .units()));
    jComboBoxDistanceUnits.setSelectedItem(DistanceUnit.getDefaultUnit());
    GridBagConstraints gbc_jComboBoxDistanceUnits = new GridBagConstraints();
    gbc_jComboBoxDistanceUnits.insets = insets;
    gbc_jComboBoxDistanceUnits.gridx = 4;
    gbc_jComboBoxDistanceUnits.gridy = 0;
    gbc_jComboBoxDistanceUnits.fill = GridBagConstraints.HORIZONTAL;
    add(jComboBoxDistanceUnits, gbc_jComboBoxDistanceUnits);

    // Row 2
    jLabelDuration = new JLabel("Durée :");
    jLabelDuration.setFont(GuiFont.FONT_PLAIN);
    jLabelDuration.setHorizontalAlignment(SwingConstants.RIGHT);
    GridBagConstraints gbc_jLabelDuration = new GridBagConstraints();
    gbc_jLabelDuration.insets = insets;
    gbc_jLabelDuration.gridx = 0;
    gbc_jLabelDuration.gridy = 1;
    gbc_jLabelDuration.fill = GridBagConstraints.EAST;
    add(jLabelDuration, gbc_jLabelDuration);

    jTextFielDurationMin = new JTextFieldTime();
    jTextFielDurationMin.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jTextFielDurationMin = new GridBagConstraints();
    gbc_jTextFielDurationMin.insets = insets;
    gbc_jTextFielDurationMin.gridx = 1;
    gbc_jTextFielDurationMin.gridy = 1;
    gbc_jTextFielDurationMin.fill = GridBagConstraints.HORIZONTAL;
    add(jTextFielDurationMin, gbc_jTextFielDurationMin);

    jLabelDurationTo = new JLabel("à");
    jLabelDurationTo.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jLabelDurationTo = new GridBagConstraints();
    gbc_jLabelDurationTo.insets = insets;
    gbc_jLabelDurationTo.gridx = 2;
    gbc_jLabelDurationTo.gridy = 1;
    gbc_jLabelDistanceTo.fill = GridBagConstraints.CENTER;
    add(jLabelDurationTo, gbc_jLabelDurationTo);

    jTextFielDurationMax = new JTextFieldTime();
    jTextFielDurationMax.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jTextFielDurationMax = new GridBagConstraints();
    gbc_jTextFielDurationMax.insets = insets;
    gbc_jTextFielDurationMax.gridx = 3;
    gbc_jTextFielDurationMax.gridy = 1;
    gbc_jTextFielDurationMax.fill = GridBagConstraints.HORIZONTAL;
    add(jTextFielDurationMax, gbc_jTextFielDurationMax);

    // Row 3
    jLabelLibEquipment = new JLabel("Equipement :");
    jLabelLibEquipment.setFont(GuiFont.FONT_PLAIN);
    jLabelLibEquipment.setHorizontalAlignment(SwingConstants.TRAILING);
    GridBagConstraints gbc_jLabelLibEquipment = new GridBagConstraints();
    gbc_jLabelLibEquipment.insets = insets;
    gbc_jLabelLibEquipment.gridx = 0;
    gbc_jLabelLibEquipment.gridy = 2;
    gbc_jLabelLibEquipment.fill = GridBagConstraints.EAST;
    add(jLabelLibEquipment, gbc_jLabelLibEquipment);

    jComboBoxEquipment = new JComboBox(modelEquipements);
    jComboBoxEquipment.setFont(GuiFont.FONT_PLAIN);
    jLabelLibEquipment.setLabelFor(jComboBoxEquipment);
    modelEquipements.setDefaultSelectedItem();
    GridBagConstraints gbc_jComboBoxEquipment = new GridBagConstraints();
    gbc_jComboBoxEquipment.insets = insets;
    gbc_jComboBoxEquipment.gridx = 1;
    gbc_jComboBoxEquipment.gridy = 2;
    gbc_jComboBoxEquipment.gridwidth = 3;
    gbc_jComboBoxEquipment.anchor = GridBagConstraints.WEST;
    gbc_jComboBoxEquipment.fill = GridBagConstraints.BOTH;
    add(jComboBoxEquipment, gbc_jComboBoxEquipment);

    // Row 4
    jLabelLibLocation = new JLabel("Location :");
    jLabelLibLocation.setFont(GuiFont.FONT_PLAIN);
    jLabelLibLocation.setHorizontalAlignment(SwingConstants.TRAILING);
    GridBagConstraints gbc_jLabelLibLocation = new GridBagConstraints();
    gbc_jLabelLibLocation.insets = insets;
    gbc_jLabelLibLocation.gridx = 0;
    gbc_jLabelLibLocation.gridy = 3;
    gbc_jLabelLibLocation.fill = GridBagConstraints.EAST;
    add(jLabelLibLocation, gbc_jLabelLibLocation);

    modelLocations = new LocationComboBoxModel();
    jComboBoxLocation = new JComboBox(modelLocations);
    jComboBoxLocation.setFont(GuiFont.FONT_PLAIN);
    jLabelLibLocation.setLabelFor(jComboBoxLocation);
    GridBagConstraints gbc_jComboBoxLocation = new GridBagConstraints();
    gbc_jComboBoxLocation.insets = insets;
    gbc_jComboBoxLocation.gridx = 1;
    gbc_jComboBoxLocation.gridy = 3;
    gbc_jComboBoxLocation.gridwidth = 3;
    gbc_jComboBoxLocation.anchor = GridBagConstraints.WEST;
    gbc_jComboBoxLocation.fill = GridBagConstraints.BOTH;
    add(jComboBoxLocation, gbc_jComboBoxLocation);

    // Row 5
    jLabelLibPeriod = new JLabel("Periode :");
    jLabelLibPeriod.setFont(GuiFont.FONT_PLAIN);
    jLabelLibPeriod.setHorizontalAlignment(SwingConstants.TRAILING);
    GridBagConstraints gbc_jLabelLibPeriod = new GridBagConstraints();
    gbc_jLabelLibPeriod.insets = insets;
    gbc_jLabelLibPeriod.gridx = 0;
    gbc_jLabelLibPeriod.gridy = 4;
    gbc_jLabelLibPeriod.fill = GridBagConstraints.EAST;
    add(jLabelLibPeriod, gbc_jLabelLibPeriod);

    jXDatePickerMin = new JXDatePickerLocale();
    jXDatePickerMin.setLanguage(LanguageManager.getManager().getCurrentLang());
    jXDatePickerMin.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jXDatePickerMin = new GridBagConstraints();
    gbc_jXDatePickerMin.insets = insets;
    gbc_jXDatePickerMin.gridx = 1;
    gbc_jXDatePickerMin.gridy = 4;
    gbc_jXDatePickerMin.fill = GridBagConstraints.EAST;
    jLabelLibPeriod.setLabelFor(jXDatePickerMin);
    add(jXDatePickerMin, gbc_jXDatePickerMin);

    jLabelPeriodTo = new JLabel("à");
    jLabelPeriodTo.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jLabelPeriodTo = new GridBagConstraints();
    gbc_jLabelPeriodTo.insets = insets;
    gbc_jLabelPeriodTo.gridx = 2;
    gbc_jLabelPeriodTo.gridy = 4;
    gbc_jLabelPeriodTo.fill = GridBagConstraints.CENTER;
    add(jLabelPeriodTo, gbc_jLabelPeriodTo);

    jXDatePickerMax = new JXDatePickerLocale();
    jXDatePickerMax.setLanguage(LanguageManager.getManager().getCurrentLang());
    jXDatePickerMax.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jXDatePickerMax = new GridBagConstraints();
    gbc_jXDatePickerMax.insets = insets;
    gbc_jXDatePickerMax.gridx = 3;
    gbc_jXDatePickerMax.gridy = 4;
    gbc_jXDatePickerMax.fill = GridBagConstraints.EAST;
    jLabelPeriodTo.setLabelFor(jXDatePickerMax);
    add(jXDatePickerMax, gbc_jXDatePickerMax);
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class EquipementComboBoxModel extends DefaultComboBoxModel {
    private String defaultEquipement;

    public EquipementComboBoxModel() throws SQLException {
      super();
      addElement("");

      List<String> list = EquipementTableManager.getInstance().retreiveNames();
      for (String d : list) {
        addElement(d);
      }
      defaultEquipement = EquipementTableManager.getInstance()
          .retreiveNameDefault();
    }

    public void setDefaultSelectedItem() {
      if (defaultEquipement != null) {
        setSelectedItem(defaultEquipement);
      }
    }

    public String getDefaultEquipement() {
      return defaultEquipement;
    }
  }
  
  /**
   * @author Denis Apparicio
   * 
   */
  public class LocationComboBoxModel extends DefaultComboBoxModel {
    public LocationComboBoxModel() {
      super();
      fill();
    }

    public void fill() {
      removeAllElements();
      addElement("");
      try {
        List<String> list = RunTableManager.getInstance()
            .retreiveLocations(MainGui.getWindow().getCurrentIdUser());
        for (String d : list) {
          if (d != null && d.trim().length() > 0) {
            addElement(d.trim());
          }
        }
      }
      catch (SQLException e) {
      }
    }

    public void setSelectedLocation(String location) {
      setSelectedItem((location == null) ? "" : location);
    }

    public boolean contains(Object value) {
      for (int i = 0; i < getSize(); i++) {
        if (getElementAt(i).equals(value)) {
          return true;
        }
      }
      return false;
    }
  }

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Location.initialize();
          Configuration.initialize();
          JFrame f = new JFrame();
          f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          f.setBounds(100, 100, 450, 300);
          JPanel contentPane = new JPanelSearch();
          contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
          f.setContentPane(contentPane);

          f.setVisible(true);
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
}

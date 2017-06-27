package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.turtlesport.Configuration;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.ui.swing.component.PanelPrefListener;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.HeightUnit;
import fr.turtlesport.unit.SpeedPaceUnit;
import fr.turtlesport.unit.TemperatureUnit;
import fr.turtlesport.unit.WeightUnit;
import fr.turtlesport.unit.event.UnitManager;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelPrefUnits extends JPanelPref implements PanelPrefListener {

  private JPanelPrefTitle jPanelTitle;

  private JPanel          jPanelCenter;

  private JLabel          jLabelLibDistance;

  private JComboBox       jComboBoxDistance;

  private JLabel          jLabelLibSpeed;

  private JComboBox       jComboBoxSpeed;

  private JLabel          jLabelLibWeight;

  private JComboBox       jComboBoxWeight;

  private JLabel          jLabelLibHeight;

  private JComboBox       jComboBoxHeight;

  private JLabel          jLabelLibTemperature;

  private JComboBox       jComboBoxTemperature;

  private ResourceBundle  rb;

  /**
   * 
   */
  public JPanelPrefUnits() {
    super();
    initialize();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.ui.swing.component.PanelPrefListener#viewChanged()
   */
  public void viewChanged() {
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), getClass());

    BorderLayout borderLayout = new BorderLayout();
    borderLayout.setHgap(5);
    borderLayout.setVgap(5);
    this.setLayout(borderLayout);
    this.add(getJPanelTitle(), BorderLayout.NORTH);
    this.add(getJPanelCenter(), BorderLayout.CENTER);

    // evenements
    jComboBoxDistance.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // mis a jour distance
        String value = (String) ((JComboBox) e.getSource()).getSelectedItem();
        Configuration.getConfig().addProperty("units", "distance", value);
        UnitManager.getManager().fireDistanceChanged(value);

        // mis a jour vitesse si besoin
        String currentSpeed = Configuration.getConfig().getProperty("units",
                                                                    "speed");
        String unitSpeed;
        if (currentSpeed.startsWith("mn/")) {
          unitSpeed = "mn/" + value;
        }
        else {
          unitSpeed = value + "/h";
        }
        if (!unitSpeed.equals(currentSpeed)) {
          jComboBoxSpeed.setSelectedItem(unitSpeed);
          Configuration.getConfig().addProperty("units", "speed", unitSpeed);
          UnitManager.getManager().fireSpeedPaceChanged(unitSpeed);
        }
      }
    });
    jComboBoxSpeed.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // mis a jour vitesse
        String value = (String) ((JComboBox) e.getSource()).getSelectedItem();
        Configuration.getConfig().addProperty("units", "speed", value);
        UnitManager.getManager().fireSpeedPaceChanged(value);

        // mis a jour distance si besoin
        String currentDist = Configuration.getConfig().getProperty("units",
                                                                   "distance");
        String unitDist;
        if (value.startsWith("mn/")) {
          unitDist = value.substring(3);
        }
        else {
          unitDist = value.substring(0, value.indexOf('/'));
        }
        if (!unitDist.equals(currentDist)) {
          jComboBoxDistance.setSelectedItem(unitDist);
          Configuration.getConfig().addProperty("units", "distance", unitDist);
          UnitManager.getManager().fireDistanceChanged(unitDist);
        }
      }
    });
    jComboBoxWeight.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String value = (String) ((JComboBox) e.getSource()).getSelectedItem();
        Configuration.getConfig().addProperty("units", "weight", value);
        UnitManager.getManager().fireWeightChanged(value);
      }
    });
    jComboBoxHeight.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String value = (String) ((JComboBox) e.getSource()).getSelectedItem();
        Configuration.getConfig().addProperty("units", "height", value);
        UnitManager.getManager().fireHeightChanged(value);
      }
    });
    jComboBoxTemperature.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String value = (String) ((JComboBox) e.getSource()).getSelectedItem();
        Configuration.getConfig().addProperty("units", "temperature", value);
        UnitManager.getManager().fireTemperatureChanged(value);
      }
    });
  }

  /**
   * This method initializes jPanelTitle
   * 
   * @return javax.swing.JPanel
   */
  private JPanelPrefTitle getJPanelTitle() {
    if (jPanelTitle == null) {
      jPanelTitle = new JPanelPrefTitle(rb.getString("title"));
    }
    return jPanelTitle;
  }

  /**
   * This method initializes jPanelCenter
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelCenter() {
    if (jPanelCenter == null) {

      jLabelLibDistance = new JLabel();
      jLabelLibDistance.setBounds(new Rectangle(5, 5, 110, 23));
      jLabelLibDistance.setText(rb.getString("jLabelLibDistance"));
      jLabelLibDistance.setFont(GuiFont.FONT_PLAIN);

      jLabelLibSpeed = new JLabel();
      jLabelLibSpeed.setBounds(new Rectangle(5, 35, 110, 23));
      jLabelLibSpeed.setText(rb.getString("jLabelLibSpeed"));
      jLabelLibSpeed.setFont(GuiFont.FONT_PLAIN);

      jLabelLibWeight = new JLabel();
      jLabelLibWeight.setBounds(new Rectangle(5, 65, 110, 23));
      jLabelLibWeight.setText(rb.getString("jLabelLibWeight"));
      jLabelLibWeight.setFont(GuiFont.FONT_PLAIN);

      jLabelLibHeight = new JLabel();
      jLabelLibHeight.setBounds(new Rectangle(5, 95, 150, 23));
      jLabelLibHeight.setText(rb.getString("jLabelLibHeight"));
      jLabelLibHeight.setFont(GuiFont.FONT_PLAIN);

      jLabelLibTemperature = new JLabel();
      jLabelLibTemperature.setBounds(new Rectangle(5, 125, 150, 23));
      jLabelLibTemperature.setText(rb.getString("jLabelLibTemperature"));
      jLabelLibTemperature.setFont(GuiFont.FONT_PLAIN);

      jPanelCenter = new JPanel();
      jPanelCenter.setLayout(null);
      jPanelCenter.add(jLabelLibDistance, null);
      jPanelCenter.add(getJComboBoxDistance(), null);
      jPanelCenter.add(jLabelLibSpeed, null);
      jPanelCenter.add(getJComboBoxSpeed(), null);
      jPanelCenter.add(jLabelLibWeight, null);
      jPanelCenter.add(getJComboBoxWeight(), null);
      jPanelCenter.add(jLabelLibHeight, null);
      jPanelCenter.add(getJComboBoxHeight(), null);
      jPanelCenter.add(jLabelLibTemperature, null);
      jPanelCenter.add(getJComboBoxTemperature(), null);
    }
    return jPanelCenter;
  }

  /**
   * This method initializes jComboBoxDistance
   * 
   * @return javax.swing.JComboBox
   */
  private JComboBox getJComboBoxDistance() {
    if (jComboBoxDistance == null) {
      jComboBoxDistance = new JComboBox(DistanceUnit.units());
      jComboBoxDistance.setSelectedItem(DistanceUnit.getDefaultUnit());
      jComboBoxDistance.setFont(GuiFont.FONT_PLAIN);
      jComboBoxDistance.setBounds(new Rectangle(120, 5, 100, 23));
    }
    return jComboBoxDistance;
  }

  /**
   * This method initializes jComboBoxSpeed
   * 
   * @return javax.swing.JComboBox
   */
  private JComboBox getJComboBoxSpeed() {
    if (jComboBoxSpeed == null) {
      jComboBoxSpeed = new JComboBox(SpeedPaceUnit.units());
      jComboBoxSpeed.setSelectedItem(SpeedPaceUnit.getDefaultUnit());
      jComboBoxSpeed.setFont(GuiFont.FONT_PLAIN);
      jComboBoxSpeed.setBounds(new Rectangle(120, 35, 100, 23));
    }
    return jComboBoxSpeed;
  }

  /**
   * This method initializes jComboBoxWeight
   * 
   * @return javax.swing.JComboBox
   */
  private JComboBox getJComboBoxWeight() {
    if (jComboBoxWeight == null) {
      jComboBoxWeight = new JComboBox(WeightUnit.units());
      jComboBoxWeight.setSelectedItem(WeightUnit.getDefaultUnit());
      jComboBoxWeight.setFont(GuiFont.FONT_PLAIN);
      jComboBoxWeight.setBounds(new Rectangle(120, 65, 100, 23));
    }
    return jComboBoxWeight;
  }

  /**
   * This method initializes jComboBoxWeight
   * 
   * @return javax.swing.JComboBox
   */
  private JComboBox getJComboBoxHeight() {
    if (jComboBoxHeight == null) {
      jComboBoxHeight = new JComboBox(HeightUnit.units());
      jComboBoxHeight.setSelectedItem(HeightUnit.getDefaultUnit());
      jComboBoxHeight.setFont(GuiFont.FONT_PLAIN);
      jComboBoxHeight.setBounds(new Rectangle(120, 95, 100, 23));
    }
    return jComboBoxHeight;
  }

  /**
   * This method initializes jComboBoxWeight
   * 
   * @return javax.swing.JComboBox
   */
  private JComboBox getJComboBoxTemperature() {
    if (jComboBoxTemperature == null) {
      jComboBoxTemperature = new JComboBox(TemperatureUnit.units());
      jComboBoxTemperature.setSelectedItem(TemperatureUnit.getDefaultUnit());
      jComboBoxTemperature.setFont(GuiFont.FONT_PLAIN);
      jComboBoxTemperature.setBounds(new Rectangle(120, 125, 100, 23));
    }
    return jComboBoxTemperature;
  }

}

package fr.turtlesport.ui.swing;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.AbstractSpinnerModel;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXPanel;

import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.MeteoTableManager;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.meteo.DataMeteo;
import fr.turtlesport.meteo.StationMeteo;
import fr.turtlesport.meteo.Wundergound;
import fr.turtlesport.ui.swing.component.GeoPositionMapKit;
import fr.turtlesport.ui.swing.component.JButtonDim;
import fr.turtlesport.ui.swing.component.JXSplitButton;
import fr.turtlesport.ui.swing.img.ImagesRepository;
import fr.turtlesport.ui.swing.model.ChangePointsEvent;
import fr.turtlesport.ui.swing.model.ChangePointsListener;
import fr.turtlesport.ui.swing.model.ModelPointsManager;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.SpeedUnit;
import fr.turtlesport.unit.TemperatureUnit;
import fr.turtlesport.unit.event.UnitEvent;
import fr.turtlesport.unit.event.UnitListener;
import fr.turtlesport.unit.event.UnitManager;
import fr.turtlesport.util.DateUtil;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelMeteo extends JXPanel implements LanguageListener,
                                        ChangePointsListener, UnitListener {
  private static TurtleLogger     log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JPanelMeteo.class);
  }

  private JLabel                  jLabelWindSpeed;

  private JXSplitButton           jxSplitButtonImgMeteo;

  private JLabel                  jLabelTemperature;

  private JLabel                  jLabelLibWind;

  private JLabel                  jLabelWindOrientation;

  private JLabel                  jLabelLibHumidity;

  private JLabel                  jLabelValHumidity;

  private JLabel                  jLabelLibPressurehPa;

  private JLabel                  jLabelValPressurehPa;

  private JLabel                  jLabelLibVisibility;

  private JLabel                  jLabelValVisibility;

  private JXBusyLabel             jBusyLabel;

  private JSpinner                spinner;

  private TemperatureSpinnerModel spinnerModel;

  private DataMeteo               meteo;

  private boolean                 isInDataBase = false;

  private JButtonDim              jButtonReload;

  /**
   * Create the panel.
   */
  public JPanelMeteo() {
    super();
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] { 132, 0, 0, 0, 0 };
    gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    gridBagLayout.columnWeights = new double[] { 0.0,
        0.0,
        0.0,
        0.0,
        Double.MIN_VALUE };
    gridBagLayout.rowWeights = new double[] { 0.0,
        0.0,
        1.0,
        1.0,
        0.0,
        1.0,
        1.0,
        1.0,
        Double.MIN_VALUE };
    setLayout(gridBagLayout);
    // setForeground(new Color(0, 0, 0));
    // setBackground(new Color(0, 0, 0));
    // setImage(ImagesMeteoRepository.getImage("elephant_cloud.jpg"));

    List<ImageIcon> listIcon = DataMeteo.getIcons();
    HashMap<String, ImageIcon> map = new HashMap<String, ImageIcon>();
    String[] values = new String[listIcon.size()];
    JPopupMenu popupMenu = new JPopupMenu();

    for (int i = 0; i < listIcon.size(); i++) {
      values[i] = Integer.toString(i);
      map.put(values[i], listIcon.get(i));

      JMenuItem item = new JMenuItem(listIcon.get(i));
      popupMenu.add(item);
      final int index = i;
      final ActionListener itemListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          jxSplitButtonImgMeteo.setSelectedIndex(index);
        }
      };
      item.addActionListener(itemListener);
    }

    jxSplitButtonImgMeteo = new JXSplitButton(null, null, popupMenu);
    // jxSplitButtonImgMeteo.setSelectedIndex(values.length - 1);
    jxSplitButtonImgMeteo.setFont(new Font("SansSerif", Font.PLAIN, 0));
    GridBagConstraints gbc_jLabelImgMeteo = new GridBagConstraints();
    gbc_jLabelImgMeteo.gridheight = 1;
    gbc_jLabelImgMeteo.fill = GridBagConstraints.VERTICAL;
    gbc_jLabelImgMeteo.insets = new Insets(5, 0, 5, 5);
    gbc_jLabelImgMeteo.gridx = 0;
    gbc_jLabelImgMeteo.gridy = 0;
    add(jxSplitButtonImgMeteo, gbc_jLabelImgMeteo);

    jLabelTemperature = new JLabel("15 " + TemperatureUnit.getDefaultUnit());
    jLabelTemperature.setHorizontalAlignment(SwingConstants.LEFT);
    jLabelTemperature.setFont(new Font("SansSerif", Font.BOLD, 16));
    jLabelTemperature.setBorder(BorderFactory.createTitledBorder(""));
    spinner = new JSpinner();
    spinner.setEditor(jLabelTemperature);
    spinnerModel = new TemperatureSpinnerModel();
    spinner.setModel(spinnerModel);
    Dimension dim = new Dimension(105, 25);
    spinner.setPreferredSize(dim);
    spinner.setMinimumSize(dim);
    GridBagConstraints gbc_jLabelTemperature = new GridBagConstraints();
    gbc_jLabelTemperature.anchor = GridBagConstraints.WEST;
    gbc_jLabelTemperature.ipady = 10;
    gbc_jLabelTemperature.gridwidth = 3;
    gbc_jLabelTemperature.insets = new Insets(5, 0, 5, 5);
    gbc_jLabelTemperature.gridx = 1;
    gbc_jLabelTemperature.gridy = 0;
    add(spinner, gbc_jLabelTemperature);

    jButtonReload = new JButtonDim();
    Dimension dimReload = new Dimension(26, 26);
    jButtonReload.setPreferredSize(dimReload);
    jButtonReload.setMinimumSize(dimReload);
    jButtonReload.setIcon(ImagesRepository.getImageIcon("refresh16.png"));
    GridBagConstraints gbc_jButtonReload = new GridBagConstraints();
    gbc_jButtonReload.anchor = GridBagConstraints.EAST;
    gbc_jButtonReload.insets = new Insets(0, 0, 5, 0);
    gbc_jButtonReload.gridx = 4;
    gbc_jButtonReload.gridy = 0;
    add(jButtonReload, gbc_jButtonReload);

    JSeparator separator = new JSeparator();
    GridBagConstraints gbc_separator = new GridBagConstraints();
    gbc_separator.fill = GridBagConstraints.HORIZONTAL;
    gbc_separator.anchor = GridBagConstraints.WEST;
    gbc_separator.gridwidth = 5;
    gbc_separator.insets = new Insets(0, 0, 5, 0);
    gbc_separator.gridx = 0;
    gbc_separator.gridy = 2;
    add(separator, gbc_separator);

    // Vent
    jLabelLibWind = new JLabel("Vent :");
    jLabelLibWind.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jLabelLibWind = new GridBagConstraints();
    gbc_jLabelLibWind.ipadx = 10;
    gbc_jLabelLibWind.anchor = GridBagConstraints.EAST;
    gbc_jLabelLibWind.fill = GridBagConstraints.VERTICAL;
    gbc_jLabelLibWind.insets = new Insets(10, 0, 5, 5);
    gbc_jLabelLibWind.gridx = 0;
    gbc_jLabelLibWind.gridy = 3;
    add(jLabelLibWind, gbc_jLabelLibWind);

    jLabelWindSpeed = new JLabel("15");
    jLabelWindSpeed.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jLabelWindSpeed = new GridBagConstraints();
    gbc_jLabelWindSpeed.fill = GridBagConstraints.BOTH;
    gbc_jLabelWindSpeed.insets = new Insets(10, 0, 5, 5);
    gbc_jLabelWindSpeed.gridx = 1;
    gbc_jLabelWindSpeed.gridy = 3;
    add(jLabelWindSpeed, gbc_jLabelWindSpeed);

    jBusyLabel = new JXBusyLabel();
    jBusyLabel.setBusy(true);
    GridBagConstraints gbc_jBusyLabel = new GridBagConstraints();
    gbc_jBusyLabel.insets = new Insets(0, 0, 5, 5);
    gbc_jBusyLabel.gridx = 3;
    gbc_jBusyLabel.gridy = 3;
    add(jBusyLabel, gbc_jBusyLabel);

    JLabel labelWindSock = new JLabel();
    labelWindSock.setIcon(new ImageIcon(JPanelMeteo.class
        .getResource("/fr/turtlesport/meteo/24px-windsock.png")));
    GridBagConstraints gbc_labelWindSock = new GridBagConstraints();
    gbc_labelWindSock.ipadx = 10;
    gbc_labelWindSock.anchor = GridBagConstraints.EAST;
    gbc_labelWindSock.insets = new Insets(0, 0, 5, 5);
    gbc_labelWindSock.gridx = 0;
    gbc_labelWindSock.gridy = 4;
    add(labelWindSock, gbc_labelWindSock);

    jLabelWindOrientation = new JLabel("NO");
    jLabelWindOrientation.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jLabelWindOrientation = new GridBagConstraints();
    gbc_jLabelWindOrientation.fill = GridBagConstraints.HORIZONTAL;
    gbc_jLabelWindOrientation.insets = new Insets(10, 0, 5, 5);
    gbc_jLabelWindOrientation.gridx = 1;
    gbc_jLabelWindOrientation.gridy = 4;
    add(jLabelWindOrientation, gbc_jLabelWindOrientation);

    // humidite
    jLabelLibHumidity = new JLabel("Humidité :");
    jLabelLibHumidity.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jLabelLibHumidity = new GridBagConstraints();
    gbc_jLabelLibHumidity.ipadx = 10;
    gbc_jLabelLibHumidity.anchor = GridBagConstraints.EAST;
    gbc_jLabelLibHumidity.fill = GridBagConstraints.VERTICAL;
    gbc_jLabelLibHumidity.insets = new Insets(0, 0, 5, 5);
    gbc_jLabelLibHumidity.gridx = 0;
    gbc_jLabelLibHumidity.gridy = 5;
    add(jLabelLibHumidity, gbc_jLabelLibHumidity);

    jLabelValHumidity = new JLabel("5");
    jLabelValHumidity.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jLabelValHumidity = new GridBagConstraints();
    gbc_jLabelValHumidity.fill = GridBagConstraints.BOTH;
    gbc_jLabelValHumidity.insets = new Insets(0, 0, 5, 0);
    gbc_jLabelValHumidity.gridx = 1;
    gbc_jLabelValHumidity.gridy = 5;
    gbc_jLabelValHumidity.gridwidth = GridBagConstraints.REMAINDER;
    add(jLabelValHumidity, gbc_jLabelValHumidity);

    // pression
    jLabelLibPressurehPa = new JLabel("Pression :");
    jLabelLibPressurehPa.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jLabelLibPressurehPa = new GridBagConstraints();
    gbc_jLabelLibPressurehPa.ipadx = 10;
    gbc_jLabelLibPressurehPa.anchor = GridBagConstraints.EAST;
    gbc_jLabelLibPressurehPa.fill = GridBagConstraints.VERTICAL;
    gbc_jLabelLibPressurehPa.insets = new Insets(0, 0, 5, 5);
    gbc_jLabelLibPressurehPa.gridx = 0;
    gbc_jLabelLibPressurehPa.gridy = 6;
    add(jLabelLibPressurehPa, gbc_jLabelLibPressurehPa);

    jLabelValPressurehPa = new JLabel("1024");
    jLabelValPressurehPa.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jLabelValPressurehPa = new GridBagConstraints();
    gbc_jLabelValPressurehPa.fill = GridBagConstraints.BOTH;
    gbc_jLabelValPressurehPa.insets = new Insets(0, 0, 5, 0);
    gbc_jLabelValPressurehPa.gridx = 1;
    gbc_jLabelValPressurehPa.gridy = 6;
    gbc_jLabelValPressurehPa.gridwidth = GridBagConstraints.REMAINDER;
    add(jLabelValPressurehPa, gbc_jLabelValPressurehPa);

    // visibilite
    jLabelLibVisibility = new JLabel("Visibilité ");
    jLabelLibVisibility.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jLabelLibVisibility = new GridBagConstraints();
    gbc_jLabelLibVisibility.ipadx = 10;
    gbc_jLabelLibVisibility.anchor = GridBagConstraints.EAST;
    gbc_jLabelLibVisibility.fill = GridBagConstraints.VERTICAL;
    gbc_jLabelLibVisibility.insets = new Insets(0, 0, 0, 5);
    gbc_jLabelLibVisibility.gridx = 0;
    gbc_jLabelLibVisibility.gridy = 7;
    add(jLabelLibVisibility, gbc_jLabelLibVisibility);

    jLabelValVisibility = new JLabel("10");
    jLabelValVisibility.setFont(GuiFont.FONT_PLAIN);
    GridBagConstraints gbc_jLabelValVisibility = new GridBagConstraints();
    gbc_jLabelValVisibility.fill = GridBagConstraints.BOTH;
    gbc_jLabelValVisibility.insets = new Insets(0, 0, 0, 5);
    gbc_jLabelValVisibility.gridx = 1;
    gbc_jLabelValVisibility.gridy = 7;
    add(jLabelValVisibility, gbc_jLabelValVisibility);

    LanguageManager.getManager().addLanguageListener(this);
    performedLanguage(LanguageManager.getManager().getCurrentLang());
    UnitManager.getManager().addUnitListener(this);

    jxSplitButtonImgMeteo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          DataRun run = ModelPointsManager.getInstance().getDataRun();
          if (run != null
              && meteo != null
              && meteo.getImageIconIndex() != jxSplitButtonImgMeteo
                  .getSelectedIndex()) {
            meteo.setImageIconIndex(jxSplitButtonImgMeteo.getSelectedIndex());
            if (isInDataBase) {
              MeteoTableManager.getInstance()
                  .updateCondition(run,
                                   jxSplitButtonImgMeteo.getSelectedIndex());
            }
            else {
              isInDataBase = true;
              MeteoTableManager.getInstance().store(meteo, run);
            }
          }
        }
        catch (SQLException sqle) {
          log.error("", sqle);
        }
      }
    });

    jButtonReload.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        changedAllPoints(null);
      }
    });
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
  }

  private void performedLanguage(ILanguage lang) {
    ResourceBundle rb = ResourceBundleUtility.getBundle(lang, getClass());
    jLabelLibWind.setText(rb.getString("jLabelLibWind"));
    jLabelLibHumidity.setText(rb.getString("jLabelLibHumidity"));
    jLabelLibPressurehPa.setText(rb.getString("jLabelLibPressurehPa"));
    jLabelLibVisibility.setText(rb.getString("jLabelLibVisibility"));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.unit.event.UnitListener#unitChanged(fr.turtlesport.unit.
   * event.UnitEvent)
   */
  public void unitChanged(UnitEvent event) {
    if (meteo == null) {
      return;
    }
    if (event.isEventTemperature()) {
      if (TemperatureUnit.isDefaultUnitDegree()) {
        spinnerModel.value = (int) TemperatureUnit
            .convertToDegree(spinnerModel.value);
        spinnerModel.max = spinnerModel.maxDegree;
        spinnerModel.min = spinnerModel.minDegree;
      }
      else {
        spinnerModel.value = (int) TemperatureUnit
            .convertToFahrenheit(spinnerModel.value);
        spinnerModel.max = spinnerModel.maxFahrenheit;
        spinnerModel.min = spinnerModel.minFahrenheit;
      }
      jLabelTemperature.setText(spinnerModel.value + " "
                                + TemperatureUnit.getDefaultUnit());
    }

    if (event.isEventDistance()) {
      // vent
      if (!meteo.isWindSpeedValid()) {
        jLabelWindSpeed.setText("- " + DistanceUnit.getDefaultUnit());
      }
      else {
        double value = meteo.getWindSpeedkmh();
        if (!DistanceUnit.isDefaultUnitKm()) {
          value = DistanceUnit.convertKmToMile(meteo.getWindSpeedkmh());
        }
        jLabelWindSpeed.setText(SpeedUnit.format(value) + " "
                                + SpeedUnit.getDefaultUnit());
      }

      // visibilite
      if (!meteo.isVisibilityValid()) {
        jLabelValVisibility.setText("- " + DistanceUnit.getDefaultUnit());
      }
      else {
        double value = meteo.getVisibility();
        if (!DistanceUnit.isDefaultUnitKm()) {
          value = DistanceUnit.convertKmToMile(meteo.getVisibility());
        }
        jLabelValVisibility.setText(DistanceUnit.format(value) + " "
                                    + DistanceUnit.getDefaultUnit());
      }
    }
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
   * fr.turtlesport.ui.swing.model.ChangePointsListener#changedPoint(fr.turtlesport
   * .ui.swing.model.ChangePointsEvent)
   */
  public void changedPoint(ChangePointsEvent e) {
  }

  public void changedLap(ChangePointsEvent e) {
  }

  public void changedAllPoints(final ChangePointsEvent changeEvent) {
    if (changeEvent != null) {
      meteo = null;
      isInDataBase = false;
    }

    jxSplitButtonImgMeteo
        .setSelectedIndex(jxSplitButtonImgMeteo.getItemCount() - 1);
    jLabelTemperature.setText("- " + TemperatureUnit.getDefaultUnit());
    spinner.setValue("- " + TemperatureUnit.getDefaultUnit());
    spinnerModel.setValue(jLabelTemperature.getText());
    jLabelWindSpeed.setText("- km/h");
    jLabelWindOrientation.setText("-");
    jLabelValHumidity.setText("- %");
    jLabelValPressurehPa.setText("- hPa");
    jLabelValVisibility.setText("- km");

    jBusyLabel.setVisible(true);
    jBusyLabel.setBusy(true);

    new SwingWorker() {

      @Override
      public Object construct() {
        DataRun dataRun = ModelPointsManager.getInstance().getDataRun();
        if (dataRun == null) {
          return null;
        }

        DataMeteo currentMeteo = null;

        // Recuperation dans la base de donnees (sauf cas de reload)
        if (changeEvent != null) {
          try {
            currentMeteo = MeteoTableManager.getInstance().retreive(dataRun);
            if (currentMeteo != null) {
              // data trouve
              isInDataBase = true;
              return currentMeteo;
            }
          }
          catch (SQLException sqle) {
            log.error("", sqle);
          }
        }

        // Donnees non trouvee en base
        if (ModelPointsManager.getInstance().hasPoints()) {
          GeoPositionMapKit geo = ModelPointsManager.getInstance().getListGeo()
              .get(0);
          try {
            // Recuperation de la location
            StationMeteo station = Wundergound.lookup(geo.getLatitude(),
                                                      geo.getLongitude());

            if (station != null) {
              // recuperation des donnees meteo historiques
              List<DataMeteo> list = Wundergound.history(station,
                                                         dataRun.getTime());
              long l = Long.MAX_VALUE;

              // recuperation des donnees les plus proche de l'heure de depart
              for (int i = 0; i < list.size(); i++) {
                long diff = Math.abs(list.get(i).getDate().getTime()
                                     - dataRun.getTime().getTime());
                if (diff < l) {
                  currentMeteo = list.get(i);
                  l = diff;
                }
              }

              // on compare avec la date du jour
              if (DateUtil.isDayDate(dataRun.getTime())) {
                DataMeteo dayMeteo = Wundergound.current(station);
                long diff = Math.abs(currentMeteo.getDate().getTime()
                                     - dataRun.getTime().getTime());
                if (diff < l) {
                  currentMeteo = dayMeteo;
                }
              }

              if (meteo != null) {
                // sauvegarde en base
                isInDataBase = true;
                MeteoTableManager.getInstance().store(meteo, dataRun);
              }
            }
          }
          catch (Throwable e) {
            log.error("", e);
            if (meteo != null) {
              return meteo;
            }
            return null;
          }
        }

        if (currentMeteo == null) {
          currentMeteo = new DataMeteo(dataRun.getTime());
        }
        return currentMeteo;
      }

      @Override
      public void finished() {
        meteo = (DataMeteo) get();
        jBusyLabel.setBusy(false);
        jBusyLabel.setVisible(false);

        if (meteo != null) {
          jxSplitButtonImgMeteo.setSelectedIndex((meteo.getImageIconIndex()));
          if (meteo.isTemperatureValid()) {
            String value;
            if (TemperatureUnit.isDefaultUnitDegree()) {
              value = meteo.getTemperature() + " "
                      + TemperatureUnit.unitDegree();
            }
            else {
              value = (int) (TemperatureUnit.convertToFahrenheit(meteo
                  .getTemperature())) + " " + TemperatureUnit.getDefaultUnit();
            }
            spinnerModel.setValue(value);
            jLabelTemperature.setText(value);
          }
          if (meteo.isWindSpeedValid()) {
            double value = meteo.getWindSpeedkmh();
            if (!DistanceUnit.isDefaultUnitKm()) {
              value = DistanceUnit.convertKmToMile(meteo.getWindSpeedkmh());
            }
            jLabelWindSpeed.setText(SpeedUnit.format(value) + " "
                                    + SpeedUnit.getDefaultUnit());
          }
          if (meteo.getWindDirection() != null) {
            jLabelWindOrientation.setText(meteo.getWindDirection());
          }
          if (meteo.getHumidity() >= 0 && meteo.getHumidity() <= 100) {
            jLabelValHumidity.setText(meteo.getHumidity() + " %");
          }
          if (meteo.getPressurehPa() > 0) {
            jLabelValPressurehPa.setText(meteo.getPressurehPa() + " hPa");
          }
          if (meteo.isVisibilityValid()) {
            double value = meteo.getVisibility();
            if (!DistanceUnit.isDefaultUnitKm()) {
              value = DistanceUnit.convertKmToMile(meteo.getVisibility());
            }
            jLabelValVisibility.setText(DistanceUnit.format(value) + " "
                                        + DistanceUnit.getDefaultUnit());
          }
        }
      }

    }.start();

  }

  private class TemperatureSpinnerModel extends AbstractSpinnerModel {
    int    maxDegree     = 54;

    int    minDegree     = -49;

    int    maxFahrenheit = (int) TemperatureUnit.convertToFahrenheit(maxDegree);

    int    minFahrenheit = (int) TemperatureUnit.convertToFahrenheit(minDegree);

    int    value;

    int    max           = maxDegree;

    int    min           = minDegree;

    String sValue;

    public TemperatureSpinnerModel() {
      if (TemperatureUnit.isFahrenheit(TemperatureUnit.getDefaultUnit())) {
        max = maxFahrenheit;
        min = minFahrenheit;
      }
    }

    public Object getValue() {
      return sValue;
    }

    public void setValue(Object value) {
      this.sValue = (String) value;
      retrieveIntValue();
    }

    public Object getNextValue() {
      if (value > max) {
        return null;
      }
      value++;
      sValue = Integer.toString(value) + " " + TemperatureUnit.getDefaultUnit();
      jLabelTemperature.setText(sValue);
      updateDB();
      return sValue;
    }

    public Object getPreviousValue() {
      if (value < min) {
        return null;
      }
      value--;
      sValue = Integer.toString(value) + " " + TemperatureUnit.getDefaultUnit();
      jLabelTemperature.setText(sValue);
      updateDB();
      return sValue;
    }

    private int retrieveIntValue() {
      String s = sValue.substring(0, sValue.indexOf(' '));
      value = (s.length() == 1 && s.charAt(0) == '-') ? 0 : Integer.parseInt(s);
      updateDB();
      return value;
    }

    private void updateDB() {
      try {
        DataRun run = ModelPointsManager.getInstance().getDataRun();
        int realValue = (TemperatureUnit.isDefaultUnitDegree()) ? value
            : (int) Math.rint(TemperatureUnit.convertToDegree(value));
        if (run != null && meteo != null && meteo.getTemperature() != realValue) {
          meteo.setTemperature(realValue);
          if (isInDataBase) {
            MeteoTableManager.getInstance().updateTemperature(run, realValue);
          }
          else {
            isInDataBase = true;
            MeteoTableManager.getInstance().store(meteo, run);
          }
        }
      }
      catch (SQLException sqle) {
        log.error("", sqle);
      }
    }

  }

}

package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import fr.turtlesport.Configuration;
import fr.turtlesport.db.DataRun;
import fr.turtlesport.db.DataRunLap;
import fr.turtlesport.db.RunLapTableManager;
import fr.turtlesport.db.RunTrkTableManager;
import fr.turtlesport.geo.FactoryGeoConvertRun;
import fr.turtlesport.geo.GeoConvertException;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.mail.IMailClient;
import fr.turtlesport.mail.Mail;
import fr.turtlesport.mail.MessageMail;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.unit.DistanceUnit;
import fr.turtlesport.unit.PaceUnit;
import fr.turtlesport.unit.SpeedPaceUnit;
import fr.turtlesport.unit.TimeUnit;
import fr.turtlesport.util.Location;
import fr.turtlesport.util.ResourceBundleUtility;
import fr.turtlesport.util.ZipUtil;

/**
 * @author Denis Apparicio
 * 
 */
public class JDialogRunSendEmail extends JDialog {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JDialogRunSendEmail.class);
  }

  private ResourceBundle      rb;

  private JButton             jButtonOK;

  private JButton             jButtonCancel;

  private JPanel              jContentPane;

  private JPanel              jPanelButton;

  private JPanel              jPanelCenter;

  private JCheckBox           jCheckBoxKml;

  private JCheckBox           jCheckBoxGoogleMap;

  private JCheckBox           jCheckBoxGpx;

  private JCheckBox           jCheckBoxHst;

  private JCheckBox           jCheckBoxDiagram;

  private DataRun             dataRun;

  /**
   * @param owner
   * @param modal
   */
  public JDialogRunSendEmail(Frame owner, boolean modal) {
    super(owner, modal);
    initialize();
  }

  public static void prompt(DataRun dataRun) {
    JDialogRunSendEmail view = new JDialogRunSendEmail(MainGui.getWindow(),
                                                       true);
    view.dataRun = dataRun;
    view.pack();
    view.setLocationRelativeTo(MainGui.getWindow());
    view.setVisible(true);
  }

  private void initialize() {
    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), getClass());

    this.setContentPane(getJContentPane());

    this.setTitle(rb.getString("title"));
    jButtonOK.setText(LanguageManager.getManager().getCurrentLang().ok());

    // Recuperation de la config precedente
    jCheckBoxHst.setSelected(Configuration.getConfig()
        .getPropertyAsBoolean("mail", "racehst", false));
    jCheckBoxGpx.setSelected(Configuration.getConfig()
        .getPropertyAsBoolean("mail", "racegpx", false));
    jCheckBoxGoogleMap.setSelected(Configuration.getConfig()
        .getPropertyAsBoolean("mail", "racegooglemap", false));
    jCheckBoxKml.setSelected(Configuration.getConfig()
        .getPropertyAsBoolean("mail", "racekml", false));
    jCheckBoxDiagram.setSelected(Configuration.getConfig()
        .getPropertyAsBoolean("mail", "racediagram", false));

    // Evenement
    jButtonOK.addActionListener(new SendMailActionListener());

    jButtonCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if (jContentPane == null) {
      jContentPane = new JPanel();
      jContentPane.setLayout(new BorderLayout(10, 5));
      jContentPane.add(new JLabel(" "), BorderLayout.NORTH);
      jContentPane.add(getJPanelCenter(), BorderLayout.CENTER);
      jContentPane.add(getJPanelButton(), BorderLayout.SOUTH);
    }
    return jContentPane;
  }

  private JPanel getJPanelCenter() {
    if (jPanelCenter == null) {
      jPanelCenter = new JPanel();
      jPanelCenter.setLayout(new BoxLayout(jPanelCenter, BoxLayout.PAGE_AXIS));
      jPanelCenter
          .setBorder(BorderFactory.createTitledBorder(null,
                                                      rb.getString("borderTitleCenter"),
                                                      TitledBorder.DEFAULT_JUSTIFICATION,
                                                      TitledBorder.DEFAULT_POSITION,
                                                      GuiFont.FONT_PLAIN,
                                                      null));

      jCheckBoxKml = new JCheckBox(rb.getString("jCheckBoxKml"));
      jCheckBoxKml.setFont(GuiFont.FONT_PLAIN);
      jCheckBoxGoogleMap = new JCheckBox(rb.getString("jCheckBoxGoogleMap"));
      jCheckBoxGoogleMap.setFont(GuiFont.FONT_PLAIN);
      jCheckBoxGpx = new JCheckBox(rb.getString("jCheckBoxGpx"));
      jCheckBoxGpx.setFont(GuiFont.FONT_PLAIN);
      jCheckBoxHst = new JCheckBox(rb.getString("jCheckBoxHst"));
      jCheckBoxHst.setFont(GuiFont.FONT_PLAIN);
      jCheckBoxDiagram = new JCheckBox(rb.getString("jCheckBoxDiagram"));
      jCheckBoxDiagram.setFont(GuiFont.FONT_PLAIN);

      jPanelCenter.add(jCheckBoxKml, null);
      jPanelCenter.add(jCheckBoxGoogleMap, null);
      jPanelCenter.add(jCheckBoxGpx, null);
      jPanelCenter.add(jCheckBoxHst, null);
      jPanelCenter.add(jCheckBoxDiagram, null);
    }
    return jPanelCenter;
  }

  private JPanel getJPanelButton() {
    if (jPanelButton == null) {
      jPanelButton = new JPanel();
      jPanelButton.setLayout(new FlowLayout(FlowLayout.RIGHT));
      jPanelButton.add(getJButtonCancel(), null);
      jPanelButton.add(getJButtonOK(), null);
    }
    return jPanelButton;
  }

  private JButton getJButtonOK() {
    if (jButtonOK == null) {
      jButtonOK = new JButton();
      jButtonOK.setFont(GuiFont.FONT_PLAIN);
    }
    return jButtonOK;
  }

  private JButton getJButtonCancel() {
    if (jButtonCancel == null) {
      jButtonCancel = new JButton();
      jButtonCancel.setText(LanguageManager.getManager().getCurrentLang()
          .cancel());
      jButtonCancel.setFont(GuiFont.FONT_PLAIN);
    }
    return jButtonCancel;
  }

  private class SendMailActionListener implements ActionListener {
    IMailClient mail = null;

    public void actionPerformed(ActionEvent e) {

      if (Mail.isConfigurable() && !Mail.isChoose()) {
        mail = JDialogChooseEmail.prompt(JDialogRunSendEmail.this);
      }
      else {
        mail = Mail.getDefaultMail();
      }

      if (mail == null) {
        dispose();
        return;
      }

      MainGui.getWindow().beforeRunnableSwing();

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          try {
            doWork();
          }
          finally {
            MainGui.getWindow().afterRunnableSwing();
            dispose();
          }
        }
      });
    }
    
    public void doWork() {
      MessageMail msg = new MessageMail();
      try {
        // sujet
        String line = rb.getString("sendSubject");

        DataRunLap[] runLaps = RunLapTableManager.getInstance()
            .findLaps(dataRun.getId());

        String tmp = LanguageManager.getManager().getCurrentLang()
            .getDateFormatter().format(runLaps[0].getStartTime())
                     + "  "
                     + new SimpleDateFormat("kk:mm:ss").format(runLaps[0]
                         .getStartTime());

        msg.setSubject(line.replaceFirst("%DATE%", tmp));

        // Body
        // -------------
        String body = rb.getString("sendBody");
        BufferedReader reader = new BufferedReader(new StringReader(body
            .replaceAll("<br>", "\n")));

        StringWriter writer = new StringWriter();
        int[] alt = RunTrkTableManager.getInstance().altitude(dataRun.getId());

        while ((line = reader.readLine()) != null) {
          if (line.contains("%DIST_TOT%")) {
            // distance
            writer.write(line.replaceFirst("%DIST_TOT%", DistanceUnit
                .formatWithUnit(dataRun.getComputeDistanceTot())));
            writer.write('\n');
          }
          else if (line.contains("%TIME_TOT%")) {
            // Temps
            writer.write(line.replaceFirst("%TIME_TOT%", TimeUnit
                .formatHundredSecondeTime(dataRun.computeTimeTot())));
            writer.write('\n');
          }
          else if (line.contains("%SPEED_AVG%")) {
            // vitesse moyenne
            writer.write(line.replaceFirst("%SPEED_AVG%", SpeedPaceUnit
                .computeFormatSpeedWithUnit(dataRun.getComputeDistanceTot(),
                                            dataRun.computeTimeTot())));
            writer.write('\n');
          }
          else if (line.contains("%SPACE_TOT%")) {
            // allure moyenne
            writer.write(line.replaceFirst("%SPACE_TOT%", PaceUnit
                .computeFormatAllureWithUnit(dataRun.getComputeDistanceTot(),
                                             dataRun.computeTimeTot())));
            writer.write('\n');
          }
          else if (line.contains("%CALORIES%")) {
            // calories.
            int value = RunLapTableManager.getInstance()
                .computeCalories(dataRun.getId());
            if (value > 0) {
              writer.write(line.replaceFirst("%CALORIES%",
                                             Integer.toString(value)));
              writer.write('\n');
            }
          }
          else if (line.contains("%HEART_AVG%")) {
            // frequence moyenne
            int value = RunLapTableManager.getInstance()
                .heartAvg(dataRun.getId());
            if (value > 0) {
              writer.write(line.replaceFirst("%HEART_AVG%",
                                             Integer.toString(value)));
              writer.write('\n');
            }
          }
          else if (line.contains("%HEART_MAX%")) {
            // frequence max.
            int value = RunLapTableManager.getInstance()
                .heartMax(dataRun.getId());
            if (value > 0) {
              writer.write(line.replaceFirst("%HEART_MAX%",
                                             Integer.toString(value)));
              writer.write('\n');
            }
          }
          else if (line.contains("%HEART_MIN%")) {
            // frequence min.
            int value = RunTrkTableManager.getInstance()
                .heartMin(dataRun.getId());
            if (value > 0) {
              writer.write(line.replaceFirst("%HEART_MIN%",
                                             Integer.toString(value)));
              writer.write('\n');
            }
          }
          else if (line.contains("%ALT_PLUS%")) {
            // Altitude +.
            if (alt[0] > 0) {
              writer.write(line.replaceFirst("%ALT_PLUS%",
                                             Integer.toString(alt[0])));
              writer.write('\n');
            }
          }
          else if (line.contains("%ALT_MOINS%")) {
            // Altitude -.
            if (alt[1] > 0) {
              writer.write(line.replaceFirst("%ALT_MOINS%",
                                             Integer.toString(alt[1])));
              writer.write('\n');
            }
          }
        }

        // body
        msg.setBody(writer.toString());

        // piece jointe
        // ----------------------
        boolean hasJoin = false;
        try {
          // image
          if (jCheckBoxDiagram.isSelected()) {
            JPanelRun panel = (JPanelRun) MainGui.getWindow()
                .getRightComponent();
            String name = LanguageManager.getManager().getCurrentLang()
                .getDateTimeFormatterWithoutSep().format(dataRun.getTime())
                          + ".jpg";
            File f = new File(Location.googleEarthLocation(), name);
            panel.getJDiagram().getJDiagram().saveComponentAsJPEG(f);
            msg.addAttachment(f);
            hasJoin = true;
          }
          // kml
          if (jCheckBoxKml.isSelected()) {
            File f = FactoryGeoConvertRun.getInstance(FactoryGeoConvertRun.KML)
                .convert(dataRun);
            if (f != null) {
              String name = f.getAbsolutePath();
              name = name.substring(0, f.getAbsolutePath().lastIndexOf('.'));
              File kmz = new File(name + ".kmz");
              ZipUtil.create(kmz, f);
              msg.addAttachment(kmz);
              hasJoin = true;
            }
          }
          // google map
          if (jCheckBoxGoogleMap.isSelected()) {
            File f = FactoryGeoConvertRun.getInstance(FactoryGeoConvertRun.MAP)
                .convert(dataRun);
            if (f != null) {
              msg.addAttachment(f);
              hasJoin = true;
            }
          }
          // gpx
          if (jCheckBoxGpx.isSelected()) {
            File f = FactoryGeoConvertRun.getInstance(FactoryGeoConvertRun.GPX)
                .convert(dataRun);
            if (f != null) {
              msg.addAttachment(f);
              hasJoin = true;
            }
          }
          // hst
          if (jCheckBoxHst.isSelected()) {
            File f = FactoryGeoConvertRun.getInstance(FactoryGeoConvertRun.HST)
                .convert(dataRun);
            if (f != null) {
              msg.addAttachment(f);
              hasJoin = true;
            }
          }
        }
        catch (GeoConvertException ge) {
          log.error("", ge);
          JShowMessage.error(ge.getMessage());
        }

        if (hasJoin) {
          try {
            Thread.sleep(1000);
          }
          catch (Exception ex) {
          }
        }
        mail.mail(msg);
      }
      catch (Throwable th) {
        log.error("", th);
      }

      // sauvegarde de la configuration
      Configuration.getConfig().addProperty("mail",
                                            "racegpx",
                                            Boolean.toString(jCheckBoxGpx
                                                .isSelected()));
      Configuration.getConfig().addProperty("mail",
                                            "racehst",
                                            Boolean.toString(jCheckBoxHst
                                                .isSelected()));
      Configuration.getConfig().addProperty("mail",
                                            "racekml",
                                            Boolean.toString(jCheckBoxKml
                                                .isSelected()));
      Configuration.getConfig().addProperty("mail",
                                            "racegooglemap",
                                            Boolean.toString(jCheckBoxGoogleMap
                                                .isSelected()));
      Configuration.getConfig().addProperty("mail",
                                            "racediagram",
                                            Boolean.toString(jCheckBoxDiagram
                                                .isSelected()));
    }
  }
}

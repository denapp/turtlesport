package fr.turtlesport.map;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.xml.DOMConfigurator;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import fr.turtlesport.Configuration;
import fr.turtlesport.db.DataRun;
import fr.turtlesport.ui.swing.SwingLookAndFeel;
import fr.turtlesport.ui.swing.component.GeoPositionMapKit;
import fr.turtlesport.ui.swing.component.JTurtleMapViewer;
import fr.turtlesport.ui.swing.model.ModelPointsManager;
import fr.turtlesport.util.Location;

public class MapView2 extends javax.swing.JDialog implements ActionListener {

  // Variables declaration - do not modify
  private javax.swing.JLabel  titleLbl;

  private javax.swing.JPanel  buttonPnl;

  private javax.swing.JButton closeBtn;

  private javax.swing.JLabel  dummyLbl;

  private javax.swing.JPanel  mapContainer;

  private javax.swing.JPanel  titlePnl;

  private javax.swing.JLabel  usernameLbl;

  private JTurtleMapViewer    jXMapKit;

  // End of variables declaration

  /** Creates new form AddServiceDlg */
  public MapView2(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
  }

  /** Creates new form AddServiceDlg */
  public MapView2() {
    setModal(true);
    initComponents();
  }

  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    titlePnl = new javax.swing.JPanel();
    titleLbl = new javax.swing.JLabel();
    buttonPnl = new javax.swing.JPanel();
    closeBtn = new javax.swing.JButton();
    dummyLbl = new javax.swing.JLabel();
    mapContainer = new javax.swing.JPanel();
    usernameLbl = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    getContentPane().setLayout(new java.awt.GridBagLayout());

    titlePnl.setLayout(new java.awt.GridBagLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipady = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 4);
    titlePnl.add(titleLbl, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.weightx = 1.0;
    getContentPane().add(titlePnl, gridBagConstraints);

    buttonPnl.setLayout(new java.awt.GridBagLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.ipadx = 7;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    buttonPnl.add(closeBtn, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.weightx = 1.0;
    buttonPnl.add(dummyLbl, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.weightx = 1.0;
    getContentPane().add(buttonPnl, gridBagConstraints);

    mapContainer.setLayout(new java.awt.GridBagLayout());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 7, 0, 7);
    mapContainer.add(usernameLbl, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    getContentPane().add(mapContainer, gridBagConstraints);

    addCustomAttributes();
    registerEventListeners();
    initializeMapKit();
    updateMap(44.73820634186268, 5.554787227883935);
    setSize(450 * 2, 300 * 2);
    setLocationRelativeTo(null);
  }

  public void addCustomAttributes() {
    closeBtn.setText("Close");
    titleLbl.setText("OpenStreet Maps");
    titlePnl.setBackground(Color.WHITE);
    titleLbl.setFont(new Font("Arial", Font.BOLD, 11));
    titleLbl.setForeground(new Color(65, 94, 117));
  }

  public void initializeMapKit() {
    jXMapKit = new JTurtleMapViewer();

    jXMapKit
        .setTileFactory(new DefaultTileFactory(new IGNMapTileProviderInfo("ign")));
    // jXMapKit
    // .setDefaultProvider(org.jdesktop.swingx.JXMapKit.DefaultProviders.OpenStreetMaps);
    jXMapKit.setCenterPosition(new GeoPosition(44.73820634186268,
                                               5.554787227883935));
    jXMapKit.setZoom(15);
    // ((DefaultTileFactory) jXMapKit.getMainMap().getTileFactory())
    // .setThreadPoolSize(8);

    GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    mapContainer.add(jXMapKit, gridBagConstraints);
  }

  public void updateMap(double latitude, double longitude) {
    jXMapKit.setAddressLocation(new GeoPosition(latitude, longitude));
    jXMapKit.setZoom(2);
  }

  public void registerEventListeners() {
    closeBtn.addActionListener(this);
  }

  /**
   * @param args
   *          the command line arguments
   */
  public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          // Initialisation des localisations
          Location.initialize();

          // Log4j
          DOMConfigurator.configure("log4J.xml");

          // Chargement du fichier de configuration.
          Configuration.initialize();

          // Mis a jour du look an feel
          SwingLookAndFeel.setDefaultLookAndFeel();

          final MapView2 dialog = new MapView2(new javax.swing.JFrame(), true);
          dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
              System.exit(0);
            }

            public void windowActivated(WindowEvent e) {
              try {
                DataRun run = new DataRun();
                run.setId(12);
                ModelPointsManager.getInstance().setDataRun(new Object(), run);

                List<GeoPosition> positions = new ArrayList<GeoPosition>();
                for (GeoPositionMapKit geo : ModelPointsManager.getInstance()
                    .getListGeo()) {
                  positions.add(geo);
                  dialog.jXMapKit.setupZoomAndCenterPosition(positions);
                }
              }
              catch (Throwable th) {
                th.printStackTrace();
              }
              super.windowActivated(e);
            }
          });
          dialog.setVisible(true);
        }
        catch (Throwable e) {
          e.printStackTrace();
        }
      }
    });
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == closeBtn) {
      dispose();
    }
  }

}

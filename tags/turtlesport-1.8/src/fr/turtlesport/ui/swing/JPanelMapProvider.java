package fr.turtlesport.ui.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.empty.EmptyTileFactory;

import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.map.AbstractTileFactoryExtended;
import fr.turtlesport.map.DataMap;
import fr.turtlesport.map.UserDefineMapTileFactory;
import fr.turtlesport.map.UserDefineMapTileProviderInfo;
import fr.turtlesport.ui.swing.component.JTextFieldLength;
import fr.turtlesport.ui.swing.component.JTurtleMapViewerZoom;
import fr.turtlesport.ui.swing.img.ImagesRepository;
import fr.turtlesport.ui.swing.model.GenericModelDocListener;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelMapProvider extends JPanel implements LanguageListener {
  private static TurtleLogger      log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JPanelMapProvider.class);
  }

  /** Paris */
  private static final GeoPosition GEO_DEFAULT = new GeoPosition(48.856638,
                                                                 2.352241);

  private static ResourceBundle    rb;

  private JLabel                   jLabelLibURL;

  private JTextField               jTextFieldURL;

  private JLabel                   jLabelLib;

  private JLabel                   jLabelLibName;

  private JLabel                   jLabelValName;

  private JLabel                   jLabelLibZoomMin;

  private JTextFieldLength         jTextFieldZoomMin;

  private JLabel                   jLabelLibZoomMax;

  private JTextFieldLength         jTextFieldZoomMax;

  private GenericModelDocListener  docURL;

  private GenericModelDocListener  docZoomMax;

  private GenericModelDocListener  docZoomMin;

  private JTurtleMapViewerZoom     mapViewer;

  private JButton                  jButtonTest;

  private DataMap                  data;

  protected JPanelMapProvider() {
    super();
    initialize();
  }

  private void initialize() {
    setLayout(null);
    setSize(400, 600);
    setMinimumSize(new Dimension(400, 600));
    setMaximumSize(new Dimension(400, 600));
    setBorder(BorderFactory
        .createTitledBorder(null,
                            " ",
                            TitledBorder.DEFAULT_JUSTIFICATION,
                            TitledBorder.DEFAULT_POSITION,
                            GuiFont.FONT_PLAIN,
                            null));

    // Nom
    jLabelLibName = new JLabel();
    jLabelLibName.setBounds(new Rectangle(5, 30, 90, 25));
    jLabelLibName.setText("Nom :");
    jLabelLibName.setFont(GuiFont.FONT_PLAIN);
    jLabelLibName.setHorizontalAlignment(SwingConstants.RIGHT);
    add(jLabelLibName);

    jLabelValName = new JLabel();
    jLabelValName.setBounds(new Rectangle(100, 30, 150, 25));
    jLabelValName.setFont(GuiFont.FONT_PLAIN);
    jLabelLibName.setLabelFor(jLabelValName);
    add(jLabelValName);

    // URL
    jLabelLibURL = new JLabel("URL :");
    jLabelLibURL.setBounds(new Rectangle(5, 60, 90, 25));
    jLabelLibURL.setFont(GuiFont.FONT_PLAIN);
    jLabelLibURL.setHorizontalAlignment(SwingConstants.RIGHT);
    add(jLabelLibURL);
    jTextFieldURL = new JTextField("http://...");
    jTextFieldURL.setBounds(new Rectangle(100, 60, 220, 25));
    jTextFieldURL.setFont(GuiFont.FONT_PLAIN);
    add(jTextFieldURL);

    // Lib
    jLabelLib = new JLabel("<html><body>avec #zoom# : niveau de zoom<br>avec #x# : x<br>avec #y# : y<br></body></html>");
    jLabelLib.setBounds(new Rectangle(100, 90, 200, 60));
    jLabelLib.setFont(GuiFont.FONT_ITALIC);
    jLabelLib.setHorizontalAlignment(SwingConstants.RIGHT);
    add(jLabelLib);

    // Zoom niveaux
    jLabelLibZoomMin = new JLabel("Zoom min :");
    jLabelLibZoomMin.setBounds(new Rectangle(5, 150, 90, 25));
    jLabelLibZoomMin.setFont(GuiFont.FONT_PLAIN);
    jLabelLibZoomMin.setHorizontalAlignment(SwingConstants.RIGHT);
    add(jLabelLibZoomMin);
    jTextFieldZoomMin = new JTextFieldLength(1);
    jTextFieldZoomMin.setBounds(new Rectangle(100, 150, 30, 25));
    jTextFieldZoomMin.setFont(GuiFont.FONT_PLAIN);
    add(jTextFieldZoomMin);

    jLabelLibZoomMax = new JLabel("Zoom max :");
    jLabelLibZoomMax.setBounds(new Rectangle(5, 180, 90, 25));
    jLabelLibZoomMax.setFont(GuiFont.FONT_PLAIN);
    jLabelLibZoomMax.setHorizontalAlignment(SwingConstants.RIGHT);
    add(jLabelLibZoomMax);
    jTextFieldZoomMax = new JTextFieldLength(1);
    jTextFieldZoomMax.setBounds(new Rectangle(100, 180, 30, 25));
    jTextFieldZoomMax.setFont(GuiFont.FONT_PLAIN);
    add(jTextFieldZoomMax);

    // Bouton
    jButtonTest = new JButton();
    jButtonTest.setBounds(new Rectangle(40, 220, 25, 25));
    jButtonTest.setFont(GuiFont.FONT_PLAIN);
    jButtonTest.setIcon(ImagesRepository.getImageIcon("refresh16.png"));
    add(jButtonTest);
    jButtonTest.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        updateMap();
      }
    });

    // Map
    mapViewer = new JTurtleMapViewerZoom();
    mapViewer.setBorder(BorderFactory.createLineBorder(Color.black));
    mapViewer.getMainMap().setCenterPosition(GEO_DEFAULT);
    mapViewer.setBounds(new Rectangle(80, 220, 300, 250));
    add(mapViewer);

    performedLanguage(LanguageManager.getManager().getCurrentLang());
    LanguageManager.getManager().addLanguageListener(this);
  }

  @Override
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

  @Override
  public void completedRemoveLanguageListener() {
  }

  private void performedLanguage(ILanguage lang) {
    rb = ResourceBundleUtility.getBundle(lang, JPanelPrefMap.class);

    jLabelLibName.setText(rb.getString("name"));
    jLabelLibZoomMin.setText(rb.getString("zoomMin"));
    jLabelLibZoomMax.setText(rb.getString("zoomMax"));
    jLabelLib.setText(rb.getString("lib"));
  }

  /**
   * Ajout des listeners de l'&eacute;quipement.
   */
  public void updateView(final DataMap data) {
    removeEvents();
    this.data = data;

    jTextFieldZoomMax.setEnabled(data.isEditable());
    jTextFieldZoomMin.setEnabled(data.isEditable());
    jTextFieldURL.setEnabled(data.isEditable());

    jLabelValName.setText(data.getName());
    jTextFieldURL.setText(data.getUrl());
    jTextFieldZoomMax.setText("" + data.getZoomMax());
    jTextFieldZoomMin.setText("" + data.getZoomMin());

    jButtonTest.setEnabled(data.isEditable());
    updateMap();

    if (data.isEditable()) {
      addEvents(data);
    }
  }

  private void updateMap() {
    try {
      if (data == null) {
        mapViewer.getMainMap().setTileFactory(new EmptyTileFactory());
        return;
      }

      AbstractTileFactoryExtended tileFactory;
      if (!data.isEditable()) {
        tileFactory = data.getTileMap();
      }
      else {
        String name = jLabelValName.getName();
        int zoomMin = Integer.parseInt(jTextFieldZoomMin.getText());
        int zoomMax = Integer.parseInt(jTextFieldZoomMax.getText());
        if (zoomMin < 1) {
          zoomMin = 1;
        }
        if (zoomMax < 2) {
          zoomMin = 2;
        }
        if (zoomMax < zoomMin) {
          jTextFieldZoomMax.setText(Integer.toString(zoomMin));
          docZoomMax.fireUpdate();
          jTextFieldZoomMin.setText(Integer.toString(zoomMax));
          docZoomMin.fireUpdate();
        }
        String baseURL = jTextFieldURL.getText();
        if (baseURL == null || baseURL.isEmpty()) {
          mapViewer.getMainMap().setTileFactory(new EmptyTileFactory());
          return;
        }
        UserDefineMapTileProviderInfo tileInfo = new UserDefineMapTileProviderInfo(baseURL,
                                                                                   name,
                                                                                   zoomMin,
                                                                                   zoomMax);
        tileFactory = new UserDefineMapTileFactory(tileInfo, baseURL);
      }
      mapViewer.getMainMap().setTileFactory(tileFactory);
      mapViewer.setZoom(4);
      mapViewer.getMainMap().setCenterPosition(GEO_DEFAULT);
    }
    catch (NumberFormatException e) {
      mapViewer.getMainMap().setTileFactory(new EmptyTileFactory());
    }
  }

  /**
   * Ajout des listeners de l'&eacute;quipement.
   */
  private void addEvents(final DataMap data) {
    try {
      docURL = new GenericModelDocListener(jTextFieldURL,
                                           data,
                                           "setUrl",
                                           String.class,
                                           true);
      jTextFieldURL.getDocument().addDocumentListener(docURL);
      docZoomMax = new GenericModelDocListener(jTextFieldZoomMax,
                                               data,
                                               "setZoomMax",
                                               Integer.TYPE,
                                               true);
      jTextFieldZoomMax.getDocument().addDocumentListener(docZoomMax);

      docZoomMin = new GenericModelDocListener(jTextFieldZoomMin,
                                               data,
                                               "setZoomMin",
                                               Integer.TYPE,
                                               true);
      jTextFieldZoomMin.getDocument().addDocumentListener(docZoomMin);
    }
    catch (NoSuchMethodException e) {
      log.error("", e);
    }
  }

  /**
   * Suppression des listeners de l'&eacute;quipement.
   */
  private void removeEvents() {
    if (docURL != null) {
      jTextFieldURL.getDocument().removeDocumentListener(docURL);
      jTextFieldZoomMax.getDocument().removeDocumentListener(docZoomMax);
      jTextFieldZoomMin.getDocument().removeDocumentListener(docZoomMin);
      
      jTextFieldURL.removeFocusListener(docURL);
      jTextFieldZoomMax.removeFocusListener(docZoomMax);
      jTextFieldZoomMin.removeFocusListener(docZoomMin);
    }
    docURL = null;
    docZoomMax = null;
    docZoomMin = null;
    data = null;
  }

}

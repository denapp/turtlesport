package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.map.AbstractTileFactoryExtended;
import fr.turtlesport.map.AllMapsFactory;
import fr.turtlesport.map.DataMap;
import fr.turtlesport.map.MapConfiguration;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.component.PanelPrefListener;
import fr.turtlesport.ui.swing.img.diagram.ImagesDiagramRepository;
import fr.turtlesport.ui.swing.model.ModelPref;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelPrefMapProvider extends JPanelPref implements
                                                     LanguageListener,
                                                     PanelPrefListener {

  private JPanelPrefTitle        jPanelTitle;

  private JPanel                 jPanelCenter;

  private ResourceBundle         rb;

  private JPanel                 jPanelListMapProvider;

  private TitledBorder           borderPanelListMapProvider;

  private JScrollPane            jScrollPaneProvider;

  private JTable                 jTableMapProvider;

  private JPanel                 jPanelButtons;

  private JButton                jButtonAdd;

  private JButton                jButtonDelete;

  private Dimension              dimButton = new Dimension(20, 20);

  private MyDefaultTableModelMap tableModelMap;

  private JPanelMapProvider      jPanelMapProvider;

  /**
   * 
   */
  public JPanelPrefMapProvider() {
    super();
    initialize();

    // Maps par defaut
    for (String name : AllMapsFactory.getInstance().getTileNames()) {
      AbstractTileFactoryExtended tileMap = AllMapsFactory.getInstance()
          .retreiveTileFactory(name);
      if (!tileMap.isEditable()) {
        DataMap map = new DataMap(tileMap.getName(), tileMap.isEditable());
        map.setTileMap(tileMap);
        map.setUrl(tileMap.getBaseURL());
        map.setZoomMax(tileMap.getInfo().getMaximumZoomLevel());
        map.setZoomMin(tileMap.getInfo().getMinimumZoomLevel());
        addMap(map);
      }
    }
    // Les maps users
    for (DataMap map : MapConfiguration.getConfig().getMapsTransaction()
        .getMaps()) {
      addMap(map);
    }

    // positionnement sur la 2eme map (apres mercator)
    jTableMapProvider.setRowSelectionInterval(1, 1);
    jTableMapProvider.requestFocus();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.ui.swing.component.PanelPrefListener#viewChanged()
   */
  public void viewChanged() {
    // commit des changements
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
    rb = ResourceBundleUtility.getBundle(lang, ModelPref.class);
    jPanelTitle.setTitle(rb.getString("modelPrefMapProvider"));
    rb = ResourceBundleUtility.getBundle(lang, JPanelPrefMap.class);
    borderPanelListMapProvider.setTitle(rb.getString("title"));
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    BorderLayout borderLayout = new BorderLayout();
    borderLayout.setHgap(5);
    borderLayout.setVgap(5);
    this.setLayout(borderLayout);
    this.add(getJPanelTitle(), BorderLayout.NORTH);
    this.add(getJPanelCenter(), BorderLayout.CENTER);

    // Evenement
    jButtonAdd.addActionListener(new AddMapActionListener());
    jButtonDelete.addActionListener(new DeleteMapActionListener());

    jTableMapProvider.getSelectionModel()
        .addListSelectionListener(new ListSelectionListener() {
          public void valueChanged(ListSelectionEvent e) {
            int row = jTableMapProvider.getSelectedRow();
            if (row == -1) {
              jButtonDelete.setEnabled(true);
              return;
            }

            DataMap data = (DataMap) tableModelMap.getValueAt(row, 0);
            jButtonDelete.setEnabled(data.isEditable());
            jPanelMapProvider.updateView(data);
          }

        });

    performedLanguage(LanguageManager.getManager().getCurrentLang());
    LanguageManager.getManager().addLanguageListener(this);
  }

  /**
   * This method initializes jPanelTitle
   * 
   * @return javax.swing.JPanel
   */
  private JPanelPrefTitle getJPanelTitle() {
    if (jPanelTitle == null) {
      jPanelTitle = new JPanelPrefTitle("Fournisseur12");
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
      jPanelCenter = new JPanel();
      jPanelCenter.setLayout(new BoxLayout(jPanelCenter, BoxLayout.X_AXIS));

      jPanelMapProvider = new JPanelMapProvider();
      jPanelCenter.add(getJPanelListMapProvider());
      jPanelCenter.add(jPanelMapProvider);
    }
    return jPanelCenter;
  }

  private JPanel getJPanelListMapProvider() {
    if (jPanelListMapProvider == null) {
      jPanelListMapProvider = new JPanel();
      jPanelListMapProvider.setSize(180, 600);
      jPanelListMapProvider.setMinimumSize(new Dimension(180, 600));
      jPanelListMapProvider.setMaximumSize(new Dimension(180, 600));

      borderPanelListMapProvider = BorderFactory
          .createTitledBorder(null,
                              "Carte",
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null);
      jPanelListMapProvider.setBorder(borderPanelListMapProvider);
      jPanelListMapProvider.setLayout(new BorderLayout());
      jPanelListMapProvider.add(getJScrollPaneMapProvider(),
                                BorderLayout.CENTER);
      jPanelListMapProvider.add(getJPanelButtons(), BorderLayout.NORTH);
    }
    return jPanelListMapProvider;
  }

  private JScrollPane getJScrollPaneMapProvider() {
    if (jScrollPaneProvider == null) {
      jTableMapProvider = new JTable();
      jTableMapProvider.setTableHeader(null);
      jTableMapProvider.setShowGrid(false);
      jTableMapProvider.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      jTableMapProvider.setFont(GuiFont.FONT_PLAIN);
      tableModelMap = new MyDefaultTableModelMap();
      jTableMapProvider.setModel(tableModelMap);

      jScrollPaneProvider = new JScrollPane(jTableMapProvider);
      jScrollPaneProvider.setColumnHeader(null);
      Dimension dim = new Dimension(100, 100);
      jScrollPaneProvider.setPreferredSize(dim);
      jScrollPaneProvider.setMinimumSize(dim);
    }
    return jScrollPaneProvider;
  }

  private JPanel getJPanelButtons() {
    if (jPanelButtons == null) {
      FlowLayout flowLayout = new FlowLayout();
      flowLayout.setAlignment(FlowLayout.RIGHT);
      flowLayout.setVgap(0);
      jPanelButtons = new JPanel();
      jPanelButtons.setLayout(flowLayout);
      jPanelButtons.add(getJButtonAdd(), null);
      jPanelButtons.add(getJButtonDelete(), null);
    }
    return jPanelButtons;
  }

  private JButton getJButtonAdd() {
    if (jButtonAdd == null) {
      jButtonAdd = new JButton();
      jButtonAdd.setIcon(ImagesDiagramRepository.getImageIcon("plus.png"));
      jButtonAdd.setMargin(new Insets(2, 2, 2, 2));
      jButtonAdd.setMaximumSize(dimButton);
      jButtonAdd.setMinimumSize(dimButton);
      jButtonAdd.setOpaque(false);
      jButtonAdd.setPreferredSize(dimButton);
    }
    return jButtonAdd;
  }

  private JButton getJButtonDelete() {
    if (jButtonDelete == null) {
      jButtonDelete = new JButton();
      jButtonDelete.setIcon(ImagesDiagramRepository.getImageIcon("minus.png"));
      jButtonDelete.setMargin(new Insets(2, 2, 2, 2));
      jButtonDelete.setMaximumSize(dimButton);
      jButtonDelete.setMinimumSize(dimButton);
      jButtonDelete.setOpaque(false);
      jButtonDelete.setPreferredSize(dimButton);
    }
    return jButtonDelete;
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class MyDefaultTableModelMap extends DefaultTableModel {

    public MyDefaultTableModelMap() {
      super();
    }

    public boolean contains(String value) {
      if (value != null) {
        for (int i = 0; i < getRowCount(); i++) {
          if (value.equals(getValueAt(i, 0).toString())) {
            return true;
          }
        }
      }
      return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.DefaultTableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
      return 1;
    }

  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class AddMapActionListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      while (true) {
        String name = JShowMessage
            .showInputDialog(rb.getString("dialogName"),
                             rb.getString("dialogAddTitle"));
        if (name == null || "".equals(name.trim())) {
          break;
        }
        if (name.length() > 100) {
          name = name.substring(0, 100);
        }
        if (tableModelMap.contains(name.trim())) {
          JShowMessage.error(MessageFormat.format(rb
              .getString("errorMapAlreadyExist"), name.trim()));
        }
        else {
          DataMap map = new DataMap(name, true);
          MapConfiguration.getConfig().addMap(map);
          addMap(map);
          // on se positionne sur la map
          jTableMapProvider.getSelectionModel()
              .setSelectionInterval(tableModelMap.getRowCount() - 1,
                                    tableModelMap.getRowCount() - 1);
          break;
        }
      }
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class DeleteMapActionListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      if (jTableMapProvider.getSelectedRow() != -1) {
        int row = jTableMapProvider.getSelectedRow();
        DataMap map = (DataMap) tableModelMap.getValueAt(row, 0);
        tableModelMap.removeRow(row);

        // on se re-positionne sur la liste
        if (jTableMapProvider.getRowCount() > 0) {
          if (row > 0) {
            row--;
          }
          jTableMapProvider.getSelectionModel().setSelectionInterval(row, row);
          MapConfiguration.getConfig().removeMap(map);
        }
      }
    }
  }

  /**
   * Ajoute une activit&eacute;.
   * 
   * @param a
   *          activit&eacute;.
   */
  public void addMap(DataMap map) {
    Object[] row = { map };
    tableModelMap.addRow(row);
  }

} // @jve:decl-index=0:visual-constraint="10,10"

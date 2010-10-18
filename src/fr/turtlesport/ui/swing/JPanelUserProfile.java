package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

import fr.turtlesport.db.AbstractDataActivity;
import fr.turtlesport.db.DataActivityBike;
import fr.turtlesport.db.DataActivityOther;
import fr.turtlesport.db.DataActivityRun;
import fr.turtlesport.db.DataEquipement;
import fr.turtlesport.db.DataUser;
import fr.turtlesport.db.DatabaseManager;
import fr.turtlesport.db.DefaultDataActivity;
import fr.turtlesport.db.EquipementTableManager;
import fr.turtlesport.db.UserActivityTableManager;
import fr.turtlesport.db.UserTableManager;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.component.JShowMessage;
import fr.turtlesport.ui.swing.img.diagram.ImagesDiagramRepository;
import fr.turtlesport.ui.swing.model.ModelActivity;
import fr.turtlesport.ui.swing.model.ModelAthlete;
import fr.turtlesport.ui.swing.model.ModelEquipement;
import fr.turtlesport.unit.event.UnitEvent;
import fr.turtlesport.unit.event.UnitListener;
import fr.turtlesport.unit.event.UnitManager;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author denis
 * 
 */
public class JPanelUserProfile extends JPanel implements LanguageListener,
                                             UnitListener {
  private static TurtleLogger           log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JPanelUserProfile.class);
  }

  private JPanelUserAthlete             jPanelAthlete;

  private JPanel                        jPanelActivities;

  private JPanelUserActivity            jPanelActivity;

  private JButton                       jButtonSave;

  private JPanel                        jPanelButtons;

  private JScrollPane                   jScrollPaneActivity;

  private JTable                        jTableActivity;

  private JPanel                        jPanelButtonsActivityList;

  private JButton                       jButtonActivityListAdd;

  private JButton                       jButtonActivityListDelete;

  private JPanel                        jPanelListActivity;

  private TitledBorder                  borderPanelActivityList;

  private JPanel                        jPanelEquipements;

  private JPanel                        jPanelListEquipement;

  private TitledBorder                  borderPanelAthletesList;

  private TitledBorder                  borderPanelEquipementList;

  private JScrollPane                   jScrollPaneAthlete;

  private JScrollPane                   jScrollPaneEquipement;

  private JButton                       jButtonEquipementListAdd;

  private JButton                       jButtonEquipementListDelete;

  private JPanel                        jPanelButtonsEquipementList;

  private JTable                        jTableEquipement;

  private JPanel                        jPanelCenter;

  private JPanelUserEquipement          jPanelEquipement;

  private Dimension                     dimButton = new Dimension(20, 20);

  private JPanel                        jPanelAthletes;

  private JPanel                        jPanelListAthlete;

  private JTable                        jTableAthlete;

  private JPanel                        jPanelButtonsAthleteList;

  private JButton                       jButtonAthleteListAdd;

  private JButton                       jButtonAthleteListDelete;

  // Model
  private ResourceBundle                rb;

  private MyDefaultTableModelActivity   tableModelActivity;

  private MyDefaultTableModelEquipement tableModelEquipement;

  private MyDefaultTableModelAthlete    tableModelAthlete;

  /**
   * 
   */
  public JPanelUserProfile() {
    super();
    initialize();
  }

  public void setDefaultActivity(AbstractDataActivity data, boolean isDefault) {
    if (isDefault) {
      for (int i = 0; i < tableModelActivity.getRowCount(); i++) {
        ((Activity) tableModelActivity.getValueAt(i, 0)).model
            .getDataActivity().setDefault(false);
      }
    }
    data.setDefault(isDefault);
  }

  public void setDefaultEquipement(DataEquipement data, boolean isDefault) {
    if (isDefault) {
      for (int i = 0; i < tableModelEquipement.getRowCount(); i++) {
        ((Equipement) tableModelEquipement.getValueAt(i, 0)).model.getData()
            .setDefault(false);
      }
    }
    data.setDefault(isDefault);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.turtlesport.unit.event.UnitListener#unitChanged(fr.turtlesport.unit.
   * event.UnitEvent)
   */
  public void unitChanged(UnitEvent e) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.unit.event.UnitListener#completedRemoveUnitListener()
   */
  public void completedRemoveUnitListener() {
    UnitManager.getManager().removeUnitListener(jPanelAthlete);
    UnitManager.getManager().removeUnitListener(jPanelEquipement);
    UnitManager.getManager().removeUnitListener(jPanelActivity);
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
    LanguageManager.getManager().removeLanguageListener(jPanelAthlete);
    LanguageManager.getManager().removeLanguageListener(jPanelEquipement);
    LanguageManager.getManager().removeLanguageListener(jPanelActivity);
  }

  private void performedLanguage(ILanguage lang) {
    rb = ResourceBundleUtility.getBundle(lang, getClass());

    borderPanelActivityList.setTitle(rb.getString("borderPanelActivityList"));
    borderPanelAthletesList.setTitle(rb.getString("borderPanelAthletesList"));
    borderPanelEquipementList.setTitle(rb
        .getString("borderPanelEquipementList"));

    jButtonSave.setText(rb.getString("jButtonSave"));

    repaint();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(660, 597);
    this.setLayout(new BorderLayout(0, 0));
    this.add(getJPanelCenter(), BorderLayout.CENTER);
    this.add(getJPanelButtons(), BorderLayout.SOUTH);
    this.setFont(GuiFont.FONT_PLAIN);

    // Evenement
    jTableActivity.getSelectionModel()
        .addListSelectionListener(new ListSelectionListener() {
          public void valueChanged(ListSelectionEvent e) {
            int row = jTableActivity.getSelectedRow();
            if (row == -1) {
              jButtonActivityListDelete.setEnabled(true);
              return;
            }

            Activity activity = (Activity) tableModelActivity
                .getValueAt(row, 0);
            if (jPanelActivity.getModel() != activity.model) {
              switch (activity.model.getDataActivity().getSportType()) {
                case DataActivityRun.SPORT_TYPE:
                case DataActivityBike.SPORT_TYPE:
                case DataActivityOther.SPORT_TYPE:
                  // run, bike, other ne pas supprimer
                  jButtonActivityListDelete.setEnabled(false);
                  break;

                default:
                  jButtonActivityListDelete.setEnabled(true);
                  break;
              }

              activity.model.updateView(jPanelActivity);
            }
          }

        });

    jTableEquipement.getSelectionModel()
        .addListSelectionListener(new ListSelectionListener() {
          public void valueChanged(ListSelectionEvent e) {
            int row = jTableEquipement.getSelectedRow();
            if (row == -1) {
              jButtonEquipementListDelete.setEnabled(true);
              return;
            }

            Equipement equipement = (Equipement) tableModelEquipement
                .getValueAt(row, 0);
            if (jPanelEquipement.getModel() != equipement.model) {
              equipement.model.updateView(jPanelEquipement);
            }
          }

        });

    jTableAthlete.getSelectionModel()
        .addListSelectionListener(new ListSelectionListener() {
          public void valueChanged(ListSelectionEvent e) {
            int row = jTableAthlete.getSelectedRow();
            if (row == -1) {
              jButtonAthleteListDelete.setEnabled(true);
              return;
            }

            Athlete athlete = (Athlete) tableModelAthlete.getValueAt(row, 0);
            if (jPanelAthlete.getModel() != athlete.model) {
              athlete.model.updateView(jPanelAthlete);
            }
          }

        });

    getJButtonSave().addActionListener(new SaveActionListener());

    jButtonAthleteListAdd.addActionListener(new AddAthleteActionListener());
    jButtonAthleteListDelete
        .addActionListener(new DeleteAthleteActionListener());

    jButtonActivityListAdd.addActionListener(new AddActivityActionListener());
    jButtonActivityListDelete
        .addActionListener(new DeleteActivityActionListener());

    jButtonEquipementListAdd
        .addActionListener(new AddEquipementActionListener());
    jButtonEquipementListDelete
        .addActionListener(new DeleteEquipementActionListener());

    LanguageManager.getManager().addLanguageListener(this);
    performedLanguage(LanguageManager.getManager().getCurrentLang());
    UnitManager.getManager().addUnitListener(this);
  }

  /**
   * @throws SQLException
   */
  public void updateView() throws SQLException {
    // recuperation des utilisateurs.
    // --------------------------------------------------
    UserTableManager.getInstance().logTable();
    List<DataUser> listUser = UserTableManager.getInstance().retreive();
    for (DataUser d : listUser) {
      addAthlete(d);
    }

    // Recuperation des activites
    // --------------------------------------------
    List<AbstractDataActivity> listAct = UserActivityTableManager.getInstance()
        .retreive();
    boolean hasRun = false;
    boolean hasBike = false;
    boolean hasOther = false;
    for (AbstractDataActivity d : listAct) {
      switch (d.getSportType()) {
        case DataActivityRun.SPORT_TYPE:
          hasRun = true;
          break;
        case DataActivityBike.SPORT_TYPE:
          hasBike = true;
          break;
        case DataActivityOther.SPORT_TYPE:
          hasOther = true;
          break;
        default:
          break;
      }
    }
    // run
    if (!hasRun) {
      listAct.add(new DataActivityRun());
    }
    // bike
    if (!hasBike) {
      listAct.add(new DataActivityBike());
    }
    // other
    if (!hasOther) {
      listAct.add(new DataActivityOther());
    }
    AbstractDataActivity[] datas = new AbstractDataActivity[listAct.size()];
    listAct.toArray(datas);
    Arrays.sort(datas);

    for (AbstractDataActivity d : datas) {
      addActivity(d);
    }

    // Recuperation des equipements
    List<DataEquipement> listEq = EquipementTableManager.getInstance()
        .retreive();
    for (DataEquipement e : listEq) {
      addEquipement(e);
    }

    // on se positionne sur le premier athlete
    if (tableModelAthlete.getRowCount() > 0) {
      jTableAthlete.getSelectionModel().setSelectionInterval(0, 0);
    }

    // on se positionne sur le premier sport
    jTableActivity.getSelectionModel().setSelectionInterval(0, 0);

    // on se positionne sur le premier equipement
    if (tableModelEquipement.getRowCount() > 0) {
      jTableEquipement.getSelectionModel().setSelectionInterval(0, 0);
    }
  }

  private JPanel getJPanelCenter() {
    if (jPanelCenter == null) {
      jPanelCenter = new JPanel();
      jPanelCenter.setLayout(new BoxLayout(jPanelCenter, BoxLayout.Y_AXIS));
      jPanelCenter.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
      jPanelCenter.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
      jPanelCenter.setFont(GuiFont.FONT_PLAIN);

      jPanelCenter.add(getJPanelAthletes());
      jPanelCenter.add(getJPanelEquipements());
      jPanelCenter.add(getJPanelActivities());
    }
    return jPanelCenter;
  }

  private JPanel getJPanelActivities() {
    if (jPanelActivities == null) {
      jPanelActivities = new JPanel();
      jPanelActivities.setLayout(new BoxLayout(jPanelActivities,
                                               BoxLayout.X_AXIS));
      jPanelActivities.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
      jPanelActivities.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
      jPanelActivities.setFont(GuiFont.FONT_PLAIN);

      jPanelActivity = new JPanelUserActivity(this);
      jPanelActivities.add(getJPanelListActivity());
      jPanelActivities.add(jPanelActivity);
    }
    return jPanelActivities;
  }

  private JScrollPane getJScrollPaneAthlete() {
    if (jScrollPaneAthlete == null) {
      jTableAthlete = new JTable();
      jTableAthlete.setTableHeader(null);
      jTableAthlete.setShowGrid(false);
      jTableAthlete.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      jTableAthlete.setFont(GuiFont.FONT_PLAIN);
      tableModelAthlete = new MyDefaultTableModelAthlete();
      jTableAthlete.setModel(tableModelAthlete);

      jScrollPaneAthlete = new JScrollPane(jTableAthlete);
      jScrollPaneAthlete.setColumnHeader(null);
      Dimension dim = new Dimension(250, 100);
      jScrollPaneAthlete.setPreferredSize(dim);
      jScrollPaneAthlete.setMinimumSize(dim);
    }
    return jScrollPaneAthlete;
  }

  private JScrollPane getJScrollPaneActivity() {
    if (jScrollPaneActivity == null) {
      jTableActivity = new JTable();
      jTableActivity.setTableHeader(null);
      jTableActivity.setShowGrid(false);
      jTableActivity.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      jTableActivity.setFont(GuiFont.FONT_PLAIN);
      tableModelActivity = new MyDefaultTableModelActivity();
      jTableActivity.setModel(tableModelActivity);

      jScrollPaneActivity = new JScrollPane(jTableActivity);
      jScrollPaneActivity.setColumnHeader(null);
      Dimension dim = new Dimension(230, 100);
      jScrollPaneActivity.setPreferredSize(dim);
      jScrollPaneActivity.setMinimumSize(dim);
    }
    return jScrollPaneActivity;
  }

  private JScrollPane getJScrollPaneEquipement() {
    if (jScrollPaneEquipement == null) {
      jTableEquipement = new JTable();
      jTableEquipement.setTableHeader(null);
      jTableEquipement.setShowGrid(false);
      jTableEquipement.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      jTableEquipement.setFont(GuiFont.FONT_PLAIN);
      tableModelEquipement = new MyDefaultTableModelEquipement();
      jTableEquipement.setModel(tableModelEquipement);

      jScrollPaneEquipement = new JScrollPane(jTableEquipement);
      jScrollPaneEquipement.setColumnHeader(null);
      Dimension dim = new Dimension(230, 100);
      jScrollPaneEquipement.setPreferredSize(dim);
      jScrollPaneEquipement.setMinimumSize(dim);
    }
    return jScrollPaneEquipement;
  }

  private JPanel getJPanelAthletes() {
    if (jPanelAthletes == null) {
      jPanelAthletes = new JPanel();
      jPanelAthletes.setLayout(new BoxLayout(jPanelAthletes, BoxLayout.X_AXIS));
      jPanelAthletes.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
      jPanelAthletes.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
      jPanelAthletes.setFont(GuiFont.FONT_PLAIN);

      jPanelAthlete = new JPanelUserAthlete(this);
      jPanelAthletes.add(getJPanelListAthletes());
      jPanelAthletes.add(jPanelAthlete);
    }
    return jPanelAthletes;
  }

  private JPanel getJPanelListAthletes() {
    if (jPanelListAthlete == null) {
      jPanelListAthlete = new JPanel();
      borderPanelAthletesList = BorderFactory
          .createTitledBorder(null,
                              "Athletes",
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null);
      jPanelListAthlete.setBorder(borderPanelAthletesList);
      jPanelListAthlete.setLayout(new BorderLayout());
      jPanelListAthlete.add(getJScrollPaneAthlete(), BorderLayout.CENTER);
      jPanelListAthlete.add(getJPanelButtonsAthleteList(), BorderLayout.NORTH);
    }
    return jPanelListAthlete;
  }

  private JPanel getJPanelEquipements() {
    if (jPanelEquipements == null) {
      jPanelEquipements = new JPanel();
      jPanelEquipements.setLayout(new BoxLayout(jPanelEquipements,
                                                BoxLayout.X_AXIS));
      jPanelEquipements.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
      jPanelEquipements.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
      jPanelEquipements.setFont(GuiFont.FONT_PLAIN);

      jPanelEquipement = new JPanelUserEquipement(this);
      jPanelEquipements.add(getJPanelListEquipement());
      jPanelEquipements.add(jPanelEquipement);
    }
    return jPanelEquipements;
  }

  private JPanel getJPanelListEquipement() {
    if (jPanelListEquipement == null) {
      jPanelListEquipement = new JPanel();
      borderPanelEquipementList = BorderFactory
          .createTitledBorder(null,
                              "Equipements",
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null);
      jPanelListEquipement.setBorder(borderPanelEquipementList);
      jPanelListEquipement.setLayout(new BorderLayout());
      jPanelListEquipement.add(getJScrollPaneEquipement(), BorderLayout.CENTER);
      jPanelListEquipement.add(getJPanelButtonsEquipementList(),
                               BorderLayout.NORTH);
    }
    return jPanelListEquipement;
  }

  private JPanel getJPanelListActivity() {
    if (jPanelListActivity == null) {
      jPanelListActivity = new JPanel();
      Dimension dim = new Dimension(200, 100);
      jPanelListActivity.setPreferredSize(dim);
      borderPanelActivityList = BorderFactory
          .createTitledBorder(null,
                              "Activites",
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null);
      jPanelListActivity.setBorder(borderPanelActivityList);
      jPanelListActivity.setLayout(new BorderLayout());
      jPanelListActivity.add(getJScrollPaneActivity(), BorderLayout.CENTER);
      jPanelListActivity
          .add(getJPanelButtonsActivityList(), BorderLayout.NORTH);
    }
    return jPanelListActivity;
  }

  private JPanel getJPanelButtonsAthleteList() {
    if (jPanelButtonsAthleteList == null) {
      FlowLayout flowLayout = new FlowLayout();
      flowLayout.setAlignment(FlowLayout.RIGHT);
      flowLayout.setVgap(0);
      jPanelButtonsAthleteList = new JPanel();
      jPanelButtonsAthleteList.setLayout(flowLayout);
      jPanelButtonsAthleteList.add(getJButtonAthleteListAdd(), null);
      jPanelButtonsAthleteList.add(getJButtonAthleteListDelete(), null);
    }
    return jPanelButtonsAthleteList;
  }

  private JPanel getJPanelButtonsEquipementList() {
    if (jPanelButtonsEquipementList == null) {
      FlowLayout flowLayout = new FlowLayout();
      flowLayout.setAlignment(FlowLayout.RIGHT);
      flowLayout.setVgap(0);
      jPanelButtonsEquipementList = new JPanel();
      jPanelButtonsEquipementList.setLayout(flowLayout);
      jPanelButtonsEquipementList.add(getJButtonEquipementListAdd(), null);
      jPanelButtonsEquipementList.add(getJButtonEquipementListDelete(), null);
    }
    return jPanelButtonsEquipementList;
  }

  private JPanel getJPanelButtonsActivityList() {
    if (jPanelButtonsActivityList == null) {
      FlowLayout flowLayout = new FlowLayout();
      flowLayout.setAlignment(FlowLayout.RIGHT);
      flowLayout.setVgap(0);
      jPanelButtonsActivityList = new JPanel();
      jPanelButtonsActivityList.setLayout(flowLayout);
      jPanelButtonsActivityList.add(getJButtonActivityListAdd(), null);
      jPanelButtonsActivityList.add(getJButtonActivityListDelete(), null);
    }
    return jPanelButtonsActivityList;
  }

  private JButton getJButtonActivityListAdd() {
    if (jButtonActivityListAdd == null) {
      jButtonActivityListAdd = new JButton();
      jButtonActivityListAdd.setIcon(ImagesDiagramRepository
          .getImageIcon("plus.png"));
      jButtonActivityListAdd.setMargin(new Insets(2, 2, 2, 2));
      jButtonActivityListAdd.setMaximumSize(dimButton);
      jButtonActivityListAdd.setMinimumSize(dimButton);
      jButtonActivityListAdd.setOpaque(false);
      jButtonActivityListAdd.setPreferredSize(dimButton);
    }
    return jButtonActivityListAdd;
  }

  private JButton getJButtonActivityListDelete() {
    if (jButtonActivityListDelete == null) {
      jButtonActivityListDelete = new JButton();
      jButtonActivityListDelete.setIcon(ImagesDiagramRepository
          .getImageIcon("minus.png"));
      jButtonActivityListDelete.setMargin(new Insets(2, 2, 2, 2));
      jButtonActivityListDelete.setMaximumSize(dimButton);
      jButtonActivityListDelete.setMinimumSize(dimButton);
      jButtonActivityListDelete.setOpaque(false);
      jButtonActivityListDelete.setPreferredSize(dimButton);
    }
    return jButtonActivityListDelete;
  }

  private JButton getJButtonAthleteListAdd() {
    if (jButtonAthleteListAdd == null) {
      jButtonAthleteListAdd = new JButton();
      jButtonAthleteListAdd.setIcon(ImagesDiagramRepository
          .getImageIcon("plus.png"));
      jButtonAthleteListAdd.setMargin(new Insets(2, 2, 2, 2));
      jButtonAthleteListAdd.setMaximumSize(dimButton);
      jButtonAthleteListAdd.setMinimumSize(dimButton);
      jButtonAthleteListAdd.setOpaque(false);
      jButtonAthleteListAdd.setPreferredSize(dimButton);
    }
    return jButtonAthleteListAdd;
  }

  private JButton getJButtonAthleteListDelete() {
    if (jButtonAthleteListDelete == null) {
      jButtonAthleteListDelete = new JButton();
      jButtonAthleteListDelete.setIcon(ImagesDiagramRepository
          .getImageIcon("minus.png"));
      jButtonAthleteListDelete.setMargin(new Insets(2, 2, 2, 2));
      jButtonAthleteListDelete.setMaximumSize(dimButton);
      jButtonAthleteListDelete.setMinimumSize(dimButton);
      jButtonAthleteListDelete.setOpaque(false);
      jButtonAthleteListDelete.setPreferredSize(dimButton);
    }
    return jButtonAthleteListDelete;
  }

  private JButton getJButtonEquipementListAdd() {
    if (jButtonEquipementListAdd == null) {
      jButtonEquipementListAdd = new JButton();
      jButtonEquipementListAdd.setIcon(ImagesDiagramRepository
          .getImageIcon("plus.png"));
      jButtonEquipementListAdd.setMargin(new Insets(2, 2, 2, 2));
      jButtonEquipementListAdd.setMaximumSize(dimButton);
      jButtonEquipementListAdd.setMinimumSize(dimButton);
      jButtonEquipementListAdd.setOpaque(false);
      jButtonEquipementListAdd.setPreferredSize(dimButton);
    }
    return jButtonEquipementListAdd;
  }

  private JButton getJButtonEquipementListDelete() {
    if (jButtonEquipementListDelete == null) {
      jButtonEquipementListDelete = new JButton();
      jButtonEquipementListDelete.setIcon(ImagesDiagramRepository
          .getImageIcon("minus.png"));
      jButtonEquipementListDelete.setMargin(new Insets(2, 2, 2, 2));
      jButtonEquipementListDelete.setMaximumSize(dimButton);
      jButtonEquipementListDelete.setMinimumSize(dimButton);
      jButtonEquipementListDelete.setOpaque(false);
      jButtonEquipementListDelete.setPreferredSize(dimButton);
    }
    return jButtonEquipementListDelete;
  }

  private JButton getJButtonSave() {
    if (jButtonSave == null) {
      jButtonSave = new JButton();
      jButtonSave.setText("Sauvegarder");
      jButtonSave.setFont(GuiFont.FONT_PLAIN);
    }
    return jButtonSave;
  }

  /**
   * This method initializes jPanelButtons
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelButtons() {
    if (jPanelButtons == null) {
      FlowLayout flowLayout = new FlowLayout();
      flowLayout.setAlignment(FlowLayout.RIGHT);
      flowLayout.setVgap(0);
      jPanelButtons = new JPanel();
      jPanelButtons.setBorder(BorderFactory
          .createTitledBorder(null,
                              "",
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_BOLD,
                              null));
      jPanelButtons.setLayout(flowLayout);
      jPanelButtons.add(getJButtonSave(), null);
    }
    return jPanelButtons;
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class SaveActionListener implements ActionListener {

    public void actionPerformed(ActionEvent actionevent) {
      MainGui.getWindow().beforeRunnableSwing();

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          try {
            if (jPanelAthlete.getJLabelLastName().getText() == null
                || ""
                    .equals(jPanelAthlete.getJLabelLastName().getText().trim())) {
              JShowMessage.error(rb.getString("errorLastName"));
              jPanelAthlete.getJLabelLastName().requestFocusInWindow();
              MainGui.getWindow().afterRunnableSwing();
              return;
            }
            if (jPanelAthlete.getJLabelFirstName().getText() == null
                || "".equals(jPanelAthlete.getJLabelFirstName().getText()
                    .trim())) {
              JShowMessage.error(rb.getString("errorFirstName"));
              jPanelAthlete.getJLabelFirstName().requestFocusInWindow();
              MainGui.getWindow().afterRunnableSwing();
              return;
            }

            // sauvegarde
            save();

            // mis a jour des utilisateurs
            MainGui.getWindow().fireUsers();
          }
          catch (SQLException e) {
            log.error("", e);
            JShowMessage.error(rb.getString("errorSave"));
          }
          catch (RuntimeException e) {
            log.error("", e);
            JShowMessage.error(rb.getString("errorSave"));
          }
          MainGui.getWindow().afterRunnableSwing();
        }

        private void save() throws SQLException {
          // Debut de sauvegarde
          DatabaseManager.beginTransaction();

          // Sauvegarde des utilisateurs
          List<DataUser> listUser = new ArrayList<DataUser>();
          for (int i = 0; i < tableModelAthlete.getRowCount(); i++) {
            Athlete a = (Athlete) tableModelAthlete.getValueAt(i, 0);
            a.model.beforeSave();
            listUser.add(a.model.getData());
          }

          UserTableManager.getInstance().store(listUser);
          UserTableManager.getInstance().logTable();

          for (int i = 0; i < tableModelAthlete.getRowCount(); i++) {
            Athlete a = ((Athlete) tableModelAthlete.getValueAt(i, 0));
            a.model.afterSave();
          }

          // sauvegarde des equipements
          EquipementTableManager.getInstance().delete();
          for (int i = 0; i < tableModelEquipement.getRowCount(); i++) {
            Equipement e = ((Equipement) tableModelEquipement.getValueAt(i, 0));
            e.model.save();
          }

          // sauvegarde des activites
          List<AbstractDataActivity> listAct = new ArrayList<AbstractDataActivity>();
          for (int i = 0; i < tableModelActivity.getRowCount(); i++) {
            Activity a = ((Activity) tableModelActivity.getValueAt(i, 0));
            a.model.beforeSave();
            listAct.add(a.model.getDataActivity());
          }

          UserActivityTableManager.getInstance().store(listAct);
          UserActivityTableManager.getInstance().logTable();

          for (int i = 0; i < tableModelActivity.getRowCount(); i++) {
            Activity a = ((Activity) tableModelActivity.getValueAt(i, 0));
            a.model.afterSave();
          }

          // Fin de sauvegarde
          DatabaseManager.commitTransaction();
        }

      });

    }

  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class AddActivityActionListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      while (true) {
        String name = JShowMessage.showInputDialog(rb.getString("name"), rb
            .getString("addTitle"));
        if (name == null || "".equals(name.trim())) {
          break;
        }
        if (name.length() > 100) {
          name = name.substring(0, 100);
        }
        if (tableModelActivity.contains(name.trim())) {
          JShowMessage.error(MessageFormat.format(rb
              .getString("errorActivityAlreadyExist"), name.trim()));
        }
        else {
          addActivity(new DefaultDataActivity(name));
          // on se positionne sur l activite creee
          jTableActivity.getSelectionModel()
              .setSelectionInterval(tableModelActivity.getRowCount() - 1,
                                    tableModelActivity.getRowCount() - 1);
          break;
        }
      }

    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class AddEquipementActionListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      while (true) {

        String name = JShowMessage.showInputDialog(rb.getString("name"), rb
            .getString("addTitleEquipement"));
        if (name == null || "".equals(name.trim())) {
          break;
        }
        if (name.length() > 50) {
          name = name.substring(0, 50);
        }
        if (tableModelEquipement.contains(name.trim())) {
          JShowMessage.error(MessageFormat.format(rb
              .getString("errorEquipementAlreadyExist"), name.trim()));
        }
        else {
          addEquipement(new DataEquipement(name));

          // on se positionne sur l activite creee
          jTableEquipement.getSelectionModel()
              .setSelectionInterval(tableModelEquipement.getRowCount() - 1,
                                    tableModelEquipement.getRowCount() - 1);

          break;
        }
      }

    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class AddAthleteActionListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      String lastname = getValue(rb.getString("lastname"));
      if (lastname == null || "".equals(lastname.trim())) {
        return;
      }
      String firstname = getValue(rb.getString("firstname"));
      if (firstname == null || "".equals(firstname.trim())) {
        return;
      }

      String fullName = firstname + " " + lastname;
      if (tableModelAthlete.contains(fullName)) {
        JShowMessage.error(MessageFormat.format(rb
            .getString("errorAthleteAlreadyExist"), fullName));
      }
      else {
        addAthlete(new DataUser(firstname, lastname));

        // on se positionne sur le user
        jTableAthlete.getSelectionModel()
            .setSelectionInterval(tableModelAthlete.getRowCount() - 1,
                                  tableModelAthlete.getRowCount() - 1);
      }
    }

    private String getValue(String libelle) {
      String value = null;
      while (true) {
        value = JShowMessage.showInputDialog(libelle, rb
            .getString("addTitleAthlete"));
        if (value == null || "".equals(value.trim())) {
          break;
        }
        if (value.length() > 100) {
          value = value.substring(0, 100);
        }
        break;
      }
      return value;
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class DeleteActivityActionListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      if (jTableActivity.getSelectedRow() != -1) {
        int row = jTableActivity.getSelectedRow();
        tableModelActivity.removeRow(row);

        // on se re-positionne sur la liste
        if (jTableActivity.getRowCount() > 0) {
          if (row > 0) {
            row--;
          }
          jTableActivity.getSelectionModel().setSelectionInterval(row, row);
        }
      }
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class DeleteEquipementActionListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      if (jTableEquipement.getSelectedRow() != -1) {
        int row = jTableEquipement.getSelectedRow();
        tableModelEquipement.removeRow(row);

        // on se re-positionne sur la liste
        if (jTableEquipement.getRowCount() > 0) {
          if (row > 0) {
            row--;
          }
          jTableEquipement.getSelectionModel().setSelectionInterval(row, row);
        }
      }
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class DeleteAthleteActionListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      if (jTableAthlete.getSelectedRow() != -1) {
        int row = jTableAthlete.getSelectedRow();
        tableModelAthlete.removeRow(row);

        // on se re-positionne sur la liste
        if (jTableAthlete.getRowCount() > 0) {
          if (row > 0) {
            row--;
          }
          jTableAthlete.getSelectionModel().setSelectionInterval(row, row);
        }
      }
    }
  }

  /**
   * Ajoute un athl&egrave;te.
   * 
   * @param a
   *          athl&egrave;te.
   */
  public void addAthlete(DataUser a) {
    Object[] row = { new Athlete(new ModelAthlete(a)) };
    tableModelAthlete.addRow(row);
  }

  /**
   * Ajoute une activit&eacute;.
   * 
   * @param a
   *          activit&eacute;.
   */
  public void addActivity(AbstractDataActivity a) {
    Object[] row = { new Activity(new ModelActivity(a)) };
    tableModelActivity.addRow(row);
  }

  /**
   * Ajoute un &eacute;quipement.
   * 
   * @param a
   *          &eacute;quipement.
   */
  public void addEquipement(DataEquipement a) {
    Object[] row = { new Equipement(new ModelEquipement(a)) };
    tableModelEquipement.addRow(row);
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class MyDefaultTableModelAthlete extends DefaultTableModel {

    public MyDefaultTableModelAthlete() {
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
  private class MyDefaultTableModelEquipement extends DefaultTableModel {

    public MyDefaultTableModelEquipement() {
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
  private class MyDefaultTableModelActivity extends DefaultTableModel {

    public MyDefaultTableModelActivity() {
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

  private class Activity {
    private ModelActivity model;

    public Activity(ModelActivity model) {
      super();
      this.model = model;
    }

    @Override
    public String toString() {
      return model.getDataActivity().getName();
    }

  }

  private class Equipement {
    private ModelEquipement model;

    public Equipement(ModelEquipement model) {
      super();
      this.model = model;
    }

    @Override
    public String toString() {
      return model.getData().getName();
    }

  }

  private class Athlete {
    private ModelAthlete model;

    public Athlete(ModelAthlete model) {
      super();
      this.model = model;
    }

    @Override
    public String toString() {
      return model.getData().getFirstName() + " "
             + model.getData().getLastName();
    }

  }

} // @jve:decl-index=0:visual-constraint="10,10"

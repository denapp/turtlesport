package fr.turtlesport.ui.swing.component.workout;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.WorkoutT;

import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.component.calendar.JPanelTreeRun;
import fr.turtlesport.ui.swing.img.diagram.ImagesDiagramRepository;
import fr.turtlesport.ui.swing.model.ModelWorkout;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelWorkout extends JPanel {

  private ModelWorkout          model;

  private TitledBorder          borderWorkout;

  private JList                 jListWorkout;

  private JPanelWorkoutTitle    jPanelWorkoutTitle;

  private JPanelWorkoutListStep jPanelSteps;

  private JPanel                jPanelDetailStep;

  /**
   * Create the panel.
   */
  public JPanelWorkout() {
    setLayout(new BorderLayout(0, 0));

    // Center
    JPanel panelCenter = new JPanel();
    panelCenter.setLayout(new BoxLayout(panelCenter, BoxLayout.Y_AXIS));
    add(panelCenter, BorderLayout.CENTER);

    jPanelWorkoutTitle = new JPanelWorkoutTitle();
    panelCenter.add(jPanelWorkoutTitle);

    jPanelSteps = new JPanelWorkoutListStep();
    BorderLayout borderLayout = (BorderLayout) jPanelSteps.getLayout();
    borderLayout.setVgap(5);
    borderLayout.setHgap(15);
    panelCenter.add(jPanelSteps);

    jPanelDetailStep = new JPanel();
    jPanelDetailStep
        .setLayout(new BoxLayout(jPanelDetailStep, BoxLayout.X_AXIS));
    JPanelWorkoutDuration jPanelDuration = new JPanelWorkoutDuration();
    jPanelDuration.setBorder(new TitledBorder(null,
                                              "Dur\u00E9e de la phase",
                                              TitledBorder.LEADING,
                                              TitledBorder.TOP,
                                              null,
                                              null));
    jPanelDetailStep.add(jPanelDuration);
    JPanelWorkoutTarget jPanelTarget = new JPanelWorkoutTarget();
    jPanelTarget.setBorder(new TitledBorder(null,
                                            "Objectif de la phase",
                                            TitledBorder.LEADING,
                                            TitledBorder.TOP,
                                            null,
                                            null));
    jPanelDetailStep.add(jPanelTarget);
    panelCenter.add(jPanelDetailStep);

    // Arbre
    JPanel jPanelButtonsWorkout = new JPanel();
    FlowLayout flowLayout = new FlowLayout();
    flowLayout.setAlignment(FlowLayout.RIGHT);
    flowLayout.setVgap(0);
    jPanelButtonsWorkout.setLayout(flowLayout);

    Dimension dimButton = new Dimension(20, 20);
    JButton jButtonAdd = new JButton();
    jButtonAdd.setIcon(ImagesDiagramRepository.getImageIcon("plus.png"));
    jButtonAdd.setMargin(new Insets(2, 2, 2, 2));
    jButtonAdd.setMaximumSize(dimButton);
    jButtonAdd.setMinimumSize(dimButton);
    jButtonAdd.setOpaque(false);
    jButtonAdd.setPreferredSize(dimButton);

    JButton jButtonDelete = new JButton();
    jButtonDelete.setIcon(ImagesDiagramRepository.getImageIcon("minus.png"));
    jButtonDelete.setMargin(new Insets(2, 2, 2, 2));
    jButtonDelete.setMaximumSize(dimButton);
    jButtonDelete.setMinimumSize(dimButton);
    jButtonDelete.setOpaque(false);
    jButtonDelete.setPreferredSize(dimButton);

    jPanelButtonsWorkout.add(jButtonAdd);
    jPanelButtonsWorkout.add(jButtonDelete);

    jListWorkout = new JList();
    jListWorkout.setCellRenderer(new ListWorkoutRender());
    jListWorkout.setModel(new DefaultListModel());
    jListWorkout.setFont(GuiFont.FONT_PLAIN);
    JScrollPane scrollPaneTree = new JScrollPane();
    scrollPaneTree.setViewportView(jListWorkout);

    JPanel jPanelWorkouts = new JPanel();
    borderWorkout = BorderFactory
        .createTitledBorder(null,
                            "Entrainements",
                            TitledBorder.DEFAULT_JUSTIFICATION,
                            TitledBorder.DEFAULT_POSITION,
                            GuiFont.FONT_PLAIN,
                            null);
    jPanelWorkouts.setBorder(borderWorkout);
    jPanelWorkouts.setLayout(new BorderLayout());
    jPanelWorkouts.add(jPanelButtonsWorkout, BorderLayout.NORTH);
    jPanelWorkouts.add(scrollPaneTree, BorderLayout.CENTER);
    add(jPanelWorkouts, BorderLayout.WEST);

    // Listener
    jListWorkout.addListSelectionListener(new JListWorkoutListener());
  }

  public JList getjListWorkout() {
    return jListWorkout;
  }

  public JPanelWorkoutTitle getjPanelWorkoutTitle() {
    return jPanelWorkoutTitle;
  }

  public JPanelWorkoutListStep getjPanelSteps() {
    return jPanelSteps;
  }

  public JPanel getjPanelDetailStep() {
    return jPanelDetailStep;
  }
  

  /**
   * @author Denis Apparicio
   * 
   */
  private class ListWorkoutRender extends DefaultListCellRenderer {
    private ImageIcon iconRun         = new ImageIcon(JPanelTreeRun.class.getResource("run2.png"));

    private ImageIcon iconBicycle     = new ImageIcon(JPanelTreeRun.class.getResource("bicycle2.png"));

    private ImageIcon iconTransparent = new ImageIcon(JPanelTreeRun.class.getResource("16px-transparent.png"));

    @Override
    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
      JLabel cmp = (JLabel) super.getListCellRendererComponent(list,
                                                               value,
                                                               index,
                                                               isSelected,
                                                               cellHasFocus);

      if (value instanceof WorkoutT) {
        WorkoutT w = (WorkoutT) value;
        cmp.setText(w.getName());
        switch (w.getSport()) {
          case RUNNING:
            setIcon(iconRun);
            break;
          case BIKING:
            setIcon(iconBicycle);
            break;
          case OTHER:
            setIcon(iconTransparent);
            break;
        }
      }

      return cmp;
    }
  }

  public void setModel(ModelWorkout model) {
    this.model = model;
    try {
      model.updateView(this);
    }
    catch (SQLException e) {
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class JListWorkoutListener implements ListSelectionListener {

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event
     * .ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e) {
      if (e != null && e.getValueIsAdjusting()) {
        return;
      }
      model.updateView(JPanelWorkout.this, jListWorkout.getSelectedValue());
    }
  }

  public static void main(String... args) {
    JFrame f = new JFrame();
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setContentPane(new JPanelWorkout());
    f.pack();
    f.setVisible(true);
  }


}

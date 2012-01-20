package fr.turtlesport.ui.swing.component.workout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.AbstractStepT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.CadenceT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.CaloriesBurnedT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.CustomHeartRateZoneT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.CustomSpeedZoneT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.DistanceT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.HeartRateAboveT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.HeartRateBelowT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.HeartRateT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.NoneT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.PredefinedHeartRateZoneT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.PredefinedSpeedZoneT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.RepeatT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.SpeedT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.StepT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TimeT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.UserInitiatedT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.WorkoutT;

import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.img.ImagesRepository;
import fr.turtlesport.ui.swing.img.diagram.ImagesDiagramRepository;
import fr.turtlesport.util.OperatingSystem;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelWorkoutListStep extends JPanel {

  private TitledBorder border;

  private JXTreeTable  jTreeStep;

  /**
   * Create the panel.
   */
  public JPanelWorkoutListStep() {
    JPanel jPanelButtons = new JPanel();
    FlowLayout flowLayout = new FlowLayout();
    flowLayout.setAlignment(FlowLayout.LEFT);
    flowLayout.setVgap(0);
    jPanelButtons.setLayout(flowLayout);

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

    jPanelButtons.add(Box.createRigidArea(new Dimension(40, 20)));
    jPanelButtons.add(jButtonAdd);
    jPanelButtons.add(jButtonDelete);

    // Tree
    jTreeStep = new JXTreeTable();
    jTreeStep.setEditable(false);
    jTreeStep.setFont(GuiFont.FONT_PLAIN);
    jTreeStep.setShowGrid(false);
    jTreeStep.setSortable(false);
    jTreeStep.setRootVisible(false);
    jTreeStep.getTableHeader().setFont(GuiFont.FONT_PLAIN);
    jTreeStep.setTreeCellRenderer(new MyTreeCellRenderer());
    jTreeStep.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jTreeStep.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    if (OperatingSystem.isMacOSX()) {
      jTreeStep.addHighlighter(HighlighterFactory.createAlternateStriping());
    }
    jTreeStep
        .addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW,
                                             null,
                                             Color.RED));
    JScrollPane scrollPaneTree = new JScrollPane();
    scrollPaneTree.setViewportView(jTreeStep);

    // move
    JPanel panelMove = new JPanel();
    JButton jButtonUp = new JButton();
    jButtonUp.setIcon(ImagesRepository.getImageIcon("up.png"));
    jButtonUp.setMargin(new Insets(2, 2, 2, 2));
    // jButtonUp.setMaximumSize(dimButton);
    // jButtonUp.setMinimumSize(dimButton);
    jButtonUp.setOpaque(false);
    // jButtonUp.setPreferredSize(dimButton);

    JButton jButtonDown = new JButton();
    jButtonDown.setIcon(ImagesRepository.getImageIcon("down.png"));
    jButtonDown.setMargin(new Insets(2, 2, 2, 2));
    // jButtonDown.setMaximumSize(dimButton);
    // jButtonDown.setMinimumSize(dimButton);
    jButtonDown.setOpaque(false);
    // jButtonDown.setPreferredSize(dimButton);
    panelMove.setLayout(new BoxLayout(panelMove, BoxLayout.Y_AXIS));
    panelMove.add(jButtonUp);
    panelMove.add(jButtonDown);

    border = BorderFactory
        .createTitledBorder(null,
                            "Etapes",
                            TitledBorder.DEFAULT_JUSTIFICATION,
                            TitledBorder.DEFAULT_POSITION,
                            GuiFont.FONT_PLAIN,
                            null);
    setBorder(border);
    setLayout(new BorderLayout());
    add(jPanelButtons, BorderLayout.NORTH);
    add(scrollPaneTree, BorderLayout.CENTER);

    add(panelMove, BorderLayout.WEST);
  }

  public JXTreeTable getJTreeStep() {
    return jTreeStep;
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class MyButtonCellRenderer extends JButton implements
                                                    TableCellRenderer {
    private ImageIcon icon = ImagesRepository.getImageIcon("loupe.png");

    private MyButtonCellRenderer() {
      super();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
      setIcon(icon);
      setMaximumSize(new Dimension(20, 20));
      setPreferredSize(new Dimension(20, 20));
      return this;
    }

  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class MyDurationCellRenderer extends JLabel implements
                                                     TableCellRenderer {
    private ImageIcon icon = ImagesRepository.getImageIcon("loupe.png");

    private MyDurationCellRenderer() {
      super();
      setFont(GuiFont.FONT_PLAIN);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object obj,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
      if (obj instanceof StepT) {
        setIcon(null);
        if (((StepT) obj).getTarget() instanceof NoneT) {
          setText("Aucun");
        }
        else if (((StepT) obj).getTarget() instanceof HeartRateT) {
          HeartRateT res = (HeartRateT) ((StepT) obj).getTarget();
          StringBuilder text = new StringBuilder("Zone de frequence cardiaque : ");
          if (res.getHeartRateZone() instanceof PredefinedHeartRateZoneT) {
            text.append("zone ");
            text.append(((PredefinedHeartRateZoneT) res.getHeartRateZone())
                .getNumber());
          }
          else if (res.getHeartRateZone() instanceof CustomHeartRateZoneT) {
            text.append(((CustomHeartRateZoneT) res.getHeartRateZone())
                .getLow());
            text.append(" a ");
            text.append(((CustomHeartRateZoneT) res.getHeartRateZone())
                .getHigh());

          }
          setText(text.toString());
        }
        else if (((StepT) obj).getTarget() instanceof CadenceT) {
          CadenceT res = (CadenceT) ((StepT) obj).getTarget();
          setText("Cadence de " + res.getLow() + " a " + res.getHigh());
        }
        else if (((StepT) obj).getTarget() instanceof SpeedT) {
          StringBuilder text = new StringBuilder("Zone de vitesse : ");
          SpeedT res = (SpeedT) ((StepT) obj).getTarget();
          if (res.getSpeedZone() instanceof CustomSpeedZoneT) {
            text.append(((CustomSpeedZoneT) res.getSpeedZone()).getViewAs()
                .value());
          }
          else if (res.getSpeedZone() instanceof PredefinedSpeedZoneT) {
            text.append("zone ");
            text.append(((PredefinedSpeedZoneT) res.getSpeedZone()).getNumber());
          }
          setText(text.toString());
        }
      }

      return this;
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class MyTreeCellRenderer extends DefaultTreeCellRenderer {
    private ImageIcon icon = ImagesRepository.getImageIcon("loupe.png");

    @Override
    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus) {
      super.getTreeCellRendererComponent(tree,
                                         value,
                                         sel,
                                         expanded,
                                         leaf,
                                         row,
                                         hasFocus);
      setFont(GuiFont.FONT_PLAIN);

      DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode) value;
      Object obj = node.getUserObject();
      System.out.println("obj=" + obj);
      if (obj instanceof StepT) {
        setIcon(null);
        if (((StepT) obj).getDuration() instanceof DistanceT) {
          DistanceT res = (DistanceT) ((StepT) obj).getDuration();
          setText("Distance " + res.getMeters());
        }
        else if (((StepT) obj).getDuration() instanceof TimeT) {
          TimeT res = (TimeT) ((StepT) obj).getDuration();
          setText("Durée " + res.getSeconds());
        }
        else if (((StepT) obj).getDuration() instanceof UserInitiatedT) {
          setText("Lap");
        }
        else if (((StepT) obj).getDuration() instanceof HeartRateAboveT) {
          HeartRateAboveT res = (HeartRateAboveT) ((StepT) obj).getDuration();
          setText("HeartRateAboveT " + res.getHeartRate());
        }
        else if (((StepT) obj).getDuration() instanceof CaloriesBurnedT) {
          CaloriesBurnedT res = (CaloriesBurnedT) ((StepT) obj).getDuration();
          setText("CaloriesBurnedT " + res.getCalories());
          setIcon(null);

        }
        else if (((StepT) obj).getDuration() instanceof HeartRateBelowT) {
          HeartRateBelowT res = (HeartRateBelowT) ((StepT) obj).getDuration();
          setText("HeartRateBelowT " + res.getHeartRate());
        }
      }
      else if (obj instanceof RepeatT) {
        setText("Repeter  " + ((RepeatT) obj).getRepetitions() + " fois");

      }
      else {
        setText("1234 ");
        setIcon(icon);
      }

      return this;
    }

  }

  public void fireStep(final WorkoutT w) {
    DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode();

    if (w != null) {
      List<AbstractStepT> listStep = w.getStep();
      addNodes(root, listStep);
    }

    TableModelStep tableModel = new TableModelStep();
    tableModel.setRoot(root);
    jTreeStep.setTreeTableModel(tableModel);

    jTreeStep.getColumn(1).setCellRenderer(new MyDurationCellRenderer());
    jTreeStep.getColumn(2).setCellRenderer(new MyButtonCellRenderer());

    jTreeStep.packAll();
  }

  private void addNodes(DefaultMutableTreeTableNode parent,
                        List<AbstractStepT> listStep) {
    for (AbstractStepT step : listStep) {
      DefaultMutableTreeTableNode node = new DefaultMutableTreeTableNode(step);
      parent.add(node);
      if (step instanceof RepeatT) {
        addNodes(node, ((RepeatT) step).getChild());
      }
    }
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class TableModelStep extends DefaultTreeTableModel {
    private String[] columnNames = { "", "", "" };

    public TableModelStep() {
      super();
    }

    @Override
    public String getColumnName(int column) {
      return columnNames[column];
    }

    @Override
    public Object getChild(Object parent, int index) {
      if (parent instanceof DefaultMutableTreeTableNode) {
        DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode) parent;
        return node.getChildAt(index);
      }
      return null;
    }

    @Override
    public Object getValueAt(Object obj, int column) {
      if (obj instanceof DefaultMutableTreeTableNode) {
        DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode) obj;
        AbstractStepT step = (AbstractStepT) node.getUserObject();
        return step;
      }
      return "";
    }

    @Override
    public int getChildCount(Object parent) {
      if (parent instanceof DefaultMutableTreeTableNode) {
        DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode) parent;
        return node.getChildCount();
      }
      return super.getChildCount(parent);
    }

    @Override
    public int getColumnCount() {
      return 3;
    }

  }

}

package fr.turtlesport.ui.swing;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;

import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.ui.swing.component.JDiagramOneComponent;
import fr.turtlesport.ui.swing.component.JPanelGraphOne;
import fr.turtlesport.ui.swing.model.ModelDialogDiagramComponents;
import fr.turtlesport.ui.swing.model.ModelMapkitManager;
import fr.turtlesport.ui.swing.model.ModelPointsManager;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JDialogDiagramComponents extends JDialog {

  private JPanelRunLap                 jPanelRight;

  private JPanelGraphOne               jPanelY1;

  private JPanelGraphOne               jPanelY2;

  private JPanelGraphOne               jPanelY3;

  private JPanel                       jPanelGraphs;

  private ResourceBundle               rb;

  // Model
  private ModelDialogDiagramComponents model = new ModelDialogDiagramComponents();

  public JDialogDiagramComponents(Frame owner) {
    super(owner, true);
    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), JDialogDiagramComponents.class);
    initialize();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.Window#dispose()
   */
  @Override
  public void dispose() {
    ModelMapkitManager.getInstance().addChangeListener(jPanelY1.getJDiagram()
        .getModel());
    ModelMapkitManager.getInstance().addChangeListener(jPanelY2.getJDiagram()
        .getModel());
    ModelMapkitManager.getInstance().addChangeListener(jPanelY3.getJDiagram()
        .getModel());
  }

  /**
   * @throws SQLException
   * 
   */
  public static void prompt() {
    JDialogDiagramComponents view = new JDialogDiagramComponents(MainGui.getWindow());

    try {
      view.model.updateView(view);
      int indexLap = ModelPointsManager.getInstance().getLapIndex();
      if (indexLap != -1) {
        view.getJPanelRight().getJComboBoxLap().setSelectedIndex(indexLap + 1);
      }
    }
    catch (SQLException e) {
      return;
    }

    view.pack();
    view.setLocationRelativeTo(MainGui.getWindow());
    view.setVisible(true);
  }

  private void initialize() {
    setTitle(rb.getString("title"));
    JPanel contentPane = new JPanel();
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
    contentPane.add(getjPanelGraphs());
    contentPane.add(Box.createRigidArea(new Dimension(5, 0)));
    contentPane.add(getJPanelRight());

    setContentPane(contentPane);
    this.setSize(880, 700);

    // Evenement
    getJPanelRight().getJComboBoxLap().addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (model != null
            && getJPanelRight().getJComboBoxLap().getSelectedIndex() > 0) {
          model.updateViewLap(JDialogDiagramComponents.this, getJPanelRight()
              .getJComboBoxLap().getSelectedIndex() - 1);
        }
      }
    });
    
  }

  public JPanelRunLap getJPanelRight() {
    if (jPanelRight == null) {
      jPanelRight = new JPanelRunLap();
    }
    return jPanelRight;
  }
  
  private JPanel getjPanelGraphs() {
    if (jPanelGraphs == null) {
      jPanelGraphs = new JPanel();
      jPanelGraphs.setLayout(new BoxLayout(jPanelGraphs, BoxLayout.Y_AXIS));

      jPanelY1 = new JPanelGraphOne(JDiagramOneComponent.HEART);
      jPanelY2 = new JPanelGraphOne(JDiagramOneComponent.ALTITUDE);
      jPanelY3 = new JPanelGraphOne(JDiagramOneComponent.SPEED);

      Dimension dim = new Dimension(0, 5);

      jPanelGraphs.add(jPanelY1);
      jPanelGraphs.add(Box.createRigidArea(dim));
      jPanelGraphs.add(jPanelY2);
      jPanelGraphs.add(Box.createRigidArea(dim));
      jPanelGraphs.add(jPanelY3);
    }
    return jPanelGraphs;
  }
}

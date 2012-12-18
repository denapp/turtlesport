package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.sql.SQLException;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Denis Apparicio
 * 
 */
public class JDialogSearchRun extends JDialog {
  private JPanelSearch        jPanelSearch;
  private JPanel jPanelContentPane;

  private JDialogSearchRun(Frame owner) throws SQLException {
    super(owner, true);

    initialize();
  }

  /**
   * @throws SQLException
   * 
   */
  public static void prompt() throws SQLException {
    JDialogSearchRun dlg = new JDialogSearchRun(MainGui.getWindow());
    dlg.setLocationRelativeTo(MainGui.getWindow());
    dlg.pack();
    dlg.setVisible(true);
  }

  /**
   * This method initializes this
   * 
   * @return void
   * @throws SQLException 
   */
  private void initialize() throws SQLException {
    this.setSize(500, 430);

    this.setContentPane(getJContentPane());
    setDefaultCloseOperation(JDialogImport.DISPOSE_ON_CLOSE);
  }
  
  /**
   * This method initializes jPanelSummary
   * 
   * @return javax.swing.JPanel
   * @throws SQLException 
   */
  private JPanel getJContentPane() throws SQLException {
    if (jPanelContentPane == null) {
      JLabel jLabelNorth = new JLabel("  ");
      JLabel jLabelSouth = new JLabel("  ");
      jPanelContentPane = new JPanel();
      jPanelSearch = new JPanelSearch();
      jPanelContentPane.setLayout(new BorderLayout(0, 0));
      jPanelContentPane.add(jPanelSearch, BorderLayout.CENTER);
      jPanelContentPane.add(jLabelNorth, BorderLayout.NORTH);
      jPanelContentPane.add(jLabelSouth, BorderLayout.SOUTH);
    }
    return jPanelContentPane;
  }

}

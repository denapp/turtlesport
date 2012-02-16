package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import fr.turtlesport.lang.LanguageManager;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class JDialogNewEquipement extends JDialog {

  private final JPanel contentPanel = new JPanel();

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      JDialogNewEquipement dialog = new JDialogNewEquipement();
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
  public JDialogNewEquipement() {
    setBounds(100, 100, 450, 300);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new GridLayout(0, 2, 0, 0));
    {
      JLabel jLabelLibName = new JLabel("Equipement");
      jLabelLibName.setFont(GuiFont.FONT_PLAIN);
      contentPanel.add(jLabelLibName);
    }
    {
      JLabel jLabelValName = new JLabel("");
      jLabelValName.setFont(GuiFont.FONT_PLAIN);
      contentPanel.add(jLabelValName);
    }
    {
      JLabel jLabelLibDistance = new JLabel("Distance");
      jLabelLibDistance.setFont(GuiFont.FONT_PLAIN);
      contentPanel.add(jLabelLibDistance);
    }
    {
      JLabel jLabelValDistance = new JLabel("New label");
      jLabelValDistance.setFont(GuiFont.FONT_PLAIN);
      contentPanel.add(jLabelValDistance);
    }
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        JButton JButtonOK = new JButton(LanguageManager.getManager().getCurrentLang().ok());
        JButtonOK.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
          }
        });
        JButtonOK.setFont(GuiFont.FONT_PLAIN);
        JButtonOK.setActionCommand("OK");
        buttonPane.add(JButtonOK);
        getRootPane().setDefaultButton(JButtonOK);
      }
      {
        JButton jButtonCancel = new JButton(LanguageManager.getManager().getCurrentLang().cancel());
        jButtonCancel.setFont(GuiFont.FONT_PLAIN);
        jButtonCancel.setActionCommand("Cancel");
        buttonPane.add(jButtonCancel);
      }
    }
  }

}

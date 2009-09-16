package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.mail.IMailClient;
import fr.turtlesport.mail.Mail;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public final class JDialogChooseEmail extends JDialog {
  private ResourceBundle rb;

  private JPanel         jContentPane;

  private JPanel         jPanelCenter;

  private JPanel         jPanelSouth;

  private JLabel         jLabelSelect;

  private JButton        jButtonCancel;

  private JCheckBox      jCheckBoxRemember;

  private IMailClient    mailClient;

  private JDialogChooseEmail(Dialog owner) {
    super(owner, true);
    initialize();
  }

  private void initialize() {
    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), getClass());
    this.setContentPane(getJContentPane());
    this.setTitle(rb.getString("title"));
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

      jLabelSelect = new JLabel();
      jLabelSelect.setFont(GuiFont.FONT_BOLD);
      jLabelSelect.setText(rb.getString("jLabelSelect"));

      jContentPane.add(jLabelSelect, BorderLayout.NORTH);
      jContentPane.add(getJPanelCenter(), BorderLayout.CENTER);
      jContentPane.add(getJPanelSouth(), BorderLayout.SOUTH);
    }
    return jContentPane;
  }

  private JPanel getJPanelCenter() {
    if (jPanelCenter == null) {
      jPanelCenter = new JPanel();
      jPanelCenter.setBorder(BorderFactory
          .createTitledBorder(null,
                              "",
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null));
      IMailClient[] clients = Mail.getMailClients();
      jPanelCenter.setLayout(new GridLayout(clients.length, 1));

      for (IMailClient mail : clients) {
        final JButton jButton = new JButton();
        jButton.setIcon(mail.getIcon());
        jButton.setText(mail.getName());
        jButton.setFont(GuiFont.FONT_PLAIN);
        jButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            mailClient = Mail.getMailClient(jButton.getText());
            dispose();
          }
        });
        jPanelCenter.add(jButton);
      }

    }
    return jPanelCenter;
  }

  private JPanel getJPanelSouth() {
    if (jPanelSouth == null) {
      jPanelSouth = new JPanel();

      jCheckBoxRemember = new JCheckBox();
      jCheckBoxRemember.setFont(GuiFont.FONT_PLAIN);
      jCheckBoxRemember.setText(rb.getString("jCheckBoxRemember"));
      jPanelCenter.add(jCheckBoxRemember);

      jButtonCancel = new JButton();
      jButtonCancel.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          mailClient = null;
          dispose();
        }
      });
      jButtonCancel.setFont(GuiFont.FONT_PLAIN);
      jButtonCancel.setText(rb.getString("jButtonCancel"));

      jPanelSouth.setLayout(new FlowLayout(FlowLayout.RIGHT));
      jPanelSouth.add(jCheckBoxRemember);
      jPanelSouth.add(jButtonCancel);
    }
    return jPanelSouth;
  }

  public static IMailClient prompt(JDialog owner) {
    if (!Mail.isConfigurable()) {
      return null;
    }

    JDialogChooseEmail view = new JDialogChooseEmail(owner);
    view.pack();
    view.setLocationRelativeTo(MainGui.getWindow());
    view.setVisible(true);

    if (view.mailClient != null && view.jCheckBoxRemember.isSelected()) {
      Mail.setChoose(view.mailClient.getName());
    }

    return view.mailClient;
  }

}

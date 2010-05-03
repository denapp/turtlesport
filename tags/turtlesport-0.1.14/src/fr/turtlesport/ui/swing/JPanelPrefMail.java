package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import fr.turtlesport.Configuration;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.mail.IMailManager;
import fr.turtlesport.mail.Mail;
import fr.turtlesport.ui.swing.component.PanelPrefListener;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelPrefMail extends JPanel implements PanelPrefListener {

  private JPanelPrefTitle jPanelTitle;

  private JPanel          jPanelCenter;

  private ResourceBundle  rb;

  /**
   * 
   */
  public JPanelPrefMail() {
    super();
    initialize();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.ui.swing.component.PanelPrefListener#viewChanged()
   */
  public void viewChanged() {
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), getClass());

    BorderLayout borderLayout = new BorderLayout();
    borderLayout.setHgap(5);
    borderLayout.setVgap(5);
    this.setLayout(borderLayout);
    this.setSize(417, 218);
    this.add(getJPanelTitle(), BorderLayout.NORTH);
    this.add(getJPanelCenter(), BorderLayout.CENTER);

    // evenements
  }

  /**
   * This method initializes jPanelTitle
   * 
   * @return javax.swing.JPanel
   */
  private JPanelPrefTitle getJPanelTitle() {
    if (jPanelTitle == null) {
      jPanelTitle = new JPanelPrefTitle(rb.getString("title"));
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
      jPanelCenter.setLayout(null);

      int y = 5;
      JRadioButton jRadioButton;
      ButtonGroup buttonGroup = new ButtonGroup();

      String defaultClient = Configuration.getConfig().getProperty("mail",
                                                                   "client");

      if (Mail.hasSystemDefaultMailAvalaible()) {
        jRadioButton = new JRadioButton(rb.getString("systemDefaultMail"));
        if (IMailManager.DEFAULT_EMAIL_SYSTEM.equals(defaultClient)) {
          jRadioButton.setSelected(true);
        }
        jRadioButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Mail.setChoose(IMailManager.DEFAULT_EMAIL_SYSTEM);
          }
        });
        jRadioButton.setBounds(new Rectangle(5, y, 250, 23));
        jRadioButton.setFont(GuiFont.FONT_PLAIN);
        jPanelCenter.add(jRadioButton, null);
        buttonGroup.add(jRadioButton);
      }

      String[] names = Mail.getMailClientNames();
      for (int i = 0; i < names.length; i++) {
        y += 30;
        jRadioButton = new JRadioButton(names[i]);
        jRadioButton.setActionCommand(names[i]);
        if (names[i].equals(defaultClient)) {
          jRadioButton.setSelected(true);
        }
        jRadioButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Mail.setChoose(e.getActionCommand());
          }
        });
        jRadioButton.setBounds(new Rectangle(5, y, 250, 23));
        jRadioButton.setFont(GuiFont.FONT_PLAIN);
        jPanelCenter.add(jRadioButton, null);
        buttonGroup.add(jRadioButton);
      }
    }
    return jPanelCenter;
  }

}

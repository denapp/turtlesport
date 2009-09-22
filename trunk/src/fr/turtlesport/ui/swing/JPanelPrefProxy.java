package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import fr.turtlesport.ProxyConfiguration;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.ui.swing.component.PanelPrefListener;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelPrefProxy extends JPanel implements PanelPrefListener {

  private JPanelPrefTitle jPanelTitle;

  private JPanel          jPanelCenter;

  private JRadioButton    jRadioButtonBoxNoProxy;

  private JRadioButton    jRadioButtonDetectProxy;

  private JRadioButton    jRadioButtonConfigProxy;

  private JLabel          jLabelLibHost;

  private JTextField      jTextFieldHost;

  private JLabel          jLabelLibPort;

  private JTextField      jTextFieldPort;

  private JLabel          jLabelLibUsername;

  private JTextField      jTextfieldUsername;

  private JLabel          jLabelLibPassword;

  private JPasswordField  jPasswordField;

  private ResourceBundle  rb;

  /**
   * 
   */
  public JPanelPrefProxy() {
    super();
    initialize();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.ui.swing.component.PanelPrefListener#viewChanged()
   */
  public void viewChanged() {

    int port = -1;
    if (jRadioButtonConfigProxy.isSelected()
        && jTextFieldHost.getText() != null && jTextFieldPort.getText() != null) {
      try {
        port = Integer.parseInt(jTextFieldPort.getText());
      }
      catch (NumberFormatException e) {
        port = 80;
      }
    }

    ProxyConfiguration.update(jRadioButtonBoxNoProxy.isSelected(),
                              jRadioButtonDetectProxy.isSelected(),
                              jTextFieldHost.getText(),
                              port,
                              jTextfieldUsername.getText(),
                              (jPasswordField.getPassword() == null) ? null
                                  : new String(jPasswordField.getPassword()));

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

    // initialise les valeurs
    // ------------------------

    // pas de proxy
    jRadioButtonBoxNoProxy.setSelected(ProxyConfiguration.hasNoProxy());
    // detection proxy
    jRadioButtonDetectProxy.setSelected(ProxyConfiguration.hasDetectProxy());

    jRadioButtonConfigProxy.setSelected(ProxyConfiguration.hasProxyConfig());
    if (ProxyConfiguration.hasProxyConfig()) {
      jTextFieldHost.setText(ProxyConfiguration.getHost());
      jTextFieldPort.setText(Integer.toString(ProxyConfiguration.getPort()));
      jTextfieldUsername.setText(ProxyConfiguration.getUsername());
      jPasswordField.setText(ProxyConfiguration.getPassword());
    }
    else {
      jTextFieldHost.setText(null);
      jTextFieldPort.setText(null);
      jTextfieldUsername.setText(null);
      jPasswordField.setText(null);
    }
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
      jRadioButtonBoxNoProxy = new JRadioButton();
      jRadioButtonBoxNoProxy.setBounds(new Rectangle(5, 5, 350, 25));
      jRadioButtonBoxNoProxy.setText(rb.getString("jCheckBoxNoProxy"));
      jRadioButtonBoxNoProxy.setFont(GuiFont.FONT_PLAIN);

      jRadioButtonDetectProxy = new JRadioButton();
      jRadioButtonDetectProxy.setBounds(new Rectangle(5, 35, 350, 25));
      jRadioButtonDetectProxy.setText(rb.getString("jCheckBoxDetectProxy"));
      jRadioButtonDetectProxy.setFont(GuiFont.FONT_PLAIN);

      jRadioButtonConfigProxy = new JRadioButton();
      jRadioButtonConfigProxy.setBounds(new Rectangle(5, 65, 350, 25));
      jRadioButtonConfigProxy.setText(rb.getString("jCheckBoxConfigProxy"));
      jRadioButtonConfigProxy.setFont(GuiFont.FONT_PLAIN);

      // Group the radio buttons.
      ButtonGroup group = new ButtonGroup();
      group.add(jRadioButtonBoxNoProxy);
      group.add(jRadioButtonDetectProxy);
      group.add(jRadioButtonConfigProxy);

      jLabelLibHost = new JLabel();
      jLabelLibHost.setBounds(new Rectangle(5, 95, 110, 25));
      jLabelLibHost.setText(rb.getString("jLabelLibHost"));
      jLabelLibHost.setFont(GuiFont.FONT_PLAIN);

      jLabelLibPort = new JLabel();
      jLabelLibPort.setBounds(new Rectangle(255, 95, 50, 25));
      jLabelLibPort.setText(rb.getString("jLabelLibPort"));
      jLabelLibPort.setFont(GuiFont.FONT_PLAIN);

      jLabelLibUsername = new JLabel();
      jLabelLibUsername.setBounds(new Rectangle(5, 125, 110, 25));
      jLabelLibUsername.setText(rb.getString("jLabelLibUsername"));
      jLabelLibUsername.setFont(GuiFont.FONT_PLAIN);

      jLabelLibPassword = new JLabel();
      jLabelLibPassword.setBounds(new Rectangle(5, 155, 110, 25));
      jLabelLibPassword.setText(rb.getString("jLabelLibPassword"));
      jLabelLibPassword.setFont(GuiFont.FONT_PLAIN);

      jPanelCenter = new JPanel();
      jPanelCenter.setLayout(null);
      jPanelCenter.add(jRadioButtonBoxNoProxy, null);
      jPanelCenter.add(jRadioButtonDetectProxy, null);
      jPanelCenter.add(jRadioButtonConfigProxy, null);
      jPanelCenter.add(jLabelLibHost, null);
      jPanelCenter.add(getJTextFieldHost(), null);
      jPanelCenter.add(jLabelLibPort, null);
      jPanelCenter.add(getJTextFieldPort(), null);
      jPanelCenter.add(jLabelLibUsername, null);
      jPanelCenter.add(getJTextFieldUsername(), null);
      jPanelCenter.add(jLabelLibPassword, null);
      jPanelCenter.add(getJPasswordField(), null);
    }
    return jPanelCenter;
  }

  private JTextField getJTextFieldHost() {
    if (jTextFieldHost == null) {
      jTextFieldHost = new JTextField();
      jTextFieldHost.setFont(GuiFont.FONT_PLAIN);
      jTextFieldHost.setBounds(new Rectangle(118, 95, 130, 25));
    }
    return jTextFieldHost;
  }

  private JTextField getJTextFieldPort() {
    if (jTextFieldPort == null) {
      jTextFieldPort = new JTextField();
      jTextFieldPort.setFont(GuiFont.FONT_PLAIN);
      jTextFieldPort.setBounds(new Rectangle(290, 95, 50, 25));
    }
    return jTextFieldPort;
  }

  private JTextField getJTextFieldUsername() {
    if (jTextfieldUsername == null) {
      jTextfieldUsername = new JTextField();
      jTextfieldUsername.setFont(GuiFont.FONT_PLAIN);
      jTextfieldUsername.setBounds(new Rectangle(118, 125, 130, 25));
    }
    return jTextfieldUsername;
  }

  private JPasswordField getJPasswordField() {
    if (jPasswordField == null) {
      jPasswordField = new JPasswordField();
      jPasswordField.setFont(GuiFont.FONT_PLAIN);
      jPasswordField.setBounds(new Rectangle(118, 155, 130, 25));
    }
    return jPasswordField;
  }

}

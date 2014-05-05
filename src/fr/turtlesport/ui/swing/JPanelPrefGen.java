package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import fr.turtlesport.Configuration;
import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.ui.swing.component.PanelPrefListener;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JPanelPrefGen extends JPanelPref implements LanguageListener,
                                         PanelPrefListener {

  // ui
  private JPanelPrefTitle jPanelTitle;

  private JPanel          jPanelCenter;

  private JLabel          jLabelLibLanguage;

  private JComboBox       jComboBoxLanguage;

  private JLabel          jLabelLibTheme;

  private JComboBox       jComboBoxTheme;

  private JCheckBox       jCheckBoxCheckUpdate;

  /**
   * 
   */
  public JPanelPrefGen() {
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
    ResourceBundle rb = ResourceBundleUtility.getBundle(lang, getClass());

    jLabelLibLanguage.setText(rb.getString("jLabelLibLanguage"));
    jLabelLibTheme.setText(rb.getString("jLabelLibTheme"));
    jPanelTitle.setTitle(rb.getString("title"));
    jCheckBoxCheckUpdate.setText(rb.getString("jCheckBoxCheckUpdate"));
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

    // evenements
    jComboBoxTheme.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) {
          return;
        }

        // Recuperation du look and feel
        final String lookName = (String) jComboBoxTheme.getSelectedItem();
        SwingLookAndFeel.setLookAndFeel(lookName);
        Component component = JPanelPrefGen.this;
        while (true) {
          component = component.getParent();
          if (component instanceof JDialogPreference) {
            break;
          }
        }
        final JDialogPreference dialog = (JDialogPreference) component;

        MainGui.getWindow().beforeRunnableSwing();
        dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            // mis a jour du look and feel
            MainGui.getWindow().updateComponentTreeUI();
            SwingUtilities.updateComponentTreeUI(dialog);

            Configuration.getConfig().addProperty("general",
                                                  "lookandfeel",
                                                  lookName);
            MainGui.getWindow().afterRunnableSwing();
            dialog.setCursor(Cursor.getDefaultCursor());
          }
        });
      }

    });

    jComboBoxLanguage.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          ILanguage lang = (ILanguage) jComboBoxLanguage.getSelectedItem();
          Configuration.getConfig().addProperty("general",
                                                "language",
                                                lang.toString());
          LanguageManager.getManager().fireLanguageChanged(lang);
        }
      }
    });

    LanguageManager.getManager().addLanguageListener(this);
    performedLanguage(LanguageManager.getManager().getCurrentLang());
    jComboBoxLanguage.setSelectedItem(LanguageManager.getManager()
        .getCurrentLang());

    jCheckBoxCheckUpdate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Configuration.getConfig().addProperty("update",
                                              "checkatbook",
                                              new Boolean(jCheckBoxCheckUpdate
                                                  .isSelected()).toString());
      }

    });
  }

  /**
   * This method initializes jPanelTitle
   * 
   * @return javax.swing.JPanel
   */
  private JPanelPrefTitle getJPanelTitle() {
    if (jPanelTitle == null) {
      jPanelTitle = new JPanelPrefTitle("General");
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

      jLabelLibLanguage = new JLabel();
      jLabelLibLanguage.setBounds(new Rectangle(5, 5, 77, 23));
      jLabelLibLanguage.setText("Langage : ");
      jLabelLibLanguage.setFont(GuiFont.FONT_PLAIN);

      jLabelLibTheme = new JLabel();
      jLabelLibTheme.setBounds(new Rectangle(5, 35, 77, 23));
      jLabelLibTheme.setFont(GuiFont.FONT_PLAIN);

      jPanelCenter = new JPanel();
      jPanelCenter.setLayout(null);

      jPanelCenter.add(jLabelLibLanguage, null);
      jPanelCenter.add(getJComboBoxLanguage(), null);
      jPanelCenter.add(jLabelLibTheme, null);
      jPanelCenter.add(getJComboBoxTheme(), null);

      jCheckBoxCheckUpdate = new JCheckBox();
      jCheckBoxCheckUpdate.setBounds(new Rectangle(5, 75, 350, 23));
      jCheckBoxCheckUpdate.setFont(GuiFont.FONT_PLAIN);
      jCheckBoxCheckUpdate.setSelected(Configuration.getConfig()
          .getPropertyAsBoolean("update", "checkatbook", false));

      jPanelCenter.add(jCheckBoxCheckUpdate, null);
    }
    return jPanelCenter;
  }

  /**
   * This method initializes jComboBoxLanguage
   * 
   * @return javax.swing.JComboBox
   */
  private JComboBox getJComboBoxLanguage() {
    if (jComboBoxLanguage == null) {
      jComboBoxLanguage = new JComboBox(LanguageManager.getManager()
          .getLanguages());
      new DefaultListCellRenderer();
      jComboBoxLanguage.setRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
          super.getListCellRendererComponent(list,
                                             value,
                                             index,
                                             isSelected,
                                             cellHasFocus);
          ILanguage language = (ILanguage) value;

          setFont(GuiFont.FONT_PLAIN);
          ImageIcon icon = language.getFlag();
          if (icon != null) {
            setIcon(icon);
          }
          setText(language.getName());
          return this;
        }
      });
      jComboBoxLanguage.setFont(GuiFont.FONT_PLAIN);
      jComboBoxLanguage.setBounds(new Rectangle(98, 5, 188, 23));
    }
    return jComboBoxLanguage;
  }

  /**
   * This method initializes jComboBoxTheme
   * 
   * @return javax.swing.JComboBox
   */
  private JComboBox getJComboBoxTheme() {
    if (jComboBoxTheme == null) {
      jComboBoxTheme = new JComboBox(SwingLookAndFeel.getLookAndFeel());
      jComboBoxTheme.setSelectedItem(SwingLookAndFeel.getCurrentLookAndFeel());
      jComboBoxTheme.setFont(GuiFont.FONT_PLAIN);
      jComboBoxTheme.setBounds(new Rectangle(98, 35, 188, 23));
    }
    return jComboBoxTheme;
  }

} // @jve:decl-index=0:visual-constraint="10,10"

package fr.turtlesport.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.ui.swing.img.animals.AnimalImagesRepository;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class JDialogChooseImage extends JDialog {

  private JPanel         jContentPane;

  private JScrollPane    jScrollPane;

  private JList          jListAnimals;

  private JButton        jButtonChoose;

  private JButton        jButtonOK;

  private JButton        jButtonCancel;

  private JPanel         jPanelButton;

  private ResourceBundle rb;

  private ImageDesc      imageDesc;

  private File           fileSelect;

  /**
   * @param owner
   * @param modal
   */
  public JDialogChooseImage(Frame owner, boolean modal) {
    super(owner, modal);
    initialize();
  }

  public static Object prompt() {
    // mis a jour du model et affichage de l'IHM
    JDialogChooseImage view = new JDialogChooseImage(MainGui.getWindow(), true);
    view.setLocationRelativeTo(MainGui.getWindow());
    view.setVisible(true);

    return (view.imageDesc != null) ? view.imageDesc : view.fileSelect;
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    rb = ResourceBundleUtility.getBundle(LanguageManager.getManager()
        .getCurrentLang(), getClass());
    this.setSize(330, 225);
    this.setContentPane(getJContentPane());
    rb = null;

    // Evenements
    jButtonCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionevent) {
        imageDesc = null;
        fileSelect = null;
        dispose();
      }
    });
    jListAnimals.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent mouseevent) {
        imageDesc = null;
        fileSelect = null;
        if (mouseevent.getClickCount() == 2
            && jListAnimals.getSelectedValue() != null) {
          String name = jListAnimals.getSelectedValue().toString();
          imageDesc = AnimalImagesRepository.getInstance().getImageDesc(name);
          dispose();
        }
      }
    });
    jButtonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionevent) {
        imageDesc = null;
        fileSelect = null;
        if (jListAnimals.getSelectedValue() != null) {
          String name = jListAnimals.getSelectedValue().toString();
          imageDesc = AnimalImagesRepository.getInstance().getImageDesc(name);
        }
        dispose();
      }
    });
    jButtonChoose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionevent) {
        final JFileChooser fc = new JFileChooser();

        FileFilter filter = new FileFilter() {
          public boolean accept(File f) {
            if (f.isDirectory()) {
              return true;
            }
            String ext = getExtension(f);
            if (ext != null) {
              if (ext.equals("jpeg") || ext.equals("jpg") || ext.equals("gif")
                  || ext.equals("tiff") || ext.equals("tif")
                  || ext.equals("png")) {
                return true;
              }
            }
            return false;
          }

          @Override
          public String getDescription() {
            return "Images";
          }

          private String getExtension(File f) {
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');
            if (i > 0 && i < s.length() - 1) {
              ext = s.substring(i + 1).toLowerCase();
            }
            return ext;
          }
        };

        fc.addChoosableFileFilter(filter);
        fc.setFileFilter(filter);
        fc.setMultiSelectionEnabled(false);

        int ret = fc.showOpenDialog(MainGui.getWindow());
        if (ret == JFileChooser.APPROVE_OPTION) {
          imageDesc = null;
          fileSelect = fc.getSelectedFile();
          dispose();
        }
      }
    });
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if (jContentPane == null) {
      jContentPane = new JPanel();
      jContentPane.setLayout(new BorderLayout());
      jContentPane.add(getJScrollPane(), BorderLayout.CENTER);
      jContentPane.add(getJPanelButton(), BorderLayout.SOUTH);
    }
    return jContentPane;
  }

  /**
   * This method initializes jScrollPane
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getJScrollPane() {
    if (jScrollPane == null) {
      jScrollPane = new JScrollPane();
      jScrollPane.setBorder(BorderFactory
          .createTitledBorder(null,
                              rb.getString("borderTitleAnimals"),
                              TitledBorder.DEFAULT_JUSTIFICATION,
                              TitledBorder.DEFAULT_POSITION,
                              GuiFont.FONT_PLAIN,
                              null));
      jScrollPane.setViewportView(getJListAnimals());
    }
    return jScrollPane;
  }

  /**
   * This method initializes jListAnimals
   * 
   * @return javax.swing.JList
   */
  private JList getJListAnimals() {
    if (jListAnimals == null) {
      // recuperation de la liste des noms
      jListAnimals = new JList(AnimalImagesRepository.getInstance().getNames());
      jListAnimals.setFont(GuiFont.FONT_PLAIN);
      jListAnimals.setCellRenderer(new ImageCellRenderer());
    }
    return jListAnimals;
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private class ImageCellRenderer extends JLabel implements ListCellRenderer {

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList,
     *      java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {

      String name = value.toString();
      ImageIcon imgIcon = AnimalImagesRepository.getInstance()
          .getSmallImageIcon(name);
      if (imgIcon != null) {
        setIcon(imgIcon);
      }
      // setText(name);

      if (isSelected) {
        setBackground(list.getSelectionBackground());
        setForeground(list.getSelectionForeground());
      }
      else {
        setBackground(list.getBackground());
        setForeground(list.getForeground());
      }

      setEnabled(list.isEnabled());
      // setFont(list.getFont());
      setOpaque(true);
      return this;
    }
  }

  /**
   * This method initializes jButtonChoose
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonChoose() {
    if (jButtonChoose == null) {
      jButtonChoose = new JButton();
      jButtonChoose.setText(rb.getString("jButtonChooose"));
      jButtonChoose.setFont(GuiFont.FONT_PLAIN);
    }
    return jButtonChoose;
  }

  /**
   * This method initializes jButtonOK
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonOK() {
    if (jButtonOK == null) {
      jButtonOK = new JButton();
      jButtonOK.setText(LanguageManager.getManager().getCurrentLang().ok());
      jButtonOK.setFont(GuiFont.FONT_PLAIN);
    }
    return jButtonOK;
  }

  /**
   * This method initializes jButtonCancel
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButtonCancel() {
    if (jButtonCancel == null) {
      jButtonCancel = new JButton();
      jButtonCancel.setText(LanguageManager.getManager().getCurrentLang().cancel());
      jButtonCancel.setFont(GuiFont.FONT_PLAIN);
    }
    return jButtonCancel;
  }

  /**
   * This method initializes jPanelButton
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJPanelButton() {
    if (jPanelButton == null) {
      jPanelButton = new JPanel();
      jPanelButton.setLayout(new FlowLayout(FlowLayout.RIGHT));
      jPanelButton.add(getJButtonChoose());
      jPanelButton.add(getJButtonOK());
      jPanelButton.add(getJButtonCancel());
    }
    return jPanelButton;
  }

} // @jve:decl-index=0:visual-constraint="10,10"

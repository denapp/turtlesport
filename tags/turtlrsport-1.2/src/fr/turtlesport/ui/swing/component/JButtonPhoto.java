package fr.turtlesport.ui.swing.component;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.ImageDesc;
import fr.turtlesport.ui.swing.MainGui;
import fr.turtlesport.ui.swing.SwingWorker;
import fr.turtlesport.util.ImageUtil;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author denis
 * 
 */
public class JButtonPhoto extends JButton implements ActionListener {
  private static TurtleLogger      log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JButtonPhoto.class);
  }

  /** List des listeners */
  private ArrayList<PhotoListener> listListener;

  /**
   * 
   */
  public JButtonPhoto() {
    super();
    setMinimumSize(new Dimension(40, 40));
  }

  /**
   * @param icon
   */
  public JButtonPhoto(Icon icon) {
    super(icon);
    setMinimumSize(new Dimension(40, 40));
  }

  /**
   * @param text
   * @param icon
   */
  public JButtonPhoto(String text, Icon icon) {
    super(text, icon);
    setMinimumSize(new Dimension(40, 40));
    addActionListener(this);
  }

  /**
   * @param text
   */
  public JButtonPhoto(String text) {
    super(text);
    setMinimumSize(new Dimension(40, 40));
    addActionListener(this);
  }

  /**
   * Ajoute le <code>ActionListener</code> par d&eacute;fault.
   */
  public void addDefaultActionListener() {
    addActionListener(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JComponent#getHeight()
   */
  @Override
  public int getHeight() {
    int h = super.getHeight();
    return (h < 10) ? getMinimumSize().height : h;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JComponent#getWidth()
   */
  @Override
  public int getWidth() {
    int w = super.getWidth();
    return (w < 10) ? getMinimumSize().width : w;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.AbstractButton#setEnabled(boolean)
   */
  @Override
  public void setEnabled(boolean isEnable) {
    removeActionListener(this);
    if (isEnable) {
      addActionListener(this);
    }
  }

  /**
   * Ajoute un listener.
   * 
   * @param listener
   */
  public void addPhotoListener(PhotoListener listener) {
    if (listener == null) {
      return;
    }
    if (listListener == null) {
      synchronized (JButtonPhoto.class) {
        listListener = new ArrayList<PhotoListener>();
      }
    }
    listListener.add(listener);
  }

  /**
   * Ajoute un listener.
   * 
   * @param listener
   */
  public void removePhotoListener(PhotoListener listener) {
    if (listener == null) {
      return;
    }
    if (listListener != null) {
      listListener.remove(listener);
    }
  }

  /**
   * Valorise le fichier de l'image.
   * 
   * @param le
   *          fichier de l'image.
   */
  public void setFile(String path) {
    if (path == null) {
      return;
    }
    if (ImageDesc.isValidPath(path)) {
      try {
        setFile(ImageDesc.createInstance(path));
      }
      catch (ClassNotFoundException e) {
        log.warn("", e);
      }
    }
    else {
      File file = new File(path);
      if (file.isFile()) {
        setFile(file);
      }
    }
  }

  /**
   * Valorise le fichier de l'image.
   * 
   * @param le
   *          fichier de l'image.
   */
  public void setFile(final File file) {
    if (SwingUtilities.isEventDispatchThread()) {
      updateImageFile(file);
    }
    else {
      MainGui.getWindow().beforeRunnableSwing();
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          updateImageFile(file);
          MainGui.getWindow().afterRunnableSwing();
        }
      });
    }
  }

  /**
   * Valorise le fichier de l'image.
   * 
   * @param le
   *          fichier de l'image.
   */
  public void setFile(final ImageDesc imageDesc) {
    if (SwingUtilities.isEventDispatchThread()) {
      updateImageFile(imageDesc);
    }
    else {
      MainGui.getWindow().beforeRunnableSwing();
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          updateImageFile(imageDesc);
          MainGui.getWindow().afterRunnableSwing();
        }
      });
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {
    final JFileChooser fc = new JFileChooser();

    FileFilter filter = new FileFilter() {
      public boolean accept(File f) {
        if (f.isDirectory()) {
          return true;
        }

        String ext = getExtension(f);
        if (ext != null) {
          if (ext.equals("jpeg") || ext.equals("jpg") || ext.equals("gif")
              || ext.equals("tiff") || ext.equals("tif") || ext.equals("png")) {
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
      final File file = fc.getSelectedFile();
      log.info("Opening: " + file.getName());
      updateImageFile(file);
    }
  }

  /**
   * 
   */
  private void fireImageChanged(File file) {
    if (listListener == null) {
      return;
    }
    PhotoEvent e = new PhotoEvent(file);
    for (PhotoListener p : listListener) {
      p.photoChanged(e);
    }
  }

  /**
   * 
   */
  private void fireImageChanged(ImageDesc imgDesc) {
    if (listListener == null) {
      return;
    }
    PhotoEvent e = new PhotoEvent(imgDesc);
    for (PhotoListener p : listListener) {
      p.photoChanged(e);
    }
  }

  /**
   * Mis a jour de l'image.
   */
  private void updateImageFile(final File newFile) {
    setText("Chargement...");
    setIcon(null);

    super.setEnabled(false);

    MainGui.getWindow().beforeRunnableSwing();
    new SwingWorker() {
      @Override
      public Object construct() {
        try {
          ImageIcon icon = ImageUtil
              .makeImage(newFile, getWidth(), getHeight());
          setIcon(icon);
          setText("");
          log.debug("icon=" + icon);
          fireImageChanged(newFile);
        }
        catch (IOException ioe) {
          ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
              .getManager().getCurrentLang(), JButtonPhoto.class);
          JShowMessage.error(MessageFormat.format(rb
              .getString("errorReadingImg"), newFile.getPath()));
        }
        return null;
      }

      public void finished() {        
        MainGui.getWindow().afterRunnableSwing();
        after();
      }
    }.start();
  }

  /**
   * Mis a jour de l'image.
   */
  private void updateImageFile(final ImageDesc imgDesc) {
    setText("Chargement...");
    setIcon(null);

    super.setEnabled(false);

    MainGui.getWindow().beforeRunnableSwing();
    new SwingWorker() {
      @Override
      public Object construct() {
        try {
          ImageIcon icon = ImageUtil.makeImage(imgDesc.getInputStream(),
                                               getWidth(),
                                               getHeight());
          setIcon(icon);
          fireImageChanged(imgDesc);
        }
        catch (IOException ioe) {
          ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
              .getManager().getCurrentLang(), JButtonPhoto.class);
          JShowMessage.error(MessageFormat.format(rb
              .getString("errorReadingImg"), imgDesc.getName()));
        }
        return null;
      }

      public void finished() {
        setText("");
        MainGui.getWindow().afterRunnableSwing();
        after();
      }
    }.start();
  }

  private void after() {
    super.setEnabled(true);
  }

}

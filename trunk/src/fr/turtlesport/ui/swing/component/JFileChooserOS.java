package fr.turtlesport.ui.swing.component;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.OperatingSystem;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public class JFileChooserOS {
  private static TurtleLogger   log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JFileChooserOS.class);
  }

  private File                  selectedFile;

  private File[]                selectedFiles;

  private ArrayList<FileFilter> listFilter;

  private boolean               isMultiSelectionEnabled = false;

  /**
   * 
   */
  public JFileChooserOS() {
    super();
  }

  /**
   * Permet au filechooser de choisir plusirurs fichier.
   * 
   * @param isMultiSelectionEnabled
   *          <code>true</code> si plusieurs fichiers sont
   *          s&eacute;lectionnables.
   */
  public void setMultiSelectionEnabled(boolean isMultiSelectionEnabled) {
    this.isMultiSelectionEnabled = isMultiSelectionEnabled;
  }

  /**
   * @param filter
   */
  public void addChoosableFileFilter(FileFilter filter) {
    if (listFilter == null) {
      listFilter = new ArrayList<FileFilter>();
    }
    listFilter.add(filter);
  }

  /**
   * @param parent
   * @parm dir le r&eacute;pertoire courant.
   * @return
   */
  public int showOpenDialog(Frame parent, final File dir) {
    log.debug(">>showOpenDialog");

    int ret = JFileChooser.CANCEL_OPTION;

    if (OperatingSystem.isMacOSX() && !isMultiSelectionEnabled) {
      FileDialog fd = new FileDialog(parent);
      fd.setMode(FileDialog.LOAD);
      if (listFilter != null) {
        fd.setFilenameFilter(new FilenameFilter() {
          public boolean accept(File dir, String name) {
            File file = new File(dir, name);
            for (FileFilter f : listFilter) {
              if (f.accept(file)) {
                return true;
              }
            }
            return false;
          }
        });
      }
      fd.setVisible(true);
      String fileName = fd.getFile();
      if (fileName != null) {
        selectedFile = new File(fd.getDirectory() + File.separator + fileName);
        ret = JFileChooser.APPROVE_OPTION;
      }
    }
    else {
      final JFileChooser fc = new JFileChooser();
      fc.setCurrentDirectory(dir);
      fc.setMultiSelectionEnabled(isMultiSelectionEnabled);
      if (listFilter != null) {
        for (FileFilter f : listFilter) {
          fc.addChoosableFileFilter(f);
        }
        fc.setAcceptAllFileFilterUsed(true);
        fc.setFileFilter(listFilter.get(0));
      }
      ret = fc.showOpenDialog(parent);
      if (ret == JFileChooser.APPROVE_OPTION) {
        if (isMultiSelectionEnabled) {
          selectedFiles = fc.getSelectedFiles();
        }
        else {
          selectedFile = fc.getSelectedFile();
        }
      }
    }

    log.debug("<<showOpenDialog ret=" + ret);
    return ret;
  }

  /**
   * @param parent
   * @return
   */
  public int showOpenDialog(Frame parent) {
    return showOpenDialog(parent, null);
  }

  /**
   * Restitue le fichier selecion&eacute;.
   * 
   * @return le fichier selecion&eacute;.
   */
  public File getSelectedFile() {
    return selectedFile;
  }

  /**
   * Restitue les fichiers selecion&eacute;s.
   * 
   * @return le fichier selecion&eacute;.
   */
  public File[] getSelectedFiles() {
    return selectedFiles;
  }

}

package fr.turtlesport.ui.swing.component;

import java.awt.Component;
import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import fr.turtlesport.Configuration;
import fr.turtlesport.ConfigurationException;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.MainGui;
import fr.turtlesport.util.Location;
import fr.turtlesport.util.OperatingSystem;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author denis apparicio
 * 
 */
public final class JFileSaver {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(JFileSaver.class);
  }

  private JFileSaver() {
  }

  /**
   * IHM pour sauvegarde de fichier.
   * 
   * @param parent
   * @param currentDirectory
   * @param name
   * @param ext
   * @param description
   * @return le fichier &agrave; sauvegarder ou <code>null</code> si non
   *         renseign&eacute;.
   */
  public static File showSaveDialog(final Component parent,
                                    final File currentDirectory,
                                    final String name,
                                    final String ext,
                                    final String description) {

    final String suffix = (ext.charAt(0) == '.') ? ext : '.' + ext;
    File out = null;    
    if (OperatingSystem.isMacOSX()) {
      // Bug sous Mac OS X les extenions ne sont pas affichees et retournees
      FileDialog dlg = new FileDialog(MainGui.getWindow());
      dlg.setLocale(LanguageManager.getManager().getLocale());
      dlg.setMode(FileDialog.SAVE);
      dlg.setDirectory(currentDirectory.getPath());
      dlg.setFile(name + suffix);
      dlg.setFilenameFilter(new FilenameFilter() {
        public boolean accept(File dir, String name) {
          return name.endsWith(suffix);
        }
      });
      dlg.setVisible(true);
      String fileName = dlg.getFile();
      if (fileName != null) {
        if (!fileName.endsWith(suffix)) {
          fileName = fileName + suffix;
        }
        out = new File(dlg.getDirectory() + File.separator + fileName);
      }
    }
    else {
      // Autres OS
      final MyJFileChooser fc = new MyJFileChooser(currentDirectory, suffix);
      if (name != null && !"".equals(name)) {
        fc.setSelectedFile(new File(currentDirectory, name + suffix));
      }

      fc.addChoosableFileFilter(new FileFilter() {
        public boolean accept(File f) {
          return f.isFile() && f.getPath().endsWith(suffix);
        }

        public String getDescription() {
          return description;
        }
      });
      fc.setAcceptAllFileFilterUsed(true);

      if (fc.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
        out = fc.getSelectedFile();
      }
    }

    if (out != null) {
      // sauvegarde dans le .ini
      Configuration.getConfig().addProperty("export",
                                            ext,
                                            out.getParentFile()
                                                .getAbsolutePath());
      try {
        Configuration.getConfig().save();
      }
      catch (ConfigurationException e) {
        log.error("", e);
      }
    }

    return out;
  }

  /**
   * IHM pour sauvegarde de fichier.
   * 
   * @param parent
   * @param currentDirectory
   * @param name
   * @param ext
   * @param description
   * @return le fichier &agrave; sauvegarder ou <code>null</code> si non
   *         renseign&eacute;.
   */
  public static File showSaveDialog(final Component parent,
                                    final String currentDirectory,
                                    final String name,
                                    final String ext,
                                    final String description) {
    return showSaveDialog(parent,
                          new File(currentDirectory),
                          name,
                          ext,
                          description);
  }

  /**
   * IHM pour sauvegarde de fichier.
   * 
   * @param parent
   * @param name
   * @param ext
   * @param description
   * @return le fichier &agrave; sauvegarder ou <code>null</code> si non
   *         renseign&eacute;.
   */
  public static File showSaveDialog(final Component parent,
                                    final String name,
                                    final String ext,
                                    final String description) {
    // recuperation du dernier repertoire
    String lastDir = Configuration.getConfig().getProperty("export", ext);

    File currentDirectory = null;
    if (lastDir != null) {
      currentDirectory = new File(lastDir);
      if (!currentDirectory.exists() || !currentDirectory.isDirectory()) {
        currentDirectory = new File(Location.userLocation());
      }
    }
    else {
      currentDirectory = new File(Location.userLocation());
    }

    // recuperation du fichier
    return showSaveDialog(parent, currentDirectory, name, ext, description);
  }

  /**
   * @author Denis Apparicio
   * 
   */
  private static class MyJFileChooser extends JFileChooser {
    private String suffix;

    public MyJFileChooser(File currentDirectory, String suffix) {
      super(currentDirectory);
      this.suffix = suffix;
      setDragEnabled(true);
      setLocale(LanguageManager.getManager().getLocale());
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JFileChooser#approveSelection()
     */
    @Override
    public void approveSelection() {
      File out = getSelectedFile();
      if (!out.getPath().endsWith(suffix)) {
        out = new File(out.getAbsolutePath() + suffix);
      }

      if (getSelectedFile().isFile()) {
        // fichier existe deja
        ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
            .getManager().getCurrentLang(), JFileSaver.class);

        String msg = MessageFormat.format(rb.getString("msgOverwrite"), out
            .getName());
        if (!JShowMessage.question(MyJFileChooser.this, msg, rb
            .getString("titleOverwrite"))) {
          return;
        }
      }
      super.approveSelection();
    }

  }

}

package fr.turtlesport.ui.swing;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

import fr.turtlesport.geo.IGeoFileDesc;
import fr.turtlesport.geo.garmin.fit.FitFile;
import fr.turtlesport.geo.garmin.hst.HstFile;
import fr.turtlesport.geo.garmin.tcx.TcxFile;
import fr.turtlesport.geo.gpx.GpxFile;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author Denis Apparicio
 * 
 */
public class ImportFileFilter extends FileFilter {
  private String[] ext;

  private String   desc;

  /**
   * Construit un <code>FileFilter</code> pour un <code>IGeoFileDesc</code>.
   * 
   * @param fileDesc
   */
  protected ImportFileFilter(IGeoFileDesc fileDesc) {
    this(fileDesc.extension(), fileDesc.description());
  }

  /**
   * Construit un <code>FileFilter</code>.
   * 
   * @param ext
   *          les extensions
   * @param desc
   *          la description.
   */
  protected ImportFileFilter(String[] ext, String desc) {
    super();
    this.ext = ext;
    this.desc = desc;
  }

  /**
   * Construit un <code>FileFilter</code>.
   * 
   * @param list
   *          la liste des extensions.
   * @param desc
   *          la description.
   */
  protected ImportFileFilter(List<String> list, String desc) {
    super();
    ext = new String[list.size()];
    list.toArray(ext);
    this.desc = desc;
  }

  /**
   * Construit un <code>FileFilter</code> pour tous les imports.
   * 
   * @return un <code>FileFilter</code> pour tous les imports.
   */
  protected static FileFilter filefilterAllImport() {

    // Recuperation de toutes les extensions et de la description
    StringBuilder st = new StringBuilder();
    ArrayList<String> list = new ArrayList<String>();
    for (String s : new HstFile().extension()) {
      list.add(s);
      st.append("*.");
      st.append(s);
      st.append(',');
    }
    for (String s : new TcxFile().extension()) {
      list.add(s);
      st.append("*.");
      st.append(s);
      st.append(',');
    }
    for (String s : new GpxFile().extension()) {
      list.add(s);
      st.append("*.");
      st.append(s);
      st.append(',');
    }
    for (String s : new FitFile().extension()) {
      list.add(s);
      st.append("*.");
      st.append(s);
      st.append(',');
    }

    ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
        .getManager().getCurrentLang(), MainGui.class);
    String desc = MessageFormat.format(rb.getString("importAllDesc"), st
        .substring(0, st.length() - 1));

    return new ImportFileFilter(list, desc);
  }

  /**
   * Restitue la liste des <code>FileFilter</code> pour tous les imports.
   * 
   * @return un <code>FileFilter</code> pour tous les imports.
   */
  protected static List<FileFilter> filefilters() {
    List<FileFilter> list = new ArrayList<FileFilter>();

    // Toutes les extensions
    list.add(filefilterAllImport());

    // gpx
    list.add(new ImportFileFilter(new GpxFile()));
    // hst
    list.add(new ImportFileFilter(new HstFile()));
    // tcx
    list.add(new ImportFileFilter(new TcxFile()));
    // fit
    list.add(new ImportFileFilter(new FitFile()));

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
   */
  @Override
  public boolean accept(File f) {
    if (f.isDirectory()) {
      return true;
    }
    String path = f.getPath();
    for (String s : ext) {
      if (path.toLowerCase().endsWith(s)) {
        return true;
      }
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.filechooser.FileFilter#getDescription()
   */
  @Override
  public String getDescription() {
    return desc;
  }

}

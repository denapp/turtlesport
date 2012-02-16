package fr.turtlesport.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import fr.turtlesport.log.TurtleLogger;

/**
 * Classe pour la gestion des fichier de configuration avec section.
 * 
 * @author Denis Apparicio
 * 
 */
public final class IniFile {
  private static TurtleLogger                            log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(IniFile.class);
  }

  /** path du du .ini */
  private String                                         path;

  /** map des sections dans le .ini. */
  private Hashtable<String, Section>                     mapSections;

  // We want to keep the same connection for a given thread
  // as long as we're in the same transaction
  private static ThreadLocal<Hashtable<String, Section>> transaction = new ThreadLocal<Hashtable<String, Section>>();

  /**
   * Construit un objet <code>IniFile</code>.
   */
  public IniFile() {
    mapSections = new Hashtable<String, Section>();
  }

  /**
   * Transaction.
   * 
   */
  public synchronized void beginTransaction() {
    log.debug(">>beginTransaction");

    if (transaction.get() != null) {
      log.warn("This thread is already in a transaction");
      return;
    }

    // on clone la map
    Hashtable<String, Section> newMap = new Hashtable<String, Section>();
    Enumeration<String> e = mapSections.keys();
    while (e.hasMoreElements()) {
      String key = e.nextElement();
      // Recuperation de la section
      Section section = mapSections.get(key);
      newMap.put(key, section.copy());
    }

    transaction.set(newMap);

    log.debug("<<beginTransaction");
  }

  /**
   * Commit.
   * 
   */
  public void commitTransaction() {
    log.debug(">>commitTransaction");

    if (transaction.get() == null) {
      log.warn("Can't commit: this thread isn't currently in a transaction");
      return;
    }
    mapSections = transaction.get();
    transaction.set(null);

    log.debug("<<commitTransaction");
  }

  /**
   * Rollback.
   * 
   */
  public void rollbackTransaction() {
    log.debug(">>rollbackTransaction");
    transaction.set(null);
    log.debug("<<rollbackTransaction");
  }

  /**
   * @return restitue le path du fichier .ini
   */
  public String getPath() {
    return path;
  }

  /**
   * 
   * @return Restitue les sections.
   */
  public Collection<?> getSections() {
    return getMapSection().values();
  }

  /**
   * Ajoute une section.
   * 
   * @param name
   *          nom de la section
   */
  public void addSection(String name) {
    if (!containsSection(name)) {
      getMapSection().put(name, new Section(name));
    }
  }

  /**
   * Supprime une section.
   * 
   * @param name
   *          nom de la section
   */
  public boolean removeSection(String name) {
    return (getMapSection().remove(name) == null) ? false : true;
  }

  /**
   * D&eacute; si la session existe.
   * 
   * @param name
   *          nom de la section.
   * @retrun <code>true</code> si la session existe, <code>false</code> sinon.
   */
  public boolean containsSection(String name) {
    return getMapSection().containsKey(name);
  }

  /**
   * Supprime la propri&eacute;t&eacute; d'une section.
   * 
   * @param sectionName
   *          nom de la section.
   * @param propName
   *          nom de la propri&eacute;t&eacute;.
   */
  public void removeProperty(String sectionName, String propName) {
    Section objSec = getMapSection().get(sectionName);
    if (objSec != null) {
      objSec.removeProperty(propName);
    }
  }

  /**
   * Ajoute une propri&eacute;t&eacute; &agrave; une section.
   * 
   * @param sectionName
   *          nom de la section.
   * @param propName
   *          nom de la propri&eacute;t&eacute;.
   * @param propValue
   *          valeur de la propri&eacute;t&eacute;.
   */
  public void addProperty(String sectionName, String propName, String propValue) {
    Section objSec = getMapSection().get(sectionName);
    if (objSec == null) {
      addSection(sectionName);
      objSec = getMapSection().get(sectionName);
    }
    objSec.addProperty(propName, propValue);
  }

  /**
   * Restitue la propri&eacute;t&eacute; &agrave; d'une section.
   * 
   * @param sectionName
   *          nom de la section.
   * @param propName
   *          nom de la propri&eacute;t&eacute;.
   */
  public String getProperty(String sectionName, String propName) {
    Section objSec = getMapSection().get(sectionName);
    if (objSec != null) {
      return objSec.getProperty(propName);
    }
    return null;
  }

  /**
   * Charge le fichier .ini.
   * 
   * @throws IOException
   */
  public void load(InputStream input) throws IOException {
    load(new BufferedReader(new InputStreamReader(input)));
  }

  /**
   * Charge le fichier .ini.
   * 
   * @throws IOException
   */
  public void load(BufferedReader reader) throws IOException {
    mapSections.clear();

    try {
      Section currentSection = null;
      int iPos;
      while (true) {
        // Get next line
        String line = reader.readLine();
        log.debug("line <" + line + ">");
        if (line == null) {
          break;
        }
        else if (line.length() == 0) {
          continue;
        }
        else if (line.charAt(0) == ';' || line.charAt(0) == '#') {
          // commentaire
        }
        else if (line.startsWith("[") && line.endsWith("]")) {
          // nouvelle section
          String sectionName = line.substring(1, line.length() - 1);
          log.debug("sectionName <" + sectionName + ">");
          currentSection = new Section(sectionName.trim());
          mapSections.put(sectionName.trim(), currentSection);
        }
        else if ((iPos = line.indexOf("=")) > 0 && currentSection != null) {
          // ligne propri�t� key=value
          String key = line.substring(0, iPos).trim();
          String value = line.substring(iPos + 1).trim();
          log.debug("key<" + key + "> value<" + value + ">");
          currentSection.addProperty(key, value);
        }
      }
    }
    finally {
      try {
        reader.close();
      }
      catch (IOException e) {
      }
      log.debug("<<load");
    }
  }

  /**
   * Charge le fichier .ini.
   * 
   * @throws IOException
   */
  public void load(String path) throws IOException {

    // verification de l'existence du fichier
    checkPath(path);

    // Chargement du fichier
    this.path = path;

    // chargement
    load(new BufferedReader(new FileReader(path)));
  }

  /**
   * Sauvegarde du fichier .ini.
   * 
   * @throws IOException
   */
  public void save(File file) throws IOException {
    log.debug(">>save");

    if (file == null) {
      throw new IllegalArgumentException();
    }

    // sauvegarde
    FileWriter writer = new FileWriter(file);
    save(writer);

    log.debug("<<save");
  }

  private void checkPath(String path) throws FileNotFoundException {
    if (path == null || path.length() == 0) {
      throw new FileNotFoundException();
    }

    // verification que le fichier existe
    File file = new File(path);
    if (!file.isFile()) {
      throw new FileNotFoundException();
    }
  }

  /**
   * Sauvegarde du fichier .ini.
   */
  private void save(Writer writer) {
    log.debug(">>save writer");

    try {
      if (mapSections.size() == 0) {
        return;
      }

      // on boucle sur les sections
      Iterator<?> it = mapSections.keySet().iterator();
      while (it.hasNext()) {
        String sectionName = (String) it.next();
        Section section = mapSections.get(sectionName);
        // ecriture d'une section
        section.save(writer);
      }
      writer.flush();
    }
    catch (IOException ioe) {
    }
    finally {
      if (writer != null) {
        try {
          writer.close();
        }
        catch (IOException ioe) {
        }
      }
    }

    log.debug("<<save writer");
  }

  private Hashtable<String, Section> getMapSection() {
    if (transaction.get() != null) {
      return transaction.get();
    }
    return mapSections;
  }

  /**
   * Repr�sente une section dans le fichier ini.
   * 
   * @author Denis Apparicio
   */
  private class Section {

    /** Variable to hold the section name. */
    private String                    name;

    /** Hasmap des properties */
    private Hashtable<String, String> mapProps;

    /**
     * Construct a new section object identified by the name specified in
     * parameter.
     * 
     * @param pstrSection
     *          The new sections name.
     */
    public Section(String name) {
      this.name = name;
      mapProps = new Hashtable<String, String>();
    }

    /**
     * @return
     */
    public Section copy() {
      Section section = new Section(name);

      Enumeration<String> e = mapProps.keys();
      String key;
      while (e.hasMoreElements()) {
        key = e.nextElement();
        section.addProperty(key, getProperty(key));
      }
      return section;
    }

    /**
     * 
     * @return Restitue le nom de la section.
     */
    public String getName() {
      return name;
    }

    /**
     * Supprime une propri�t� dela section
     * 
     * @param name
     *          le nom de la propri�t�
     */
    public void removeProperty(String name) {
      if (mapProps.containsKey(name)) {
        mapProps.remove(name);
      }
    }

    /**
     * Ajoute ou modifie une propri�t� dans la section
     */
    public void addProperty(String name, String value) {
      mapProps.put(name, value);
    }

    /**
     * Restitue la valeur d'une propri&eacute;t&eacute;.
     */
    public String getProperty(String name) {
      return mapProps.get(name);
    }

    public Hashtable<?, ?> getProps() {
      return mapProps;
    }

    /**
     * Sauvegarde de la section
     */
    public void save(Writer wr) throws IOException {

      // ecriture du nom de la section
      StringBuffer stSection = new StringBuffer();
      stSection.append("[");
      stSection.append(name);
      stSection.append("]");
      wr.write(stSection.toString());
      writeln(wr);

      // ecriture des proprietes
      Iterator<?> it = mapProps.keySet().iterator();
      while (it.hasNext()) {
        String key = (String) it.next();
        String val = mapProps.get(key);
        writeln(wr, key + "=" + val);
      }
      writeln(wr);
    }

    private void writeln(Writer wr, String s) throws IOException {
      wr.write(s);
      writeln(wr);
    }

    private void writeln(Writer wr) throws IOException {
      wr.write("\r\n");
    }

  }
}
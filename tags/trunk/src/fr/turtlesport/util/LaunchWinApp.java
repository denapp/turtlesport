package fr.turtlesport.util;


/**
 * @author Denis apparicio
 *
 */
public final class LaunchWinApp {
  static {
    if (!OperatingSystem.isWindows()) {
      throw new RuntimeException("os different de windows <"
                                 + OperatingSystem.name() + ">");
    }
    // chargement de la librairie
    Library.load(LaunchWinApp.class, "launchWinApp");
  }

  private LaunchWinApp() {
  }
  
  /**
   * Launch the application for the given file.
   * 
   * @param filePath
   *          Path name of the given file.
   * @param verb
   *          Specify the verb to be executed.
   * @return error code
   */
  public static int launch(String verb, String path) {
    if (verb == null || "".equals(verb)) {
      throw new IllegalArgumentException("verb");
    }
    if (path == null || "".equals(path)) {
      throw new IllegalArgumentException("path");
    }

    return launchInner(verb, path);
  }
  
  /**
   * Launch the application for the given file.
   * 
   * @param filePath
   *          Path name of the given file.
   * @param verb
   *          Specify the verb to be executed.
   * @return error code
   */
  private static native int launchInner(String verb, String path);
}

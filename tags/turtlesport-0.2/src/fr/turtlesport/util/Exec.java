package fr.turtlesport.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Cette classe presente un utilitaire pour executer une commande. Elle permet
 * de vider les streams de process. Sous certaines OS si ça n'est pas fait cela
 * peut bloquer le process.
 * 
 * @author Denis Apparicio
 * 
 */
public final class Exec {

  /** Stream du process */
  private InputStreamReader stdout;

  /** Stream erreur du process */
  private InputStreamReader stderr;

  /**
   * Execute un process, en vidant ces treams pour eviter de bloquer le process.
   * 
   * @param process
   */
  private Exec(Process process) {
    stdout = new InputStreamReader(process.getInputStream());
    stderr = new InputStreamReader(process.getErrorStream());
  }

  /**
   * Lance la lecture des entrees et sorties du process.
   */
  public void run() {

    // Vide la sortie
    new Thread() {
      public void run() {
        BufferedReader br = new BufferedReader(stdout);
        try {
          while (br.readLine() != null) {
          }
        }
        catch (IOException e) {
        }
      }
    }.start();

    // vide les erreurs
    new Thread() {
      public void run() {
        BufferedReader br = new BufferedReader(stderr);
        try {
          while (br.readLine() != null) {
          }
        }
        catch (IOException e) {
        }
      }
    }.start();
  }

  /**
   * Execute le process.
   * 
   * @param process
   */
  public static void exec(Process process) {
    new Exec(process).run();
  }

  /**
   * 
   * @param command
   * @throws IOException
   */
  public static void exec(String command) throws IOException {
    new Exec(Runtime.getRuntime().exec(command)).run();
  }

  /**
   * 
   * @param cmd
   * @param envp
   * @throws IOException
   */
  public static void exec(String cmd, String[] envp) throws IOException {
    new Exec(Runtime.getRuntime().exec(cmd, envp)).run();
  }

  /**
   * 
   * @param command
   * @param envp
   * @param dir
   * @throws IOException
   */
  public static void exec(String command, String[] envp, File dir) throws IOException {
    new Exec(Runtime.getRuntime().exec(command, envp, dir)).run();
  }

  /**
   * Execute une commande avec des arguments.
   * 
   * @param cmdarray
   *          le tableau de paramètres.
   * @throws IOException
   */
  public static void exec(String[] cmdarray) throws IOException {
    new Exec(Runtime.getRuntime().exec(cmdarray)).run();
  }

  /**
   * Execute une commande avec des arguments et des variables d'environnement.
   * 
   * @param cmdarray
   *          les arguments
   * @param envp
   *          les variables d'environnement.
   * @throws IOException
   */
  public static void exec(String[] cmdarray, String[] envp) throws IOException {
    new Exec(Runtime.getRuntime().exec(cmdarray, envp)).run();
  }

  /**
   * Execute une commande avec des arguments et des variables d'environnement en
   * sp&eacute;cifiant le r&eacute;pertoire d'execution.
   * 
   * @param cmdarray
   *          les arguments
   * @param envp
   *          les variables d'environnement.
   * @param dir
   *          le rep&eacute;rtoire d'execution.
   * @throws IOException
   */
  public static void exec(String[] cmdarray, String[] envp, File dir) throws IOException {
    new Exec(Runtime.getRuntime().exec(cmdarray, envp)).run();
  }
}

package fr.turtlesport;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.security.SecurePassword;
import fr.turtlesport.security.SecurePasswordException;

/**
 * @author Denis Appariciod
 * 
 */
public final class ProxyConfiguration {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ProxyConfiguration.class);
  }

  private ProxyConfiguration() {    
  }
  
  /**
   * Configure le proxy.
   */
  public static void configure() {
    System.setProperty("proxyHost", "");
    System.setProperty("proxyPort", "");
    System.setProperty("java.net.useSystemProxies", "false");

    if (hasDetectProxy()) {
      System.setProperty("java.net.useSystemProxies", "true");
    }
    else if (hasProxyConfig()) {
      log.error("host="+getHost());
      log.error("port="+getPort());

      System.setProperty("http.proxyHost", getHost());
      System.setProperty("http.proxyPort", Integer.toString(getPort()));
      final String username = getUsername();
      final String password = getPassword();
      if (username != null && password != null) {
        Authenticator.setDefault(new ProxyAuthenticator(username, password));
      }
      else {
        Authenticator.setDefault(null);
      }
    }
  }

  /**
   * D&eacute;termine si pas de proxy.
   * 
   * @return <code>true</code> si pas de proxy, <code>false</code>.
   */
  public static boolean hasNoProxy() {
    return Configuration.getConfig().getPropertyAsBoolean("proxy",
                                                          "noproxy",
                                                          false);
  }

  /**
   * D&eacute;termine si proxy d&eacute;tect&eacute; par le syst&egrave;me.
   * 
   * @return <code>true</code> si proxy d&eacute;tect&eacute; par le
   *         syst&egrave;me, <code>false</code>.
   */
  public static boolean hasDetectProxy() {
    return !hasNoProxy()
           && !hasProxyConfig()
           && Configuration.getConfig().getPropertyAsBoolean("proxy",
                                                             "detect",
                                                             true);
  }

  /**
   * D&eacute;termine si proxy configur&eacute; par l'utilisateur.
   * 
   * @return <code>true</code> si proxy configur&eacute; par l'utilisateur.
   */
  public static boolean hasProxyConfig() {
    return Configuration.getConfig().getProperty("proxy", "host") != null;
  }

  /**
   * Restitue le host du proxy.
   * 
   * @return le host du proxy.
   */
  public static String getHost() {
    return Configuration.getConfig().getProperty("proxy", "host");
  }

  /**
   * Restitue le port du proxy.
   * 
   * @return le port du proxy.
   */
  public static int getPort() {
    if (hasProxyConfig()) {
      try {
        return Integer.parseInt(Configuration.getConfig().getProperty("proxy",
                                                                      "port"));
      }
      catch (NumberFormatException e) {
        return 80;
      }
    }
    return -1;
  }

  /**
   * Restitue le nom de l'utilisateur.
   * 
   * @return le nom de l'utilisateur.
   */
  public static String getUsername() {
    return Configuration.getConfig().getProperty("proxy", "username");
  }

  /**
   * Restitue le mot de passe de l'utilisateur.
   * 
   * @return le mot de passe de l'utilisateur.
   */
  public static String getPassword() {
    String pwd = Configuration.getConfig().getProperty("proxy", "password");
    if (pwd != null) {
      try {
        return SecurePassword.decrypt(pwd);
      }
      catch (SecurePasswordException e) {
        log.error("", e);
      }
    }
    return null;
  }

  /**
   * Mis &agrave; jour du proxy.
   * 
   * @param hasNoProxy
   * @param hasDetect
   * @param host
   * @param port
   * @param username
   * @param password
   */
  public static void update(boolean hasNoProxy,
                            boolean hasDetect,
                            String host,
                            int port,
                            String username,
                            String password) {
    // pas de proxy
    Configuration.getConfig().addProperty("proxy",
                                          "noproxy",
                                          Boolean.toString(hasNoProxy));

    // detection proxy
    Configuration.getConfig().addProperty("proxy",
                                          "detect",
                                          Boolean.toString(hasDetect));

    Configuration.getConfig().removeProperty("proxy", "host");
    Configuration.getConfig().removeProperty("proxy", "port");
    Configuration.getConfig().removeProperty("proxy", "username");
    Configuration.getConfig().removeProperty("proxy", "password");

    if (host != null && port > 0) {
      Configuration.getConfig().addProperty("proxy", "host", host);
      Configuration.getConfig().addProperty("proxy",
                                            "port",
                                            Integer.toString(port));
      if (username != null && !"".equals(username) && password != null
          && !"".equals(password)) {
        Configuration.getConfig().addProperty("proxy", "username", username);

        // on encrypte le mot de passe
        try {
          Configuration.getConfig().addProperty("proxy",
                                                "password",
                                                SecurePassword
                                                    .encrypt(password));
        }
        catch (SecurePasswordException e) {
          log.error("", e);
        }
      }
    }

    // mis a jour du proxy
    configure();
  }

  private static class ProxyAuthenticator extends Authenticator {

    private String userName, password;

    protected PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(userName, password.toCharArray());
    }

    public ProxyAuthenticator(String userName, String password) {
      this.userName = userName;
      this.password = password;
    }
  }

}

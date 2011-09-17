package fr.turtlesport;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import fr.turtlesport.lang.ILanguage;
import fr.turtlesport.lang.LanguageEvent;
import fr.turtlesport.lang.LanguageListener;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.ui.swing.GuiFont;
import fr.turtlesport.ui.swing.JDialogAbout;
import fr.turtlesport.ui.swing.JDialogPreference;
import fr.turtlesport.ui.swing.MainGui;
import fr.turtlesport.ui.swing.component.JMenuItemTurtle;
import fr.turtlesport.util.OperatingSystem;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author denisapparicio
 * 
 */
public final class MacOSXTurleApp implements InvocationHandler {
  private static TurtleLogger    log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(MacOSXTurleApp.class);
  }

  private Object                 targetObject;

  private Method                 targetMethod;

  private String                 proxySignature;

  private static Object          macOSXApplication;

  private static JMenu           jMenuWindow;

  private static JMenuItem       jMenuItemZoom;

  private static JMenuItem       jMenuItemToFront;

  private static JMenuItemTurtle jMenuItemDock;

  /**
   * @param proxySignature
   * @param target
   * @param handler
   */
  private MacOSXTurleApp(String proxySignature, Object target, Method handler) {
    this.proxySignature = proxySignature;
    this.targetObject = target;
    this.targetMethod = handler;
  }

  /**
   * Configuration application MacOSX.
   */
  public static void configure() {
    if (macOSXApplication != null) {
      return;
    }

    // Menu MacOSX
    // ---------------------
    // Mac OS X application menu name
    System.setProperty("com.apple.mrj.application.apple.menu.about.name",
                       "Turtle Sport");
    // Mac OS X menu bar
    System.setProperty("apple.laf.useScreenMenuBar", "true");

    try {
      macOSXApplication = Class.forName("com.apple.eawt.Application")
          .newInstance();

      setQuitHandler(Launcher.getInstance(), Launcher.class
          .getDeclaredMethod("stopIt"));
      setAboutHandler(JDialogAbout.class, JDialogAbout.class
          .getDeclaredMethod("prompt"));
      setPreferencesHandler(JDialogPreference.class, JDialogPreference.class
          .getDeclaredMethod("prompt"));
    }
    catch (SecurityException e) {
      log.error("", e);
    }
    catch (NoSuchMethodException e) {
      log.error("", e);
    }
    catch (InstantiationException e) {
      log.error("", e);
    }
    catch (IllegalAccessException e) {
      log.error("", e);
    }
    catch (ClassNotFoundException e) {
      log.error("", e);
    }
  }

  /**
   * Ajoute les menus Mac OS X.
   */
  public static void addWindowMenu() {
    if (!OperatingSystem.isMacOSX()) {
      return;
    }

    JMenuBar menuBar = MainGui.getWindow().getJMenuBar();

    // Menu Fenetre
    jMenuWindow = new JMenu();
    jMenuWindow.setFont(GuiFont.FONT_PLAIN);
    menuBar.add(jMenuWindow);

    // Item dans le dock
    jMenuItemDock = new JMenuItemTurtle();
    jMenuItemDock.setAccelerator(MainGui.getWindow().getMenuProperties(),
                                 "jMenuItemDock");
    jMenuItemDock.setFont(GuiFont.FONT_PLAIN);

    // Item reduire/Agrandir
    jMenuItemZoom = new JMenuItem();
    jMenuItemZoom.setFont(GuiFont.FONT_PLAIN);

    // Item au premier plan
    jMenuItemToFront = new JMenuItem();
    jMenuItemToFront.setFont(GuiFont.FONT_PLAIN);

    jMenuWindow.add(jMenuItemDock);
    jMenuWindow.add(jMenuItemZoom);
    jMenuWindow.addSeparator();
    jMenuWindow.add(jMenuItemToFront);

    // Evenements
    jMenuItemDock.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        MainGui.getWindow().setState(Frame.ICONIFIED);
      }
    });

    jMenuItemZoom.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        JFrame frame = MainGui.getWindow();
        if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) != 0) {
          frame.setExtendedState(frame.getExtendedState()
                                 & ~Frame.MAXIMIZED_BOTH);
        }
        else {
          frame.setExtendedState(frame.getExtendedState()
                                 | Frame.MAXIMIZED_BOTH);
        }
      }
    });

    jMenuItemToFront.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        JFrame frame = MainGui.getWindow();
        frame.setAlwaysOnTop(true);
      }
    });

    MainGui.getWindow().addWindowListener(new WindowAdapter() {
      @Override
      public void windowDeiconified(WindowEvent e) {
        jMenuItemDock.setEnabled(true);
        jMenuItemZoom.setEnabled(true);
        jMenuItemToFront.setEnabled(true);
      }

      @Override
      public void windowIconified(WindowEvent e) {
        jMenuItemDock.setEnabled(false);
        jMenuItemZoom.setEnabled(false);
        jMenuItemToFront.setEnabled(false);
      }
    });

    // Language
    LanguageManager.getManager().addLanguageListener(new LanguageListener() {
      public void completedRemoveLanguageListener() {
      }

      /*
       * (non-Javadoc)
       * 
       * @see fr.turtlesport.lang.LanguageListener#languageChanged(fr.turtlesport.lang.LanguageEvent)
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

    });

    performedLanguage(LanguageManager.getManager().getCurrentLang());
  }

  private static void performedLanguage(ILanguage lang) {
    ResourceBundle rb = ResourceBundleUtility.getBundle(lang,
                                                        MacOSXTurleApp.class);
    jMenuWindow.setText(rb.getString("jMenuWindow"));
    jMenuItemDock.setText(rb.getString("jMenuItemDock"));
    jMenuItemZoom.setText(rb.getString("jMenuItemZoom"));
    jMenuItemToFront.setText(rb.getString("jMenuItemToFront"));
  }

  /**
   * Menu Quit.
   */
  private static void setQuitHandler(Object target, Method method) {
    setHandler(new MacOSXTurleApp("handleQuit", target, method));
  }

  /**
   * Menu About.
   */
  private static void setAboutHandler(Object target, Method method) {
    setHandler(new MacOSXTurleApp("handleAbout", target, method));
    try {
      Method enableAboutMethod = macOSXApplication.getClass()
          .getDeclaredMethod("setEnabledAboutMenu",
                             new Class[] { boolean.class });
      enableAboutMethod.invoke(macOSXApplication, new Object[] { Boolean
          .valueOf(true) });
    }
    catch (Exception e) {
      log.error("", e);
    }
  }

  /**
   * Menu preferences.
   */
  private static void setPreferencesHandler(Object target, Method method) {
    setHandler(new MacOSXTurleApp("handlePreferences", target, method));
    try {
      Method enablePrefsMethod = macOSXApplication.getClass()
          .getDeclaredMethod("setEnabledPreferencesMenu",
                             new Class[] { boolean.class });
      enablePrefsMethod.invoke(macOSXApplication, new Object[] { Boolean
          .valueOf(true) });
    }
    catch (Exception e) {
      log.error("", e);
    }
  }

  private static void setHandler(MacOSXTurleApp adapter) {
    try {
      Class<?> applicationClass = Class.forName("com.apple.eawt.Application");

      Class<?> applicationListenerClass = Class
          .forName("com.apple.eawt.ApplicationListener");
      Method addListenerMethod = applicationClass
          .getDeclaredMethod("addApplicationListener",
                             new Class[] { applicationListenerClass });

      Object osxAdapterProxy = Proxy.newProxyInstance(MacOSXTurleApp.class
          .getClassLoader(), new Class[] { applicationListenerClass }, adapter);
      addListenerMethod.invoke(macOSXApplication,
                               new Object[] { osxAdapterProxy });
    }
    catch (ClassNotFoundException e) {
      log.error("", e);
    }
    catch (IllegalArgumentException e) {
      log.error("", e);
    }
    catch (IllegalAccessException e) {
      log.error("", e);
    }
    catch (InvocationTargetException e) {
      log.error("", e);
    }
    catch (SecurityException e) {
      log.error("", e);
    }
    catch (NoSuchMethodException e) {
      log.error("", e);
    }

  }

  // Override this method to perform any operations on the event
  // that comes with the various callbacks
  // See setFileHandler above for an example
  public boolean callTarget(Object appleEvent) throws InvocationTargetException,
                                              IllegalAccessException {
    Object result = targetMethod.invoke(targetObject, (Object[]) null);
    if (result == null) {
      return true;
    }
    return Boolean.valueOf(result.toString()).booleanValue();
  }

  // InvocationHandler implementation
  // This is the entry point for our proxy object; it is called every time an
  // ApplicationListener method is invoked
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (isCorrectMethod(method, args)) {
      boolean handled = callTarget(args[0]);
      setApplicationEventHandled(args[0], handled);
    }
    // All of the ApplicationListener methods are void; return null regardless
    // of what happens
    return null;
  }

  // Compare the method that was called to the intended method when the
  // OSXAdapter instance was created
  // (e.g. handleAbout, handleQuit, handleOpenFile, etc.)
  protected boolean isCorrectMethod(Method method, Object[] args) {
    return (targetMethod != null && proxySignature.equals(method.getName()) && args.length == 1);
  }

  // It is important to mark the ApplicationEvent as handled and cancel the
  // default behavior
  // This method checks for a boolean result from the proxy method and sets the
  // event accordingly
  protected void setApplicationEventHandled(Object event, boolean handled) {
    if (event != null) {
      try {
        Method setHandledMethod = event.getClass()
            .getDeclaredMethod("setHandled", boolean.class);
        // If the target method returns a boolean, use that as a hint
        setHandledMethod.invoke(event,
                                new Object[] { Boolean.valueOf(handled) });
      }
      catch (Exception e) {
        log.error("unable to handle an ApplicationEvent: " + event);
        log.error("", e);
      }
    }
  }
}
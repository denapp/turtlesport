package fr.turtlesport;

import java.util.ArrayList;
import java.util.Hashtable;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.AppProtocolCommand;
import fr.turtlesport.protocol.StartSessionCommand;
import fr.turtlesport.protocol.data.ProductDataType;
import fr.turtlesport.protocol.data.ProtocolCapability;
import fr.turtlesport.protocol.data.ProtocolDataType;

/**
 * @author Denis Apparicio
 * 
 */
public final class GarminDevice {
  private static TurtleLogger                  log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(GarminDevice.class);
  }

  /** Liste des prototoles. */
  private Hashtable<String, ArrayList<String>> listProtocol;

  /** Le produit. */
  private ProductDataType                      pdtDataType;

  private static ThreadLocal<GarminDevice>     trans = new ThreadLocal<GarminDevice>();

  /**
   * Initilisation du garmin device.
   * 
   * @return le garmin device.
   * @throws UsbProtocolException
   *           si erreur.
   * @throws GarminDeviceNotInitialized
   *           si garmin non initialis&eacute;.
   */
  public static GarminDevice init() throws UsbProtocolException {
    GarminDevice device;
    if (trans.get() != null) {
      log.warn("GarminDevice deja initialise");
      device = trans.get();
    }
    else {
      // Construction du device
      device = new GarminDevice();
      trans.set(device);
      try {
        // Demarrage de la session
        StartSessionCommand.getInstance().retreive();

        // Recuperation des protocoles
        AppProtocolCommand app = new AppProtocolCommand();
        app.retreive();

        device.pdtDataType = app.getPdtDataType();
        device.initProtocol(app.getProtoCapability());
      }
      catch (UsbProtocolException e) {
        try {
          close();
        }
        catch (UsbProtocolException e1) {
          log.error("close", e1);
        }
        throw e;
      }

      trans.set(device);
    }

    return device;
  }

  /**
   * D&eacute;termine si le garmin device est initialis&eacute;.
   * 
   * @return <code>true</code> si si le garmin device est initialis&eacute;,
   *         <code>false</code> sinon.
   */
  public static boolean isInit() {
    return (trans.get() != null);
  }

  /**
   * Restitue le garmin device.
   * 
   * @return le garmin device.
   * @throws GarminDeviceNotInitialized
   *           si garmin non initialis&eacute;.
   */
  public static GarminDevice getDevice() {
    checkInit();
    return trans.get();
  }

  /**
   * Fermeture du device.
   * 
   * @throws UsbProtocolException
   * @throws GarminDeviceNotInitialized
   *           si garmin non initialis&eacute;.
   */
  public static void close() throws UsbProtocolException {
    checkInit();
    trans.set(null);
    UsbProtocol.getInstance().close();
  }

  /**
   * Envoie un packet au device USB.
   * 
   * @throws UsbProtocolException
   */
  public synchronized void send(UsbPacket packet) throws UsbProtocolException {
    checkInit();
    UsbProtocol.getInstance().send(packet);
  }

  /**
   * Recoie un packet du device USB.
   * 
   * @throws UsbProtocolException
   */
  public synchronized UsbPacket read() throws UsbProtocolException {
    checkInit();
    return UsbProtocol.getInstance().read();
  }

  /**
   * D&eacute;termine si ce ce protocole est pr&eacute;sent.
   * 
   * @param protocol
   *          le protocole.
   * @return <code>true</code> si pr&eacute; dans la liste.
   */
  public boolean containsProtocol(String protocol) {
    if (listProtocol == null) {
      return false;
    }
    return listProtocol.containsKey(protocol);
  }

  /**
   * Restitue la liste des data protocoles du protocole.
   * 
   * @param protocol
   *          le protocole.
   * @return la liste des data protocoles.
   */
  public String[] getDataProtocol(String protocol) {
    if (listProtocol == null || !containsProtocol(protocol)) {
      return new String[0];
    }

    ArrayList<String> listData = listProtocol.get(protocol);
    String[] res = new String[listData.size()];
    if (res.length > 0) {
      listData.toArray(res);
    }

    return res;
  }

  /**
   * L'ID du produit.
   * 
   * @return l'ID du produit
   */
  public int getProductID() {
    return pdtDataType.getProductID();
  }

  /**
   * La version du software.
   * 
   * @return la version du software.
   */
  public int getSoftwareVersion() {
    return pdtDataType.getSoftwareVersion();
  }

  /**
   * Restitue la description.
   * 
   * @return la a description.
   */
  public String[] getDescription() {
    return pdtDataType.getDescription();
  }

  /**
   * 
   */
  private GarminDevice() throws UsbProtocolException {
    super();

    log.debug(">>GarminDevice");

    // Initialisation du garmin
    UsbProtocol.getInstance().init();

    log.debug("<<GarminDevice");
  }

  private void initProtocol(ProtocolCapability pc) {
    listProtocol = new Hashtable<String, ArrayList<String>>();

    ArrayList<String> listData = new ArrayList<String>();
    String pdtName;
    for (ProtocolDataType pdt : pc.getListProtocolDataType()) {
      if (pdt.isTagA()) {
        pdtName = pdt.toString();
        listData = new ArrayList<String>();
        listProtocol.put(pdtName, listData);
      }
      else if (pdt.isTagD()) {
        listData.add(pdt.toString());
      }
    }
  }

  private static void checkInit() {
    if (!isInit()) {
      throw new GarminDeviceNotInitialized();
    }
  }

}

package fr.turtlesport.protocol.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import fr.turtlesport.GarminDevice;
import fr.turtlesport.log.TurtleLogger;

public abstract class AbstractRunType extends AbstractData {
  private static TurtleLogger         log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(AbstractRunType.class);
  }

  /** Protocole A1000. */
  private static final String         PROTOCOL                        = "A1000";

  private static final int            MULTISPORT_NO                   = 0;

  private static final int            MULTISPORT_YES                  = 1;

  private static final int            MULTISPORT_YES_AND_LAST_INGROUP = 2;

  /** Index pas de track associe. */
  private static final int            TRACK_INDEX_NOTRACK             = -1;

  /** Index de la track. */
  private int                         trackIndex;

  /** Index du premier tour. */
  private int                         firstLapIndex;

  /** Index du dernier tour. */
  private int                         lastLapIndex;

  /** Type de sport. */
  private int                         sportType;

  /** Type de programme. */
  private int                         programType;

  /** Multisport. */
  private int                         multisport;

  /** Liste des lap */
  private ArrayList<AbstractLapType>  listLapType;

  /** Liste des points */
  private ArrayList<AbstractTrkPointType> listTrkPointType;

  /** Date de debut */
  private Date                        computeStartTime;

  /** Calcul du temps ecoule (en dehors du protocol) */
  private int                         computeTime                     = 0;

  /** Calcul de la distance parcouru (en dehors du protocol) */
  private float                       computeDistance                 = 0;

  private Object                      extra;

  /**
   * @return
   */
  public static AbstractRunType newInstance() {
    log.debug(">>newInstance");

    AbstractRunType res;
    String[] data = GarminDevice.getDevice().getDataProtocol(PROTOCOL);
    if (data.length != 1) {
      throw new RuntimeException("pas de protocole " + PROTOCOL);
    }
    log.debug(PROTOCOL + "-->" + data[0]);

    if (D1009RunType.PROTOCOL.equals(data[0])) {
      res = new D1009RunType();
    }
    else if (D1010RunType.PROTOCOL.equals(data[0])) {
      res = new D1010RunType();
    }
    else {
      throw new RuntimeException("protocole non supporte" + data[0]);
    }

    log.debug("<<newInstance");
    return res;
  }

  /**
   * Restitue le nombre de points.
   * 
   * @return le nombre de points.
   */
  public int sizeTrkPointType() {
    return (listTrkPointType == null) ? 0 : listTrkPointType.size();
  }

  /**
   * Restiitue la liste des points.
   * 
   * @return la liste des points.
   */
  public List<AbstractTrkPointType> getListTrkPointType() {
    return listTrkPointType;
  }

  /**
   * Ajoute un point.
   * 
   * @param d304
   */
  public void addTrkPointType(AbstractTrkPointType pt) {
    if (pt == null) {
      return;
    }
    if (listTrkPointType == null) {
      listTrkPointType = new ArrayList<AbstractTrkPointType>();
    }
    listTrkPointType.add(pt);
  }

  /**
   * Ajoute des donn&eacute;es; compl&eacute;mentaires.
   * 
   * @return tles donn&eacute;es; compl&eacute;mentaire.
   */
  public Object getExtra() {
    return extra;
  }

  /**
   * Valorise les des donn&eacute;es; compl&eacute;mentaires.
   * 
   * @param extra
   *          la nouvelle valeur.
   */
  public void setExtra(Object extra) {
    this.extra = extra;
  }

  /**
   * @return
   */
  public float getComputeDistance() {
    compute();
    return computeDistance;
  }

  /**
   * @param computeDistance
   */
  public void setComputeDistance(float computeDistance) {
    this.computeDistance = computeDistance;
  }

  /**
   * @return
   */
  public int getComputeTime() {
    compute();
    return computeTime;
  }

  /**
   * @param computeTime
   */
  public void setComputeTime(int computeTime) {
    this.computeTime = computeTime;
  }

  /**
   * @return
   */
  public Date getComputeStartTime() {
    if (computeStartTime == null) {
      setComputeStartTime(listLapType.get(0).getStartTime());
    }
    return computeStartTime;
  }

  /**
   * @param computeStartTime
   */
  public void setComputeStartTime(Date computeStartTime) {
    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime(computeStartTime);
    cal.set(Calendar.MILLISECOND, 0);

    this.computeStartTime = cal.getTime();
  }

  /**
   * Calcul distance totale et temps total.
   */
  private void compute() {
    if (computeTime == 0) {
      computeTime = 0;
      for (AbstractLapType lap : listLapType) {
        computeDistance += lap.getTotalDist();
        computeTime += lap.getTotalTime();
      }

    }
  }

  /**
   * D&eacute;termine si ce run a des tracks.
   * 
   * @return <code>true</code> si track associ&eacute;
   */
  public boolean hasTrack() {
    return (trackIndex != TRACK_INDEX_NOTRACK);
  }

  /**
   * Restitue la liste des tours interm&eacute;diaires.
   * 
   * @return la liste des tours interm&eacute;diaires.
   */
  public ArrayList<AbstractLapType> getListLapType() {
    return listLapType;
  }

  /**
   * Restitue le nombre de tour interm&eacute;diaire.
   * 
   * @return le nombre de tour interm&eacute;diaire.
   */
  public int sizeLapType() {
    return (listLapType == null) ? 0 : listLapType.size();
  }

  /**
   * Ajoute un tour interm&eacute;diaire.
   * 
   * @param lapType
   *          le tour interm&eacute;diaire.
   */
  public void addLapType(AbstractLapType lapType) {
    if (lapType == null) {
      return;
    }
    if (listLapType == null) {
      listLapType = new ArrayList<AbstractLapType>();
    }
    listLapType.add(lapType);
  }

  /**
   * @return the sportType
   */
  public int getSportType() {
    return sportType;
  }

  /**
   * D&eacute;termine si le sport est de la course &agrve; pied.
   * 
   * @return <code>true</code> si course &agrve; pied.
   */
  public boolean isSportRunning() {
    return isSportRunning(sportType);
  }

  /**
   * D&eacute;termine si le sport est du v&eacute,lo.
   * 
   * @return <code>true</code> si le sport est du v&eacute,lo.
   */
  public boolean isSportBike() {
    return isSportBike(sportType);
  }

  /**
   * D&eacute;termine si le sport n'est pas v&eacute,lo ou de la la course
   * &agrve; pied.
   * 
   * @return <code>true</code> si n'est pas v&eacute,lo ou de la la course
   *         &agrve; pied.
   */
  public boolean isSportOther() {
    return isSportOther(sportType);
  }

  /**
   * @param sportType
   *          the sportType to set
   */
  public void setSportType(int sportType) {
    this.sportType = sportType;
  }

  /**
   * @return the trackIndex
   */
  public int getTrackIndex() {
    return trackIndex;
  }

  /**
   * @param trackIndex
   *          the trackIndex to set
   */
  public void setTrackIndex(int trackIndex) {
    this.trackIndex = trackIndex;
  }

  /**
   * @return the firstLapIndex
   */
  public int getFirstLapIndex() {
    return firstLapIndex;
  }

  /**
   * @param firstLapIndex
   *          the firstLapIndex to set
   */
  public void setFirstLapIndex(int firstLapIndex) {
    this.firstLapIndex = firstLapIndex;
  }

  /**
   * @return the lastLapIndex
   */
  public int getLastLapIndex() {
    return lastLapIndex;
  }

  /**
   * @param lastLapIndex
   *          the lastLapIndex to set
   */
  public void setLastLapIndex(int lastLapIndex) {
    this.lastLapIndex = lastLapIndex;
  }

  /**
   * @return the multisport
   */
  public int getMultisport() {
    return multisport;
  }

  /**
   * @return
   */
  public boolean isMultisportNo() {
    return (multisport == MULTISPORT_NO);
  }

  /**
   * @return
   */
  public boolean isMultisportYes() {
    return (multisport == MULTISPORT_YES);
  }

  /**
   * @return
   */
  public boolean isMultisportLastInGroup() {
    return (multisport == MULTISPORT_YES_AND_LAST_INGROUP);
  }

  /**
   * @param multisport
   *          the multisport to set
   */
  public void setMultisport(int multisport) {
    this.multisport = multisport;
  }

  /**
   * @return the programType
   */
  public int getProgramType() {
    return programType;
  }

  /**
   * @param programType
   *          the programType to set
   */
  public void setProgramType(int programType) {
    this.programType = programType;
  }

}

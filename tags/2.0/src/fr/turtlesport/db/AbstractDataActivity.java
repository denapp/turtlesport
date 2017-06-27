package fr.turtlesport.db;

import fr.turtlesport.ui.swing.img.activity.ImagesActivityRepository;
import fr.turtlesport.unit.SpeedPaceUnit;

/**
 * @author Denis Apparicio
 * 
 */
public abstract class AbstractDataActivity implements
                                          Comparable<AbstractDataActivity> {

  /** ** Nombre max. de zone cardiaque. */
  public static final int MAX_HEART_ZONE    = 5;

  /** ** Nombre max. de zone de vitesse. */
  public static final int MAX_SPEED_ZONE    = 10;

  /** Zones FC. */
  private DataHeartZone[] tabHeartZones;

  /** Zones Vitesses. */
  private DataSpeedZone[] tabSpeedZones;

  /** FCMax */
  private int             maxHeartRate;

  /** Activit&eacute; par d&eacute;faut */
  private boolean         isDefaultActivity = false;

  private String          name;

  private String          iconName;

  /**
   * Construit une activit&eacute.
   */
  public AbstractDataActivity() {
    tabHeartZones = new DataHeartZone[MAX_HEART_ZONE];
    for (int i = 0; i < MAX_HEART_ZONE; i++) {
      tabHeartZones[i] = new DataHeartZone();
    }

    tabSpeedZones = new DataSpeedZone[MAX_SPEED_ZONE];
    for (int i = 0; i < MAX_SPEED_ZONE; i++) {
      tabSpeedZones[i] = new DataSpeedZone("Vitesse" + (i + 1),
                                           0,
                                           0,
                                           SpeedPaceUnit.getDefaultUnit());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(AbstractDataActivity o) {
    return getName().toLowerCase().compareTo(o.getName().toLowerCase());
  }

  /**
   * D&eacute;termine si activit&eacute; par d&eacute;faut.
   * 
   * @return <code>true</code> si activit&eacute; par d&eacute;faut.
   */
  public boolean isDefaultActivity() {
    return isDefaultActivity;
  }

  /**
   * Valorise l'activit&eacute; par d&eacute,faut.
   * 
   * @param isDefaultActivity
   */
  public void setDefault(boolean isDefaultActivity) {
    this.isDefaultActivity = isDefaultActivity;
  }

  /**
   * Restitue le type d'activit&eacute;.
   * 
   * @return le type d'activit&eacute;
   */
  public abstract int getSportType();

  /**
   * Valorise le type d'activit&eacute;.
   * 
   * @param sportType
   *          la nouvelle valeur.
   */
  public abstract void setSportType(int sportType);

  /**
   * Restitue la fr&eacute;quence cardiaque maximale.
   * 
   * @return la fr&eacute;quence cardiaque maximale.
   */
  public int getMaxHeartRate() {
    return maxHeartRate;
  }

  /**
   * Valorise la fr&eacute;quence cardiaque maximale.
   * 
   * @param maxHeartRate
   *          la nouvelle valeur.
   */
  public void setMaxHeartRate(int maxHeartRate) {
    if (maxHeartRate < 60) {
      maxHeartRate = 60;
    }
    this.maxHeartRate = maxHeartRate;
  }

  /**
   * Restitue les zones cardiaques.
   * 
   * @return les zones cardiaques.
   */
  public DataHeartZone[] getHeartZones() {
    return tabHeartZones;
  }

  /**
   * Restitue les zones de vitesse.
   * 
   * @return les zones de vitesse.
   */
  public DataSpeedZone[] getSpeedZones() {
    return tabSpeedZones;
  }

  /**
   * Valorise une zone cardiaque.
   * 
   * @param index
   *          position.
   * @param heartZone
   *          une zone cardiaque.
   * @throws IndexOutOfBoundsException
   * @throws IllegalArgumentException
   */
  public void setHeartZone(DataHeartZone heartZone, int index) {
    if (index < 0 || index > tabHeartZones.length) {
      throw new IndexOutOfBoundsException("index=" + index);
    }
    if (heartZone == null) {
      throw new IllegalArgumentException("heartZone null");
    }
    tabHeartZones[index] = heartZone;
  }

  /**
   * Valorise une zone de vitesse.
   * 
   * @param index
   *          position.
   * @param speedZone
   *          la zone de vitesse.
   * @throws IndexOutOfBoundsException
   * @throws IllegalArgumentException
   */
  public void setSpeedZone(DataSpeedZone speedZone, int index) {
    if (index < 0 || index > tabSpeedZones.length) {
      throw new IndexOutOfBoundsException("index=" + index);
    }
    if (speedZone == null) {
      throw new IllegalArgumentException("speedZone null");
    }
    tabSpeedZones[index] = speedZone;
  }

  /**
   * Restitue le nom de l activit&eacute;.
   * 
   * @return le nom de l activit&eacute;
   */
  public String getName() {
    return name==null?"":name;
  }

  /**
   * Valorise le nom de l'activit&eacute;.
   * 
   * @param name
   *          l'activit&eacute;
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return Restitue le nom de l'icone
   */
  public String getIconName() {
    return iconName==null?ImagesActivityRepository.IMAGE_SPORT_OTHER:iconName;
  }

  /**
   * Valorise le nom de l'icone
   * 
   * @param iconName
   */
  public void setIconName(String iconName) {
    this.iconName = iconName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getName();
  }

}

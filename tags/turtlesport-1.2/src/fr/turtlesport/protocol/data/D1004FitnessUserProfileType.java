package fr.turtlesport.protocol.data;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.UsbPacketOutputStream;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.GarminProtocolException;

// typedef struct
// {
// struct
// {
// struct
// {
// uint8 low_heart_rate; /* In beats-per-minute, must be > 0 */
// uint8 high_heart_rate; /* In beats-per-minute, must be > 0 */
// uint16 unused; /* Unused. Set to 0. */
// } heart_rate_zones[5];
// struct
// {
// float32 low_speed; /* In meters-per-second */
// float32 high_speed; /* In meters-per-second */
// char name[16]; /* Null-terminated speed-zone name */
// } speed_zones[10];
// float32 gear_weight; /* Weight of equipment in kilograms */
// uint8 max_heart_rate; /* In beats-per-minute, must be > 0 */
// uint8 unused1; /* Unused. Set to 0. */
// uint16 unused2; /* Unused. Set to 0. */
// } activities[3];
// float32 weight; /* User's weight, in kilograms */
// uint16 birth_year; /* No base value (i.e. 1990 means 1990) */
// uint8 birth_month; /* 1 = January, etc. */
// uint8 birth_day; /* 1 = first day of month, etc. */
// uint8 gender; /* See below */
// spec incomplete ?
// uint8 unused1; /* Unused. Set to 0. */
// uint16 unused2;
// } D1004_Fitness_User_Profile_Type;

/**
 * @author Denis Apparicio
 * 
 */
public class D1004FitnessUserProfileType extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger
        .getLogger(D1004FitnessUserProfileType.class);
  }

  /** Zoones d'activites. */
  private D1004Activity[]     activities;

  /** Le Poids en kg de l'utilisateur. */
  private float               weight;

  /** Annee de naisance. (valeur par defaur 1990) */
  private int                 birthYear;

  /** Mois de naissance. */
  private int                 birthMonth;

  /** Jour de naissance. */
  private int                 birthDay;

  /** Masculin/Feminin. */
  private byte                gender;

  // Constante
  private static final byte   MALE   = 1;

  private static final byte   FEMALE = 0;

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>decode");

    // Zones activites
    activities = new D1004Activity[3];
    for (int i = 0; i < activities.length; i++) {
      activities[i] = new D1004Activity();
      activities[i].parse(input);
    }

    weight = input.readFloat();
    birthYear = input.readShort();
    birthMonth = input.read();
    birthDay = input.read();
    gender = input.readByte();

    input.readUnused(3);

    log.debug("<<decode");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#serialize(fr.turtlesport.UsbPacketOutputStream)
   */
  @Override
  public void serialize(UsbPacketOutputStream output) throws GarminProtocolException {
    log.debug(">>serialize");

    // Zones activites
    for (int i = 0; i < activities.length; i++) {
      activities[i].serialize(output);
    }

    output.writeFloat(weight);
    output.writeShort(birthYear);
    output.write(birthMonth);
    output.write(birthDay);
    output.write(gender);

    log.debug("<<serialize");
  }

  /**
   * Restitue le jour de naissance.
   * 
   * @return le jour de naissance.
   */
  public int getBirthDay() {
    return birthDay;
  }

  /**
   * Valorise le jour de naissance.
   * 
   * @param birthDay
   *          la nouvelle valeur.
   */
  public void setBirthDay(int birthDay) {
    this.birthDay = birthDay;
  }

  /**
   * Restitue le mois de naissance.
   * 
   * @return le mois de naissance.
   */
  public int getBirthMonth() {
    return birthMonth;
  }

  /**
   * Valorise le mois de naissance.
   * 
   * @param birthMonth
   *          la nouvelle valeur.
   */
  public void setBirthMonth(int birthMonth) {
    this.birthMonth = birthMonth;
  }

  /**
   * Restitue l'ann&eacute;e de naissance.
   * 
   * @return l'ann&eacute;e de naissance.
   */
  public int getBirthYear() {
    return birthYear;
  }

  /**
   * Valorise l'ann&eacute;e de naissance.
   * 
   * @param birthYear
   *          l'ann&eacute;e de naissance.
   */
  public void setBirthYear(int birthYear) {
    this.birthYear = birthYear;
  }

  /**
   * @return the gender
   */
  public int getGender() {
    return gender;
  }

  /**
   * 
   */
  public void setGenderMale() {
    this.gender = MALE;
  }

  /**
   * 
   */
  public void setGenderFemale() {
    this.gender = FEMALE;
  }

  /**
   * Restitue le poids de l'utilisateur.
   * 
   * @return le poids de l'utilisateur.
   */
  public float getWeight() {
    return weight;
  }

  /**
   * Valorise le poids de l'utilisateur.
   * 
   * @param weight
   *          le poids de l'utilisateur.
   */
  public void setWeight(float weight) {
    this.weight = weight;
  }

  /**
   * @return the activities
   */
  public D1004Activity[] getActivities() {
    return activities;
  }

  /**
   * Determine si l'utilisateur est un homme.
   * 
   * @return <code></code> si c'est un homme, <code>false</code> sinon.
   */
  public boolean isMale() {
    return (gender == MALE);
  }

  /**
   * Determine si l'utilisateur est une femme.
   * 
   * @return <code></code> si c'est une femme, <code>false</code> sinon.
   */
  public boolean isFemale() {
    return (gender == FEMALE);
  }

}

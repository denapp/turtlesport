package fr.turtlesport.protocol.data;

import java.util.Date;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.log.TurtleLogger;

// typedef struct
// {
// char name[11]; /* Null-terminated name */
// uint8 unused1; /* Unused. Set to 0. */
// uint16 course_index; /* Index of associated course */
// uint16 unused2; /* Unused. Set to 0. */
// time_type track_point_time; /* Time */
// uint8 point_type; /* See below */
// } D1012_Course_Point_Type;

/**
 * @author Denis Apparicio
 * 
 */
public class D1012CoursePointType extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D1012CoursePointType.class);
  }

  // Constante point type
  private static final byte   POINT_TYPE_GENERIC         = 0;

  private static final byte   POINT_TYPE_SUMMIT          = 1;

  private static final byte   POINT_TYPE_VALLEY          = 2;

  private static final byte   POINT_TYPE_WATER           = 3;

  private static final byte   POINT_TYPE_FOOD            = 4;

  private static final byte   POINT_TYPE_DANGER          = 5;

  private static final byte   POINT_TYPE_LEFT            = 6;

  private static final byte   POINT_TYPE_RIGHT           = 7;

  private static final byte   POINT_TYPE_STRAIGHT        = 8;

  private static final byte   POINT_TYPE_FIRST_AID       = 9;

  private static final byte   POINT_TYPE_FOURTH_CATEGORY = 10;

  private static final byte   POINT_TYPE_THIRD_CATEGORY  = 11;

  private static final byte   POINT_TYPE_SECOND_CATEGORY = 12;

  private static final byte   POINT_TYPE_FIRST_CATEGORY  = 13;

  private static final byte   POINT_TYPE_HORS_CATEGORY   = 14;

  private static final byte   POINT_TYPE_SPRINT          = 15;

  /** Nom */
  private String              name;

  /** Index de la course. */
  private int                 courseIndex;

  /** Temps. */
  private Date                trackPointTime;

  /** . */
  private byte                pointType                  = POINT_TYPE_GENERIC;

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>decode");

    name = input.readString(11);
    input.readUnused();
    courseIndex = input.readShort();
    input.readUnused(2);
    trackPointTime = input.readTime();
    pointType = input.readByte();

    log.debug("<<decode");
  }

  /**
   * @return the courseIndex
   */
  public int getCourseIndex() {
    return courseIndex;
  }

  /**
   * @param courseIndex
   *          the courseIndex to set
   */
  public void setCourseIndex(int courseIndex) {
    this.courseIndex = courseIndex;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the pointType
   */
  public byte getPointType() {
    return pointType;
  }

  /**
   * @param pointType
   *          the pointType to set
   */
  public void setPointType(byte pointType) {
    this.pointType = pointType;
  }

  /**
   * @return the trackPointTime
   */
  public Date getTrackPointTime() {
    return trackPointTime;
  }

  /**
   * @param trackPointTime
   *          the trackPointTime to set
   */
  public void setTrackPointTime(Date trackPointTime) {
    this.trackPointTime = trackPointTime;
  }

  /**
   * @return
   */
  public boolean isPointTypeGeneric() {
    return (pointType == POINT_TYPE_GENERIC);
  }

  /**
   * @return
   */
  public boolean isPointTypeSummit() {
    return (pointType == POINT_TYPE_SUMMIT);
  }

  /**
   * @return
   */
  public boolean isPointTypeValley() {
    return (pointType == POINT_TYPE_VALLEY);
  }

  /**
   * @return
   */
  public boolean isPointTypeWater() {
    return (pointType == POINT_TYPE_WATER);
  }

  /**
   * @return
   */
  public boolean isPointTypeFood() {
    return (pointType == POINT_TYPE_FOOD);
  }

  /**
   * @return
   */
  public boolean isPointTypeDanger() {
    return (pointType == POINT_TYPE_DANGER);
  }

  /**
   * @return
   */
  public boolean isPointTypeLeft() {
    return (pointType == POINT_TYPE_LEFT);
  }

  /**
   * @return
   */
  public boolean isPointTypeRight() {
    return (pointType == POINT_TYPE_RIGHT);
  }

  /**
   * @return
   */
  public boolean isPointTypeStraight() {
    return (pointType == POINT_TYPE_STRAIGHT);
  }

  /**
   * @return
   */
  public boolean isPointTypeFirstAid() {
    return (pointType == POINT_TYPE_FIRST_AID);
  }

  /**
   * @return
   */
  public boolean isPointTypeFourthCategory() {
    return (pointType == POINT_TYPE_FOURTH_CATEGORY);
  }

  /**
   * @return
   */
  public boolean isPointTypeThirdCategory() {
    return (pointType == POINT_TYPE_THIRD_CATEGORY);
  }

  /**
   * @return
   */
  public boolean isPointTypeSecondCategory() {
    return (pointType == POINT_TYPE_SECOND_CATEGORY);
  }

  /**
   * @return
   */
  public boolean isPointTypeFirstCategory() {
    return (pointType == POINT_TYPE_FIRST_CATEGORY);
  }

  /**
   * @return
   */
  public boolean isPointTypeHorsCategory() {
    return (pointType == POINT_TYPE_HORS_CATEGORY);
  }

  /**
   * @return
   */
  public boolean isPointTypeSprint() {
    return (pointType == POINT_TYPE_SPRINT);
  }

}

package fr.turtlesport.protocol.data;

import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.log.TurtleLogger;

// typedef struct
// {
// uint32 max_courses; /* Maximum courses */
// uint32 max_course_laps; /* Maximum course laps */
// uint32 max_course_pnt; /* Maximum course points */
// uint32 max_course_trk_pnt; /* Maximum course track points */
// } D1013_Course_Limits_Type;

/**
 * @author Denis Apparicio
 * 
 */
public class D1013CourseLimitsType extends AbstractData {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D1013CourseLimitsType.class);
  }

  /** Nombre max. de course. */
  private int                 maxCourses;

  /** Nombre max de lap. */
  private int                 maxCourseLaps;

  /** Nombre max. de points. */
  private int                 maxCoursePnt;

  /** Nombre max de track point. */
  private int                 maxCourseTrkPnt;

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>decode");

    maxCourses = input.readInt();
    maxCourseLaps = input.readInt();
    maxCoursePnt = input.readInt();
    maxCourseTrkPnt = input.readInt();

    log.debug("<<decode");
  }

  /**
   * @return the maxCourseLaps
   */
  public int getMaxCourseLaps() {
    return maxCourseLaps;
  }

  /**
   * @param maxCourseLaps
   *          the maxCourseLaps to set
   */
  public void setMaxCourseLaps(int maxCourseLaps) {
    this.maxCourseLaps = maxCourseLaps;
  }

  /**
   * @return the maxCoursePnt
   */
  public int getMaxCoursePnt() {
    return maxCoursePnt;
  }

  /**
   * @param maxCoursePnt
   *          the maxCoursePnt to set
   */
  public void setMaxCoursePnt(int maxCoursePnt) {
    this.maxCoursePnt = maxCoursePnt;
  }

  /**
   * @return the maxCourses
   */
  public int getMaxCourses() {
    return maxCourses;
  }

  /**
   * @param maxCourses
   *          the maxCourses to set
   */
  public void setMaxCourses(int maxCourses) {
    this.maxCourses = maxCourses;
  }

  /**
   * @return the maxCourseTrkPnt
   */
  public int getMaxCourseTrkPnt() {
    return maxCourseTrkPnt;
  }

  /**
   * @param maxCourseTrkPnt
   *          the maxCourseTrkPnt to set
   */
  public void setMaxCourseTrkPnt(int maxCourseTrkPnt) {
    this.maxCourseTrkPnt = maxCourseTrkPnt;
  }

}

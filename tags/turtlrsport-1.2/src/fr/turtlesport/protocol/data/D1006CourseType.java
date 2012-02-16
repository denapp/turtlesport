package fr.turtlesport.protocol.data;

import java.util.ArrayList;

import fr.turtlesport.UsbPacket;
import fr.turtlesport.UsbPacketInputStream;
import fr.turtlesport.UsbPacketOutputStream;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.protocol.GarminProtocolException;

// typedef struct
// {
// uint16 index; /* Unique among courses on device */
// uint16 unused; /* Unused. Set to 0. */
// char course_name[16]; /* Null-terminated, unique course name */
// uint16 track_index; /* Index of the associated track */
// } D1006_Course_Type;

/**
 * @author Denis Apparicio
 * 
 */
public class D1006CourseType extends AbstractData {
  private static TurtleLogger             log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(D1006CourseType.class);
  }

  /** Index de la course. */
  private int                             index;

  /** Nom de la course. */
  private String                          courseName;

  /** Index of the associated track */
  private int                             trackIndex;

  /** Liste des laps */
  private ArrayList<D1007CourseLapType>   listCourseLapType;

  /** Liste des trk point. */
  private ArrayList<D304TrkPointType>     listTrkPointType;

  /** Liste des points. */
  private ArrayList<D1012CoursePointType> listPointType;

  /**
   * 
   */
  public D1006CourseType() {
    super();
  }

  /**
   * Restitue la distance totale.
   * 
   * @return la distance totale.
   */
  public double getTotalDist() {
    double totalDist = 0;
    if (listCourseLapType != null) {
      for (D1007CourseLapType d : listCourseLapType) {
        totalDist += d.getTotalDist();
      }
    }
    return totalDist;
  }

  /**
   * Restitue le temps total.
   * 
   * @return le temps total.
   */
  public int getTotalTime() {
    int totalTime = 0;
    if (listCourseLapType != null) {
      for (D1007CourseLapType d : listCourseLapType) {
        totalTime += d.getTotalTime();
      }
    }
    return totalTime;
  }

  /**
   * Restitue le nombre de tour interm&eacte;diaire.
   * 
   * @return le nombre de tour interm&eacte;diaire.
   */
  public int getListCourseLapTypeSize() {
    return (listCourseLapType == null) ? 0 : listCourseLapType.size();
  }

  /**
   * Restitue le nombre de track points.
   * 
   * @return le nombre de track points.
   */
  public int getListTrkPointTypeSize() {
    return (listTrkPointType == null) ? 0 : listTrkPointType.size();
  }

  /**
   * Restitue le nombre de points.
   * 
   * @return le nombre de points.
   */
  public int getListPointTypeSize() {
    return (listPointType == null) ? 0 : listPointType.size();
  }

  /**
   * Restitue la liste des trk points.
   * 
   * @return la liste des trk points.
   */
  public ArrayList<D304TrkPointType> getListTrkPointType() {
    return listTrkPointType;
  }

  /**
   * Restitue le trk point &agrave; l'index sp&eacute;cifi&eacute;.
   * 
   * @return le trk point &agrave; l'index sp&eacute;cifi&eacute;.
   */
  public D304TrkPointType getListTrkPointType(int index) {
    if (listTrkPointType == null) {
      throw new IndexOutOfBoundsException("Index: 0, Size: 0");
    }
    return listTrkPointType.get(index);
  }

  /**
   * Restitue la liste des points.
   * 
   * @return la liste des points.
   */
  public ArrayList<D1012CoursePointType> getListPointType() {
    return listPointType;
  }

  /**
   * Ajoute un track point.
   * 
   * @param point
   *          le track point.
   */
  public void addTrkPointType(D304TrkPointType point) {
    if (point == null) {
      throw new IllegalArgumentException();
    }
    if (listTrkPointType == null) {
      listTrkPointType = new ArrayList<D304TrkPointType>();
    }
    listTrkPointType.add(point);
  }

  /**
   * Ajoute un point.
   * 
   * @param point
   *          le point.
   */
  public void addPointType(D1012CoursePointType point) {
    if (point == null) {
      throw new IllegalArgumentException();
    }
    if (point.getCourseIndex() != getIndex()) {
      throw new IllegalArgumentException("index=" + index + " CourseIndex="
                                         + point.getCourseIndex());
    }
    if (listPointType == null) {
      listPointType = new ArrayList<D1012CoursePointType>();
    }
    listPointType.add(point);
  }

  /**
   * Ajoute un tour interm&eacute;diaire.
   * 
   * @param lapType
   *          le tour interm&eacute;diaire.
   */
  public void addCourseLapType(D1007CourseLapType lapType) {
    if (lapType == null) {
      return;
    }
    if (lapType.getCourseIndex() != getIndex()) {
      throw new IllegalArgumentException("index=" + index + " CourseIndex="
                                         + lapType.getCourseIndex());
    }
    if (listCourseLapType == null) {
      listCourseLapType = new ArrayList<D1007CourseLapType>();
    }
    listCourseLapType.add(lapType);
  }

  /**
   * @return
   */
  public D1007CourseLapType getListCourseLapType(int index) {
    if (listCourseLapType == null) {
      throw new IndexOutOfBoundsException("Index: 0, Size: 0");
    }
    return listCourseLapType.get(index);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#parse(fr.turtlesport.UsbPacketInputStream)
   */
  @Override
  public void parse(UsbPacketInputStream input) {
    log.debug(">>parse");

    index = input.readShort();

    // unused
    input.readUnused(2);

    courseName = input.readString(16);
    trackIndex = input.readShort();

    log.debug("<<parse");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.turtlesport.protocol.data.AbstractData#serialize(fr.turtlesport.UsbPacketOutputStream)
   */
  @Override
  public void serialize(UsbPacketOutputStream output) throws GarminProtocolException {
    log.debug(">>serialize");

    output.writeShort(index);

    // unused
    output.writeUnused(2);

    output.write(courseName, 16);
    output.writeShort(trackIndex);

    log.debug("<<serialize");
  }

  /**
   * Decodage des donn&eacute;es.
   * 
   * @param input
   *          le packet.
   */
  @Override
  public void parse(UsbPacket input) {
    parse(new UsbPacketInputStream(input));
  }

  /**
   * Restitue le nom de la course.
   * 
   * @return le nom de la course.
   */
  public String getCourseName() {
    return courseName;
  }

  /**
   * Valorise le nom de la course.
   * 
   * @param courseName
   *          la nouvelle valeur.
   */
  public void setCourseName(String courseName) {
    this.courseName = courseName;
  }

  /**
   * Restitue l'index de la source.
   * 
   * @return l'index de la source.
   */
  public int getIndex() {
    return index;
  }

  /**
   * Valorise l'index de la course.
   * 
   * @param index
   *          l'index de la course.
   */
  public void setIndex(int index) {
    this.index = index;
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

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return courseName;
  }

  /**
   * Restitue une description complete.
   * 
   * @return
   */
  public String fullDescription() {
    return "Course name=" + courseName + " index=" + index + " trackIndex="
           + trackIndex + " nbLap=" + getListCourseLapTypeSize()
           + " nbTrkPoint=" + getListTrkPointTypeSize() + " nbPoint="
           + getListPointTypeSize();
  }

}

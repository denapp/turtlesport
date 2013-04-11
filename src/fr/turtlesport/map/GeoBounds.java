package fr.turtlesport.map;

import java.awt.geom.Rectangle2D;
import java.util.List;

import fr.turtlesport.geo.GeoPosition;
import fr.turtlesport.geo.IGeoPosition;
import fr.turtlesport.log.TurtleLogger;

/**
 * 
 * @author Denis Apparicio
 */
public class GeoBounds {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(GeoBounds.class);
  }

  /** Internal representation of the bounds */
  private Rectangle2D[]       rects;

  /**
   * Constructor.
   * 
   * @param minLat
   *          The minimum latitude.
   * @param minLng
   *          The minimum longitude.
   * @param maxLat
   *          The maximum latitude.
   * @param maxLng
   *          The maximum longitude.
   */
  public GeoBounds(double minLat, double minLng, double maxLat, double maxLng) {
    setRect(minLat, minLng, maxLat, maxLng);
  }

  /**
   * Constructor.
   * 
   * @param geoPositions
   *          A non null list of 2 or more different <code>GeoBounds</code>
   *          objects.
   */
  public GeoBounds(List<IGeoPosition> list) {
    if (list == null || list.size() < 2) {
      throw new IllegalArgumentException("The attribute 'geoPositions' cannot be null and must "
                                         + "have 2 or more elements.");
    }
    double minLat = Integer.MAX_VALUE;
    double minLng = Integer.MAX_VALUE;
    double maxLat = Integer.MIN_VALUE;
    double maxLng = Integer.MIN_VALUE;
    for (IGeoPosition position : list) {
      if (!position.isInvalidPosition()) {
        log.error(position.toString());
        minLat = Math.min(minLat, position.getLatitude());
        minLng = Math.min(minLng, position.getLongitude());
        maxLat = Math.max(maxLat, position.getLatitude());
        maxLng = Math.max(maxLng, position.getLongitude());
      }
    }    
    setRect(minLat, minLng, maxLat, maxLng);
  }

  /**
   * Sets the internal rectangle representation.
   * 
   * @param minLat
   *          The minimum latitude.
   * @param minLng
   *          The minimum longitude.
   * @param maxLat
   *          The maximum latitude.
   * @param maxLng
   *          The maximum longitude.
   */
  private void setRect(double minLat,
                       double minLng,
                       double maxLat,
                       double maxLng) {
    log.error("minLat=" + minLat);
    log.error("minLng=" + minLng);
    log.error("maxLat=" + maxLat);
    log.error("maxLng=" + maxLng);
    if (!(minLat < maxLat)) {
      throw new IllegalArgumentException("GeoBounds is not valid - minLat must be less that maxLat.");
    }
    if (!(minLng < maxLng)) {
      if (minLng > 0 && minLng < 180 && maxLng < 0) {
        rects = new Rectangle2D[] {
        // split into two rects e.g. 176.8793 to 180 and -180 to
            // -175.0104
            new Rectangle2D.Double(minLng, minLat, 180 - minLng, maxLat
                                                                 - minLat),
            new Rectangle2D.Double(-180, minLat, maxLng + 180, maxLat - minLat) };
      }
      else {
        rects = new Rectangle2D[] { new Rectangle2D.Double(minLng,
                                                           minLat,
                                                           maxLng - minLng,
                                                           maxLat - minLat) };

        throw new IllegalArgumentException("GeoBounds is not valid - minLng must be less that maxLng or "
                                           + "minLng must be greater than 0 and maxLng must be less than 0.");
      }
    }
    else {
      rects = new Rectangle2D[] { new Rectangle2D.Double(minLng,
                                                         minLat,
                                                         maxLng - minLng,
                                                         maxLat - minLat) };
    }
    log.error(rects);
  }

  /**
   * Determines if this bounds intersects the other bounds.
   * 
   * @param other
   *          The other bounds to test for intersection with.
   * @return Returns true if bounds intersect.
   */
  public boolean intersects(GeoBounds other) {
    boolean rv = false;
    for (Rectangle2D r1 : rects) {
      for (Rectangle2D r2 : other.rects) {
        rv = r1.intersects(r2);
        if (rv) {
          break;
        }
      }
      if (rv) {
        break;
      }
    }
    return rv;
  }

  /**
   * Gets the north west position.
   * 
   * @return Returns the north west position.
   */
  public GeoPosition getNorthWest() {
    return new GeoPosition(rects[0].getY(), rects[0].getMaxX());
  }

  /**
   * Gets the south east position.
   * 
   * @return Returns the south east position.
   */
  public GeoPosition getSouthEast() {
    Rectangle2D r = rects[0];
    if (rects.length > 1) {
      r = rects[1];
    }
    return new GeoPosition(r.getMaxY(), r.getX());
  }
  
  /**
   * Gets the south east position.
   * 
   * @return Returns the south east position.
   */
  public GeoPosition getCenter() {
    Rectangle2D r = rects[0];
    if (rects.length > 1) {
      r = rects[1];
    }
    return new GeoPosition(r.getMaxY(), r.getX());
  }

}
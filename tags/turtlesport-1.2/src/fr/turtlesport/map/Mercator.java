package fr.turtlesport.map;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import fr.turtlesport.geo.IGeoPosition;
import fr.turtlesport.util.Wgs84;

/**
 * Classe utilitaire de Mercator projection.
 * 
 * @author Denis Apparicio
 * 
 */
public class Mercator {

  private List<Point2D.Double> listPoints;

  private Rectangle2D.Double   bounds;

  private Point2D.Double       northWest;

  private Point2D.Double       southEast;

  private double               dist;

  public Mercator() {
  }

  /**
   * Restitue la liste des points.
   * 
   * @param listGeo
   * @param radius
   * @return
   */
  public List<Point2D.Double> project(List<IGeoPosition> listGeo, double radius) {
    if (listGeo == null) {
      return null;
    }

    double xMin = Integer.MAX_VALUE;
    double yMin = Integer.MAX_VALUE;
    double xMax = Integer.MIN_VALUE;
    double yMax = Integer.MIN_VALUE;

    listPoints = new ArrayList<Point2D.Double>();
    for (IGeoPosition geo : listGeo) {
      Point2D.Double p = project(geo, radius);
      listPoints.add(p);

      xMin = Math.min(xMin, p.x);
      xMax = Math.max(xMax, p.x);
      yMin = Math.min(yMin, p.y);
      yMax = Math.max(yMax, p.y);
    }

    bounds = new Rectangle2D.Double(xMin, yMin, xMax - xMin, yMax - yMin);
    northWest = new Point2D.Double(bounds.getMinX(), bounds.getMinY());
    southEast = new Point2D.Double(bounds.getMaxX(), bounds.getMaxY());

    double lonNorthWest = xToLong(bounds.getMinX(), radius);
    double latNorthWest = yToLat(bounds.getMinY(), radius);
    double lonSouthWest = xToLong(bounds.getMaxX(), radius);
    double latSouthWest = yToLat(bounds.getMaxY(), radius);

    dist = Wgs84.computeWsg84(latNorthWest,
                              lonNorthWest,
                              latSouthWest,
                              lonSouthWest);

    return listPoints;
  }

  /**
   * Restitue le nombre de points.
   * 
   * @return le nombre de points.
   */
  public int size() {
    checkState();
    return listPoints.size();
  }

  /**
   * Restitue le pount de projection.
   * 
   * @param index
   * @return
   */
  public Point2D.Double getPoint(int index) {
    checkState();
    return listPoints.get(index);
  }

  /**
   * Restitue les bounds.
   * 
   * @return
   */
  public Rectangle2D.Double getBounds() {
    checkState();
    return bounds;
  }

  /**
   * Restitue le point de coordonn&eacute;e nord ouest.
   * 
   * @return le point de coordonn&eacute;e nord ouest.
   */
  public Point2D.Double getNorthWest() {
    checkState();
    return northWest;
  }

  /**
   * Restitue le point de coordonn&eacute;e sud est.
   * 
   * @return le point de coordonn&eacute;e sud est.
   */
  public Point2D.Double getSouthEast() {
    checkState();
    return southEast;
  }

  /**
   * Restitue la distance entre le nord west et le sud est.
   * 
   * @return la distance entre le nord west et le sud est.
   */
  public double dist() {
    checkState();
    return dist;
  }

  /**
   * @param geo
   * @param radius
   * @return
   */
  public static Point2D.Double project(IGeoPosition geo, double radius) {
    return new Point2D.Double(longToX(geo.getLongitude(), radius), latToY(geo
        .getLatitude(), radius));
  }

  /**
   * @param longitudeDegrees
   * @param latitudeDegrees
   * @param radius
   * @return
   */
  public static Point2D.Double project(double longitudeDegrees,
                                       double latitudeDegrees,
                                       double radius) {
    return new Point2D.Double(longToX(longitudeDegrees, radius),
                              latToY(latitudeDegrees, radius));
  }

  /**
   * Restitue la projection coordonn&eacute;e x.
   * 
   * @param longitudeDegrees
   * @param radius
   * @return
   */
  public static double longToX(double longitudeDegrees, double radius) {
    return (radius * Math.toRadians(longitudeDegrees));
  }

  /**
   * Restitue la projection coordonn&eacute;e y.
   * 
   * @param longitudeDegrees
   * @param radius
   * @return
   */
  public static double latToY(double latitudeDegrees, double radius) {
    double latitude = Math.toRadians(latitudeDegrees);
    double y = radius
               / 2.0
               * Math.log((1.0 + Math.sin(latitude))
                          / (1.0 - Math.sin(latitude)));
    return y;
  }

  /**
   * Restitue la longitude.
   * 
   * @param x
   *          coordonn&eacute;e de projection x.
   * @param radius
   * @return
   */
  public static double xToLong(double x, double radius) {
    double longRadians = x / radius;
    double longDegrees = Math.toDegrees(longRadians);
    /*
     * The user could have panned around the world a lot of times. Lat long goes
     * from -180 to 180. So every time a user gets to 181 we want to subtract
     * 360 degrees. Every time a user gets to -181 we want to add 360 degrees.
     */
    int rotations = (int) Math.floor((longDegrees + 180) / 360);
    double longitude = longDegrees - (rotations * 360);
    return longitude;
  }

  /**
   * Restitue la latitude.
   * 
   * @param x
   *          coordonn&eacute;e de projection x.
   * @param radius
   * @return
   */
  public static double yToLat(double y, double radius) {
    double latitude = (Math.PI / 2)
                      - (2 * Math.atan(Math.exp(-1.0 * y / radius)));
    return Math.toDegrees(latitude);
  }

  private void checkState() {
    if (listPoints == null) {
      throw new IllegalStateException();
    }
  }
}

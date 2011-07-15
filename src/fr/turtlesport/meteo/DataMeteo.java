package fr.turtlesport.meteo;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import fr.turtlesport.lang.LanguageEn;
import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * Donn&eacute;es meteo historique de Wunderground
 * 
 * @author Denis Apparicio
 * 
 */
public class DataMeteo {
  private static TurtleLogger              log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(DataMeteo.class);
  }

  // TimeCEST,TemperatureC,Dew PointC,Humidity,Sea Level
  // PressurehPa,VisibilityKm,Wind Direction,Wind SpeedKm/h,Gust
  // SpeedKm/h,Precipitationmm,Events,Conditions,WindDirDegrees,DateUTC
  // 12:00 AM,
  // 19.0,
  // 4.0,
  // 37,1015,
  // -9999.0,
  // SE,4.6,-,N/A,,Clear,130,2011-05-25 22:00:00
  // 12:30 AM,19.0,4.0,37,1015,-9999.0,SE,6.9,-,N/A,,Clear,140,2011-05-25
  // 22:30:00
  // 1:00 AM,18.0,5.0,42,1014,-9999.0,SSE,6.9,-,N/A,,Clear,160,2011-05-25
  // 23:00:00
  // 1:30 AM,17.0,5.0,45,1014,-9999.0,SSE,5.8,-,N/A,,Clear,150,2011-05-25
  // 23:30:00
  // 2:00 AM,16.0,5.0,48,1013,-9999.0,SE,6.9,-,N/A,,Clear,140,2011-05-26
  // 00:00:00
  // 2:30 AM,16.0,6.0,52,1013,-9999.0,SE,6.9,-,N/A,,Clear,140,2011-05-26
  // 00:30:00
  // 3:00 AM,16.0,5.0,48,1012,-9999.0,SSE,6.9,-,N/A,,Clear,150,2011-05-26
  // 01:00:00
  // 3:30 AM,16.0,6.0,52,1012,-9999.0,South,5.8,-,N/A,,Clear,170,2011-05-26
  // 01:30:00
  // 4:00 AM,16.0,5.0,48,1012,-9999.0,South,8.1,-,N/A,,Clear,170,2011-05-26
  // 02:00:00
  // 4:30 AM,16.0,5.0,48,1011,-9999.0,South,9.2,-,N/A,,Clear,170,2011-05-26
  // 02:30:00
  // 5:00 AM,15.0,4.0,48,1011,-9999.0,South,10.4,-,N/A,,Clear,190,2011-05-26
  // 03:00:00
  // 5:30 AM,16.0,4.0,45,1011,-9999.0,WSW,11.5,-,N/A,,Clear,250,2011-05-26
  // 03:30:00
  // West,11.5,-,N/A,,Clear,270,2011-05-26 04:00:00

  // Icones
  // ---------------
  // Clear = clear
  // Cloudy = cloudy
  // Flurries = flurries
  // Fog = fog
  // Hazy = hazy
  // Mostly Cloudy = mostlycloudy
  // Mostly Sunny = mostlysunny
  // Partly Cloudy = partlycloudy
  // Partly Sunny = partlysunny
  // Rain = rain
  // Light Drizzle=rain
  // Sleet = sleet
  // Snow = snow
  // Sunny = sunny
  // Thunderstorms = tstorms
  // Thunderstorms = tstorms
  // Unknown = unknown

  private static Hashtable<String, String> mapIcon;
  static {
    mapIcon = new Hashtable<String, String>();
    mapIcon.put("Thunderstorms", "32px-storm.png");
    mapIcon.put("Sunny", "32px-sunny.png");
    mapIcon.put("Clear", "32px-mostlysunny.png");
    mapIcon.put("Overcast", "32px-mostlysunny.png");
    mapIcon.put("Cloudy", "32px-cloudy.png");
    mapIcon.put("Mostly Cloudy", "32px-mostlycloudy.png");
    mapIcon.put("Scattered Clouds", "32px-mostlycloudy.png");
    mapIcon.put("Partly Cloudy", "32px-mostlycloudy.png");
    mapIcon.put("Partly Sunny", "32px-mostlysunny.png");
    mapIcon.put("Snow", "32px-snow.png");
    mapIcon.put("Light Snow", "32px-snow.png");
    mapIcon.put("Flurries", "32px-snow.png");
    mapIcon.put("Sleet", "32px-snow.png");
    mapIcon.put("Rain", "32px-rain.png");
    mapIcon.put("Light Rain", "32px-rain.png");
    mapIcon.put("Light Rain Showers", "32px-rain.png");
    mapIcon.put("Light Drizzle", "32px-rain.png");
    mapIcon.put("Fog", "32px-fog.png");
    mapIcon.put("Mist", "32px-fog.png");
    mapIcon.put("Unknown", "32px-unknown.png");
  }

  private static final String[]            IMAGES_ICON_NAMES = { "32px-sunny.png",
      "32px-mostlysunny.png",
      "32px-mostlycloudy.png",
      "32px-cloudy.png",
      "32px-rain.png",
      "32px-heavyrain.png",
      "32px-snow.png",
      "32px-fog.png",
      "32px-storm.png",
      "32px-unknown.png"                                    };

  private static List<ImageIcon>           listImageICon     = new ArrayList<ImageIcon>();
  static {
    for (String s : IMAGES_ICON_NAMES) {
      listImageICon.add(new ImageIcon(DataMeteo.class.getResource(s)));
    }
  }

  private static final String              DATE_FORMAT       = "yyyy-MM-dd HH:mm:ss";

  private int                              temperature       = Integer.MAX_VALUE;

  private int                              humidity          = -1;

  private int                              pressurehPa       = -1;

  private String                           windDirection;

  private float                            windSpeedkmh      = -1;

  private int                              windDirectionDegree;

  private String                           condition;

  private int                              conditionIndex    = -1;

  private Date                             date;

  private float                            visibility        = -1;

  private static List<String>              listWinDir;
  static {
    listWinDir = new ArrayList<String>();
    listWinDir.add("Calm");
    listWinDir.add("Variable");
    listWinDir.add("West");
    listWinDir.add("East");
    listWinDir.add("South");
    listWinDir.add("North");
  }

  public DataMeteo(Date date) {
    if (date == null) {
      throw new IllegalArgumentException();
    }
    this.date = date;
  }

  /**
   * Valorise les champs..
   * 
   * @param datas
   * @return
   */
  protected static DataMeteo compute(String[] datas) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
      Date date = sdf.parse(datas[datas.length - 1]);

      DataMeteo d = new DataMeteo(date);
      d.temperature = (int) Float.parseFloat(datas[1]);
      d.humidity = (int) Float.parseFloat(datas[3]);
      d.pressurehPa = Integer.parseInt(datas[4]);
      d.visibility = Float.parseFloat(datas[5]);

      d.windDirection = datas[6];

      if ("Calm".equals(datas[7])) {
        d.windSpeedkmh = 0;
      }
      else {
        d.windSpeedkmh = Float.parseFloat(datas[7]);
      }
      d.windDirectionDegree = Integer.parseInt(datas[datas.length - 2]);

      d.condition = datas[datas.length - 3];
      return d;
    }
    catch (Throwable e) {
      log.error("", e);
      return null;
    }
  }

  /**
   * Restitue la liste des icones
   * 
   * @return la liste des icones
   */
  public static List<ImageIcon> getIcons() {
    return listImageICon;
  }

  public boolean isTemperatureValid() {
    return temperature > -60 && temperature < 60;
  }

  public int getTemperature() {
    return temperature;
  }

  public int getHumidity() {
    return humidity;
  }

  public int getPressurehPa() {
    return pressurehPa;
  }

  public String getWindDirection() {
    if (!listWinDir.contains(windDirection)
        || LanguageManager.getManager().getCurrentLang() instanceof LanguageEn) {
      return windDirection;
    }

    ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
        .getManager().getCurrentLang(), getClass());
    try {
      return rb.getString(windDirection);
    }
    catch (Throwable e) {
      return windDirection;
    }
  }

  public float getWindSpeedkmh() {
    return windSpeedkmh;
  }

  public int getWindDirectionDegree() {
    return windDirectionDegree;
  }

  public float getVisibility() {
    return visibility;
  }

  public boolean isVisibilityValid() {
    return visibility >= 0;
  }

  public boolean isWindSpeedValid() {
    return windSpeedkmh >= 0;
  }

  public Date getDate() {
    return date;
  }

  public void setTemperature(int temperature) {
    this.temperature = temperature;
  }

  public void setHumidity(int humidity) {
    this.humidity = humidity;
  }

  public void setPressurehPa(int pressurehPa) {
    this.pressurehPa = pressurehPa;
  }

  public void setWindDirection(String windDirection) {
    this.windDirection = windDirection;
  }

  public void setWindSpeedkmh(float windSpeedkmh) {
    this.windSpeedkmh = windSpeedkmh;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public void setVisibility(float visibility) {
    this.visibility = visibility;
  }

  /**
   * @return Restitue l'index de l'image correspond aux conditions m&eacute;teo.
   */
  public int getImageIconIndex() {
    if (conditionIndex == -1) {
      String name = "32px-unknown.png";
      if (condition != null) {
        name = mapIcon.get(condition);
        if (name == null) {
          name = "32px-unknown.png";
        }
      }

      for (int i = 0; i < IMAGES_ICON_NAMES.length; i++) {
        if (IMAGES_ICON_NAMES[i].equals(name)) {
          conditionIndex = i;
          return conditionIndex;
        }
      }
      conditionIndex = IMAGES_ICON_NAMES.length - 1;
    }
    return conditionIndex;
  }

  public void setImageIconIndex(int index) {
    conditionIndex = index;
  }

  /**
   * Restitue une image.
   * 
   * @param name
   *          nom de l'image.
   * @return
   */
  public BufferedImage getImage() {
    try {
      if (condition != null) {
        String name = mapIcon.get(condition);
        if (name != null) {
          return ImageIO.read(getClass().getResource(name));
        }
      }
    }
    catch (IOException e) {
      log.error("", e);
    }
    return null;
  }

}

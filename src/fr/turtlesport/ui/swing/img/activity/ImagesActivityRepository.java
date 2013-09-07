package fr.turtlesport.ui.swing.img.activity;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import fr.turtlesport.log.TurtleLogger;

/**
 * @author denis
 * 
 */
public final class ImagesActivityRepository {
  private static TurtleLogger           log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(ImagesActivityRepository.class);
  }

  public static final String            IMAGE_SPORT_RUN   = "run_jogging.png";

  public static final String            IMAGE_SPORT_BIKE  = "cycling.png";

  public static final String            IMAGE_SPORT_OTHER = "blank.png";

  private static final String[]         NAMES             = { IMAGE_SPORT_OTHER,
      IMAGE_SPORT_BIKE,
      "cycling_downhill.png",
      "cycling_sprint.png",
      "cycling_rising.png",
      "cycling_mountainbiking.png",
      "run_fast.png",
      IMAGE_SPORT_RUN,
      "run_hiking.png",
      "run_fitness.png",
      "swimming.png",
      "car_karting.png",
      "car_sportscar.png",
      "car_atv.png",
      "car_fourbyfour.png",
      "car_motorbike.png",
      "rollerblade.png",
      "rollerskate.png",
      "iceskating.png",
      "skiing.png",
      "nordicski.png",
      "snowboarding.png",
      "snowshoeing.png",
      "snowmobiling.png",
      "boardercross.png",
      "skijump.png",
      "bobsleigh.png",
      "horseriding.png",
      "golfing.png",
      "handball.png",
      "usfootball.png",
      "australianfootball.png",
      "rugbyfield.png",
      "soccer.png",
      "tennis.png",
      "sailing.png",
      "paragliding.png",
      "watercraft.png",
      "hanggliding.png",
      "kayaking.png",
      "discgolf.png",
      "kitesurfing.png",
      "sledge.png",
      "waterskiing.png",
      "windsurfing.png",
      "ropescourse.png",
      "sledge_summer.png",
      "surfpaddle.png",
      "rowboat.png",
      "climbing.png"                                     };

  private static Map<String, ImageIcon> mapSmallIcons     = new HashMap<String, ImageIcon>();
  static {
    for (String iconName : NAMES) {
      String name = iconName.substring(0, iconName.lastIndexOf('.')) + "16.png";
      mapSmallIcons.put(iconName, getImageIcon(name));
    }
  }

  public static final ImageIcon         ICON_SMALL_RUN    = getImageIconSmall(IMAGE_SPORT_RUN);

  public static final ImageIcon         ICON_SMALL_BIKE   = getImageIconSmall(IMAGE_SPORT_BIKE);

  public static final ImageIcon         ICON_SMALL_OTHER  = getImageIconSmall(IMAGE_SPORT_OTHER);

  public static final ImageIcon         ICON_RUN          = getImageIcon(IMAGE_SPORT_RUN);

  public static final ImageIcon         ICON_BIKE         = getImageIcon(IMAGE_SPORT_BIKE);

  public static final ImageIcon         ICON_OTHER        = getImageIcon(IMAGE_SPORT_OTHER);

  /**
   * 
   */
  private ImagesActivityRepository() {
    super();
  }

  public static String getImageName(int index) {
    return NAMES[index];
  }

  public static int getImageIndex(String name) {
    if (name != null) {
      for (int i = 0; i < NAMES.length; i++) {
        if (NAMES[i].equals(name)) {
          return i;
        }
      }
    }
    return -1;
  }

  public static ImageIcon[] getImageIcons() {
    ImageIcon[] imgs = new ImageIcon[NAMES.length];
    for (int i = 0; i < NAMES.length; i++) {
      imgs[i] = getImageIcon(NAMES[i]);
    }
    return imgs;
  }

  /**
   * Restitue une image icone.
   * 
   * @param name
   *          nom de l'image icone.
   * @return
   */
  public static ImageIcon getImageIcon(String name) {
    return new ImageIcon(ImagesActivityRepository.class.getResource(name));
  }

  /**
   * Restitue l'image icone de taille petite
   * 
   * @param iconName
   *          nom de l'icone
   * @return
   */
  public static ImageIcon getImageIconSmall(String iconName) {
    return mapSmallIcons.get(iconName);
  }

  /**
   * Restitue l'image icone de taille petite transparente.
   * 
   * @return
   */
  public static ImageIcon getImageIconSmallTransparent() {
    return mapSmallIcons.get("blank16.png");
  }

  /**
   * Restitue une image.
   * 
   * @param name
   *          nom de l'image.
   * @return
   */
  public static BufferedImage getImage(String name) {
    try {
      return ImageIO.read(ImagesActivityRepository.class.getResource(name));
    }
    catch (IOException e) {
      log.error("", e);
    }
    return null;
  }

}

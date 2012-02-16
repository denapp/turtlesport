package fr.turtlesport.map;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import javax.imageio.ImageIO;

import org.jdesktop.swingx.mapviewer.TileCache;

import fr.turtlesport.log.TurtleLogger;
import fr.turtlesport.util.FileUtil;

/**
 * @author Denis Apparicio
 * 
 */
public class DiskTitleCache extends TileCache {
  private static TurtleLogger           log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(DiskTitleCache.class);
  }

  /** * Repertoire du cache. */
  private File                          dirCache;

  /** Max age du cache en jour. */
  private int                           maxAge = 10;

  /** Taille courante du cache */
  private long                          currentSize;

  private OpenStreetMapTileProviderInfo tileProviderInfo;

  /**
   * @param dirCache
   * @param tileProviderInfo
   */
  protected DiskTitleCache(File dirCache,
                           OpenStreetMapTileProviderInfo tileProviderInfo) {
    this.tileProviderInfo = tileProviderInfo;
    this.dirCache = dirCache;
    if (!dirCache.exists()) {
      dirCache.mkdir();
    }
    cleanCache();

    if (log.isInfoEnabled()) {
      currentSize = FileUtil.dirLength(dirCache);
      log.error("currentSize=" + currentSize / 1024.0 / 1024.0 + " mo");
    }

  }

  /**
   * @return
   */
  public OpenStreetMapTileProviderInfo getTileProviderInfo() {
    return tileProviderInfo;
  }

  /**
   * @return the maxAge
   */
  public int getMaxAge() {
    return maxAge;
  }

  /**
   * @param maxAge
   *          the maxAge to set
   */
  public void setMaxAge(int maxAge) {
    if (maxAge > 1) {
      this.maxAge = maxAge;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdesktop.swingx.mapviewer.TileCache#needMoreMemory()
   */
  @Override
  public void needMoreMemory() {
    FileUtil.deleteDirectory(dirCache);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdesktop.swingx.mapviewer.TileCache#get(java.net.URI)
   */
  @Override
  public BufferedImage get(URI uri) throws IOException {
    if (uri == null) {
      return null;
    }

    // Recuperation de la cle
    String key = makeKey(uri);
    if (key == null) {
      return null;
    }

    File file = new File(dirCache, key);

    // Restitue l'image.
    if (exist(file)) {
      try {
        log.info("get Cache image: " + file.getPath());
        return ImageIO.read(file);
      }
      catch (IOException e) {
        log.warn("Echec cache : " + e);
      }
    }

    // Non trouvee
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdesktop.swingx.mapviewer.TileCache#put(java.net.URI, byte[],
   *      java.awt.image.BufferedImage)
   */
  @Override
  public void put(URI uri, byte[] bimg, BufferedImage img) {
    if (uri == null || img == null) {
      return;
    }

    // Recuperation de la cle
    String key = makeKey(uri);
    if (key == null) {
      return;
    }

    // Copie de l'image.
    dirCache.mkdirs();

    File file = new File(dirCache, key);
    copyFile(bimg, file);
    log.info("put cache image: " + file);
  }

  /**
   * Restitue la taille du cahce en octects.
   * 
   * @return la taille du cahce en octects.
   */
  public long length() {
    return FileUtil.dirLength(dirCache);
  }

  /**
   * Restitue la cl&eacute; pour le cache d'une url.
   * 
   * @param url
   *          l'url.
   * @return la cl&eacute;.
   */
  private String makeKey(URI uri) {
    if (uri == null) {
      return null;
    }
    String q = uri.getPath();
    return q.replace('/', '_');
  }

  /**
   * Determine si ce fichier est trop vieux.
   */
  private boolean exist(File file) {
    if (!file.isFile()) {
      return false;
    }

    long lNow = System.currentTimeMillis();
    long lHistory = lNow - 1000L * 60 * 60 * 24 * maxAge;
    boolean isTooOld = (file.lastModified() < lHistory);
    if (isTooOld) {
      file.delete();
    }
    return true;
  }

  /**
   * Copie un tableau de byte dans un fichier.
   */
  private void copyFile(byte[] bytes, File outFile) {
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(outFile);
      BufferedOutputStream os = new BufferedOutputStream(fos);
      os.write(bytes, 0, bytes.length);
      os.flush();
      os.close();
    }
    catch (IOException e) {
      log.error("", e);
    }
    finally {
      if (fos != null) {
        try {
          fos.close();
        }
        catch (Exception e) {
        }
      }
    }
  }

  private void cleanCache() {
    File[] files = dirCache.listFiles();
    if (files != null) {
      for (File f : files) {
        exist(f);
      }
    }
  }
}

package fr.turtlesport.util;

import java.util.Comparator;

/**
 * @author Denis Apparicio
 * 
 */
public class StringIgnoreCaseComparator implements Comparator<String> {
  public int compare(String obj1, String obj2) {
    return obj1.toUpperCase().compareTo(obj2.toUpperCase());
  }

}

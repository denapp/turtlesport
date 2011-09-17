package fr.turtlesport.unit.event;

import java.lang.reflect.Method;

import fr.turtlesport.log.TurtleLogger;

/**
 * @author Denis Apparicio
 * 
 */
public class Unit {
  private static TurtleLogger log;
  static {
    log = (TurtleLogger) TurtleLogger.getLogger(Unit.class);
  }

  protected Unit() {    
  }
  
  /**
   * Conversion de <code>unit1</code> vers <code>unit2</code>.
   * 
   * @param unit1
   *          libell&eacute; de l'unit&eacute;.
   * @param unit2
   *          libell&eacute; de l'unit&eacute;
   * @param value
   *          la valeur.
   * @return la valeur convertie en unit&eacute; <code>unit2</code>.
   */
  protected static Object convertUnit(Class<?> clazz,
                                      String[] units,
                                      String unit1,
                                      String unit2,
                                      Object value,
                                      Class<?> clazzArg) {
    checkUnit(units, unit1);
    checkUnit(units, unit2);

    if (unit1.equals(unit2)) {
      return value;
    }
    
    unit1 = unit1.replaceFirst("/", "per");
    unit2 = unit2.replaceFirst("/", "per");
    
    StringBuilder st = new StringBuilder();
    st.append("convert");
    st.append(unit1.toUpperCase().charAt(0));
    st.append(unit1.substring(1));
    st.append("To");
    st.append(unit2.toUpperCase().charAt(0));
    st.append(unit2.substring(1));

    // par introspection
    try {
      Method method = clazz.getMethod(st.toString(), clazzArg);
      return method.invoke(null, value);
    }
    catch (Throwable e) {
      log.error("", e);
      // ne peut arriver
      throw new RuntimeException(e);
    }

  }

  protected static String computeSecToTime(int sec) {
    StringBuilder st = new StringBuilder();

    int mn = sec / 60;
    if (mn == 0) {
      st.append("00");
    }
    else if (mn < 10) {
      st.append('0');
      st.append(mn);
    }
    else {
      st.append(mn);
    }
    st.append(":");

    sec = sec % 60;
    if (sec == 0) {
      st.append("00");
    }
    else if (sec < 10) {
      st.append('0');
      st.append(sec);
    }
    else {
      st.append(sec);
    }

    return st.toString();
  }
  
  private static void checkUnit(String[] units, String unit) {
    for (String u : units) {
      if (u.equals(unit)) {
        return;
      }
    }
    throw new IllegalArgumentException("unit=" + unit);
  }
  
  

}
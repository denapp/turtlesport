package fr.turtlesport.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 
 * @author Denis Apparicio
 * 
 */
public final class IntrospectorUtil {

  private IntrospectorUtil() {
  }

  /**
   * M&eacute;thode pour r&eacute;cup&eacute;rer un objet d'un accesseur Get.
   * 
   * @param clazz
   *          le nom de la classe
   * @param field
   *          le field.
   * @param obj
   *          l'objet.
   * @return
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */
  public static Object getter(Class<?> clazz, Field field, Object obj) throws IllegalAccessException,
                                                                   InvocationTargetException,
                                                                   NoSuchMethodException {
    return methodGetter(clazz, field).invoke(obj);
  }

  /**
   * Restitue la m�thode Get d'un <code>attribut</code>.
   * 
   * @param clazz
   *          <code>Class</code> de l'attribut.
   * @param field
   *          <code>Field</code> de l'attribut.
   * @return la m�thode Get d'un <code>attribut</code>.
   * @throws NoSuchMethodException
   * @throws SecurityException
   */
  public static Method methodGetter(Class<?> clazz, Field field) throws NoSuchMethodException {
    return clazz.getMethod(methodNameGetter(field));
  }

  /**
   * Restitue l'accesseur Get d'un <code>Field</code>.
   * 
   * @param field
   *          le <code>Field</code>.
   * @return le nom de la m�thode
   */
  public static String methodNameGetter(Field field) {
    StringBuffer st = new StringBuffer();

    String name = field.getName();

    if (field.getType() == Boolean.TYPE) {
      if (name.startsWith("is")) {
        return name;
      }
      st.append("is");
    }
    else {
      st.append("get");
    }

    addName(name, st);
    return st.toString();
  }

  /**
   * M�thode d'un accesseur Set.
   * 
   * @param clazz
   * @param field
   * @param obj
   * @param arg
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  public static void setter(Class<?> clazz, Field field, Object obj, Object arg) throws NoSuchMethodException,
                                                                             IllegalAccessException,
                                                                             InvocationTargetException {
    // Recuperation de la methode
    Method method = methodSetter(clazz, field);

    // Appel du setter
    method.invoke(obj, arg);
  }

  /**
   * Restitue la m�thode Set d'un <code>attribut</code>.
   * 
   * @param clazz
   *          <code>Class</code> de l'attribut.
   * @param field
   *          <code>Field</code> de l'attribut.
   * @return la m�thode Set d'un <code>attribut</code>.
   * 
   * @throws NoSuchMethodException
   * @throws SecurityException
   */
  public static Method methodSetter(Class<?> clazz, Field field) throws NoSuchMethodException {
    return clazz.getMethod(methodNameSetter(field), field.getType());
  }

  /**
   * Restitue le nom de l'accesseur Set d'un <code>attribut</code>.
   * 
   * @param <code>Field</code> de l'attribut.
   * @return le nom de la m�thode.
   */
  public static String methodNameSetter(Field field) {

    String name = field.getName();
    if (field.getType() == Boolean.TYPE && name.startsWith("is")) {
      name = name.substring(2);
    }

    StringBuffer st = new StringBuffer("set");
    addName(name, st);
    return st.toString();
  }

  /**
   * Restitue la m�thode add normalis�e d'une <code>List</code> :<br> - add<i>NomDuChamp</i>.
   * 
   * @param clazz
   *          <code>Class</code> qui contient la liste.
   * @param clazzToAdd
   *          <code>Class</code> de l'objet � ajouter.
   * @param field
   *          <code>Field</code> de l'attribut.
   * @return la m�thode Set d'un <code>attribut</code>.
   * 
   * @throws NoSuchMethodException
   * @throws SecurityException
   */
  public static Method methodListAdd(Class<?> clazz, Class<?> clazzToAdd, Field field) throws NoSuchMethodException {
    return clazz.getMethod(methodNameListAdd(field), clazzToAdd);
  }

  /**
   * Restitue le nom de la m�thode add normalis�e d'une <code>List</code> :<br> -
   * add<i>NomDuChamp</i>.
   * 
   * @param field
   *          le <code>Field</code>.
   * @return le nom de la m�thode
   */
  public static String methodNameListAdd(Field field) {
    StringBuffer st = new StringBuffer("add");
    addName(field.getName(), st);
    return st.toString();
  }

  /**
   * Restitue la taille d'une liste � partir de sa m�thode normalis�e.
   * 
   * @param clazz
   *          le nom de la classe
   * @param field
   *          le field.
   * @param obj
   *          l'objet.
   * @return
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */
  public static int listSize(Class<?> clazz, Field field, Object obj) throws IllegalAccessException,
                                                                  InvocationTargetException,
                                                                  NoSuchMethodException {

    return ((Integer) methodListSize(clazz, field).invoke(obj)).intValue();
  }

  /**
   * Restitue la m�thode normalis�e d'une <code>List</code> :<br> - add<i>NomDuChamp</i>.
   * 
   * @param clazz
   *          <code>Class</code> qui contient la liste.
   * @param field
   *          <code>Field</code> de l'attribut.
   * @return la m�thode Set d'un <code>attribut</code>.
   * 
   * @throws NoSuchMethodException
   * @throws SecurityException
   */
  public static Method methodListSize(Class<?> clazz, Field field) throws NoSuchMethodException {
    return clazz.getMethod(methodNameListSize(field));
  }

  /**
   * Restitue le nom de la m�thode add normalis�e d'une <code>List</code> :<br> -
   * add<i>NomDuChamp</i>.
   * 
   * @param field
   *          le <code>Field</code>.
   * @return le nom de la m�thode
   */
  public static String methodNameListSize(Field field) {
    return field.getName() + "Size";
  }

  /**
   * Restitue la m�thode normalis�e get <code>object</code>d'une
   * <code>List</code> :<br> - get<i>NomListe</i>.
   * 
   * @param clazz
   *          <code>Class</code> qui contient la liste.
   * @param field
   *          <code>Field</code> de l'attribut.
   * @return la m�thode Get d'une <code>List</code>.
   * 
   * @throws NoSuchMethodException
   * @throws SecurityException
   */
  public static Method methodListGetIndex(Class<?> clazz, Field field) throws NoSuchMethodException {
    return clazz.getMethod(methodNameListGet(field), Integer.TYPE);
  }

  /**
   * Restitue le nom de la m�thode normalis�e get <code>object</code>d'une
   * <code>List</code> :<br> - get<i>NomListe</i>.
   * 
   * @param field
   *          le <code>Field</code>.
   * @return le nom de la m�thode
   */
  public static String methodNameListGet(Field field) {
    StringBuffer st = new StringBuffer("get");
    addName(field.getName(), st);
    return st.toString();
  }

  private static void addName(String name, StringBuffer st) {
    char[] chars = new char[1];
    chars[0] = name.charAt(0);

    st.append(new String(chars).toUpperCase());
    st.append(name.substring(1));
  }

}

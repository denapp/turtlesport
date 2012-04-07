package fr.turtlesport.garmin;

/**
 * Carat&eacute;rise un produit Garmin.
 * 
 * @author Denis Apparicio
 * 
 */
public interface IGarminDevice {

  String displayName();

  String id();

  String softwareVersion();
  
}
package fr.turtlesport.util;

import java.text.DecimalFormat;
import java.util.ResourceBundle;

import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.unit.HeightUnit;
import fr.turtlesport.unit.WeightUnit;

/**
 * Cette classe permet de calculer l'IMC (Indice masse corporelle) ou BMI (Body
 * Mass Index).
 * 
 * @author Denis Apparicio
 * 
 */
public final class Bmi {

  private Bmi() {
  }

  /**
   * Calcul de l'IMC.
   * 
   * @param weightKg
   *          poids en kg.
   * @param sizeCm
   *          taille en cm.
   * @return l'IMC.
   */
  public static double compute(double weightKg, double sizeCm) {
    return (100.0 * 100.0 * weightKg) / (sizeCm * sizeCm);
  }

  /**
   * Calcul de l'IMC.
   * 
   * @param weightKg
   *          poids en kg.
   * @param sizeCm
   *          taille en cm.
   * @return l'IMC.
   */
  public static String computeFormat(double weightKg, double sizeCm) {
    DecimalFormat df = new DecimalFormat("###.#");
    return df.format(compute(weightKg, sizeCm));
  }

  /**
   * Calcul de l'IMC.
   * 
   * @param unitWeight
   *          unit&eacute; de poids.
   * @param unitHeight
   *          unit&eacute; de taille.
   * @param weight
   *          le poids.
   * @param height
   *          la hauteur.
   * @return l'IMC.
   */
  public static double compute(String unitWeight,
                               String unitHeight,
                               double weight,
                               double height) {
    double weightKg = weight;
    if (!WeightUnit.isUnitKg(unitWeight)) {
      weightKg = WeightUnit.convert(unitWeight, WeightUnit.unitKg(), weight);
    }

    double heightCm = height;
    if (!HeightUnit.isUnitCm(unitHeight)) {
      heightCm = HeightUnit.convert(unitHeight, HeightUnit.unitCm(), height);
    }

    return compute(weightKg, heightCm);
  }

  /**
   * Calcul de l'IMC.
   * 
   * @param unitWeight
   *          unit&eacute; de poids.
   * @param unitHeight
   *          unit&eacute; de taille.
   * @param weight
   *          le poids.
   * @param height
   *          la hauteur.
   * @return l'IMC.
   */
  public static String computeFormat(String unitWeight,
                                     String unitHeight,
                                     double weight,
                                     double height) {
    DecimalFormat df = new DecimalFormat("###.#");
    return df.format(compute(unitWeight, unitHeight, weight, height));
  }

  /**
   * Restitue le libell&eacute; selon l'OMC.
   * 
   * @param weightKg
   *          poids en kg.
   * @param sizeCm
   *          taille en cm.
   * @return le libell&eacute;:
   */
  public static String getLibelle(double weightKg, int sizeCm) {
    return getLibelle(compute(weightKg, sizeCm));
  }

  /**
   * Restitue le libell&eacute; selon l'OMC.
   * 
   * @param unitWeight
   *          unit&eacute; de poids.
   * @param unitHeight
   *          unit&eacute; de taille.
   * @param weight
   *          le poids.
   * @param height
   *          la hauteur.
   * @return l'IMC.
   */
  public static String getLibelle(String unitWeight,
                                  String unitHeight,
                                  double weight,
                                  double height) {
    return getLibelle(compute(unitWeight, unitHeight, weight, height));
  }

  /**
   * Restitue le libell&eacute selon l'OMC.
   * 
   * @param imc
   *          l'IMC.
   * @return le libell&eacute selon l'OMC.
   */
  public static String getLibelle(double imc) {
    ResourceBundle r = ResourceBundleUtility.getBundle(LanguageManager
        .getManager().getCurrentLang(), Bmi.class);

    if (imc < 18.5) {
      return r.getString("bmi1");
    }
    if (imc >= 18.5 && imc < 25) {
      return r.getString("bmi2");
    }
    if (imc >= 25 && imc < 30) {
      return r.getString("bmi3");
    }
    if (imc >= 30 && imc < 40) {
      return r.getString("bmi4");
    }
    return r.getString("bmi5");
  }
}

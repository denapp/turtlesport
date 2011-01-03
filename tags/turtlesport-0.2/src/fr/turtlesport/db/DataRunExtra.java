package fr.turtlesport.db;

/**
 * @author Denis Apparicio
 * 
 */
public class DataRunExtra {

  /** Les commentaires. */
  private String comments;

  /** Le nom de l'equipement utilise pour le run. */
  private String equipement;

  /** Identifiant de l'utilisateur du run. */
  private int    idUser = -1;

  /**
   * 
   */
  public DataRunExtra() {
    super();
  }

  /**
   * Restitue l'id de l'utilisateur.
   * 
   * @return l'id de l'utilisateur.
   */
  public int getIdUser() {
    return idUser;
  }

  /**
   * Valorise l'id de l'utilisateur.
   * 
   * @param idUser
   *          la nouvelle valeur.
   */
  public void setIdUser(int idUser) {
    this.idUser = idUser;
  }

  /**
   * Restitue le nom de l'&eacute;quipement.
   * 
   * @return le nom de l'&eacute;quipement.
   */
  public String getEquipement() {
    return equipement;
  }

  /**
   * Valorise le nom de l'&eacute;quipement.
   * 
   * @param equipement
   *          la nouvelle valeur.
   */
  public void setEquipement(String equipement) {
    this.equipement = equipement;
  }

  /**
   * Restitue les commentaires de la course.
   * 
   * @return les commentaires de la course.
   */
  public String getComments() {
    return comments;
  }

  /**
   * Valorise les commentaires de la course.
   * 
   * @param comments
   *          la nouvelle valeur.
   */
  public void setComments(String comments) {
    this.comments = comments;
  }
}

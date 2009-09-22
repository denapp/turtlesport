package fr.turtlesport.db;

import java.util.Date;
import java.util.ResourceBundle;

import fr.turtlesport.lang.LanguageManager;
import fr.turtlesport.util.ResourceBundleUtility;

/**
 * @author denis
 * 
 */
public class DataUser implements Comparable<DataUser> {
  private static final AllUser ALLUSER = new AllUser();

  private int                  id      = -1;

  private String               firstName;

  private String               lastName;

  private boolean              isMale;

  private Date                 birthDate;

  private float                weight;

  private float                height;

  private String               path;

  /**
   * 
   */
  public DataUser() {
    super();
  }

  /**
   * @param firstName
   * @param lastName
   */
  public DataUser(String firstName, String lastName) {
    super();
    this.firstName = firstName;
    this.lastName = lastName;
  }

  /**
   * D&eacute;termine si utilisateur all.
   * 
   * @param currentIdUser
   *          id utilisateur
   * @return <code>true</code> si utilisateur all, <code>false</code> sinon.
   */
  public static boolean isAllUser(int currentIdUser) {
    return (currentIdUser == ALLUSER.getId());
  }

  /**
   * Restitue l'utilisateur All.
   * 
   * @return l'utilisateur All.
   */
  public static DataUser getAllUser() {
    return ALLUSER;
  }

  /**
   * @return
   */
  public int getId() {
    return id;
  }

  /**
   * @param id
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * @return the birthDate
   */
  public Date getBirthDate() {
    return birthDate;
  }

  /**
   * @param birthDate
   *          the birthDate to set
   */
  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  /**
   * @return the firstName
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * @param firstName
   *          the firstName to set
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * @return the isMale
   */
  public boolean isMale() {
    return isMale;
  }

  /**
   * @param isMale
   *          the isMale to set
   */
  public void setMale(boolean isMale) {
    this.isMale = isMale;
  }

  /**
   * @return the lastName
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * @param lastName
   *          the lastName to set
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * @return the height
   */
  public float getHeight() {
    return height;
  }

  /**
   * @param height
   *          the height to set
   */
  public void setHeight(float height) {
    this.height = height;
  }

  /**
   * @return the weight
   */
  public float getWeight() {
    return weight;
  }

  /**
   * @param weight
   *          the weight to set
   */
  public void setWeight(float weight) {
    this.weight = weight;
  }

  /**
   * @return the path
   */
  public String getPath() {
    return path;
  }

  /**
   * @param path
   *          the path to set
   */
  public void setPath(String path) {
    this.path = path;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(DataUser o) {
    if (lastName == null || firstName == null) {
      return -1;
    }
    int val = lastName.compareTo(o.getLastName());
    if (val == 0) {
      val = firstName.compareTo(o.getFirstName());
    }
    return val;
  }

  /**
   * @author Denis Apparicio
   * 
   */
  public static class AllUser extends DataUser {
    public AllUser() {
      super();
      setId(-1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.turtlesport.db.DataUser#getFirstName()
     */
    @Override
    public String getFirstName() {
      ResourceBundle rb = ResourceBundleUtility.getBundle(LanguageManager
          .getManager().getCurrentLang(), DataUser.class);
      return rb.getString("allUserName");
    }

    /**
     * @return
     */
    @Override
    public String getLastName() {
      return "";
    }
  }

}

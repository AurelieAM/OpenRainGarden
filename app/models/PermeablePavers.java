package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import play.db.ebean.Model;

/**
 * An object that represents a permeable pavers.
 */
@Entity
public class PermeablePavers extends Model {
  private static final long serialVersionUID = 1L;
  private static final long VALID_DATE_LENGTH = 8;
  
  @Id
  private Long id;
  private String title = "Permeable Pavers";
  private String propertyType;
  private String address;
  private String hideAddress;
  @Lob
  private String description;
  private String dateInstalled;
  private String material;
  private String previousMaterial;
  private String size;
  private String installer;
  private String commentKey;
  @Lob
  private byte [] image;
  private String externalImageURL;
  private boolean isApproved = false;
  
  @ManyToOne
  private UserInfo owner;
  
  @OneToMany (mappedBy = "paver", cascade = CascadeType.ALL)
  private List<Comment> commentList = new ArrayList<Comment>();

  /**
   * Constructor.
   * @param title Title of permeable paver.
   * @param propertyType Type of property of paver's address.
   * @param address Address of permeable paver.
   * @param hideAddress Specify whether to hide address from public.
   * @param description Description of permeable paver.
   * @param dateInstalled Date paver was installed.
   * @param material Material of previous paver.
   * @param previousMaterial Material of material before paver installation.
   * @param size Size of paver.
   * @param installer Installer if paver.
   */
  public PermeablePavers(String title, String propertyType, String address, String hideAddress,
      String description, String dateInstalled, String material, String previousMaterial, String size,
      String installer) {
    this.title = title;
    this.propertyType = propertyType;
    this.address = address;
    this.hideAddress = hideAddress;
    this.description = description;
    this.dateInstalled = dateInstalled;
    this.material = material;
    this.previousMaterial = previousMaterial;
    this.size = size;
    this.installer = installer;
    this.commentKey = "pp" + this.id;
  }

  /**
   * @return the id
   */
  public long getID() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setID(long id) {
    this.id = id;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return the propertyType
   */
  public String getPropertyType() {
    return propertyType;
  }

  /**
   * @param propertyType the propertyType to set
   */
  public void setPropertyType(String propertyType) {
    this.propertyType = propertyType;
  }

  /**
   * @return the address
   */
  public String getAddress() {
    return address;
  }

  /**
   * @param address the address to set
   */
  public void setAddress(String address) {
    this.address = address;
  }

  /**
   * @return the hideAddress
   */
  public String getHideAddress() {
    return hideAddress;
  }

  /**
   * @param hideAddress the hideAddress to set
   */
  public void setHideAddress(String hideAddress) {
    this.hideAddress = hideAddress;
  }
  
  /**
   * Returns true if hideAddress equals "Yes" and false if hideAddress equals "No".
   * @return True if hideAddress is "Yes", otherwise False.
   */
  public boolean hideAddress() {
	  return this.hideAddress.equals("Yes");
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the material
   */
  public String getMaterial() {
    return material;
  }

  /**
   * @param material the material to set
   */
  public void setMaterial(String material) {
    this.material = material;
  }

  /**
   * @return the previousMaterial
   */
  public String getPreviousMaterial() {
    return previousMaterial;
  }

  /**
   * @param previousMaterial the previousMaterial to set
   */
  public void setPreviousMaterial(String previousMaterial) {
    this.previousMaterial = previousMaterial;
  }

  /**
   * @return the size
   */
  public String getSize() {
    return size;
  }

  /**
   * @param size the size to set
   */
  public void setSize(String size) {
    this.size = size;
  }

  /**
   * @return the installer
   */
  public String getInstaller() {
    return installer;
  }

  /**
   * @param installer the installer to set
   */
  public void setInstaller(String installer) {
    this.installer = installer;
  }

  /**
   * @return the owner
   */
  public UserInfo getOwner() {
    return owner;
  }

  /**
   * @param owner the owner to set
   */
  public void setOwner(UserInfo owner) {
    this.owner = owner;
  }
  
  /**
   * @return the dateInstalled
   */
  public String getDateInstalled() {
    if (this.dateInstalled.length() < VALID_DATE_LENGTH) {
      return "N/A";
    }
    return dateInstalled;
  }

  /**
   * @param dateInstalled the dateInstalled to set
   */
  public void setDateInstalled(String dateInstalled) {
    this.dateInstalled = dateInstalled;
  }
  
  
  /**
   * @return the image
   */
  public byte [] getImage() {
    return image;
  }

  /**
   * @param image the image to set
   */
  public void setImage(byte [] image) {
    this.image = image;
  }

  /**
   * @return the externalImageURL
   */
  public String getExternalImageURL() {
    return externalImageURL;
  }

  /**
   * @param externalImageURL the externalImageURL to set
   */
  public void setExternalImageURL(String externalImageURL) {
    this.externalImageURL = externalImageURL;
  }

  /**
   * @return the comments
   */
  public List<Comment> getComments() {
    return commentList;
  }

  /**
   * @return the isApproved
   */
  public boolean isApproved() {
    return isApproved;
  }

  /**
   * @param isApproved the isApproved to set
   */
  public void setApproved(boolean isApproved) {
    this.isApproved = isApproved;
  }

  /**
   * @return the hasPicture
   */
  public boolean hasPicture() {
    if (this.image == null) {
      return false;
    }
    else {
      return (this.image.length > 0);
    }
  }

  /**
   * Get month installed.
   * @return The month the rain garden was installed.
   */
  public String getMonthInstalled() {
    String [] date = this.getDateInstalled().split("/");
    if (date.length > 0) {
    return date[0];
    }
    return "";
  }
  
  /**
   * Get day installed.
   * @return The day the rain garden was installed.
   */
  public String getDayInstalled() {
    String [] date = this.getDateInstalled().split("/");
    if (date.length > 1) {
    return date[1];
    }
    return "";
  }
  
  /**
   * Get year installed.
   * @return The year the rain garden was installed.
   */
  public String getYearInstalled() {
    String [] date = this.getDateInstalled().split("/");
    if (date.length > 2) {
    return date[2];
    }
    return "";
  }
  
  /**
   * Return name of owner.
   * @return The owner's name.
   */
  public String getNameOfOwner() {
    return this.owner.getFirstName() + " " + this.owner.getLastName();
  }
  
  /**
   * Get the picture name tied to a rain garden.
   * @return The name of the picture tied to the rain garden.
   * @throws IOException 
   */
  public String getPictureName() throws IOException {
    if (hasPicture()) {
      return "images/upload/pp" + this.id;
    }
    return "images/placeholder.gif";
  }
  
  /**
   * Return key of rain garden.
   * @return The key of the rain garden.
   */
  public String getKey() {
    return this.commentKey;
  }
  
  /**
   * Check if a user is the owner of a permeable pavers.
   * @param userInfo The user to check.
   * @return True if the user is the owner, false otherwise.
   */
  public boolean isOwner(UserInfo userInfo) {
    if (userInfo == null) {
      return false;
    }
    return (this.owner.getId() == userInfo.getId());
  }
  
  /**
   * Check if a user can edit a permeable pavers.
   * @param userInfo The user to check. 
   * @return True if the user is the owner or an admin otherwise false.
   */
  public boolean canEdit(UserInfo userInfo) {
    if (userInfo == null) {
      return false;
    }
    return (userInfo.isAdmin() || isOwner(userInfo));
  }
  
  
  /**
   * The EBean ORM finder method for database queries on ID.
   * @return The finder method for rain gardens.
   */
  public static Finder<Long, PermeablePavers> find() {
    return new Finder<Long, PermeablePavers>(Long.class, PermeablePavers.class);
  }
  
  /**
   * Return a valid date if it exists.
   * @return The string for date installed formatted for CSV format.
   */
  public String getDateInstalledAsCSV() {
    if (this.dateInstalled.length() < VALID_DATE_LENGTH) {
      return "";
    }
    return dateInstalled;   
  }
  

  /**
   * Format permable paver information in CSV format.
   * @return The permeable paver's information in CSV format.
   */
  public String formatToCSV() {
    return "\"" + this.title + "\", " + "\"" + this.propertyType + "\", " + "\"" + this.address + "\", " + "\"" 
           + this.description + "\", " + "\"" + getDateInstalledAsCSV() + "\", " + "\"" + this.material + "\", " 
           + this.previousMaterial + "\", " + this.size + "\", " + this.installer + "\", " 
           + this.owner.getEmail() + "\"\n";
  }
}

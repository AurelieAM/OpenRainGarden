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
 * An object that represents a rain barrel.
 */
@Entity
public class RainBarrel extends Model {
  private static final long serialVersionUID = 1L;
  private static final long VALID_DATE_LENGTH = 8;
  
  @Id
  private Long id;
  private String title = "Rain Barrel";
  private String propertyType;
  private String address;
  private String hideAddress;
  @Lob
  private String description;
  private String dateInstalled;
  private String rainBarrelType;
  private String capacity;
  private String color;
  private String material;
  private String estimatedCost;
  private String waterUse;
  private String overflowFrequency;
  private String cover;
  private String obtainedFrom;
  private String installationType;
  @Lob
  private byte [] image;
  private String externalImageURL;
  private boolean isApproved = false;
  
  @ManyToOne
  private UserInfo owner;

  @OneToMany (mappedBy = "barrel", cascade = CascadeType.ALL)
  private List<Comment> commentList = new ArrayList<Comment>();
    
  /**
   * Constructor.
   * @param title Title of rain barrel.
   * @param propertyType Property type of rain barrel address.
   * @param address Address of rain barrel.
   * @param hideAddress Hide address from public.
   * @param description Description of rain barrel.
   * @param dateInstalled Date barrel was installed.
   * @param rainBarrelType Type of rain barrel.
   * @param capacity Capacity of rain barrel.
   * @param color Color of rain barrel.
   * @param material Material of rain barrel.
   * @param estimatedCost Cost of rain barrel.
   * @param waterUse Water usage from rain barrel collection.
   * @param overflowFrequency Overflow frequency.
   * @param cover Cover type for barrel.
   * @param obtainedFrom Place rain barrel was obtained.
   * @param installationType Type of installation for rain barrel.
   */
  public RainBarrel(String title, String propertyType, String address, String hideAddress, String description,
      String dateInstalled, String rainBarrelType, String capacity, String color, String material,
      String estimatedCost, String waterUse, String overflowFrequency, String cover, String obtainedFrom,
      String installationType) {
    this.title = title;
    this.propertyType = propertyType;
    this.address = address;
    this.hideAddress = hideAddress;
    this.description = description;
    this.dateInstalled = dateInstalled;
    this.rainBarrelType = rainBarrelType;
    this.capacity = capacity;
    this.color = color;
    this.material = material;
    this.estimatedCost = estimatedCost;
    this.waterUse = waterUse;
    this.overflowFrequency = overflowFrequency;
    this.cover = cover;
    this.obtainedFrom = obtainedFrom;
    this.installationType = installationType;
  }
  
  /**
   * @return the id
   */
  public Long getID() {
    return id;
  }
  /**
   * @param id the id to set
   */
  public void setID(Long id) {
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
   * @return True if hideAddress is "Yes", false otherwise.
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
   * @return the rainBarrelType
   */
  public String getRainBarrelType() {
    return rainBarrelType;
  }

  /**
   * @param rainBarrelType the rainBarrelType to set
   */
  public void setRainBarrelType(String rainBarrelType) {
    this.rainBarrelType = rainBarrelType;
  }

  /**
   * @return the capacity
   */
  public String getCapacity() {
    return capacity;
  }

  /**
   * @param capacity the capacity to set
   */
  public void setCapacity(String capacity) {
    this.capacity = capacity;
  }

  /**
   * @return the color
   */
  public String getColor() {
    return color;
  }

  /**
   * @param color the color to set
   */
  public void setColor(String color) {
    this.color = color;
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
   * @return the estimatedCost
   */
  public String getEstimatedCost() {
    return estimatedCost;
  }

  /**
   * @param estimatedCost the estimatedCost to set
   */
  public void setEstimatedCost(String estimatedCost) {
    this.estimatedCost = estimatedCost;
  }

  /**
   * @return the waterUse
   */
  public String getWaterUse() {
    return waterUse;
  }

  /**
   * @param waterUse the waterUse to set
   */
  public void setWaterUse(String waterUse) {
    this.waterUse = waterUse;
  }

  /**
   * @return the overflowFrequency
   */
  public String getOverflowFrequency() {
    return overflowFrequency;
  }

  /**
   * @param overflowFrequency the overflowFrequency to set
   */
  public void setOverflowFrequency(String overflowFrequency) {
    this.overflowFrequency = overflowFrequency;
  }

  /**
   * @return the cover
   */
  public String getCover() {
    return cover;
  }

  /**
   * @param cover the cover to set
   */
  public void setCover(String cover) {
    this.cover = cover;
  }

  /**
   * @return the obtainedFrom
   */
  public String getObtainedFrom() {
    return obtainedFrom;
  }

  /**
   * @param obtainedFrom the obtainedFrom to set
   */
  public void setObtainedFrom(String obtainedFrom) {
    this.obtainedFrom = obtainedFrom;
  }

  /**
   * @return the installationType
   */
  public String getInstallationType() {
    return installationType;
  }

  /**
   * @param installationType the installationType to set
   */
  public void setInstallationType(String installationType) {
    this.installationType = installationType;
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
   * Get month installed.
   * @return The month the rain barrel was installed.
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
   * @return The day the rain barrel was installed.
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
   * @return The year the rain barrel was installed.
   */
  public String getYearInstalled() {
    String [] date = this.getDateInstalled().split("/");
    if (date.length > 2) {
    return date[2];
    }
    return "";
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
   * @return the comments
   */
  public List<Comment> getComments() {
    return commentList;
  }

  /**
   * Get the picture name tied to a rain barrel..
   * @return The name of the picture tied to the rain barrel.
   * @throws IOException Thrown if there is an error during file checking.
   */
  public String getPictureName() throws IOException {
    if (hasPicture()) {
      return "images/upload/rb" + this.id;
    }
    return "images/placeholder.gif";
  }
  
  /**
   * Check if a user is the owner of a rain barrel.
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
   * Check if a user can edit a rain barrel.
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
   * @return The finder method for rain barrels.
   */
  public static Finder<Long, RainBarrel> find() {
    return new Finder<Long, RainBarrel>(Long.class, RainBarrel.class);
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
   * Format barrel information to CSV format.
   * @return The barrel information in CSV format.
   */
  public String formatToCSV() {
    return "\"" + this.title + "\", " + "\"" + this.propertyType + "\", " + "\"" + this.address + "\", " + "\"" 
           + this.description + "\", " + "\"" + getDateInstalledAsCSV() + "\", " + "\"" + this.rainBarrelType + "\", "
           + "\"" + this.capacity + "\", " + "\"" + this.color + "\", " + "\"" + this.material + "\", " 
           + "\"" + this.estimatedCost + "\", " + "\"" + this.waterUse + "\", " 
           + "\"" + this.overflowFrequency + "\", " + "\"" + this.cover + "\", " + "\"" + this.obtainedFrom + "\", "
           + "\"" + this.installationType + "\", " + this.getOwner().getEmail() + "\"\n";
  }
}

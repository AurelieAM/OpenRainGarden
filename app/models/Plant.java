package models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import play.db.ebean.Model;

/**
 * Represents plants used in a rain garden.
 * @author eduardgamiao
 *
 */
@Entity
public class Plant extends Model {
  private static final long serialVersionUID = 1L;

  @Id
  private Long id;
  private String name;
  private String scientificName;
  private String placement;
  private String growth; 
  private String climateType;
  private boolean isInitialPlant = false;
  @Lob
  private byte [] image;
  private String externalImageURL;
  
  // Relationships.
  @ManyToMany (mappedBy = "plants", cascade = CascadeType.ALL)
  List<RainGarden> gardens = new ArrayList<RainGarden>();

  /**
   * Constructor.
   * @param name Name of plant.
   * @param scientificName Scientific name of plant.
   * @param placement Placement in rain garden.
   * @param growth Growth type.
   * @param climateType Plant climate type. 
   */
  public Plant(String name, String scientificName, String placement, String growth, String climateType) {
    this.name = name;
    this.scientificName = scientificName;
    this.placement = placement;
    this.growth = growth;
    this.climateType = climateType;
  }
  
  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the scientificName
   */
  public String getScientificName() {
    return scientificName;
  }

  /**
   * @param scientificName the scientificName to set
   */
  public void setScientificName(String scientificName) {
    this.scientificName = scientificName;
  }

  /**
   * @return the placement
   */
  public String getPlacement() {
    return placement;
  }

  /**
   * @param placement the placement to set
   */
  public void setPlacement(String placement) {
    this.placement = placement;
  }

  /**
   * @return the growth
   */
  public String getGrowth() {
    return growth;
  }

  /**
   * @param growth the growth to set
   */
  public void setGrowth(String growth) {
    this.growth = growth;
  }

  /**
   * @return the climateType
   */
  public String getClimateType() {
    return climateType;
  }

  /**
   * @param climateType the climateType to set
   */
  public void setClimateType(String climateType) {
    this.climateType = climateType;
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
   * @return the isInitialPlant
   */
  public boolean isInitialPlant() {
    return isInitialPlant;
  }

  /**
   * @param isInitialPlant the isInitialPlant to set
   */
  public void setIsInitialPlant(boolean isInitialPlant) {
    this.isInitialPlant = isInitialPlant;
  }

  /**
   * Check if the plant has an image.
   * @return True if the plant has an image, false otherwise.
   */
  public boolean hasPicture() {
    return (this.image != null);
  }

  /**
   * Return the name of the plant without markings/accents. 
   * @return The name of the plant without markings/accents.
   */
  public String getPictureName() {
    return this.id + ".jpg";
    //return Normalizer.normalize(this.name, Normalizer.Form.NFD).replaceAll("[^A-Za-z0-9]", "").toLowerCase() + ".jpg";
  }
  
  /**
   * Return a list of gardens that contain a plant.
   * @return A list of gardens that contain a plant.
   */
  public List<RainGarden> getGardens() {
    return this.gardens;
  }
  
  /**
   * The EBean ORM finder method for database queries on ID.
   * @return The finder method for plants.
   */
  public static Finder<Long, Plant> find() {
    return new Finder<Long, Plant>(Long.class, Plant.class);
  }
}

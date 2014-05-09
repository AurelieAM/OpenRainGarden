package models;

import java.util.List;
import play.Logger;
import views.formdata.IndexContentFormData;

/**
 * A database of plants.
 */
public class IndexContentDB {

  /**
   * Add/Edit content block in database.
   * @param formData Processed form data for an IndexContent.
   * @return The IndexContent that has been added/ediited.
   */
	public static IndexContent add(IndexContentFormData formData) {
	  IndexContent indexContent;
	  Logger.debug("ID: " + formData.id);
	  if (formData.id == -1) {
	    indexContent = new IndexContent(formData.title, formData.content, formData.firstButtonText, 
	                                    formData.firstButtonURL, formData.secondButtonText, 
	                                    formData.secondButtonURL);
	    indexContent.save();
	  }
	  else {
	    indexContent = getIndexContent(formData.id);
	    if (indexContent != null) {
	      Logger.debug(formData.toString());
	      indexContent.setTitle(formData.title);
	      indexContent.setContent(formData.content);
	      indexContent.setFirstButtonText(formData.firstButtonText);
	      indexContent.setFirstButtonURL(formData.firstButtonURL);
	      indexContent.setSecondButtonText(formData.secondButtonText);
	      indexContent.setSecondButtonURL(formData.secondButtonURL);
	      indexContent.save();
	    }
	  }
	  return indexContent;
	}
	
	/**
	 * Retrieve an IndexContent from the database.
	 * @param id The ID of the IndexContent.
	 * @return The IndexContnet with the matching ID if it exists. Otherwise null.
	 */
	public static IndexContent getIndexContent(Long id) {
	  return IndexContent.find().byId(id);
	}
	
	/**
	 * Return a list of all IndexContents in the database.
	 * @return A List of IndexContent.
	 */
	public static List<IndexContent> getIndexContents() {
	  return IndexContent.find().all();
	}

	/**
	 * Delete an IndexContent from the database.
	 * @param id The ID of the IndexContent to delete.
	 */
  public static void deleteIndexContent(Long id) {
    IndexContent indexContent = IndexContentDB.getIndexContent(id);
    if (indexContent != null) {
      indexContent.delete();
    }
  }
  
}
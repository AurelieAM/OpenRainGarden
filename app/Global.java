import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Comment;
import models.CommentDB;
import models.HeaderFooterDB;
import models.IndexContent;
import models.IndexContentDB;
import models.PermeablePavers;
import models.PermeablePaversDB;
import models.Plant;
import models.RainBarrel;
import models.RainBarrelDB;
import models.RainGarden;
import models.RainGardenDB;
import models.Resource;
import models.ResourceDB;
import models.UserInfo;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.libs.Yaml;
import models.UserInfoDB;
import views.formdata.CommentFormData;
import views.formdata.HeaderFooterFormData;
import views.formdata.IndexContentFormData;
import views.formdata.PermeablePaversFormData;
import views.formdata.RainBarrelFormData;
import views.formdata.RainGardenFormData;
import views.formdata.ResourceFormData;
import org.mindrot.jbcrypt.BCrypt;
import com.avaje.ebean.Ebean;
import controllers.routes;

/**
 * Implements a Global object for the Play Framework.
 * 
 * @author eduardgamiao
 * 
 */
public class Global extends GlobalSettings {
  /**
   * Initialization method for this Play Framework web application.
   * 
   * @param app A Play Framework application.
   */
  public void onStart(Application app) {
    populatePlantDB();
    populateIndexContentDB();
    
    //Add seeded users.
    Long id = null;
    if (UserInfoDB.getUsers().isEmpty()) {
      String admin_email = Play.application().configuration().getString("admin_email");
      String admin_pw = Play.application().configuration().getString("admin_pw");
      if (admin_email != null && admin_pw != null) {
    	  id = UserInfoDB.addUserInfo("Site", "Administrator", admin_email, "", BCrypt.hashpw(admin_pw, 
    	                              BCrypt.gensalt()), true, true);
      }
    }
        
    // Add rain garden.
    if (RainGarden.find().all().isEmpty() && id != null) {
      List<String> plants = new ArrayList<String>();
      plants.add("Carex");
      plants.add("Pa'uohi'iaka");
      plants.add("'Ilima");
      plants.add("'Ohai");
      RainGarden garden = RainGardenDB.addRainGarden(new RainGardenFormData("", "Residential", 
        "977 Kahili Street.", "Yes", " Residential rain garden installed in front yard right next to street corner "
            + "storm drain.  The rain garden replaced the turf grass with absorbent soils and native vegetation "
            + "which captures and infiltrates water from the roughly 550 square foot roof.  Since turf grass "
            + "absorbs only about 30% of storm water runoff, this rain garden will prevent a significant amount "
            + "of storm water runoff from entering into the street corner storm drain which eventually drains "
            + "into Enchanted Lakes.", 
        "", "", "", plants, "0-60 Square Feet", "500-750 Square Feet", "The primary source of runoff is coming from "
            + "the roof through the downspout and into the rain garden.", 
        "14.00"), UserInfoDB.getUser(id));
      
      garden.setApproved(true);
      garden.setExternalImageURL(routes.Assets.at("images/garden.png").url());
      garden.save();
    }
    
    // Add rain barrel.
    if (RainBarrel.find().all().isEmpty() && id != null) {
    RainBarrel barrel = RainBarrelDB.addRainBarrel(new RainBarrelFormData("", "Residential", 
        "1051 Keolu Dr.", "No", "We installed a set of rain barrels on the side of our house because we wanted to "
            + "protect the plants from excessive flooding. The water is collected from the gutters on the roof and flow"
            + " into these barrels. The collected water is then used for gardening. It helps cutdown our water bill!", 
            "4", "5", "2014", "Old Drum", "50 Gallons", "Blue", "Plastic", "25.00", "Gardening", "Once a year.", 
            "Covered", "Home Depot", "Self-Installed"), 
        UserInfoDB.getUser(id));
    barrel.setApproved(true);
    barrel.setExternalImageURL(routes.Assets.at("images/barrel.png").url());
    barrel.save();
    }
    
    // Add permeable paver.
    if (PermeablePavers.find().all().isEmpty() && id != null) {
    PermeablePavers paver = PermeablePaversDB.addPermeablePavers(new PermeablePaversFormData("", 
        "Residential", "1051 Keolu Dr.", "No", "Whenever it rains, our driveway becomes a mini river. The rain would "
            + "just pool in our driveway. After we installed our permeable paver, we noticed a drastic decrease in "
            + "pooling and the lawn around our driveway looks more green.", "4", "5", "2014", "Asphalt", "Concrete", 
            "<200 Square Feet", "Self-Installed"),  UserInfoDB.getUser(id));
    paver.setApproved(true);
    paver.setExternalImageURL(routes.Assets.at("images/paver.png").url());
    paver.save();
    }
    
    //Learn More Resources Database
    if (Resource.find().all().isEmpty()) {
    	Resource resource;
    	
    	//HOK program
        resource = ResourceDB.addGardenResource(new ResourceFormData(-1, "Hui o Ko'olaupoko Rain Garden Program", "http://www.huihawaii.org/rain-gardens.html"));
        resource.setExternalImage(routes.Assets.at("images/hokprogram.jpg").url());
        resource.save();
        
        //Rain Garden Manual
        resource = ResourceDB.addGardenResource(new ResourceFormData(-1, "Hawaii Rain Garden Manual", "http://www.huihawaii.org/uploads/1/6/6/3/16632890/raingardenmanual-web-res-smaller.pdf"));
        resource.setExternalImage(routes.Assets.at("images/raingardenmanual.jpg").url());
        resource.save();
        
        //Native Plant Care Manual
        resource = ResourceDB.addGardenResource(new ResourceFormData(-1, "Native Plant Care Manual", "http://www.huihawaii.org/uploads/1/6/6/3/16632890/plant_foster_parent_handbook_final_draft_for_pdf.pdf"));
        resource.setExternalImage(routes.Assets.at("images/nativeplantmanual.jpg").url());
        resource.save();
        
        //Board of Water Supply Rain Barrel Program
        resource = ResourceDB.addBarrelResource(new ResourceFormData(-1, "Honolulu Board of Water Supply Rain Barrel Program", "http://www.hbws.org/cssweb/display.cfm?sid=2091"));
        resource.setExternalImage(routes.Assets.at("images/watersupplyprogram.jpg").url());
        resource.save();
        
        //AquaPave
        resource = ResourceDB.addPaverResource(new ResourceFormData(-1, "AquaPave", "http://www.aquapave.com/index.htm"));
        resource.setExternalImage(routes.Assets.at("images/aquapave.jpg").url());
        resource.save();
        
        //Futura Stone
        resource = ResourceDB.addPaverResource(new ResourceFormData(-1, "Futura Stone of Hawaii", "http://futurastonehawaii.com/"));
        resource.setExternalImage(routes.Assets.at("images/futurastone.jpg").url());
        resource.save();
    }
  }
  
  /**
   * Populate plant database with plants from file.
   */
  private static void populatePlantDB() {
    if (Plant.find().all().isEmpty()) {
      @SuppressWarnings("unchecked")
      Map<String, List<Object>> all = (Map<String, List<Object>>) Yaml.load("initial-data.yml");
      Ebean.save(all.get("plants"));
    }
  }
  
  /**
   * Populate the Index page.
   */
  private static void populateIndexContentDB() {
    if (HeaderFooterDB.isEmpty()) {
	  HeaderFooterDB.add(new HeaderFooterFormData("Hawaii Rainwater Solutions: Sponsored by Hui o Koolaupoko",  
	      "Registry & Gallery", "Website sponsered by Hui o Ko'olaupoko and the Hawaii State Department of Health"
	          + " - 2014 Privacy", "This project has been jointly funded by the U.S. Environmental Protection "
	              + "Agency (EPA) under Section 319(h) of the Clean Water Act, and the Hawaii State Department of "
	              + "Health (HDOH), Clean Water Branch. Although the information in this document has been funded "
	              + "wholly or in part by a Federal Grant to the HDOH, it may not necessarily reflect the views of "
	              + "the EPA and the HDOH and no offical endorsement should be inferred."));
    }
	  
	if (IndexContentDB.getIndexContents().isEmpty()) {  
	  IndexContent ic1 = IndexContentDB.add(new IndexContentFormData("What Harm Can A Little Rainwater Do?", "When "
	      + "it rains, the resulting rainwater runoff washes pollutants into Hawaii's streams, rivers, lakes and "
	      + "the ocean. Rainwater runoff solutions allow the resulting runoff to instead be collected, reused or "
	      + "absorbed naturually into the Earth. Share your solutions with your community and inspire your "
	      + "neighbors to be green!", "Sign Up", "/signup", "View Map", "/map"));
	  ic1.setExternalImageURL(routes.Assets.at("images/alawai.png").url());
	  ic1.save();
	  IndexContent ic2 = IndexContentDB.add(new IndexContentFormData("Solution: Rain Garden" , "A Rain Garden is a "
	      + "low-lying area populated with native plants. They reduce the amount of water diverted from roofs/"
	      + "driveways/parking lots into storm drains by absorbing and filtering the water.", "Learn More", 
	      "/learnmore#garden_resources", "View Gallery", "/gallery/rain-garden"));
    ic2.setExternalImageURL(routes.Assets.at("images/garden.png").url());
    ic2.save();
	  IndexContent ic3 = IndexContentDB.add(new IndexContentFormData("Solution: Permeable Paver" , "A permeable "
	      + "interlocking concrete pavement is comprised of a layer of permeable pavers separated by joints filled "
	      + "with small stones. The stones in the joints provide 100% surface permeability while the stone base "
	      + "effectively filters stormwater and reduces pollutants and debris that would otherwise be washed into "
	      + "streams and rivers.", "Learn More", "/learnmore#paver_resources", "View Gallery", 
	      "/gallery/permeable-paver"));
    ic3.setExternalImageURL(routes.Assets.at("images/paver.png").url());
    ic3.save();
	  IndexContent ic4 = IndexContentDB.add(new IndexContentFormData("Solution: Rain Barrel", "During the summer "
	      + "months it is estimated that nearly 40 percent of household water is used for lawn and garden "
	      + "maintenance. A rain barrel collects water and stores it for those times that you need it most --- "
	      + "during the dry summer months. Using rain barrels potentially helps homeowners lower water bills, "
	      + "while also improving the vitality of plants, flowers, trees, and lawns.", "Learn More", 
	      "/learnmore#barrel_resources", "View Gallery", "/gallery/rain-barrel"));
    ic4.setExternalImageURL(routes.Assets.at("images/barrel.png").url());
    ic4.save();
	}
  }
  
 
}

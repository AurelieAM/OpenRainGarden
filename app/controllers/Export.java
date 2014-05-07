package controllers;

import models.PermeablePavers;
import models.PermeablePaversDB;
import models.RainBarrel;
import models.RainBarrelDB;
import models.RainGarden;
import models.RainGardenDB;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Handles exporting data from application.
 * @author eduardgamiao
 *
 */
public class Export extends Controller {
  
  /**
   * Export the rain garden information as a csv.
   * @return The rain garden information as a csv.
   */
  @Security.Authenticated(Secured.class)
  public static Result exportRainGarden() {
    if (Secured.isLoggedIn(ctx()) && Secured.getUserInfo(ctx()).isAdmin()) {
      String output = "Title,Property_Type,Address,Description,Date_Installed,Rain_Garden_Size,Waterflow_Source_Size,"
          + "Waterflow_Description,Infiltration_Rate,Owner_Email\n";
      for (RainGarden garden : RainGardenDB.getRainGardens()) {
        output += garden.formatToCSV();
      }
      return ok(output).as("text/csv");
    }
    return redirect(routes.Application.index());
  }
  
  /**
   * Export the rain barrel information as a csv.
   * @return The rain barrel information as a csv.
   */
  @Security.Authenticated(Secured.class)
  public static Result exportRainBarrel() {
    if (Secured.isLoggedIn(ctx()) && Secured.getUserInfo(ctx()).isAdmin()) {
      String output = "Title,Property_Type,Address,Description,Date_Installed,Rain_Barrel_Type,Capacity,Color,Material,"
          + "Estimated_Cost,Water_Use,Overflow_Frequency,Cover,Obtained_From,Installation_Type,Owner_Email\n";
      for (RainBarrel barrel : RainBarrelDB.getRainBarrels()) {
        output += barrel.formatToCSV();
      }
      return ok(output).as("text/csv");
    }
    return redirect(routes.Application.index()); 
  }
  
  /**
   * Export the permeable pavers information as a csv.
   * @return The permeable pavers information as a csv.
   */
  @Security.Authenticated(Secured.class)
  public static Result exportPermeablePavers() {
    if (Secured.isLoggedIn(ctx()) && Secured.getUserInfo(ctx()).isAdmin()) {
      String output = "Title,Property_Type,Address,Description,Date_Installed,Material,Previous_Material,Size,"
          + "Installer,Owner_Email\n";
      for (PermeablePavers paver : PermeablePaversDB.getPermeablePavers()) {
        output += paver.formatToCSV();
      }
      return ok(output).as("text/csv");
    }
    return redirect(routes.Application.index());
  }

}

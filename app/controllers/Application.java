package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import com.google.common.io.Files;
import com.typesafe.plugin.*;

import org.mindrot.jbcrypt.BCrypt;

import play.Logger;
import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Security;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import models.*;
import views.formdata.*;
import views.html.*;

/**
 * Implements the controllers for this application.
 */
public class Application extends Controller {
  private static final long MAX_FILE_SIZE = 2621440;

  /**
   * Returns the home page. 
   * @return The resulting home page. 
   */
  public static Result errorReport(String s) {    
	  	return ok(ErrorReport.render(s));	  
  }
  
  /**
   * Returns the index page.
   * @return The index page.
   */
  public static Result index() {
    return ok(Index.render(IndexContentDB.getIndexContents(), HeaderFooterDB.getHeader(1), 
                           HeaderFooterDB.getSubHeader(1), HeaderFooterDB.getFooter(1),
                           HeaderFooterDB.getSubFooter(1)));
  }
  /**
   * Returns the user profile page. 
   * @return The resulting user profile page. 
   */
  public static Result profile(long id) {
	  UserInfo user = UserInfoDB.getUser(id);
	  /*if (Secured.isLoggedIn(ctx())) {
		  UserInfo current = Secured.getUserInfo(ctx());
		  if (current.getId() == user.getId()) {
			  return ok(Profile.render(user, true));
		  }
	  }
	  return ok(Profile.render(user, false));*/
	  return ok(Profile.render(user));
  }
  
  /**
   * Returns the edit profile page.
   * @return edit profile page
   */
  @Security.Authenticated(Secured.class)
  public static Result editProfile() {
	EditProfileFormData data = new EditProfileFormData(UserInfoDB.getUser(Secured.getUserInfo(ctx()).getId()));
	Form<EditProfileFormData> formData = Form.form(EditProfileFormData.class).fill(data);
	return ok(EditProfile.render(formData));
  }
  
  /**
   * Processes the edited profile form.
   * @return
   */
  @Security.Authenticated(Secured.class)
  public static Result postEditProfile() {	  
	  Form<EditProfileFormData> formData = Form.form(EditProfileFormData.class).bindFromRequest();
	  if (formData.hasErrors() == true) {
		  return badRequest(EditProfile.render(formData));
	  }
	  else {
		  EditProfileFormData data = formData.get();
		
		  UserInfo user = Secured.getUserInfo(ctx());
		  user.setFirstName(data.firstName);
		  user.setLastName(data.lastName);
		  user.setTelephone(data.telephone);
		  
		  if (data.change_email == true) {
			  session().clear();
			  
			  user.setEmail(data.email);
			  user.setConfirm(false);
			  
			  session("email", data.email);
		  }
		  if (data.change_pw == true) {
			  user.setPassword(BCrypt.hashpw(data.new_password, BCrypt.gensalt()));
		  }
		  
		  user.save();
		  
		  return redirect(routes.Application.profile(user.getId()));
	  }
  }
  /**
   * Open the admin control panel.
   * @return
   */
  @Security.Authenticated(Secured.class)
  public static Result adminPanel() {
	    /*IndexContentFormData data = (!Secured.isLoggedIn(ctx())) 
	        ? new IndexContentFormData() : new IndexContentFormData();
		  Form<IndexContentFormData> formData = Form.form(IndexContentFormData.class).fill(data);
		  if(Secured.isLoggedIn(ctx())){
			  if(Secured.getUserInfo(ctx()).isAdmin()){
				  return ok(EditIndexContent.render(formData,  UserInfoDB.getUser(Secured.getUser(ctx()))));
			  }
		  }
		  return redirect(routes.Application.errorReport("No admin logged in,open admin control panel fails."));*/
	  
	  if (Secured.getUserInfo(ctx()).isAdmin() == true) {
		  return ok(AdminPanel.render());
	  }
	  return redirect(routes.Application.index());
  }
  
 
  @Security.Authenticated(Secured.class)
  public static Result editIndexContentFormData() {
      if (Secured.isLoggedIn(ctx()) && Secured.getUserInfo(ctx()).isAdmin()) {
        HeaderFooterFormData data = (HeaderFooterDB.getHeaderFooter(1) == null) 
            ? new HeaderFooterFormData() : new HeaderFooterFormData(HeaderFooterDB.getHeaderFooter(1));
        Form<HeaderFooterFormData> formData = Form.form(HeaderFooterFormData.class).fill(data);
        return ok(EditIndexContent.render(formData,  UserInfoDB.getUser(Secured.getUser(ctx()))));         
      }
      else {
        return badRequest(ErrorReport.render(""));
      }
	  }
  
  /**
   * Processes the edited IndexContentFormData .
   * @return
   */
  @Security.Authenticated(Secured.class)
  public static Result postIndexContentFormData() throws IOException {
	  System.out.println("Post Edit");
	  
	  if(Secured.isLoggedIn(ctx())){
		  if(Secured.getUserInfo(ctx()).isAdmin()){
			  Form<HeaderFooterFormData> formData = Form.form(HeaderFooterFormData.class).bindFromRequest();
			  if (formData.hasErrors() == true) {
				  System.out.println("Edit profile Errors found.");
				  return badRequest(EditIndexContent.render(formData,  UserInfoDB.getUser(Secured.getUser(ctx()))));
			  }
			  else {
				  HeaderFooterFormData data = formData.get();
				 // System.out.println(data.firstName + " " + data.lastName + " " + data.email + " " + data.telephone + " " + data.password);
				  HeaderFooter hf = HeaderFooterDB.getHeaderFooter(1);
				  //create new userinfo and add it to the "database"
				  HeaderFooterDB.add(data);
				  MultipartFormData body = request().body().asMultipartFormData();
			      FilePart picture = body.getFile("uploadFile");
			      if (picture != null) {
				    	System.out.print("debug: check hasPicture.");
				    	hf.setHeaderImage(Files.toByteArray(picture.getFile()));
				    	hf.save();
				        System.out.print("debug: check saved Picture.");
			      }
			      /*else {
			          RainBarrelFormData data = formData.get();
			          RainBarrel barrel = RainBarrelDB.addRainBarrel(data, Secured.getUserInfo(ctx()));
			          MultipartFormData body = request().body().asMultipartFormData();
			          FilePart picture = body.getFile("uploadFile");
			          if (picture != null) {
			              File source = picture.getFile();
			              barrel.setImage(Files.toByteArray(source));
			              barrel.save();
			          }
			          return redirect(routes.Application.viewBarrel(barrel.getID()));
			      }*/   
				  
				  return redirect(routes.Application.editIndexContentFormData());
			  }
		  }
	  }
	  return redirect(routes.Application.errorReport("No admin logged in, post index content form data fails."));
  }
  /**
   * Returns the registration navigation menu.
   * @return The registration navigation menu page.
   */
  public static Result registerMenu() {
    return ok(RegisterMenu.render("Registration - Main"));
  }
  
  /**
   * Register new rain garden.
   * @return The rain garden registration form.
   */
  @Security.Authenticated(Secured.class)
  public static Result newRainGarden() {
	  if (Secured.getUserInfo(ctx()).isConfirmed()) {
    RainGardenFormData data = new RainGardenFormData();
    Form<RainGardenFormData> formData = Form.form(RainGardenFormData.class).fill(data);
    String url = routes.Application.retrieveGardenImage(data.id).url();
    return ok(RegisterRainGarden.render(formData, true, YesNoChoiceType.getChoiceList(), 
        PropertyTypes.getTypes(), DateTypes.getMonthTypes(), 
        DateTypes.getDayTypes(), DateTypes.getYearTypes(), 
        PlantTypes.getPlantMap(), RainGardenSizeTypes.getTypes(), 
        WaterSourceSizeTypes.getTypes(), 
        InfiltrationRateTypes.getTypes(), url, Secured.getUserInfo(ctx())));
	  }
	  return redirect(routes.Application.index());
  }
  
  /**
   * Returns the created/edited rain garden page.
   * @param isNew Specifies if the entry being processed is new or not.
   * @return The resulting rain garden page if information was valid, else the registration form.
   * @throws IOException When there is an issue when copying the file to the byte array.
   */
  @Security.Authenticated(Secured.class)
  public static Result postRainGardenRegister(boolean isNew) throws IOException {
    Form<RainGardenFormData> formData = Form.form(RainGardenFormData.class).bindFromRequest();
    validateGardenUpload(formData, request().body().asMultipartFormData());
    if (formData.hasErrors()) {
      Map<String, String> dataMap = formData.data();
      List<String> plantList = new ArrayList<String>();
      for (String key : dataMap.keySet()) {
        if (key.contains("plants")) {
          plantList.add(dataMap.get(key));
        }
      }
      Long id = Long.parseLong(dataMap.get("id"));
      String url = routes.Application.retrieveGardenImage(id).url();
      return badRequest(RegisterRainGarden.render(formData, isNew, YesNoChoiceType.getChoiceList(), 
                        PropertyTypes.getTypes(dataMap.get("propertyType")), 
                        DateTypes.getMonthTypes(dataMap.get("month")), 
                        DateTypes.getDayTypes(dataMap.get("day")), 
                        DateTypes.getYearTypes(dataMap.get("year")),
                        PlantTypes.getPlantMap(plantList),
                        RainGardenSizeTypes.getTypes(dataMap.get("rainGardenSize")),
                        WaterSourceSizeTypes.getTypes(dataMap.get("waterFlowSourceSize")),
                        InfiltrationRateTypes.getTypes(dataMap.get("infiltrationRate")),
                        url,
                        Secured.getUserInfo(ctx())));   
    } 
    else {
      RainGardenFormData data = formData.get();
      Logger.debug("GardenID: " + data.id);
      RainGarden garden = RainGardenDB.addRainGarden(data, Secured.getUserInfo(ctx()));    
      MultipartFormData body = request().body().asMultipartFormData();
      FilePart picture = body.getFile("uploadFile");
      if (picture != null) {
          File source = picture.getFile();
          garden.setImage(Files.toByteArray(source));
      }
      garden.save();
      
      sendAdminNotificationEmail(routes.Application.viewGarden(garden.getID()).absoluteURL(request()));
      
      return redirect(routes.Application.viewGarden(garden.getID()));
     }     
    }
  
  /**
   * Manage information for a rain garden.
   * @param id The ID of the rain garden to manage.
   * @return The rain garden edit form.
   */
  @Security.Authenticated(Secured.class)
  public static Result manageRainGarden(long id) {
    if (RainGardenDB.hasID(id)) {  
      RainGarden garden = RainGardenDB.getRainGarden(id);
      if (Secured.isLoggedIn(ctx()) && garden.canEdit(Secured.getUserInfo(ctx()))) {
        RainGardenFormData data = new RainGardenFormData(garden);
        Form<RainGardenFormData> formData = Form.form(RainGardenFormData.class).fill(data);
        String url = routes.Application.retrieveGardenImage(id).url();
        return ok(RegisterRainGarden.render(formData, false, YesNoChoiceType.getChoiceList(), 
                  PropertyTypes.getTypes(data.propertyType), DateTypes.getMonthTypes(data.month), 
                  DateTypes.getDayTypes(data.day), DateTypes.getYearTypes(data.year), 
                  PlantTypes.getPlantMap(data.plants), RainGardenSizeTypes.getTypes(data.rainGardenSize), 
                  WaterSourceSizeTypes.getTypes(data.waterFlowSourceSize), 
                  InfiltrationRateTypes.getTypes(data.infiltrationRate), url, Secured.getUserInfo(ctx())));        
      }
    }
    return redirect(routes.Application.viewGarden(id));
  }
    
  /**
   * Register a new rain barrel.
   * @return The rain barrel registration form.
   */
  @Security.Authenticated(Secured.class)
  public static Result newRainBarrel() {
	  if (Secured.getUserInfo(ctx()).isConfirmed() == true) {
    RainBarrelFormData data = new RainBarrelFormData();
    Form<RainBarrelFormData> formData = Form.form(RainBarrelFormData.class).fill(data);
    String url = routes.Application.retrieveBarrelImage(data.id).url();
    return ok(RegisterRainBarrel.render(formData, true, YesNoChoiceType.getChoiceList(), 
              PropertyTypes.getTypes(), DateTypes.getMonthTypes(), 
              DateTypes.getDayTypes(), DateTypes.getYearTypes(), 
              RainBarrelTypes.getRainBarrelTypes(), MaterialTypes.getMaterialTypes(),
              WaterUsageTypes.getWaterUsageTypes(), CoverTypes.getCoverTypes(), 
              InstallationTypes.getInstallationTypes(),
              RainBarrelCapacityTypes.getTypes(),
              url));
	  }
	  return redirect(routes.Application.index());
  }
  
  /**
   * Returns the created/edited rain barrel page.
   * @param isNew Specifies if the entry is a new rain barrel.
   * @return The resulting rain barrel page if information was valid, else the registration form.
   * @throws IOException When there is an issue when copying the file to the byte array.
   */
  @Security.Authenticated(Secured.class)
  public static Result postRainBarrelRegister(boolean isNew) throws IOException {
    Form<RainBarrelFormData> formData = Form.form(RainBarrelFormData.class).bindFromRequest();
    validateBarrelUpload(formData, request().body().asMultipartFormData());
    if (formData.hasErrors()) {
      Map<String, String> dataMap = formData.data();
      Long id = Long.parseLong(dataMap.get("id"));
      String url = routes.Application.retrieveBarrelImage(id).url();
      return badRequest(RegisterRainBarrel.render(formData, isNew, YesNoChoiceType.getChoiceList(), 
                        PropertyTypes.getTypes(dataMap.get("propertyType")), 
                        DateTypes.getMonthTypes(dataMap.get("month")), 
                        DateTypes.getDayTypes(dataMap.get("day")), 
                        DateTypes.getYearTypes(dataMap.get("year")),
                        RainBarrelTypes.getRainBarrelTypes(dataMap.get("rainBarrelType")),
                        MaterialTypes.getMaterialTypes(dataMap.get("material")),
                        WaterUsageTypes.getWaterUsageTypes(dataMap.get("waterUse")),
                        CoverTypes.getCoverTypes(dataMap.get("cover")),
                        InstallationTypes.getInstallationTypes(dataMap.get("installationType")),
                        RainBarrelCapacityTypes.getTypes(dataMap.get("capacity")),
                        url));  
    } 
    else {
      RainBarrelFormData data = formData.get();
      RainBarrel barrel = RainBarrelDB.addRainBarrel(data, Secured.getUserInfo(ctx()));
      MultipartFormData body = request().body().asMultipartFormData();
      FilePart picture = body.getFile("uploadFile");
      if (picture != null) {
          File source = picture.getFile();
          barrel.setImage(Files.toByteArray(source));
      }
      barrel.save();
      
      sendAdminNotificationEmail(routes.Application.viewBarrel(barrel.getID()).absoluteURL(request()));
      
      return redirect(routes.Application.viewBarrel(barrel.getID()));
     }     
    }
  
  /**
   * Manage information for a rain barrel.
   * @param id The ID of the rain barrel to manage.
   * @return The rain barrel edit form.
   */
  @Security.Authenticated(Secured.class)
  public static Result manageRainBarrel(long id) {
    if (RainBarrelDB.hasID(id)) {
      RainBarrel barrel = RainBarrelDB.getRainBarrel(id);
      if (Secured.isLoggedIn(ctx()) && barrel.canEdit(Secured.getUserInfo(ctx()))) {
        RainBarrelFormData data = new RainBarrelFormData(barrel);
        Form<RainBarrelFormData> formData = Form.form(RainBarrelFormData.class).fill(data);
        String url = routes.Application.retrieveBarrelImage(id).url();
        return ok(RegisterRainBarrel.render(formData, false, YesNoChoiceType.getChoiceList(), 
                  PropertyTypes.getTypes(data.propertyType), DateTypes.getMonthTypes(data.month), 
                  DateTypes.getDayTypes(data.day), DateTypes.getYearTypes(data.year), 
                  RainBarrelTypes.getRainBarrelTypes(data.rainBarrelType), 
                  MaterialTypes.getMaterialTypes(data.material), WaterUsageTypes.getWaterUsageTypes(data.waterUse), 
                  CoverTypes.getCoverTypes(data.cover), InstallationTypes.getInstallationTypes(data.installationType),
                  RainBarrelCapacityTypes.getTypes(data.capacity),
                  url));      
      }
    }
    return redirect(routes.Application.viewBarrel(id));
  }
  
  /**
   * Register a new permeable paver.
   * @return The permeable paver registration form.
   */
  @Security.Authenticated(Secured.class)
  public static Result newPermeablePavers() {
	  if (Secured.getUserInfo(ctx()).isConfirmed() == true) {
    PermeablePaversFormData data = new PermeablePaversFormData();
    Form<PermeablePaversFormData> formData = Form.form(PermeablePaversFormData.class).fill(data);
    String url = routes.Application.retrievePaverImage(data.id).url();
    return ok(RegisterPermeablePavers.render(formData, true, YesNoChoiceType.getChoiceList(), 
              PropertyTypes.getTypes(), DateTypes.getMonthTypes(), 
              DateTypes.getDayTypes(), DateTypes.getYearTypes(), 
              PaverMaterialTypes.getMaterialTypes(), 
              PaverMaterialTypes.getMaterialTypes(), 
              PermeablePaversSizeTypes.getTypes(), url, Secured.getUserInfo(ctx())));
	  }
	  return redirect(routes.Application.index());
  }
  
  /**
   * Returns the created/edited permeable pavers page.
   * @param isNew Specifies if the permeable pavers is new or not.
   * @return The resulting permeable pavers page if information was valid, else the registration form.
   * @throws IOException When there is an issue when copying the file to the byte array.
   */
  @Security.Authenticated(Secured.class)
  public static Result postPermeablePaversRegister(boolean isNew) throws IOException {
    Form<PermeablePaversFormData> formData = Form.form(PermeablePaversFormData.class).bindFromRequest();
    validatePaverUpload(formData, request().body().asMultipartFormData());
    if (formData.hasErrors()) {
      Map<String, String> dataMap = formData.data();
      Long id = Long.parseLong(dataMap.get("id"));
      String url = routes.Application.retrievePaverImage(id).url();
      return badRequest(RegisterPermeablePavers.render(formData, isNew,
                        YesNoChoiceType.getChoiceList(), 
                        PropertyTypes.getTypes(dataMap.get("propertyType")), 
                        DateTypes.getMonthTypes(dataMap.get("month")), 
                        DateTypes.getDayTypes(dataMap.get("day")), 
                        DateTypes.getYearTypes(dataMap.get("year")),
                        PaverMaterialTypes.getMaterialTypes(dataMap.get("material")),
                        PaverMaterialTypes.getMaterialTypes(dataMap.get("previousMaterial")),
                        PermeablePaversSizeTypes.getTypes(dataMap.get("size")),
                        url,
                        Secured.getUserInfo(ctx())));   
    } 
    else {
      PermeablePaversFormData data = formData.get();
      PermeablePavers paver = PermeablePaversDB.addPermeablePavers(data, Secured.getUserInfo(ctx()));
      MultipartFormData body = request().body().asMultipartFormData();
      FilePart picture = body.getFile("uploadFile");
      if (picture != null) {
          File source = picture.getFile();
          paver.setImage(Files.toByteArray(source));
      }
      paver.save();
      
      sendAdminNotificationEmail(routes.Application.viewPaver(paver.getID()).absoluteURL(request()));
      
      return redirect(routes.Application.viewPaver(paver.getID()));
     }     
    }
  
  /**
   * Manage information for a permeable paver.
   * @param id The ID of the permeable paver to manage.
   * @return The rain barrel edit form.
   */
  @Security.Authenticated(Secured.class)
  public static Result managePermeablePavers(long id) {
    if (PermeablePaversDB.hasID(id)) {
      PermeablePavers paver = PermeablePaversDB.getPermeablePavers(id);
      if (Secured.isLoggedIn(ctx()) && paver.canEdit(Secured.getUserInfo(ctx()))) {
            PermeablePaversFormData data = new PermeablePaversFormData(paver);
            Form<PermeablePaversFormData> formData = Form.form(PermeablePaversFormData.class).fill(data);
            String url = routes.Application.retrievePaverImage(id).url();
            return ok(RegisterPermeablePavers.render(formData, false, YesNoChoiceType.getChoiceList(), 
                      PropertyTypes.getTypes(data.propertyType), DateTypes.getMonthTypes(data.month), 
                      DateTypes.getDayTypes(data.day), DateTypes.getYearTypes(data.year), 
                      PaverMaterialTypes.getMaterialTypes(data.material), 
                      PaverMaterialTypes.getMaterialTypes(data.previousMaterial), 
                      PermeablePaversSizeTypes.getTypes(data.size), url, Secured.getUserInfo(ctx())));     
      }
    }
    return redirect(routes.Application.index());
  }
  
  /**
   * View garden page.
   * @param id ID of garden to view.
   * @return The garden view page of the rain garden matching the given ID. 
   */
  public static Result viewGarden(Long id) {
    RainGarden garden = RainGardenDB.getRainGarden(id);
    CommentFormData commentFormData = new CommentFormData();
    Form<CommentFormData> commentForm = Form.form(CommentFormData.class).fill(commentFormData);
    if (garden != null) {
      if (garden.isApproved()) {
        return ok(ViewGarden.render(garden, commentForm));
      }
      else if (Secured.isLoggedIn(ctx()) 
               && (garden.isOwner(Secured.getUserInfo(ctx())) || Secured.getUserInfo(ctx()).isAdmin())) {
        return ok(ViewGarden.render(garden, commentForm));        
      }
    }
    return redirect(routes.Application.gardengallery());
  }
  
  /**
   * Delete a garden from application.
   * @param id ID of garden to delete.
   * @return The index page.
   */
  @Security.Authenticated(Secured.class)
  public static Result deleteGarden(Long id) {
    if (RainGardenDB.hasID(id)) {
      RainGarden garden = RainGardenDB.getRainGarden(id);
      if (garden != null) {
        RainGardenDB.deleteRainGarden(garden.getID());
      }
    }
    if (Secured.isLoggedIn(ctx()) && Secured.getUserInfo(ctx()).isAdmin()) {
      return redirect(routes.Application.viewSolutions());
    }
    return ok(Profile.render(Secured.getUserInfo(ctx())));
  }
  
  /**
   * View barrel page.
   * @param id ID of barrel to view.
   * @return The barrel view page of the rain barrel matching the given ID. 
   */
  public static Result viewBarrel(Long id) {
    RainBarrel barrel = RainBarrelDB.getRainBarrel(id);
    CommentFormData commentFormData = new CommentFormData();
    Form<CommentFormData> commentForm = Form.form(CommentFormData.class).fill(commentFormData);
    if (barrel != null) {
      if (barrel.isApproved()) {
        return ok(ViewBarrel.render(barrel, commentForm));
      }
      else if (Secured.isLoggedIn(ctx()) 
               && (barrel.isOwner(Secured.getUserInfo(ctx())) || Secured.getUserInfo(ctx()).isAdmin())) {
        return ok(ViewBarrel.render(barrel, commentForm));
        
      }
    }
    return redirect(routes.Application.barrelgallery());
  }
  
  /**
   * Delete a barrel from application.
   * @param id ID of barrel to delete.
   * @return The index page.
   */
  @Security.Authenticated(Secured.class)
  public static Result deleteBarrel(Long id) {
    if (RainBarrelDB.hasID(id)) {
      RainBarrel barrel = RainBarrelDB.getRainBarrel(id);
      if (barrel != null) {
        RainBarrelDB.deleteRainBarrel(id);
      }
    }
    if (Secured.isLoggedIn(ctx()) && Secured.getUserInfo(ctx()).isAdmin()) {
      return redirect(routes.Application.viewSolutions());
    }
    return ok(Profile.render(Secured.getUserInfo(ctx())));
  }
  
  /**
   * View paver page.
   * @param id ID of paver to view.
   * @return The paver view page of the permeable paver matching the given ID. 
   */
  public static Result viewPaver(Long id) {
    PermeablePavers paver = PermeablePaversDB.getPermeablePavers(id);
    CommentFormData commentFormData = new CommentFormData();
    Form<CommentFormData> commentForm = Form.form(CommentFormData.class).fill(commentFormData);
    if (paver != null) {
      if (paver.isApproved()) {
        return ok(ViewPaver.render(paver, commentForm));
      }
      else if (Secured.isLoggedIn(ctx()) 
               && (paver.isOwner(Secured.getUserInfo(ctx())) || Secured.getUserInfo(ctx()).isAdmin())) {
        return ok(ViewPaver.render(paver, commentForm));
        
      }
    }
    return redirect(routes.Application.pavergallery());
  }
  
  /**
   * Delete a paver from application.
   * @param id ID of paver to delete.
   * @return The owner's profile page.
   */
  @Security.Authenticated(Secured.class)
  public static Result deletePaver(Long id) {
    if (PermeablePaversDB.hasID(id)) {
      PermeablePavers paver = PermeablePaversDB.getPermeablePavers(id);
      if (paver != null) {
        PermeablePaversDB.deletePermeablePaver(paver.getID());
      }
    }
    if (Secured.isLoggedIn(ctx()) && Secured.getUserInfo(ctx()).isAdmin()) {
      return redirect(routes.Application.viewSolutions());
    }
    return ok(Profile.render(Secured.getUserInfo(ctx())));
  }
  
  /**
   * Returns page1, a simple example of a second page to illustrate navigation.
   * @return The Page1.
   */
  @Security.Authenticated(Secured.class)
  public static Result page1() {
    return ok(Page1.render("Welcome to Page1.", Secured.getUserInfo(ctx())));
  }
  
  /**
   * Returns the sign in page.
   * @return sign in page
   */
  public static Result signup() {
	  Form<SignUpFormData> formData = Form.form(SignUpFormData.class);
	  return ok(SignUp.render(formData));
  }
 
 
  /**
   * Processes the sign up form.
   * @return
   */
  public static Result postSignUp() {
	  //System.out.println("Post Sign Up");
	  Form<SignUpFormData> formData = Form.form(SignUpFormData.class).bindFromRequest();
	  if (formData.hasErrors() == true) {
		  //System.out.println("Sign up Errors found.");
		  return badRequest(SignUp.render(formData));
	  }
	  else {
		  SignUpFormData data = formData.get();
		  //System.out.println(data.firstName + " " + data.lastName + " " + data.email + " " + data.telephone + " " + data.password);
		  
		  //create new userinfo and add it to the "database"
		  long id = UserInfoDB.addUserInfo(data.firstName, data.lastName, data.email, data.telephone, BCrypt.hashpw(data.password, BCrypt.gensalt()), false, false);
		  
		  //log in user
		  session().clear();
		  session("email", data.email);
		  
		  //return redirect(routes.Application.login(routes.Application.index().url()));
		  return redirect(routes.Application.thankYou(id));
	  }
  }
  
  /**
   * Sends a confirmation email to the user with the given id
   * @param id
   */
  private static void sendConfirmationEmail(long id) {
	  UserInfo user = UserInfoDB.getUser(id);
	  
	  try {
		  SecureRandom random = new SecureRandom();
		  byte bytes[] = new byte[20];
		  random.nextBytes(bytes);
		  String message = bytes.toString();
		  
		  MessageDigest md = MessageDigest.getInstance("MD5");
		  byte[] hash = md.digest(message.getBytes("UTF-8"));
		  StringBuffer buffer = new StringBuffer();
		  for (int i = 0; i < hash.length; i++){
			  buffer.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
		  }
		  user.setConfirmHash(buffer.toString());
		  user.save();
	  }
	  catch (UnsupportedEncodingException ex) {
		  System.err.println(ex.getStackTrace());
	  }
	  catch (NoSuchAlgorithmException ex) {
		  System.err.println(ex.getStackTrace());
	  }
	  
	  //send email
	  MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
	  mail.setSubject("Sign up Email Confirmation");
	  mail.setRecipient(user.getEmail());
	  mail.setFrom("openraingarden@gmail.com");
	  
	  String url = routes.Application.index().absoluteURL(request()) + "confirm/" + id + "/" + user.getConfirmHash();
	  //System.out.println(url);
	  
	  mail.send("Please confirm your registration at our website by following the below link: " + url + " \nPlease ignore this email if you did not register at " + routes.Application.index().absoluteURL(request()));
  }
  
  /**
   * Returns the Thank You page
   * @return
   */
  @Security.Authenticated(Secured.class)
  public static Result thankYou(long id) {
	  if (Secured.getUserInfo(ctx()).getId() == id) {
		  sendConfirmationEmail(id);
		  return ok(ThankYou.render(id));
	  }
	  return redirect(routes.Application.index());
  }
  
  /**
   * Returns the send email confirmation 
   * @return
   */
  @Security.Authenticated(Secured.class)
  public static Result emailConfirmSend() {
	  UserInfo user = Secured.getUserInfo(ctx());
	  
	  if (user.isConfirmed() == false) {
		  sendConfirmationEmail(user.getId());
		  return ok(SendEmailConfirm.render());
	  }
	  else {
		  return ok(EmailConfirm.render(true));
	  }
  }
  
  /**
   * Returns the email confirmation page
   * @param id
   * @param hash
   * @return
   */
  public static Result emailConfirm(long id, String hash) {
	  UserInfo user = UserInfoDB.getUser(id);
	  if (user != null && user.getConfirmHash() != null && user.getConfirmHash().equals(hash) == true) {
		  user.setConfirm(true);
		  user.save();
		  return ok(EmailConfirm.render(true));
	  }
	  return ok(EmailConfirm.render(false));
  }
  
  /**
   * Returns login page
   * @return Log in
   */
  public static Result login(String target) {    
	  Form<LoginFormData> formData = Form.form(LoginFormData.class);
	  return ok(Login.render("Login", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), formData, target));
  }
  
  /**
   * Processes the log in form
   * @return
   */
  public static Result postLogin(String target) {
	  //System.out.println("Post Login");
	  Form<LoginFormData> formData = Form.form(LoginFormData.class).bindFromRequest();
	  
	  if (formData.hasErrors() == true) {
		  //System.out.println("Login errors found");
		  flash("error", "Login credentials not valid.");
		  return badRequest(Login.render("Login", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), formData, target));
	  }
	  else {
		  session().clear();
		  session("email", formData.get().email);
		  return redirect(target);
	  }
  }
  
  /**
   * Logout the current user
   * @return the index page
   */
  @Security.Authenticated(Secured.class)
  public static Result logout() {
	  session().clear();
	  return redirect(routes.Application.index());
  }
  
  /**
   * Returns the Learn More page
   * @return
   */
  public static Result learnmore() {
      return ok(LearnMore.render(ResourceDB.getGardenResources(), ResourceDB.getBarrelResources(), ResourceDB.getPaverResources()));
  }
  
  /**
   * Validate a given form's upload file.
   * @param formData The form to check.
   * @param body Multipart form data that holds the uploaded file to check.
   * @return A form with validated upload data.
   */
  private static Form<RainGardenFormData> validateGardenUpload(Form<RainGardenFormData> formData, 
      MultipartFormData body) {
    List<ValidationError> errors = new ArrayList<ValidationError>();
    if (formData.errors().get("uploadFile") != null) {
      errors = formData.errors().get("uploadFile");
    }
    FilePart picture = body.getFile("uploadFile"); 
    if (picture != null) {
      if (!(picture.getContentType().contains("image"))) {
        errors.add(new ValidationError("uploadFile", "The file \"" + picture.getFilename() + "\" is not an accepted "
            + "file type. Please select a file with the extension .jpg/.jpeg or .png"));
      }
      if (picture.getFile().length() > MAX_FILE_SIZE) {
        errors.add(new ValidationError("uploadFile", "The file \"" + picture.getFilename() + "\" is too large. "
            + "Please select a file that is under 2.5 MB"));
      }
    }
    if (!errors.isEmpty()) {
      formData.errors().put("uploadFile", errors);
    }
    return formData;
  }
  
  /**
   * Validate a given form's upload file.
   * @param formData The form to check.
   * @param body Multipart form data that holds the uploaded file to check.
   * @return A form with validated upload data.
   */
  private static Form<RainBarrelFormData> validateBarrelUpload(Form<RainBarrelFormData> formData,
      MultipartFormData body) {
    List<ValidationError> errors = new ArrayList<ValidationError>();
    if (formData.errors().get("uploadFile") != null) {
      errors = formData.errors().get("uploadFile");
    }
    FilePart picture = body.getFile("uploadFile"); 
    if (picture != null) {
      if (!(picture.getContentType().contains("image"))) {
        errors.add(new ValidationError("uploadFile", "The file \"" + picture.getFilename() + "\" is not an accepted "
            + "file type. Please select a file with the extension .jpg/.jpeg or .png"));
      }
      if (picture.getFile().length() > MAX_FILE_SIZE) {
        errors.add(new ValidationError("uploadFile", "The file \"" + picture.getFilename() + "\" is too large. "
            + "Please select a file that is under 2.5 MB"));
      }
    }
    if (!errors.isEmpty()) {
      formData.errors().put("uploadFile", errors);
    }
    return formData;
  }
  
  /**
   * Validate a given form's upload file.
   * @param formData The form to check.
   * @param body Multipart form data that holds the uploaded file to check.
   * @return A form with validated upload data.
   */
  private static Form<PermeablePaversFormData> validatePaverUpload(Form<PermeablePaversFormData> formData,
      MultipartFormData body) {
    List<ValidationError> errors = new ArrayList<ValidationError>();
    if (formData.errors().get("uploadFile") != null) {
      errors = formData.errors().get("uploadFile");
    }
    FilePart picture = body.getFile("uploadFile"); 
    if (picture != null) {
      if (!(picture.getContentType().contains("image"))) {
        errors.add(new ValidationError("uploadFile", "The file \"" + picture.getFilename() + "\" is not an accepted "
            + "file type. Please select a file with the extension .jpg/.jpeg or .png"));
      }
      if (picture.getFile().length() > MAX_FILE_SIZE) {
        errors.add(new ValidationError("uploadFile", "The file \"" + picture.getFilename() + "\" is too large. "
            + "Please select a file that is under 2.5 MB"));
      }
    }
    if (!errors.isEmpty()) {
      formData.errors().put("uploadFile", errors);
    }
    return formData;
  }
  
  /**
   * Returns the rain garden gallery page
   * @return
   */
  public static Result gardengallery() {
	  List<RainGarden> gardens = RainGardenDB.getRainGardens();
	  Collections.shuffle(gardens);
	  return ok(RainGardenGallery.render(gardens));
  }
  
  /**
   * Returns the rain barrel gallery page
   * @return
   */
  public static Result barrelgallery() {
	  List<RainBarrel> barrels = RainBarrelDB.getRainBarrels();
	  Collections.shuffle(barrels);
	  return ok(RainBarrelGallery.render(barrels));
  }
  
  /**
   * returns the permeable pavers gallery page
   * @return
   */
  public static Result pavergallery() {
	  List<PermeablePavers> pavers = PermeablePaversDB.getPermeablePavers();
	  Collections.shuffle(pavers);
	  return ok(PermeablePaverGallery.render(pavers));
  }
  
  /**
   * returns the map page
   * @return
   */
  public static Result map() {
	  return ok(MapPage.render("Map"));
  }
  
  /**
   * Post a comment.
   * @param id ID of the garden being commented on. 
   * @param uri Target of redirect.
   * @return The page that the user was commenting on.
   */
  @Security.Authenticated(Secured.class)
  public static Result postGardenComment(Long id, String uri) {
    Form<CommentFormData> formData = Form.form(CommentFormData.class).bindFromRequest();
    if (formData.hasErrors()) {
      return redirect(uri);
    }
    CommentFormData data = formData.get();
    CommentDB.addComment(data, RainGardenDB.getRainGarden(id), Secured.getUserInfo(ctx()));
    return redirect(uri);
  }
  
  /**
   * Post a comment.
   * @param id ID of the solution being commented on.
   * @param uri Target of redirect.
   * @return The page that the user was commenting on.
   */
  @Security.Authenticated(Secured.class)
  public static Result postBarrelComment(Long id, String uri) {
    Form<CommentFormData> formData = Form.form(CommentFormData.class).bindFromRequest();
    if (formData.hasErrors()) {
      return redirect(uri);
    }
    CommentFormData data = formData.get();
    CommentDB.addComment(data, RainBarrelDB.getRainBarrel(id), Secured.getUserInfo(ctx()));
    return redirect(uri);
  }
  
  /**
   * Post a comment.
   * @param id ID of the solution being commented on.
   * @param uri Target of redirect.
   * @return The page that the user was commenting on.
   */
  @Security.Authenticated(Secured.class)
  public static Result postPaverComment(Long id, String uri) {
    Form<CommentFormData> formData = Form.form(CommentFormData.class).bindFromRequest();
    if (formData.hasErrors()) {
      return redirect(uri);
    }
    CommentFormData data = formData.get();
    CommentDB.addComment(data, PermeablePaversDB.getPermeablePavers(id), Secured.getUserInfo(ctx()));
    return redirect(uri);
  }
  
  public static Result retrieveHomeBannerImage() {
	  HeaderFooter headerfooter = HeaderFooterDB.getHeaderFooter(1);
	    if (headerfooter != null && headerfooter.hasPicture()) {      
	      return ok(HeaderFooterDB.getHeaderFooter(1).getHeaderImage()).as("image/jpeg");
	    }
	    return redirect(routes.Assets.at("images/index_banner.jpg"));
  }
  
  /**
   * Retrieve a garden image.
   * @param id The ID of the garden to retrieve the image from.
   * @return The image matching the ID given.
   */
  public static Result retrieveGardenImage(long id) {
    RainGarden garden = RainGardenDB.getRainGarden(id);
    if (garden != null) {
      if (garden.hasPicture()) {
        return ok(RainGardenDB.getRainGarden(id).getImage()).as("image/jpeg");
      }
      else if (garden.getExternalImageURL() != null && !(garden.getExternalImageURL().isEmpty())) {
        return redirect(garden.getExternalImageURL());
      }
    }
    return redirect(routes.Assets.at("images/placeholder.gif"));
  }
  
  /**
   * Retrieve a paver image.
   * @param id The ID of the paver to retrieve the image from.
   * @return The image matching the ID given.
   */
  public static Result retrieveBarrelImage(long id) {
    RainBarrel barrel = RainBarrelDB.getRainBarrel(id);
    if (barrel != null) {
      if (barrel.hasPicture()) {
        return ok(RainBarrelDB.getRainBarrel(id).getImage()).as("image/jpeg");
      }
      else if (barrel.getExternalImageURL() != null && !barrel.getExternalImageURL().isEmpty()) {
        return redirect(barrel.getExternalImageURL());
      }
    }
    return redirect(routes.Assets.at("images/placeholder.gif"));
  }
  
  /**
   * Retrieve a paver image.
   * @param id The ID of the paver to retrieve the image from.
   * @return The image matching the ID given.
   */
  public static Result retrievePaverImage(long id) {
    PermeablePavers paver = PermeablePaversDB.getPermeablePavers(id);
    if (paver != null) {
      if (paver.hasPicture()) {
        return ok(PermeablePaversDB.getPermeablePavers(id).getImage()).as("image/jpeg");
      }
      else if (paver.getExternalImageURL() != null && !paver.getExternalImageURL().isEmpty()) {
        return redirect(paver.getExternalImageURL());
      }
    }
    return redirect(routes.Assets.at("images/placeholder.gif"));
  }
  
  /**
   * Retrieve a plant image.
   * @param plantName The name of the plant to retrieve the image from.
   * @return The image matching the plant name given.
   */
  public static Result retrievePlantImage(String plantName) {
    Plant plant = PlantDB.getPlant(plantName);
    if (plant != null) {
      if (plant.hasPicture()) {
        return ok(plant.getImage()).as("image/jpeg");
      }
      else if (plant.isInitialPlant()) {
        return redirect(routes.Assets.at("plants/" + plant.getPictureName()));
      }
    }
    return redirect(routes.Assets.at("images/placeholder.gif"));
  }
  
  /**
   * Register new plant.
   * @return The plant registration form.
   */
  @Security.Authenticated(Secured.class)
  public static Result newPlant() {
    PlantFormData data = new PlantFormData();
    Form<PlantFormData> formData = Form.form(PlantFormData.class).fill(data);
    return ok(RegisterPlant.render(formData, true, PlantFormDropdownTypes.getPlacementTypes(), 
                                   PlantFormDropdownTypes.getGrowthTypes(), PlantFormDropdownTypes.getClimateTypes(),
                                   "/assets/images/placeholder.gif"));
  }
  
  /**
   * Handles form submission for plant registration/edit.
   * @param isNew Specifies if the plant is new or not.
   * @return The plant listing, else the registration form.
   * @throws IOException When there is an issue when copying the file to the byte array.
   */
  @Security.Authenticated(Secured.class)
  public static Result postPlantRegistration(boolean isNew) throws IOException {
    Form<PlantFormData> formData = Form.form(PlantFormData.class).bindFromRequest();
    validatePlantUpload(formData, request().body().asMultipartFormData());
    if (formData.hasErrors()) {
      Map<String, String> dataMap = formData.data();
      String url = routes.Application.retrievePlantImage(dataMap.get("name")).toString();
      return badRequest(RegisterPlant.render(formData, isNew, 
                                             PlantFormDropdownTypes.getPlacementTypes(dataMap.get("placement")), 
                                             PlantFormDropdownTypes.getGrowthTypes(dataMap.get("growth")), 
                                             PlantFormDropdownTypes.getClimateTypes(dataMap.get("climateType")),
                                             url));   
    } 
    else {      
      PlantFormData data = formData.get();
      MultipartFormData body = request().body().asMultipartFormData();
      FilePart picture = body.getFile("uploadFile");
      Plant plant = PlantDB.addPlant(data);
      if (picture != null) {
        plant.setImage(Files.toByteArray(picture.getFile()));
      }
      plant.save();
      return redirect(routes.Application.viewPlants());
     }     
    }
  
  /**
   * Manage a plant's information.
   * 
   * @param plantName The name of the plant to manage.
   * @return The plant edit form.
   */
  @Security.Authenticated(Secured.class)
  public static Result managePlant(String plantName) {
    if (PlantDB.hasName(plantName)) {
      if (Secured.isLoggedIn(ctx()) && Secured.getUserInfo(ctx()).isAdmin()) {
        PlantFormData data = new PlantFormData(PlantDB.getPlant(plantName));
        data.hasPicture = true;
        Form<PlantFormData> formData = Form.form(PlantFormData.class).fill(data);
        return ok(RegisterPlant.render(formData, false, PlantFormDropdownTypes.getPlacementTypes(data.placement), 
                                       PlantFormDropdownTypes.getGrowthTypes(data.growth), 
                                       PlantFormDropdownTypes.getClimateTypes(data.climateType),
                                       routes.Application.retrievePlantImage(data.name).absoluteURL(request())));
      }
    }
    else if (!PlantDB.hasName(plantName)) {
      return badRequest(ErrorReport.render("\"" + request().host() + request().uri() + "\" is not a valid URL."));
    }
    return badRequest(ViewPlant.render("", Secured.getUserInfo(ctx())));
  }
  
  /**
   * Validate a given form's upload file.
   * @param formData The form to check.
   * @param body Multipart form data that holds the uploaded file to check.
   * @return A form with validated upload data.
   */
  private static Form<PlantFormData> validatePlantUpload(Form<PlantFormData> formData, MultipartFormData body) {
    List<ValidationError> errors = new ArrayList<ValidationError>();
    if (formData.errors().get("uploadFile") != null) {
      errors = formData.errors().get("uploadFile");
    }
    FilePart picture = body.getFile("uploadFile"); 
    if (picture != null) {
      if (!(picture.getContentType().contains("image"))) {
        errors.add(new ValidationError("uploadFile", "The file \"" + picture.getFilename() + "\" is not an accepted "
            + "file type. Please select a file with the extension .jpg/.jpeg or .png"));
      }
      if (picture.getFile().length() > MAX_FILE_SIZE) {
        errors.add(new ValidationError("uploadFile", "The file \"" + picture.getFilename() + "\" is too large. "
            + "Please select a file that is under 2.5 MB"));
      }
    }
    if (!errors.isEmpty()) {
      formData.errors().put("uploadFile", errors);
    }
    return formData;
  }
  
  /**
   * Shows all the plants registered in the plant database.
   * @return A page showing all the plants in the database.
   */
  public static Result viewPlants() {
    return ok(ViewPlant.render("", Secured.getUserInfo(ctx())));
  }
  
  /**
   * Delete a plant from the system.
   * @param name Name of the plant to delete.
   * @return The view plant page.
   */
  public static Result deletePlant(long id) {
    PlantDB.deletePlant(id);
    return ok(ViewPlant.render("", Secured.getUserInfo(ctx())));
  }
  
  /**
   * Returns the edit form for the resource matching the given header
   * @param header
   * @return
   */
  @Security.Authenticated(Secured.class)
  public static Result editResource(String find, Long id) {
	  if (Secured.getUserInfo(ctx()).isAdmin() == true) {
		  ResourceFormData data;
		  //System.out.println("ID = " + id);
		  if (id == -1) {
			  data = new ResourceFormData();
		  }
		  else {
			  data = new ResourceFormData(ResourceDB.getResource(id));
		  }
		  
		  Form<ResourceFormData> formData = Form.form(ResourceFormData.class).fill(data);
		  return ok(EditResource.render(formData, find));
	  }
	  return redirect(routes.Application.index());
  }
  
  /**
   * Processes the edit Resource form
   * @param find
   * @return
   * @throws IOException
   */
  @Security.Authenticated(Secured.class)
  public static Result postEditResource(String find) throws IOException{
	  if (Secured.getUserInfo(ctx()).isAdmin() == true) {
		  Form<ResourceFormData> formData = Form.form(ResourceFormData.class).bindFromRequest();
		  validateResourceUpload(formData, request().body().asMultipartFormData());
		  if (formData.hasErrors() == true) {
			  //System.out.println("Errors found in edit resource form");
			  return badRequest(EditResource.render(formData, find));
		  }
		  else {
			  //System.out.println("Post Edit Resource");
			  ResourceFormData data = formData.get();			  
			  MultipartFormData body = request().body().asMultipartFormData();
			  FilePart picture = body.getFile("uploadFile");
			  
			  Resource resource = null;
			  if (find.equals("garden") ==  true) {
				  resource = ResourceDB.addGardenResource(data);
			  }
			  else if (find.equals("barrel") == true) {
				  resource = ResourceDB.addBarrelResource(data);
			  }
			  else if (find.equals("paver") == true) {
				  resource = ResourceDB.addPaverResource(data);
			  }
			  
			  if (picture != null) {
				  //System.out.println("Setting resource picture");
				  resource.setImage(Files.toByteArray(picture.getFile()));
				  resource.save();
			  }
			  return redirect(routes.Application.adminPanel());
		  }
	  }
	  return redirect(routes.Application.index());
  }
  
  /**
   * Deletes the resource with the given id, if admin
   * @param id
   * @return adminPanel if admin or index otherwise
   */
  @Security.Authenticated(Secured.class)
  public static Result deleteResource(long id) {
	  if (Secured.getUserInfo(ctx()).isAdmin() == true) {
		  ResourceDB.deleteResource(id);
		  return redirect(routes.Application.adminPanel());
	  }
	  return redirect(routes.Application.index());
  }
  
  /**
   * Retrieves the resource image based on the given id
   * @param id
   * @return
   */
  public static Result retrieveResourceImage(long id) {
	  Resource resource = ResourceDB.getResource(id);
	  if (resource != null) {
		  if (resource.hasPicture() == true) {
			  System.out.println("Picture!");
			  return ok(resource.getImage()).as("image/jpeg");
		  }
		  else if (resource.getExternalImage() != null && resource.getExternalImage().isEmpty() == false) {
			  System.out.println("External Picture!");
			  return redirect(resource.getExternalImage());
		  }
	  }
	  System.out.println("Placeholder!");
	  return redirect(routes.Assets.at("images/placeholder.gif"));
  }
  
  /**
   * Validates the picture upload for the resource form
   * @param formData
   * @param body
   */
  private static void validateResourceUpload(Form<ResourceFormData> formData, MultipartFormData body) {
	  List <ValidationError> errors = new ArrayList<ValidationError>();
	  if (formData.errors().get("uploadFile") != null) {
		  errors = formData.errors().get("uploadFile");
	  }
	  FilePart picture = body.getFile("uploadFile");
	  if (picture != null) {
		  if (picture.getContentType().contains("image") == false) {
			  errors.add(new ValidationError("uploadFile", "The file: " + picture.getFilename() + " is not an accepted file type. Please select a .jpg, .jpeg, or .png file."));
		  }
		  if (picture.getFile().length() > MAX_FILE_SIZE) {
			  errors.add(new ValidationError("uploadFile", "The file " + picture.getFilename() + "if too large. Please select a file that is under 2.5 MB"));
		  }
	  }
	  if (errors.isEmpty() == false) {
		  formData.errors().put("uploadFile", errors);
	  }
  }
  
  /**
   * Returns the solution approval page.
   * @return The solution approval page.
   */
  @Security.Authenticated(Secured.class)
  public static Result viewSolutions() {
    if (Secured.isLoggedIn(ctx()) && Secured.getUserInfo(ctx()).isAdmin()) {
      return ok(ViewSolutions.render());
    }
    return redirect(routes.Application.index());
  }
  
  /**
   * Create an IndexContent.
   * @return The IndexContent form.
   */
  @Security.Authenticated(Secured.class)
  public static Result newIndexBlock() {
    if (Secured.isLoggedIn(ctx()) && Secured.getUserInfo(ctx()).isAdmin()) {
      IndexContentFormData data = new IndexContentFormData();
      Form<IndexContentFormData> formData = Form.form(IndexContentFormData.class).fill(data);
      return ok(EditIndexBlock.render(formData, routes.Application.retrieveIndexContentImage(data.id).url(), true));
    }
    return redirect(routes.Application.index());
  }
  
  /**
   * Process a submitted IndexContent form.
   * @param isNew Specify if the registration is for a new IndexContent object.
   * @return The admin control panel.
   * @throws IOException When error reading uploaded image to byte array.
   */
  @Security.Authenticated(Secured.class)
  public static Result postEditIndexBlock(boolean isNew) throws IOException {
    Form<IndexContentFormData> formData = Form.form(IndexContentFormData.class).bindFromRequest();
    validateIndexContentUpload(formData, request().body().asMultipartFormData());
    if (formData.hasErrors()) {
      Long id = Long.parseLong(formData.data().get("id"));
      return badRequest(EditIndexBlock.render(formData, routes.Application.retrieveIndexContentImage(id).url(), isNew));
    }
    else {
      IndexContentFormData data = formData.get();
      IndexContent indexContent = IndexContentDB.add(data);
      MultipartFormData body = request().body().asMultipartFormData();
      FilePart picture = body.getFile("uploadFile");
      if (picture != null) {
        File source = picture.getFile();
        indexContent.setImage(Files.toByteArray(source));
        indexContent.save();
      }
      return redirect(routes.Application.adminPanel());
    }
  }
  
  /**
   * Edit an IndexContent.
   * @param id The ID of the IndexContent.
   * @return The IndexContent form.
   */
  @Security.Authenticated(Secured.class)
  public static Result editIndexBlock(Long id) {
    if (Secured.isLoggedIn(ctx()) && Secured.getUserInfo(ctx()).isAdmin()) {
        IndexContent indexContent = IndexContentDB.getIndexContent(id);
        if (indexContent != null) {
          IndexContentFormData data = new IndexContentFormData(indexContent);
          Form<IndexContentFormData> formData = Form.form(IndexContentFormData.class).fill(data);
          return ok(EditIndexBlock.render(formData, 
                                          routes.Application.retrieveIndexContentImage(data.id).url(), 
                                          false));
        }
    }
    return redirect(routes.Application.index());    
  }
  
  /**
   * Approves/Revokes the approval status of a rain garden.
   * @param id The ID of the rain garden.
   * @param url The URL to redirect to.
   * @return The page specifed in the URL.
   */
  @Security.Authenticated(Secured.class)
  public static Result switchGardenStatus(Long id, String url) {
    RainGarden garden = RainGardenDB.getRainGarden(id);
    if (Secured.isLoggedIn(ctx()) && Secured.getUserInfo(ctx()).isAdmin() && garden != null) {
      garden.setApproved(!garden.isApproved());
      garden.save();
    }
    return redirect(url);
  }
  
  /**
   * Approves/Revokes the approval status of a rain barrel.
   * @param id The ID of the rain garden. 
   * @param url The URL to redirect to.
   * @return The solution management page.
   */
  @Security.Authenticated(Secured.class)
  public static Result switchBarrelStatus(Long id, String url) {
    RainBarrel barrel = RainBarrelDB.getRainBarrel(id);
    if (Secured.isLoggedIn(ctx()) && Secured.getUserInfo(ctx()).isAdmin() && barrel != null) {
      barrel.setApproved(!barrel.isApproved());
      barrel.save();
    }
    return redirect(url);
  }
  
  /**
   * Approves/Revokes the approval status of a permeable pavers.
   * @param id The ID of the rain garden.
   * @param url The URL to redirect to.
   * @return The solution management page.
   */
  @Security.Authenticated(Secured.class)
  public static Result switchPaverStatus(Long id, String url) {
    PermeablePavers paver = PermeablePaversDB.getPermeablePavers(id);
    if (Secured.isLoggedIn(ctx()) && Secured.getUserInfo(ctx()).isAdmin() && paver != null) {
      paver.setApproved(!paver.isApproved());
      paver.save();
    }
    return redirect(url);
  }
  
  /**
   * Delete a comment.
   * @param id The ID of the comment to delete.
   * @return The page the comment was on.
   */
  @Security.Authenticated(Secured.class)
  public static Result deleteComment(Long id) {
    String url = "/";
    Comment comment = CommentDB.getComment(id);
    if (comment != null) {
      url = comment.getCommentURL();
      comment.setActive(false);
      comment.save();
    }    
    return redirect(url);
  }
  
  /**
   * Sends the admin accounts an email notifying that a new solution was registered
   * @param url The url of the new registered solution
   */
  private static void sendAdminNotificationEmail(String url) {
	  List<UserInfo> admins = UserInfoDB.getAdmins();
	  
	  UserInfo current;
	  for (int i = 0; i < admins.size(); i++) {
		  current = admins.get(i);
		  if (current.isConfirmed() == false) {
			  continue;
		  }
		  
		  MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
		  mail.setSubject("New Registered Solution Notification");
		  mail.setRecipient(current.getEmail());
		  mail.setFrom("openraingarden@gmail.com");
		  
		  String message = "A new solution has been registered!\n" + "Please view the solution and verify that it is legit:\n" + url;
		  mail.send(message);
	  }
  }
  
  /**
   * Returns the password recovery page
   * @return
   */
  public static Result passwordRecovery() {
	  Form<PasswordRecoveryFormData> formData = Form.form(PasswordRecoveryFormData.class);
	  return ok(PasswordRecovery.render(formData));
  }
  
  /**
   * Processes the password recovery form
   * @return
   */
  public static Result postPasswordRecovery() {
	  Form<PasswordRecoveryFormData> formData = Form.form(PasswordRecoveryFormData.class).bindFromRequest();
	  
	  if (formData.hasErrors() == true) {
		  return badRequest(PasswordRecovery.render(formData));
	  }
	  else {
		  PasswordRecoveryFormData data = formData.get();
		  
		  //create new random password
		  SecureRandom random = new SecureRandom();
		  String new_pw = new BigInteger(130, random).toString(32);
		  System.out.println("new_pw = " + new_pw);
		  
		  //set the user's password to the new random password
		  UserInfo user = UserInfoDB.getUser(data.email);		  
		  user.setPassword(BCrypt.hashpw(new_pw, BCrypt.gensalt()));
		  user.save();
		  
		  //send email to user containing his new password w/ link to login
		  MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
		  mail.setSubject("Password Recovery");
		  mail.setRecipient(data.email);
		  mail.setFrom("openraingarden@gmail.com");
		  
		  String message = "Your password has been reset to:\n" + new_pw;
		  mail.send(message);
				  
		  return redirect(routes.Application.login("/"));
	  }
  }
  
  /**
   * Retrieve an image for an IndexContent object.
   * @param id The ID of the IndexContent.
   * @return The image associated with the IndexContent.
   */
  public static Result retrieveIndexContentImage(Long id) {
    IndexContent indexContent = IndexContentDB.getIndexContent(id);
    if (indexContent != null) {
      if (indexContent.hasPicture()) {
        return ok(indexContent.getImage()).as("image/jpeg");
      }
      else if (indexContent.getExternalImageURL() != null && !indexContent.getExternalImageURL().isEmpty()) {
        return redirect(indexContent.getExternalImageURL());
      }
    }
    return redirect(routes.Assets.at("#"));
  }
  
  /**
   * Validate a given form's upload file.
   * @param formData The form to check.
   * @param body Multipart form data that holds the uploaded file to check.
   * @return A form with validated upload data.
   */
  private static Form<IndexContentFormData> validateIndexContentUpload(Form<IndexContentFormData> formData, 
      MultipartFormData body) {
    List<ValidationError> errors = new ArrayList<ValidationError>();
    Long id = Long.parseLong(formData.data().get("id"));
    if (formData.errors().get("uploadFile") != null) {
      errors = formData.errors().get("uploadFile");
    }
    FilePart picture = body.getFile("uploadFile");
    if (id == -1 && picture == null) {
      errors.add(new ValidationError("uploadFile", "A picture is required."));      
    }
    if (picture != null) {
      if (!(picture.getContentType().contains("image"))) {
        errors.add(new ValidationError("uploadFile", "The file \"" + picture.getFilename() + "\" is not an accepted "
            + "file type. Please select a file with the extension .jpg/.jpeg or .png"));
      }
      if (picture.getFile().length() > MAX_FILE_SIZE) {
        errors.add(new ValidationError("uploadFile", "The file \"" + picture.getFilename() + "\" is too large. "
            + "Please select a file that is under 2.5 MB"));
      }
    }
    if (!errors.isEmpty()) {
      formData.errors().put("uploadFile", errors);
    }
    return formData;
  }
}

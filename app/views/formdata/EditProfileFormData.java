package views.formdata;

import java.util.List;
import java.util.ArrayList;
import models.UserInfo;
import models.UserInfoDB;
import play.data.validation.ValidationError;

/**
 * Backing class for the edit profile form
 * @author Kyle
 *
 */
public class EditProfileFormData {
	/** First Name of the user	 */
	public String firstName;
	
	/** Last Name of the user */
	public String lastName;
	
	/** Telephone of the user */
	public String telephone;
	
	/** New email address of the user */
	public String email;
	
	/** Flag for whether a new email address was entered */
	public boolean change_email;
	
	/** New password of the user */
	public String new_password;
	
	/** Repeat password */
	public String repeat_pw;
	
	/** Flag for whether a new passwrod was entered */
	public boolean change_pw;
	
	public EditProfileFormData() {
		
	}
	
	/**
	 * Constructor
	 * @param userInfo the User
	 */
	public EditProfileFormData(UserInfo userInfo) {
		this.firstName = userInfo.getFirstName();
		this.lastName = userInfo.getLastName();
		this.change_email = false;
		this.telephone = userInfo.getTelephone();
		this.change_pw = false;
	}
	
	/**
	 * Validation for the edit profile form
	 * @return A list of errors or null if form is valid
	 */
	public List<ValidationError> validate() {
		ArrayList<ValidationError> errors = new ArrayList<ValidationError>();
		
		if (this.firstName == null || this.firstName.length() == 0) {
			errors.add(new ValidationError("firstName", "Please enter your first name."));
		}
		if (this.lastName == null || this.lastName.length() == 0) {
			errors.add(new ValidationError("lastName", "Please enter your last name."));
		}
		
		if (this.email != null && this.email.length() != 0) {
			if (UserInfoDB.getUser(this.email) != null) {
				errors.add(new ValidationError("email", "Email address is already registered."));
			}
			else {
				change_email = true;
			}
		}
		
		boolean pw = false;
		if (this.new_password != null && this.new_password.length() != 0) {
			pw = true;
		}
		boolean rpw = false;
		if (this.repeat_pw != null && this.repeat_pw.length() != 0) {
			rpw = true;
			
		}
		
		if (pw == true && rpw == true && this.new_password.equals(this.repeat_pw) == true) {
			change_pw = true;
		}
		else if (!(pw == false && rpw == false)) {
			errors.add(new ValidationError("new_password", "Passwords must match!"));
			errors.add(new ValidationError("repeat_pw", "Passwords must match!"));
		}
		
		return errors.isEmpty() ? null : errors;
	}
}

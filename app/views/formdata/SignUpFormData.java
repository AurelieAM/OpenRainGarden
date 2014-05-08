package views.formdata;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import models.UserInfo;
import models.UserInfoDB;
import play.data.validation.ValidationError;

/**
 * Backing class for Sign in form.
 * @author Kyle
 *
 */
public class SignUpFormData {
	/** First Name of user.   */
	public String firstName;
	
	/** Last Name of user.	 */
	public String lastName;
	
	/** Email of user.	 */
	public String email;
	
	/** Telephone of user.	 */
	public String telephone;
	
	/** Password of user.	 */
	public String password;
	
	/** Required password pattern: length of 6-20 characters containing at least one lowercase letter, one upper case letter, one number, and one symbol (~!@#$%^&*) */
	private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[~!@#$%^&*]).{6,20})";
	
	/**
	 * Repeat password
	 */
	public String repeatpw;
	
	/**
	 * Constructor.
	 */
	public SignUpFormData() {	  
	  
	}
	
	/**
	 * Constructor.
	 * @param userInfo The user.
	 */
	public SignUpFormData(UserInfo userInfo) {
	  this.firstName = userInfo.getFirstName();
	  this.lastName = userInfo.getLastName();
	  this.email = userInfo.getEmail();
	  this.telephone = userInfo.getTelephone();
	  this.password = userInfo.getPassword();
	}
	
	/**
	 * Validation for Sign Up Form.
	 * @return A list of errors (empty list if form is valid)
	 */
	public List<ValidationError> validate() {
		Pattern pattern;
		Matcher matcher;
		ArrayList<ValidationError> errors = new ArrayList<ValidationError>();
		
		if (this.firstName == null || this.firstName.length() == 0) {
			errors.add(new ValidationError("firstName", "Please enter your first name."));
		}
		
		if (this.lastName == null || this.lastName.length() == 0) {
			errors.add(new ValidationError("lastName", "Please enter your last name."));
		}
		
		if (this.email == null || this.email.length() == 0) {
			errors.add(new ValidationError("email", "Please enter your email address."));
		}
		
		if (this.telephone != null && this.telephone.length() > 0)  {
			pattern = Pattern.compile("\\d{3}-\\d{3}-\\d{7}");
			matcher = pattern.matcher(this.telephone);
			if (matcher.matches() == false) {
				errors.add(new ValidationError("telephone", "Please enter your telephone in the format: ###-###-####"));
			}
		}
		
		pattern = Pattern.compile(PASSWORD_PATTERN);
		matcher = pattern.matcher(this.password);
		if (this.password == null || this.password.length() == 0) {
			errors.add(new ValidationError("password", "Please enter your desired password."));
		}
		else if (matcher.matches() == false) {
			errors.add(new ValidationError("password", ""));
		}
		
		if (this.repeatpw == null || this.repeatpw.length() == 0) {
			errors.add(new ValidationError("repeatpw", "Please repeat your password."));
		}
		else if (this.password.equals(this.repeatpw) == false) {
			errors.add(new ValidationError("password", "Passwords much match."));
			errors.add(new ValidationError("repeatpw", "Passwords much match."));
		}
		
		if (UserInfoDB.isUser(this.email) == true) {
			errors.add(new ValidationError("email", "Email is already registered."));
		}
		
		return errors.isEmpty() ? null : errors;
	}
}

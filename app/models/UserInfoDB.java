package models;

import java.util.List;

/**
 * In memory storage of UserInfo
 * @author Kyle
 *
 */
public class UserInfoDB {
	
	/**
	 * Adds given user info to the UserDB
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param telephone
	 * @param password
	 */
	public static long addUserInfo(String firstName, String lastName, String email, String telephone, String password,
	    boolean isAdmin) {
		UserInfo userInfo = new UserInfo(firstName, lastName, email, telephone, password, isAdmin);
		userInfo.save();
		return userInfo.getId();
	}
	
	/**
	 * Adds the given user info the UserDB
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param telephone
	 * @param password
	 * @param isAdmin
	 * @param isConfirm
	 * @return
	 */
	public static long addUserInfo(String firstName, String lastName, String email, String telephone, String password, boolean isAdmin, boolean isConfirm) {
		UserInfo userInfo = new UserInfo(firstName, lastName, email, telephone, password, isAdmin, isConfirm);
		userInfo.save();
		return userInfo.getId();
	}
	
	/**
	 * Returns whether given email is in the userDB
	 * @param email
	 * @return
	 */
	public static boolean isUser(String email) {
		return (UserInfo.find().where().eq("email", email).findUnique() != null);
	}
	
	/**
	 * Returns the UserInfo based on the given email
	 * @param email
	 * @return
	 */
	public static UserInfo getUser(String email) {
		return UserInfo.find().where().eq("email", email).findUnique();
	}
	
	/**
	 * Returns the UserInfo based on the given id
	 * @param id
	 * @return
	 */
	public static UserInfo getUser(Long id) {
		return UserInfo.find().where().eq("id", id).findUnique();
	}
	
	/**
	 * Return list of all users.
	 * @return A list of all users.
	 */
	public static List<UserInfo> getUsers() {
	  return UserInfo.find().all();
	}
	
	/**
	 * Returns a list of all admin accounts
	 * @return A list of all admin accounts
	 */
	public static List<UserInfo> getAdmins() {
		return UserInfo.find().where().eq("admin", true).findList();
	}
	
	/**
	 * Returns whether the given email and password pair are valid
	 * @param email
	 * @param password
	 * @return
	 */
	public static boolean isValid(String email, String password) {
		if (email != null && password != null) {
			if (isUser(email) == true) {
				if (getUser(email).getPassword().equals(password) == true)
					return true;
			}
		}
		return false;
	}
}

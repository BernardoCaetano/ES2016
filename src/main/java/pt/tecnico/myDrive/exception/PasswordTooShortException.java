package pt.tecnico.myDrive.exception;

public class PasswordTooShortException extends MyDriveException {
	
	private String password;
	
	public PasswordTooShortException(String password) {
		this.password = password;
	}
	
	public String getPassword() { return password; }
	
	public String getMessage() {
		return "Password too short '" + password + "'. "
				+ "Password must have at least 8 characters for "
				+ "you to be able to login";
	}

}

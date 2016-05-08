package pt.tecnico.myDrive.exception;

public class PasswordTooShortException extends MyDriveException {
	
	private String password;
	
	public PasswordTooShortException(String password) {
		this.password = password;
	}
	
	public String getPassword() { return password; }
	
	public String getMessage() {
		return "Invalid password '" + password + "'. Passwords must have at least 8 characters";
	}

}

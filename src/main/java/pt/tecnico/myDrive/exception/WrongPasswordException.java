package pt.tecnico.myDrive.exception;

public class WrongPasswordException extends MyDriveException {
	
	 private static final long serialVersionUID = 1L;


	public WrongPasswordException() {}

	@Override
	public String getMessage() {
		return "Wrong password. Please try again.";
	}

}

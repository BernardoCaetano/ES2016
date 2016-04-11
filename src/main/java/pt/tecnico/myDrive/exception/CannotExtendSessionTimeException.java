package pt.tecnico.myDrive.exception;

public class CannotExtendSessionTimeException extends MyDriveException {

	private static final long serialVersionUID = 1L;

	public CannotExtendSessionTimeException() {}
	
	public String getMessage() {
		return "Session time cannot be set to future dates";
	}
	

}

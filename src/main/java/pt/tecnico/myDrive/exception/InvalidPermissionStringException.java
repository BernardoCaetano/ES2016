package pt.tecnico.myDrive.exception;

public class InvalidPermissionStringException extends MyDriveException {

	private static final long serialVersionUID = -6203091045884912763L;
	String invalidMask;

	public InvalidPermissionStringException(String invalidMask) {
		this.invalidMask = invalidMask;
	}

	@Override
	public String getMessage() {
		return "Invalid mask: \"" + invalidMask + "\"";
	}
	
	public String getInvalidMask(){
		return invalidMask;
	}

}

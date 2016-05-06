package pt.tecnico.myDrive.exception;

public class InvalidPermissionStringException extends MyDriveException {

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

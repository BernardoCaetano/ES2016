package pt.tecnico.myDrive.exception;

public class CyclicLinkException extends MyDriveException {

	private static final long serialVersionUID = 1L;

	public CyclicLinkException() {	}
	
	public String getMessage() {
		return "Cannot reach target. There is a link loop."; 
	}

}

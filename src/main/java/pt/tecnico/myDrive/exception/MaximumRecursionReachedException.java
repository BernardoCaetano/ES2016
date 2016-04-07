package pt.tecnico.myDrive.exception;

public class MaximumRecursionReachedException extends MyDriveException {

	private static final long serialVersionUID = 1L;

	public MaximumRecursionReachedException() {	}
	
	public String getMessage() {
		return "Cannot reach target. There is a link loop."; 
	}

}

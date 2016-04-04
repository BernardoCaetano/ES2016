
package pt.tecnico.myDrive.exception;

public class PathMaximumLengthException extends MyDriveException {

	private static final long serialVersionUID = 1L;

	public PathMaximumLengthException() {
	}
	
	@Override
	public String getMessage() {
		return "The length of an Absolute path has a maximum of 1024 characters.";
	}
}

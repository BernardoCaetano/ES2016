package pt.tecnico.myDrive.exception;

public class InvalidDirectoryContentException extends MyDriveException {

	private static final long serialVersionUID = 1L;

	public InvalidDirectoryContentException() {
	}
	
	@Override
	public String getMessage() {
		return "A directory can't be created with content.";
	}
	

}

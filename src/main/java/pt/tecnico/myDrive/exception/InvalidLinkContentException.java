package pt.tecnico.myDrive.exception;

public class InvalidLinkContentException extends MyDriveException {

	private static final long serialVersionUID = 1L;

	public InvalidLinkContentException() {
	}
	
	@Override
	public String getMessage() {
		return "A Link must be have a correct path as a Content.";
	}
}

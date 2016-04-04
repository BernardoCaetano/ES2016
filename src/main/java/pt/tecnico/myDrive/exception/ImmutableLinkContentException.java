package pt.tecnico.myDrive.exception;

public class ImmutableLinkContentException extends MyDriveException {
	
	private static final long serialVersionUID = 1L;

	public ImmutableLinkContentException() {}
	
	@Override
	public String getMessage() {
		return "Link content can only be defined during creation";
	}
	
}

package pt.tecnico.myDrive.exception;

public class InvalidLinkContentException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	private long content;

	public InvalidLinkContentException(long content) {
		this.content = content;
	}
	
	public long getContent() { return content; }
	
	@Override
	public String getMessage() {
		return "The content '" + content + "' is not valid for Link Files.";
	}
	

}

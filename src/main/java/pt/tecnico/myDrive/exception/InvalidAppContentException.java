package pt.tecnico.myDrive.exception;

public class InvalidAppContentException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	private long content;

	public InvalidAppContentException(long content) {
		this.content = content;
	}
	
	public long getContent() { return content; }
	
	@Override
	public String getMessage() {
		return "The content '" + content + "' is not valid for App Files.";
	}
}
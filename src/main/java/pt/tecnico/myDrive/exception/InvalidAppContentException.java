package pt.tecnico.myDrive.exception;

public class InvalidAppContentException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	private String content;

	public InvalidAppContentException(String content) {
		this.content = content;
	}
	
	public String getContent() { return content; }
	
	@Override
	public String getMessage() {
		return "The content '" + content + "' is not valid for App Files.";
	}
}
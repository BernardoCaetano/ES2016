package pt.tecnico.myDrive.exception;

public class InvalidTextFileContentException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	private long content;

	public InvalidTextFileContentException(long content) {
		this.content = content;
	}
	
	public long getContent() { return content; }
	
	@Override
	public String getMessage() {
		return "The content '" + content + "' is not valid for TextFile Files.";
	}
}
package pt.tecnico.myDrive.exception;

public class ExecuteFileException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	String message;
	
	public ExecuteFileException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
		
	}
}

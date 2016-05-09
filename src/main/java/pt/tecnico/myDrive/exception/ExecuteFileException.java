package pt.tecnico.myDrive.exception;

public class ExecuteFileException extends MyDriveException {
	
	String message;
	
	public ExecuteFileException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
		
	}
}

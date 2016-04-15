package pt.tecnico.myDrive.exception;

public class InvalidOperationException extends MyDriveException {
	
	private static final long serialVersionUID = 1L;
	
	String operation;

	public InvalidOperationException(String operation) {
		this.operation = operation;
	}
	
	public String getMessage() {
		return "Cannot execute operation: " + operation;
	}

	

}

package pt.tecnico.myDrive.exception;

public class EnvironmentVariableDoesNotExistException extends MyDriveException{
	private static final long serialVersionUID = 1L;
	private String name;

	public EnvironmentVariableDoesNotExistException(String name) {
		this.name = name;
	}
	
	public String getName() { return  name; }
	
	@Override
	public String getMessage() {
		return "The environment variable '" + name + "' does not exist.";
	}
}

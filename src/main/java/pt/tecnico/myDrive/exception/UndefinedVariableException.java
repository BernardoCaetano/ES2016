package pt.tecnico.myDrive.exception;

public class UndefinedVariableException extends MyDriveException{
	private static final long serialVersionUID = 1L;
	private String name;

	public UndefinedVariableException(String name) {
		this.name = name;
	}
	
	public String getName() { return  name; }
	
	@Override
	public String getMessage() {
		return "The environment variable '" + name + "' does not exist.";
	}
}

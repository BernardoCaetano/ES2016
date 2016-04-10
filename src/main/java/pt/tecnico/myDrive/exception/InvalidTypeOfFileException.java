package pt.tecnico.myDrive.exception;

public class InvalidTypeOfFileException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	private String typeOfFile;

	public InvalidTypeOfFileException(String typeOfFile) {
		this.typeOfFile = typeOfFile;
	}
	
	public String getTypeOfFile() { return typeOfFile; }
	
	@Override
	public String getMessage() {
		return "The file type '" + typeOfFile + "' is not valid.";
	}
	

}

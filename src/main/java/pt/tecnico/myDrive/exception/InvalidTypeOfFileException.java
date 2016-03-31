package pt.tecnico.myDrive.exception;

public class InvalidTypeOfFileException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	private long typeOfFile;

	public InvalidTypeOfFileException(long typeOfFile) {
		this.typeOfFile = typeOfFile;
	}
	
	public long getTypeOfFile() { return typeOfFile; }
	
	@Override
	public String getMessage() {
		return "The file type '" + typeOfFile + "' is not valid.";
	}
	

}

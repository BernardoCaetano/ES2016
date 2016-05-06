package pt.tecnico.myDrive.exception;

public class AssociationDoesNotExistExceotion extends MyDriveException{
	private static final long serialVersionUID = 1L;
	private String fileName;

	public AssociationDoesNotExistExceotion(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileName() { return fileName; }
	
	@Override
	public String getMessage() {
		return "There is no association for the file '" + fileName + "'.";
	}
}

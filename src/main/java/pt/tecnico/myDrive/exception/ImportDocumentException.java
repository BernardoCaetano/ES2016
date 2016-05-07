package pt.tecnico.myDrive.exception;

public class ImportDocumentException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    public ImportDocumentException() {
        super("Error importing file system from XML");
    }
    
    public ImportDocumentException(String message) {
    	super("Error importing file system from XML: " + message);
	}
}
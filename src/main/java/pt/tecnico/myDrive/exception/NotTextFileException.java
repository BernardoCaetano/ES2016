package pt.tecnico.myDrive.exception;

public class NotTextFileException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	private String fileName;

	public NotTextFileException(String fileName) {
        this.fileName = fileName;
    }

	public String getFileName() {
		return fileName;
	}

	@Override
	public String getMessage() {
		return "The file '" + fileName + "' does not support content reading nor writing";
	}
}

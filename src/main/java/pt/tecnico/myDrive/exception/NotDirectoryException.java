package pt.tecnico.myDrive.exception;

public class NotDirectoryException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	private String dirName;

	public NotDirectoryException(String dirName) {
        this.dirName = dirName;
    }

	public String getDirName() {
		return dirName;
	}

	@Override
	public String getMessage() {
		return "The file '" + dirName + "' is not a directory";
	}
}

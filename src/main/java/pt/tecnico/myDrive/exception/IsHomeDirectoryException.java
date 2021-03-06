package pt.tecnico.myDrive.exception;

public class IsHomeDirectoryException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	private String homeDirName;

	public IsHomeDirectoryException(String homeDirName) {
        this.homeDirName = homeDirName;
    }

	public String getHomeDirName() {
		return homeDirName;
	}

	@Override
	public String getMessage() {
		return "Directory '" + homeDirName + "' is a home directory and cannot be deleted";
	}
}

package pt.tecnico.myDrive.exception;

public class IsCurrentDirectoryException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	private String currentDirName;

	public IsCurrentDirectoryException(String currentDirName) {
        this.currentDirName = currentDirName;
    }

	public String getcurrentDirName() {
		return currentDirName;
	}

	@Override
	public String getMessage() {
		return "Directory '" + currentDirName + "' is a current directory to another user and cannot be deleted";
	}
}

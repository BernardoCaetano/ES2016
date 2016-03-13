package pt.tecnico.myDrive.exception;

public class DirectoryNotEmptyException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	private String dirName;

	public DirectoryNotEmptyException(String dirName) {
        this.dirName = dirName;
    }

	public String getDirName() {
		return dirName;
	}

	@Override
	public String getMessage() {
		return "Directory '" + dirName + "' is not empty and cannot be deleted";
	}
}

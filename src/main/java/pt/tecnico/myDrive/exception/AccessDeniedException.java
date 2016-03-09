package pt.tecnico.myDrive.exception;

public class AccessDeniedException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private String _username;
    private String _filename;

    public AccessDeniedException(String username, String filename) {
        _username = username;
        _filename = filename;
    }

    public String getUsername() { return _username; }
    public String getFilename() { return _filename; }

    @Override 
    public String getMessage() { 
    	return "Access denied: '" + _username + "' does not have permission to access '" + _filename + "'";
    }
}
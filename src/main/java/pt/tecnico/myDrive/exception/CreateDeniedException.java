package pt.tecnico.myDrive.exception;

public class CreateDeniedException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private String _username;

    public CreateDeniedException(String username) {
        _username = username;
    }

    public String getUsername() { return _username; }

    @Override 
    public String getMessage() { 
    	return "User '" + _username + "' does not have permissions to create a file in the current directory .";
    }
}


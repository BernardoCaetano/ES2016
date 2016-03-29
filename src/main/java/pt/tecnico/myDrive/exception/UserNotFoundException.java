package pt.tecnico.myDrive.exception;

public class UserNotFoundException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private String _username;

    public UserNotFoundException(String username) {
        _username = username;
    }

    public String getUnknownName() { return _username; }

    @Override
    public String getMessage() {
        return "The user '" + _username + "' does not exist";
    }
}
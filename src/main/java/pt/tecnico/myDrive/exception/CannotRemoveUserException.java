package pt.tecnico.myDrive.exception;

import pt.tecnico.myDrive.domain.User;

public class CannotRemoveUserException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private User _user;

    public CannotRemoveUserException(User user) {
        _user = user;
    }

    public User getUser() { return _user; }

    @Override 
    public String getMessage() { 
    	return "Access denied: '" + _user.getUsername() + "' cannot be removed '";
    }
}
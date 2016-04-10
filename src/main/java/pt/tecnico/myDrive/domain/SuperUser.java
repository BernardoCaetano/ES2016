package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.exception.InvalidUsernameException;

public class SuperUser extends SuperUser_Base {
    
    public SuperUser(MyDriveFS mydrive, String username, String password, String name, String umask) throws InvalidUsernameException {
        init(mydrive, username, password, name, umask);
    }
    
    @Override
    public boolean canAccess(AbstractFile file, char type) {
    	return true;
    }
}

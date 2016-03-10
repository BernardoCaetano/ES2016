package pt.tecnico.myDrive.domain;

import org.apache.commons.lang.StringUtils;

import pt.tecnico.myDrive.exception.InvalidUsernameException;

public class User extends User_Base {
    
    public User() {
        super();
    }

    public User(MyDriveFS mydrive, String username) {
        super();
    	init(mydrive, username, username, username, "rwxd----"); 
    }

    public User(MyDriveFS mydrive, String username, String password, String name, String umask) {
        super();
        init(mydrive, username, password, name, umask);
    }

    public void init(MyDriveFS mydrive, String username, String password, String name, String umask) {
        setUsername(username);
        setPassword(password);
        setName(name);
        setUmask(umask);
        setMyDrive(mydrive);
    }

    @Override
    public void setMyDrive(MyDriveFS mydrive) {
        if (mydrive == null)
            super.setMyDrive(null);
        else
            mydrive.addUsers(this);
    }

    @Override
    public void setUsername(String username) throws InvalidUsernameException{
        if(username.equals("") || !StringUtils.isAlphanumeric(username.toLowerCase())) {
            throw new InvalidUsernameException(username);
        }
        super.setUsername(username);
    }
}

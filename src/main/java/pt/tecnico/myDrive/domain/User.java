package pt.tecnico.myDrive.domain;

public class User extends User_Base {
    
    public User(MyDriveFS mydrive, String username) {
    	setUsername(username);
    	setPassword(username);
    	setName(username);
    	setUmask("rwxd----");  
        setMyDrive(mydrive);      
    }

    public User(MyDriveFS mydrive, String username, String password, String name, String umask) {
        setUsername(username);
    	setPassword(username);
    	setName(username);
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
}

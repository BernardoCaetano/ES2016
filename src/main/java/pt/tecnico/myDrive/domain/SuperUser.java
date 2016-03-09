package pt.tecnico.myDrive.domain;

public class SuperUser extends User {
    
    public SuperUser(MyDriveFS mydrive, String username, String password, String name, String umask) {
        super(mydrive, username, password, name, umask);
    }
}

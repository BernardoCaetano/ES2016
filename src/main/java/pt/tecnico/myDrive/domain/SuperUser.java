package pt.tecnico.myDrive.domain;

public class SuperUser extends SuperUser_Base {
    
    public SuperUser(MyDriveFS mydrive, String username, String password, String name, String umask) {
        init(mydrive, username, password, name, umask);
    }
}

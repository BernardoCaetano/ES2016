package pt.tecnico.myDrive.domain;

public class User extends User_Base {
    
    public User(String username) {
    	setUsername(username);
    	setPassword(username);
    	setName(username);
    	setUmask("rwxd----");        
    }

    public User(String username, String password, String name, String umask) {
    	setUsername(username);
    	setPassword(username);
    	setName(username);
    	setUmask(umask);
    }
}

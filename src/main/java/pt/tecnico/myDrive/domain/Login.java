package pt.tecnico.myDrive.domain;

import java.math.BigInteger;
import java.util.Random;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.CannotExtendSessionTimeException;
import pt.tecnico.myDrive.exception.UserNotFoundException;
import pt.tecnico.myDrive.exception.WrongPasswordException;

public class Login extends Login_Base {
	
    public Login(MyDriveFS myDrive, String username, String password) 
    		throws UserNotFoundException, WrongPasswordException {
    	super();
    	User user = myDrive.getUserByUsername(username);
    	if (!user.checkPassword(password)) {
    		throw new WrongPasswordException();
    	}
    	this.setMyDrive(myDrive);
    	this.setUser(user);
        this.setCurrentDir(user.getHomeDirectory());
        this.setToken(new BigInteger(64, new Random()).longValue());
        super.setLastActivity(DateTime.now());  
        
        myDrive.deleteInvalidLogins();
    }
    
    public boolean isValid() {
    	return (DateTime.now().getMillis() - this.getLastActivity().plusHours(2).getMillis()) < 0;
    }
    
    public void updateLastActivity() {
    	if (this.isValid()) {
    		super.setLastActivity(DateTime.now());
    	}
    }
    
    @Override
    public void setLastActivity(DateTime newTime) throws CannotExtendSessionTimeException {
    	if (newTime.isAfter(this.getLastActivity())) {
    		throw new CannotExtendSessionTimeException();
    	}
    	super.setLastActivity(newTime);
    }
    
    public void remove() {
    	this.setCurrentDir(null);
    	this.setUser(null);
    	this.setMyDrive(null);
    	deleteDomainObject();
    }
    
}

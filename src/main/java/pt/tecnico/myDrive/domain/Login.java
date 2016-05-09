package pt.tecnico.myDrive.domain;

import java.math.BigInteger;
import java.util.Random;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.AccessDeniedException;
import pt.tecnico.myDrive.exception.CannotExtendSessionTimeException;
import pt.tecnico.myDrive.exception.InvalidLoginException;
import pt.tecnico.myDrive.exception.InvalidOperationException;
import pt.tecnico.myDrive.exception.PasswordTooShortException;
import pt.tecnico.myDrive.exception.UndefinedVariableException;
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
    	if (!user.canLogin()) {
    		throw new PasswordTooShortException(password);
    	}
		super.setUser(user);
		this.setCurrentDir(user.getHomeDirectory());
		super.setToken(generateUniqueToken(myDrive));
		super.setMyDrive(myDrive);
		super.setLastActivity(DateTime.now());

        myDrive.deleteInvalidLogins();
    }
    
    private long generateUniqueToken(MyDriveFS myDrive) {
    	 long newToken;
         while (true) {
         	newToken = new BigInteger(64, new Random()).longValue();
         	try {
         		myDrive.getLoginByToken(newToken);
         	} catch (InvalidLoginException e) { break; }
         }
         return newToken;
    }
    
    @Override
    public void setUser(User user) {
    	throw new InvalidOperationException("Changing user associated with a login session");
    }
    
    @Override
    public void setToken(java.lang.Long token) {
    	throw new InvalidOperationException("Changing token associated with a login session");
    }
        
    @Override
    public void setMyDrive(MyDriveFS myDrive) {
    	throw new InvalidOperationException("Changing application associated with a login session");
    }
    
    public boolean isValid() {
    	return getUser().isLoginValid(this.getLastActivity());
    }
    
    protected void updateLastActivity() {
    	if (this.isValid()) {
    		super.setLastActivity(DateTime.now());
    	}
    }
    
    @Override 
    public void setCurrentDir(Directory currentDir) throws AccessDeniedException {
    	if (!canChangeToDirectory(currentDir)) {
    		throw new AccessDeniedException(getUser().getUsername(), currentDir.getName());
    	}
		super.setCurrentDir(currentDir);
    }
    
    private boolean canChangeToDirectory(Directory newDir) {
    	do {
    		if(!this.getUser().canExecute(newDir)) {
    			return false;
    		}
    		newDir = newDir.getParent();
    	} while (!newDir.getPath().equals("/"));
    	
    	return true;
    }
    
    @Override
    public void setLastActivity(DateTime newTime) throws CannotExtendSessionTimeException {
    	if (newTime.isAfter(this.getLastActivity())) {
    		throw new CannotExtendSessionTimeException();
    	}
    	super.setLastActivity(newTime);
    }
    
    public void cleanup() {
    	for (Variable v : this.getVariableSet()) {
    		v.cleanup();
    	}
    	super.setCurrentDir(null);
    	super.setUser(null);
    	super.setMyDrive(null);
    	deleteDomainObject();
    }
    
    public void addVariable(String name, String value) {
    	try {
			Variable v = getVariableByName(name);
			v.setValue(value);
		}catch (UndefinedVariableException e){
			addVariable(new Variable(name, value));	
		}
		
    	
    }
       
    public Variable getVariableByName(String name) throws UndefinedVariableException {
    	for (Variable v : getVariableSet()) {
    		if (v.getName().equals(name)) 
    			return v;
    	}
    	throw new UndefinedVariableException(name);
    }
    
}

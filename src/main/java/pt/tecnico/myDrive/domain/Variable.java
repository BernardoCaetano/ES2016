package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.exception.InvalidOperationException;

public class Variable extends Variable_Base {
    
    protected Variable(Login login, String name, String value) {
        super();
        setName(name);
        setValue(value);
    }
    
    @Override
    public void setLogin(Login login) {
    	throw new InvalidOperationException("Changing login to which a variable is associated");
    }
    
    protected void cleanup() {
    	super.setLogin(null);
    	this.deleteDomainObject();
	}
    
}

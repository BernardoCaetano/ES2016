package pt.tecnico.myDrive.domain;

public class Variable extends Variable_Base {
    
    public Variable(String name, String value) {
        super();
        setName(name);
        setValue(value);
    }
    
    protected void cleanup() {
    	this.setLogin(null);
    	this.deleteDomainObject();
	}
    
}

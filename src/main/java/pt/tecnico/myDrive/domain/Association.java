package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.exception.InvalidOperationException;

public class Association extends Association_Base {
    
    protected Association(String fileExtension, TextFile applicationFile) {
    	super();
    	if (applicationFile == null)
    		throw new InvalidOperationException("Application File can't be null");
        setFileExtension(fileExtension);
        setApplicationFile(applicationFile);
    }
    
    @Override
    public void setUser(User user) {
    	throw new InvalidOperationException("Changing User to which an Association is associated");
    }
    protected void cleanup() {
    	super.setUser(null);
    	super.setApplicationFile(null);
    }
}
package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.InvalidOperationException;
import pt.tecnico.myDrive.exception.InvalidPathException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;

class Guest extends Guest_Base {

	Guest(MyDriveFS myDrive) throws InvalidPathException, InvalidUsernameException{
		super.setValidPassword("", false);
		setUsername("nobody");
		setName("Guest");
		setUmask("rwxdr-x-");
		setMyDrive(myDrive);
		setHomeDirectory(myDrive, "/home/nobody");
	}
	
	@Override
    public boolean canWrite(AbstractFile file) {
    	if (file.getOwner() == this)
    		return canAccess(file, 'w');
    	else
    		return false;
    }
	
	@Override
	public void setPassword(String password) {
		throw new InvalidOperationException("Cannot change password of Guest user");
	}
	
	@Override
	public boolean canLogin() {
		return true;
	}
 
    @Override
    public boolean canDelete(AbstractFile file) {
    	if (file.getOwner() == this)
    		return canAccess(file, 'd');
    	else
    		return false;
    }
    
	@Override
	public void xmlImport(MyDriveFS myDrive, Element userElement) {
		throw new ImportDocumentException("Cannot import Guest user");
	}

	@Override
	protected void cleanup() {
		for (Login l : this.getLoginSet()) l.cleanup();
		for (AbstractFile f : this.getFilesSet()) 
			if (!(f.getPath().equals("/home/") || f.getPath().equals("/home/nobody/"))) {
				try {	
					f.setOwner(null);
				} catch (InvalidOperationException e) {}
			}
	}
	
	@Override
	public boolean isLoginValid(DateTime lastActivity) {
		return true;
	}
}

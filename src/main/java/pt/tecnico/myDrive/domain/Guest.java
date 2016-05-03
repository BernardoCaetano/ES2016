package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.exception.InvalidOperationException;
import pt.tecnico.myDrive.exception.InvalidPathException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;

class Guest extends Guest_Base {

	Guest(MyDriveFS myDrive) throws InvalidPathException, InvalidUsernameException{
		setPassword("");
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
    public boolean canDelete(AbstractFile file) {
    	if (file.getOwner() == this)
    		return canAccess(file, 'd');
    	else
    		return false;
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
}

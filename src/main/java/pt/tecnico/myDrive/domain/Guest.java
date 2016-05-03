package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.exception.InvalidPathException;

class Guest extends Guest_Base {

	Guest(MyDriveFS myDrive) throws InvalidPathException{
		setPassword("");
		setUsername("nobody");
		setName("Guest");
		setUmask("rxwdr-x-");
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
}

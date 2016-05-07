package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.InvalidOperationException;
import pt.tecnico.myDrive.exception.InvalidPathException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;

public class SuperUser extends SuperUser_Base {

	public SuperUser(MyDriveFS mydrive, String username, String password, String name, String umask, String homeDirPath)
			throws InvalidUsernameException, InvalidPathException {
		init(mydrive, username, password, name, umask, homeDirPath);
	}
	
	@Override
	protected boolean canAccess(AbstractFile file, char type) {
		return true;
	}
	
	@Override
	public void xmlImport(MyDriveFS myDrive, Element userElement) {
    	throw new ImportDocumentException("Cannot import Root user");	
	}
	
	@Override
	protected void cleanup() {
		for (Login l : this.getLoginSet()) l.cleanup();
		for (AbstractFile f : this.getFilesSet()) 
			if (!(f.getPath().equals("/home/") || f.getPath().equals("/home/root/"))) {
				try {	
					f.setOwner(null);
				} catch (InvalidOperationException e) {}
			}
	}
	
	@Override
	public boolean isLoginValid(DateTime lastActivity) {
		return (DateTime.now().getMillis() - lastActivity.plusMinutes(10).getMillis()) <= 0;
	}
}

package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;

public class ChangeDirectoryService extends MyDriveService {
	
	String path;
	long token;

	public ChangeDirectoryService(long token, String path) {
		this.token = token;
		this.path = path;
	}


	@Override
	public final void dispatch() {
		MyDriveFS md = getMyDrive();
		Login login = md.getLoginByToken(token);
		Directory newDir = md.getDirectoryByPath(login.getCurrentDir(), path);
		login.setCurrentDir(newDir);		
	}
}

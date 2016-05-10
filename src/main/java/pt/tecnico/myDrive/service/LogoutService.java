package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.exception.InvalidLoginException;

public class LogoutService extends MyDriveService {
	
	private long token;

	public LogoutService(long token) {
		this.token = token;
	}

	@Override
	protected void dispatch() throws InvalidLoginException {
		MyDriveFS md = MyDriveFS.getInstance();
		Login login = md.getLoginByToken(token);
		login.cleanup();
	}

}

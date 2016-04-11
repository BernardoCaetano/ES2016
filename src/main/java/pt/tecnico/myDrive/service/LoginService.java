package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.Login;

import pt.tecnico.myDrive.exception.MyDriveException;

public class LoginService extends MyDriveService {
	
	private String username;
	private String password;
	long token;
	
	public LoginService(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	protected void dispatch() throws MyDriveException {
		MyDriveFS mD = MyDriveFS.getInstance();
		Login login = new Login(mD, this.username, this.password);
		token = login.getToken();
	}

	public final long result() {
		
		return token; 
	}
}

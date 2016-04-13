package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.TextFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.MyDriveException;

public class ReadFileService extends MyDriveService {

	private long _token;
	private String _name;
	private String result;
	
	public ReadFileService(long token, String name) {
		_token = token;
		_name = name;
	}

	@Override
	protected void dispatch() throws MyDriveException {
		MyDriveFS md = MyDriveFS.getInstance();
		Login login = md.getLoginByToken(_token);
		Directory dir = login.getCurrentDir();
		User user = login.getUser();
		TextFile file = dir.getTextFileByName(_name);
		result = file.getContent(user);
	}

	public final String result() {
		return result;
	}

}

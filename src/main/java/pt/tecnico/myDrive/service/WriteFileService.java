package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.TextFile;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.exception.MyDriveException;

public class WriteFileService extends MyDriveService {

	long token;
	String path;
	String content;

	public WriteFileService(long token, String path, String content) {
		
		this.token = token;
		this.path = path;
		this.content = content;
	}

	@Override
	protected void dispatch() throws MyDriveException {
		MyDriveFS md = getMyDrive();
		Login login = md.getLoginByToken(token);
		Directory cd = login.getCurrentDir();
		TextFile file = md.getTextFileByPath(cd, path);
				
		file.setContent(content, login.getUser());
		
		

	}

}

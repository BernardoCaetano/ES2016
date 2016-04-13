package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.TextFile;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.exception.ImmutableLinkContentException;
import pt.tecnico.myDrive.exception.MyDriveException;

public class WriteFileService extends MyDriveService {

	long token;
	String fileName;
	String content;

	public WriteFileService(long token, String fileName, String content) {
		this.token = token;
		this.fileName = fileName;
		this.content = content;
	}

	@Override
	protected void dispatch() throws MyDriveException {
		Login login = getMyDrive().getLoginByToken(token);
		TextFile file = login.getCurrentDir().getTextFileByName(fileName);
		
		
		file.setContent(content, login.getUser());
		

	}

}

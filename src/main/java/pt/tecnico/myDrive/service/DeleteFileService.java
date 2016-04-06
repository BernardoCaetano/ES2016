package pt.tecnico.myDrive.service;

import java.nio.file.AccessDeniedException;

import pt.tecnico.myDrive.domain.AbstractFile;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.exception.MyDriveException;

public class DeleteFileService extends MyDriveService {

	long token;
	String name;
	
	public DeleteFileService(long token, String name) {
		this.token = token;
		this.name = name;
	}

	@Override
	protected void dispatch() throws MyDriveException {
		Login login = getMyDrive().getLoginByToken(token);
		AbstractFile file = login.getCurrentDir().getFileByName(name);
		file.remove();
	}

	
}

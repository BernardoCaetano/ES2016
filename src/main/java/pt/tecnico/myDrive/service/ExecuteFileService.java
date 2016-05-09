package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.TextFile;
import pt.tecnico.myDrive.exception.MyDriveException;

public class ExecuteFileService extends MyDriveService {

	long token;
	String path;
	String[] args;
	
	public ExecuteFileService(long token, String path, String[] args) {
		this.token = token;
		this.path = path;
		this.args = args;
	}
	
	public ExecuteFileService(long token, String path){
		this.token = token;
		this.path = path;
	}
	
	@Override
	protected void dispatch() throws MyDriveException {
		MyDriveFS md = getMyDrive();
		Login l = md.getLoginByToken(token);
		TextFile t = md.getTextFileByPath(l.getCurrentDir(), path);
		t.execute(l.getUser(), args);
	}
}

package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.TextFile;
import pt.tecnico.myDrive.exception.InvalidDirectoryContentException;
import pt.tecnico.myDrive.exception.InvalidTypeOfFileException;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Login;

public class CreateFileService extends MyDriveService {
	long token;
	String fileName;
	String typeOfFile;
	String content;

	public CreateFileService(long token, String fileName, String typeOfFile) {
		this.token = token;
		this.fileName = fileName;
		this.typeOfFile = typeOfFile;
	}

	public CreateFileService(long token, String fileName, String typeOfFile, String content) {
		this.token = token;
		this.fileName = fileName;
		this.typeOfFile = typeOfFile;
		this.content = content;
	}

	@Override
	public final void dispatch() throws MyDriveException {
		Login login = getMyDrive().getLoginByToken(token);

		if (typeOfFile == "TextFile") {
			new TextFile(login.getMyDrive(), login.getCurrentDir(), login.getUser(), fileName, content);
		} else if (typeOfFile == "Directory") {
			if (content != "") {
				new Directory(login.getMyDrive(), login.getCurrentDir(), login.getUser(), fileName);
			} else {
				throw new InvalidDirectoryContentException();
			}
		} else if (typeOfFile == "Link") {
			new Link(login.getMyDrive(), login.getCurrentDir(), login.getUser(), fileName, content);
		} else if (typeOfFile == "App") {
			new App(login.getMyDrive(), login.getCurrentDir(), login.getUser(), fileName, content);
		} else {
			throw new InvalidTypeOfFileException(typeOfFile);
		}
	}
}

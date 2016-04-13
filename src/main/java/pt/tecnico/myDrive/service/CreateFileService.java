package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.domain.TextFile;
import pt.tecnico.myDrive.exception.CreateDeniedException;
import pt.tecnico.myDrive.exception.InvalidAppContentException;
import pt.tecnico.myDrive.exception.InvalidDirectoryContentException;
import pt.tecnico.myDrive.exception.InvalidLinkContentException;
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

		if (login.getUser().canWrite(login.getCurrentDir())) {
			if (typeOfFile == "TextFile") {
				if (content != null) {
					new TextFile(login.getMyDrive(), login.getCurrentDir(), login.getUser(), fileName, content);
				} else {
					new TextFile(login.getMyDrive(), login.getCurrentDir(), login.getUser(), fileName);
				}

			} else if (typeOfFile == "Directory") {
				if (content != null) {
					throw new InvalidDirectoryContentException();
				} else {
					new Directory(login.getMyDrive(), login.getCurrentDir(), login.getUser(), fileName);
				}

			} else if (typeOfFile == "Link") {
				if (content != null) {
					new Link(login.getMyDrive(), login.getCurrentDir(), login.getUser(), fileName, content);
				} else {
					throw new InvalidLinkContentException();
				}

			} else if (typeOfFile == "App") {
				if (content != null) {
					new App(login.getMyDrive(), login.getCurrentDir(), login.getUser(), fileName, content);
				} else {
					new App(login.getMyDrive(), login.getCurrentDir(), login.getUser(), fileName);
				}

			} else {
				throw new InvalidTypeOfFileException(typeOfFile);
			}
		}else{
			throw new CreateDeniedException(login.getUser().getUsername());
		}
	}
}

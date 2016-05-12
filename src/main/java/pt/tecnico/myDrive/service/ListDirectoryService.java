package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.service.dto.AbstractFileDTO;

import java.util.ArrayList;
import java.util.List;

import pt.tecnico.myDrive.domain.AbstractFile;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.exception.MyDriveException;

public class ListDirectoryService extends MyDriveService {
	long loginToken;
	ArrayList<AbstractFileDTO> result = new ArrayList<AbstractFileDTO>();
	String path;

	public ListDirectoryService(long token) {
		loginToken = token;
		path = ".";
	}
	
	public ListDirectoryService(long token, String path) {
		loginToken = token;
		this.path = path;
	}

	@Override
	protected void dispatch() throws MyDriveException {
		MyDriveFS mD = getMyDrive();
		Login login = mD.getLoginByToken(loginToken);
		Directory currentDirectory = login.getCurrentDir();
		Directory toListDirectory = mD.getDirectoryByPath(currentDirectory, path);
		ArrayList<AbstractFile> files = toListDirectory.getFilesSimpleSorted(login.getUser());
		
		result.add(new AbstractFileDTO(toListDirectory, "."));
		result.add(new AbstractFileDTO(toListDirectory.getParent(), ".."));
		
		for (AbstractFile file : files) {
			result.add(file.convertToDTO());
		}
	}
	
	public final ArrayList<AbstractFileDTO> result() {
		return result;
	}
}

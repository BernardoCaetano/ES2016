package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.service.dto.AbstractFileDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pt.tecnico.myDrive.domain.AbstractFile;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.exception.MyDriveException;

public class ListDirectoryService extends MyDriveService {
	long loginToken;
	ArrayList<AbstractFileDTO> result = new ArrayList<AbstractFileDTO>();

	public ListDirectoryService(long token) {
		loginToken = token;
	}

	@Override
	protected void dispatch() throws MyDriveException {
		Login login = getMyDrive().getLoginByToken(loginToken);
		Directory directory = login.getCurrentDir();
		ArrayList<AbstractFile> files = directory.getFilesSimpleSorted();
		
		result.add(new AbstractFileDTO(directory, "."));
		result.add(new AbstractFileDTO(directory.getParent(), ".."));
		
		for (AbstractFile file : files) {
			result.add(new AbstractFileDTO(file));
		}
	}
	
	public final List<AbstractFileDTO> result() {
		return result;
	}
}

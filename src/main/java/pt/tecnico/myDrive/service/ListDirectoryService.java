package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.service.dto.AbstractFileDTO;

import java.util.List;

import pt.tecnico.myDrive.exception.MyDriveException;

public class ListDirectoryService extends MyDriveService {
	List<AbstractFileDTO> result;

	public ListDirectoryService(long token) {
		// TODO 
	}

	@Override
	protected void dispatch() throws MyDriveException {
		// TODO

	}

	public final List<AbstractFileDTO> result() {
		// TODO
		return result; // FIXME
	}
}

package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.service.dto.VariableDTO;

public class ShowVariableService extends MyDriveService {

	long token;
	String name;
	VariableDTO result;

	public ShowVariableService(long token, String name) {
		this.token = token;
		this.name = name;
	}

	@Override
	protected void dispatch() throws MyDriveException {
		// TODO Auto-generated method stub

	}
	
	public VariableDTO result() {
		return null; //TODO
	}

}

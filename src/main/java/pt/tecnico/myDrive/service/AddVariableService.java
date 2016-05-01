package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.exception.MyDriveException;

public class AddVariableService extends MyDriveService {
	
	long token;
	String name;
	String value;
	
	public AddVariableService(long token, String name, String value) {
		this.token = token;
		this.name = name;
		this.value = value;
	}
	
	@Override
	protected void dispatch() throws MyDriveException {
		// TODO Auto-generated method stub

	}
}

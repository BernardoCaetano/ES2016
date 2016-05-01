package pt.tecnico.myDrive.service;

import java.util.List;

import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.service.dto.VariableDTO;

public class AddVariableService extends MyDriveService {
	
	long token;
	String name;
	String value;
	VariableDTO result;
	
	public AddVariableService(Long token, String name) {
		this.token = token;
		this.name = name;
	}
	
	public AddVariableService(long token, String name, String value) {
		this.token = token;
		this.name = name;
		this.value = value;
	}
	
	@Override
	protected void dispatch() throws MyDriveException {
		// TODO Auto-generated method stub

	}
	
	public List<VariableDTO> result() {
		return null; //TODO 
	}

}
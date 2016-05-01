package pt.tecnico.myDrive.service;

import java.util.List;

import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.service.dto.VariableDTO;

public class AddVariableService extends MyDriveService {
	
	String name;
	String value;
	VariableDTO result;
	
	public AddVariableService(String name) {
		this.name = name;
	}
	
	public AddVariableService(String name, String value) {
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

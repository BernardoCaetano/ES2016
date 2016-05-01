package pt.tecnico.myDrive.service;

import java.util.List;

import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.service.dto.VariableDTO;

public class ListVariablesService extends MyDriveService {

	long token;
	List<VariableDTO> result;
	
	public ListVariablesService(long token) {
	}

	@Override
	protected void dispatch() throws MyDriveException {
		// TODO Auto-generated method stub
	}
	
	public List<VariableDTO> result() {
		return null; //TODO
	}
}

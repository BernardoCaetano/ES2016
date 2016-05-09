package pt.tecnico.myDrive.service;

import java.util.Set;

import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.Variable;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.service.dto.VariableDTO;

public class ShowVariableService extends MyDriveService {

	private long token;
	private String name;
	private VariableDTO result;

	public ShowVariableService(long token, String name) {
		this.token = token;
		this.name = name;
	}

	@Override
	protected void dispatch() throws MyDriveException {
		MyDriveFS md = getMyDrive();
		Login login = md.getLoginByToken(token);
		Variable v = login.getVariableByName(name);
		result =new VariableDTO(v);
		
	}
	
	public VariableDTO result() {
		return result;
	}

}

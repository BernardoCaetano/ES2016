package pt.tecnico.myDrive.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.Variable;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.service.dto.VariableDTO;

public class AddVariableService extends MyDriveService {
	
	private long token;
	private String name;
	private String value;
	private List<VariableDTO> result = new ArrayList<VariableDTO>();
	
	public AddVariableService(long token, String name, String value) {
		this.token = token;
		this.name = name;
		this.value = value;
	}

	@Override
	protected void dispatch() throws MyDriveException {
		MyDriveFS mD = getMyDrive();
		Login login = mD.getLoginByToken(token);
		login.addVariable(name, value);

		for (Variable var : login.getVariableSet()) {
			result.add(new VariableDTO(var));
		}
		
		Collections.sort(result);
	}
	
	public List<VariableDTO> result() {
		return result;
	}
}

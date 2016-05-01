package pt.tecnico.myDrive.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.Variable;
import pt.tecnico.myDrive.exception.MyDriveException;
import pt.tecnico.myDrive.service.dto.VariableDTO;

public class ListVariablesService extends MyDriveService {

	long token;
	List<VariableDTO> result = new ArrayList<VariableDTO>();
	
	public ListVariablesService(long token) {
		this.token = token;
	}

	@Override
	protected void dispatch() throws MyDriveException {
		Login login = getMyDrive().getLoginByToken(token);
		Set<Variable> vars = login.getVariableSet();
		
		for (Variable v : vars) {
			result.add(new VariableDTO(v));
		}
		
		Collections.sort(result);
	}
	
	public List<VariableDTO> result() {
		return result;
	}
}

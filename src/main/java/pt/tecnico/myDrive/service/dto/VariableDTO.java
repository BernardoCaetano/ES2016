package pt.tecnico.myDrive.service.dto;

import pt.tecnico.myDrive.domain.Variable;

public class VariableDTO implements Comparable<VariableDTO> {
	
	String name;
	String value;
	
	public VariableDTO(Variable variable) {
		name = variable.getName();
		value = variable.getValue();
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public int compareTo(VariableDTO other) {
		return getName().compareTo(other.getName());
	}
}

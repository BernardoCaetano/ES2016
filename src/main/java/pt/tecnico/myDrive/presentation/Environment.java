package pt.tecnico.myDrive.presentation;

import java.util.List;

import pt.tecnico.myDrive.service.AddVariableService;
import pt.tecnico.myDrive.service.ListVariablesService;
import pt.tecnico.myDrive.service.ShowVariableService;
import pt.tecnico.myDrive.service.dto.VariableDTO;

public class Environment extends MyDriveCommand {

	public Environment(MyDriveShell shell) {
		super(shell, "env", "list or set environment variables");
	}
	

	@Override
	void execute(String[] args) {
		if (args.length == 0) {
			ListVariablesService lvs = new ListVariablesService(getCurrentToken());
			lvs.execute();
			List<VariableDTO> vars = lvs.result();
			
			for (VariableDTO v : vars) {
				println(v.getName() + '=' + v.getValue());
			}
			println("Use '" + getName() + " <name>' to list a single variable");
			println("Use '" + getName() + "<name> <value>' to set the value of a variable with name <name>");
			
		} else if (args.length == 1) {
			ShowVariableService svs = new ShowVariableService(getCurrentToken(), args[0]);
			svs.execute();
			VariableDTO v = svs.result();
			println(v.getName() + '=' + v.getValue());
			
		} else {
			AddVariableService avs = new AddVariableService(getCurrentToken(), args[0], args[1]);
			avs.execute();
		}

	}

}

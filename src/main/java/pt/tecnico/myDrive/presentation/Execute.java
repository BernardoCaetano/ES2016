package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.service.ExecuteFileService;

public class Execute extends MyDriveCommand {

	public Execute(MyDriveShell shell) {
		super(shell, "do", "execute file");
	}

	@Override
	public void execute(String[] args) {

		if (args.length < 1)
			throw new RuntimeException("USAGE: '" + getName() + " <path>' to  execute a file in path <path> or '" + getName() + " <path> <args>' to execute a file with the arguments <args> in path <path> ");

		if (args.length == 1) {
			new ExecuteFileService(getCurrentToken(), args[0]).execute();
		} else {
			
			String[] al = new String[args.length-1];

			for (int i = 1; i < args.length; i++) {
				al[i-1]= args[i];
			}
			new ExecuteFileService(getCurrentToken(), args[0], al).execute();
		}
	}
}

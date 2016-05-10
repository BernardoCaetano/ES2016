package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.service.ChangeDirectoryService;

public class ChangeWorkingDirectory extends MyDriveCommand {

	public ChangeWorkingDirectory(MyDriveShell shell) {
		super(shell, "cwd", "change working directory" );
	}

	@Override
	void execute(String[] args) {
		if(args.length != 1){
			throw new RuntimeException("USAGE: " + getName() + " <path>");
		}
		
		ChangeDirectoryService service = new ChangeDirectoryService(getCurrentToken(), args[0]);
		service.execute();
		String result = service.result();
		
		println(result);
	}
}


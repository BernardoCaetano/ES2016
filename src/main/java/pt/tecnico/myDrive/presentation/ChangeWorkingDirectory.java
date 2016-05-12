package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.service.ChangeDirectoryService;

public class ChangeWorkingDirectory extends MyDriveCommand {

	public ChangeWorkingDirectory(MyDriveShell shell) {
		super(shell, "cwd", "change working directory" );
	}

	@Override
	public void execute(String[] args) {
		ChangeDirectoryService service;
		
		if(args.length != 0 && args.length != 1){
			throw new RuntimeException("USAGE: '" + getName() + " <path>'");
		}
		
		if(args.length == 1){
			service = new ChangeDirectoryService(getCurrentToken(), args[0]);	
		} 
		else {
			service = new ChangeDirectoryService(getCurrentToken());
		}
		
		service.execute();
		String result = service.result();		
		println(result);
	}
}


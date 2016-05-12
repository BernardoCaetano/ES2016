package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.service.WriteFileService;

public class Write extends MyDriveCommand {

	public Write(MyDriveShell shell) {
		super(shell, "update", "write text to file");
	}

	@Override
	public void execute(String[] args) {
		if (args.length != 2) {
			throw new RuntimeException("USAGE: " + getName() + " <path> <text>");
		} else {
			WriteFileService write = new WriteFileService(getCurrentToken(), args[0], args[1]);
			write.execute();
		}
	}

}

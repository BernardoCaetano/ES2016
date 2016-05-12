package pt.tecnico.myDrive.presentation;

import java.util.ArrayList;

import pt.tecnico.myDrive.service.ListDirectoryService;
import pt.tecnico.myDrive.service.dto.AbstractFileDTO;

public class List extends MyDriveCommand {

	public List(MyDriveShell shell) {
		super(shell, "ls", "list files in a directory");
	}

	@Override
	public void execute(String[] args) {
		ListDirectoryService service;
		
		if (args.length < 1) {
			service = new ListDirectoryService(getCurrentToken(), ".");
		} else {
			service = new ListDirectoryService(getCurrentToken(), args[0]);
		}
		
		service.execute();
		
		ArrayList<AbstractFileDTO> result = new ArrayList<AbstractFileDTO>();
		result = service.result();
		
		String line;
		
		for (AbstractFileDTO file : result) {
			line = "";
			line += file.getType() + " ";
			line += file.getPermissions() + " ";
			line += file.getDimension() + " ";
			line += file.getOwner() + " ";
			line += file.getId() + " ";
			line += file.getLastModified() + " ";
			line += file.getName();
			line += file.getContent();
			println(line);
		}
	}

}

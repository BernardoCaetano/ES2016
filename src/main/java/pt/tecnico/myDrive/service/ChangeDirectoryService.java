package pt.tecnico.myDrive.service;

public class ChangeDirectoryService extends MyDriveService {
	
	String path;

	public ChangeDirectoryService(long token, String newPath) {
		path = newPath;
	}


	@Override
	public final void dispatch() {
		
	}
}

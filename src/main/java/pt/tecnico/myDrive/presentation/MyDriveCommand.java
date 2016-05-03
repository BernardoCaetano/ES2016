package pt.tecnico.myDrive.presentation;

public abstract class MyDriveCommand extends Command {

	public MyDriveCommand(MyDriveShell shell, String name) {
		super(shell, name);
	}

	public MyDriveCommand(MyDriveShell shell, String name, String help) {
		super(shell, name, help);
	}
	
	@Override
	protected MyDriveShell getShell() {
		return (MyDriveShell) super.getShell();
	}

	Long switchToToken(String username) {
		return getShell().switchToToken(username);
	}

	void switchToNewToken(String username, long token) {
		getShell().switchToNewToken(username, token);
	}

	String getCurrentUsername() {
		return getShell().getCurrentUsername();
	}

	long getCurrentToken() {
		return getShell().getCurrentToken();
	}

}

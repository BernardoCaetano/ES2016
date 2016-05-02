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

	long getToken(String username) {
		return getShell().getToken(username);
	}

	void addToken(String username, long token) {
		getShell().addToken(username, token);
	}

	String getCurrentUsername() {
		return getShell().getCurrentUsername();
	}

	long getCurrentToken() {
		return getShell().getCurrentToken();
	}

}

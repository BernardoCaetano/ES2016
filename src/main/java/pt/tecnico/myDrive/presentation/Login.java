package pt.tecnico.myDrive.presentation;

public class Login extends MyDriveCommand {

	public Login(MyDriveShell shell) {
		super(shell, "login", "start session as user");
	}

	@Override
	void execute(String[] args) {
		if (args.length != 1 && args.length != 2) {
			throw new RuntimeException("USAGE: login <username> [<password]]");
		} else {
			// TODO
		}
	}
}

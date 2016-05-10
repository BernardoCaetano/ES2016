package pt.tecnico.myDrive.presentation;

import pt.tecnico.myDrive.service.LoginService;

public class Login extends MyDriveCommand {

	public Login(MyDriveShell shell) {
		super(shell, "login", "start session as user");
	}

	@Override
	void execute(String[] args) {
		if (args.length != 1 && args.length != 2) {
			throw new RuntimeException("USAGE: login <username> [<password]]");
		} else {
			LoginService service = new LoginService(args[0], args[1]);
			service.execute();
		}
	}
}

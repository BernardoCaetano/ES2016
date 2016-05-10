package pt.tecnico.myDrive.presentation;

public class Key extends MyDriveCommand {

	public Key(MyDriveShell shell) {
		super(shell, "token", "change current session");
	}

	@Override
	void execute(String[] args) {
		
		if (args.length > 1 ) {
			throw new RuntimeException("USAGE: '" + getName() + " <username>' to change current session or '" + getName()
					+ "' to see current session details");
		} else if (args.length == 0) {
			println("Active Session: Username- " + getCurrentUsername() + " Token- " + getCurrentToken());
		} else if (args.length == 1) {
			if (switchToToken(args[0]) != null) {
				println("Active Session: Username- " + getCurrentUsername() + " Token- " + getCurrentToken());
			} else {
				throw new RuntimeException(getName() + " : " + args[0] + " is not logged in");
			}
		}
	}
}
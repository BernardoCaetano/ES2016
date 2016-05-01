package pt.tecnico.myDrive.presentation;

public class Key extends Command {

	public Key(Shell shell) {
		super(shell, "token", "change current session");
	}

	@Override
	void execute(String[] args) {
		// TODO Auto-generated method stub
		if (args.length < 1 || args.length > 1)
			throw new RuntimeException("USAGE: " + getName() + " [username]");
	}

}
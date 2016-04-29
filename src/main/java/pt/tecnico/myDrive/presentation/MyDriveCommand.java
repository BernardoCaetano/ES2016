package pt.tecnico.myDrive.presentation;

public abstract class MyDriveCommand extends Command {

	public MyDriveCommand(Shell shell, String name) {
		super(shell, name);
	}

	public MyDriveCommand(Shell shell, String name, String help) {
		super(shell, name, help);
	}

}

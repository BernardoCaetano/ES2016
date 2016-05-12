package pt.tecnico.myDrive.presentation;

public class Help extends Command {

	public Help(Shell shell) {
		super(shell, "help", "this help command");
	}

	@Override
	public void execute(String[] args) {
		if (args.length == 0) {
			println("Valid commands:");
			for (String s : getShell().list()) {
				println("\t" + s);
			}
			println("Use '" + getName() + " <command name>' for command details.");
		} else {
			for (String s : args) {
				Command c = getShell().getCommand(s);
				if (c != null) {
					println(c.getName() + ": " + c.getHelp());
				} else {
					println(s + ": (invalid command)");
				}
			}
		}
	}

}

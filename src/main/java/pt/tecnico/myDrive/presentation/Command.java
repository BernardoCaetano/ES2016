package pt.tecnico.myDrive.presentation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/* Based on the tecnico-softeng/PhoneBook-V3 example */
public abstract class Command {

	protected static final Logger log = LogManager.getRootLogger();
	private String name;
	private String help;
	private Shell shell;

	public Command(Shell shell, String name) {
		this(shell, name, "<no help>");
	}

	public Command(Shell shell, String name, String help) {
		this.name = name;
		this.help = help;
		this.shell = shell;
		shell.add(Command.this);
	}

	public String getName() {
		return name;
	}

	public String getHelp() {
		return help;
	}

	protected Shell getShell() {
		return shell;
	}

	abstract void execute(String args[]);

	public void print(String s) {
		shell.print(s);
	}

	public void println(String s) {
		shell.println(s);
	}

	public void flush() {
		shell.flush();
	}

}

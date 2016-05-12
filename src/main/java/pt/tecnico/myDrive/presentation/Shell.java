package pt.tecnico.myDrive.presentation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/* Based on the tecnico-softeng/PhoneBook-V3 example */
public abstract class Shell {

	protected static final Logger log = LogManager.getRootLogger();
	private Map<String, Command> commands = new TreeMap<String, Command>();

	private PrintWriter out;
	private String name;

	public Shell(String name, Writer write) {
		this(name, write, true);
	}

	public Shell(String name) {
		this(name, new PrintWriter(System.out, true));
	}

	public Shell(String name, Writer writer, boolean flush) {
		this.name = name;
		this.out = new PrintWriter(writer, flush);

		new Command(Shell.this, "quit", "quit the command interpreter") {
			@Override
			public void execute(String[] args) {
				Shell.this.quit();
			}
		};

		new Help(Shell.this);
	}

	public void print(String s) {
		out.print(s);
	}

	public void println(String s) {
		out.println(s);
	}

	public void flush() {
		out.flush();
	}

	/* false if it redefines an existing command */
	boolean add(Command c) {
		return (commands.put(c.getName(), c) == null ? true : false);

	}

	public Command getCommand(String name) {
		return commands.get(name);
	}

	public Collection<String> list() {
		return Collections.unmodifiableCollection(commands.keySet());
	}

	private String getPrompt() {
		return "$ ";
	}

	public void execute() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		println("Welcome to " + name + " shell.");
		println("('quit' to leave; 'help' for help)");
		print(getPrompt());
		flush();

		String line;
		try {
			while ((line = in.readLine()) != null) {
				String args[] = line.split(" ");
				String arg0 = args[0];
				Command c = getCommand(arg0);

				if (c == null) {
					if (arg0.length() > 0) {
						println(arg0 + ": command not found ('help' for command list).");
					}
				} else {
					try {
						c.execute(Arrays.copyOfRange(args, 1, args.length));
					} catch(RuntimeException e){
						println(arg0 + ": " + e.getMessage());
					}
				}
				print(getPrompt());
				flush();
			}
		} catch (IOException e) {
			println("failed to read line: " + e.getMessage());
		}

		println(name + " ending.");
		Shell.this.quit();
		
	}
	
	protected void quit() {
		println(name + " quit.");
		System.exit(0);
	}

}

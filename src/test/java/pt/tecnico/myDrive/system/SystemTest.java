package pt.tecnico.myDrive.system;

import org.junit.Test;

import pt.tecnico.myDrive.presentation.ChangeWorkingDirectory;
import pt.tecnico.myDrive.presentation.Environment;
import pt.tecnico.myDrive.presentation.Execute;
import pt.tecnico.myDrive.presentation.Import;
import pt.tecnico.myDrive.presentation.Key;
import pt.tecnico.myDrive.presentation.List;
import pt.tecnico.myDrive.presentation.Login;
import pt.tecnico.myDrive.presentation.MyDriveShell;
import pt.tecnico.myDrive.presentation.Write;
import pt.tecnico.myDrive.service.AbstractServiceTest;

public class SystemTest extends AbstractServiceTest {

	private MyDriveShell sh;
	
	@Override
	protected void populate() {
		sh = new MyDriveShell();
	}

	@Test
	public void success() {
		
		// import <xml filename>
		// this file is incuded in resources
		(new Import(sh)).execute(new String[]{"execute.xml"});

		// login	username	[password]
		(new Login(sh)).execute(new String[]{"root", "***"});
		
		// cwd	[path]
		(new ChangeWorkingDirectory(sh)).execute(new String[]{"/home/jtb/bin"});
		
		// ls	[path]
		(new List(sh)).execute(new String[]{});

		// do	path		[args]
		(new Execute(sh)).execute(new String[]{"greetings", "hello there"});
		
		// update	path		text
		(new Write(sh)).execute(new String[]{"/home/jtb/example", "text"});
		
		// env	[name		[value]]
		(new Environment(sh)).execute(new String[]{});
		
		// token	[username]
		(new Key(sh)).execute(new String[]{});

	}
}

package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.presentation.FileViewer;
import pt.tecnico.myDrive.presentation.Hello;

import mockit.Mock;
import mockit.MockUp;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;

import pt.tecnico.myDrive.domain.AbstractFile;
import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.TextFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.AssociationDoesNotExistException;
import pt.tecnico.myDrive.exception.ExecuteFileException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidLoginException;
import pt.tecnico.myDrive.exception.UndefinedVariableException;

@RunWith(JMockit.class)
public class ExecuteFileTest extends TokenReceivingTest {

	MyDriveFS mD;
	ExecuteFileService service;
	private App appNP;
	private App exampleApp;
	User newUser;
	private static final String pdfFile = "appWithoutPermissions.pdf";
	private static final String linkFile = "link to useless app";
	private static final String helloClass = "pt.tecnico.myDrive.presentation.Hello";
	private static final String helloExecute = helloClass + ".execute";
	private static final String helloSum = helloClass + ".sum";
	
	private static final String invalidTextFileContent = helloSum + " 42 80085\n"
								+ helloSum + " universe and everything\n";
	
	
	@Override
	protected void populate() {
		
		mD = MyDriveFS.getInstance();
		Login login;
		
		newUser = new User(mD, "insertUsername", "insertPassword", "insert Name", "rwxdrwxd", null);
		super.populate("insertUsername", "insertPassword");
		login = mD.getLoginByToken(validToken);
		
 		Directory currentDir = login.getCurrentDir();
 		
 		appNP = new App(mD, currentDir, newUser, pdfFile);
 		appNP.setPermissions("rw-d----");
 		
 		new Link(mD, currentDir ,newUser, "link to useless app" ,"./appWithoutPermissions.pdf");
 		
 		exampleApp = new App(mD, currentDir, newUser, "exampleApp", helloSum);
 		
 		new Link(mD, currentDir, newUser, "linkWith$", "/home/$NEWUSER/exampleApp");
		new Link(mD, currentDir, newUser, "linkWith$FailFile", "/home/$JOHN/exampleApp");
		new Link(mD, currentDir, newUser, "linkWith$FailEnv", "/home/$JAKE/exampleApp");
		
		User john = new User(mD, "john", "windything", "john", "rwxdrwxd", null);
	}
	
	private void executeApp(String method, String[] arguments) {
 		exampleApp.setPermissions("rwxdrwxd");
 		exampleApp.setContent(method);
		
		ExecuteFileService service = new ExecuteFileService(validToken, "exampleApp", arguments);
		service.execute();

		new Verifications() {{
				Hello.sum(arguments);
		}};
	}	
	
	@Test
	public void successApp() {
		executeApp(helloSum, new String[]{"42", "80085"});
	}
	
	@Test
	public void successAppMain() {
		executeApp(helloClass, new String[]{});
	}

	@Test(expected=ExecuteFileException.class)
	public void failAppArguments() {
		executeApp(helloSum, new String[]{"four", "two"});
	}
	
	@Test(expected=ExecuteFileException.class)
	public void failMethod() {
		executeApp(helloClass + ".fail", new String[]{});
	}	
	
	@Test
	public void successTextFile() { 		
 		exampleApp.setPermissions("rwxdrwxd");
 		Directory currentDir = mD.getLoginByToken(validToken).getCurrentDir();
 		App helloExecuteApp = new App(mD, currentDir, newUser, "helloExecuteApp", helloExecute);
 		
 		String content = exampleApp.getPath() + " 42 80085\n"
 									+ exampleApp.getPath() + " 1 -1 1 -1 1 -1\n"
 									+ helloExecuteApp.getPath() + " rabbit pig chipmunk\n";
 		
 		TextFile file = new TextFile(mD, currentDir, newUser, "file", content);
		ArrayList<String[]> arguments = new ArrayList<String[]>();
		arguments.addAll(file.getArguments());

		ExecuteFileService service = new ExecuteFileService(validToken, "file");
		service.execute();

		new Verifications() {{
				Hello.sum(arguments.get(0));
				Hello.sum(arguments.get(1));
				Hello.execute(arguments.get(2));
		}};
	}

	
	@Test
	public void successExecuteAssociationApplication() {
		new MockUp<ExecuteFileService>() {
			@Mock
			void dispatch() { 
				FileViewer.executePDF(pdfFile);
			}
		};
		
		service = new ExecuteFileService(validToken, pdfFile, null);
		service.execute();
		
		new Verifications() {
			{
				FileViewer.executePDF(pdfFile);
			}
        };
	}
	
	@Test
	public void successExecuteAssociationLinkThatPointsToFile() {
		new MockUp<ExecuteFileService>() {
			@Mock
			void dispatch() {
				FileViewer.executePDF(linkFile);
			}
		};

		service = new ExecuteFileService(validToken, linkFile, null);
		service.execute();

		new Verifications() {
			{
				FileViewer.executePDF(linkFile);
			}
		};
	}
	
	@Test(expected=AssociationDoesNotExistException.class)
	public void noAssociationFound() {
		final String nonExisting = "random.org";

		new MockUp<ExecuteFileService>() {
			@Mock
			void dispatch() { 
				throw new AssociationDoesNotExistException(nonExisting);
			}
		};
		
		new ExecuteFileService(validToken, nonExisting, null).execute();;
	}
	
	@Test(expected = InvalidLoginException.class)
	public void expiredSessionTest2h05minAgo() throws InvalidLoginException {
		super.setLastActivity2h05minAgo();
		ExecuteFileService service = new ExecuteFileService(validToken, ".");
		service.execute();		
	}

	@Test
	public void sessionStillValidTest1h55min() {
		super.setLastActivity1h55minAgo();
		executeApp(helloSum, new String[]{"42", "80085"});
	}

	@Test(expected = InvalidLoginException.class)
	public void nonExistentTokenTest() throws InvalidLoginException {
		ExecuteFileService service = new ExecuteFileService(invalidToken, pdfFile);
		service.execute();
	}
	
	/// ENVIRONMENT LINKS TEST ///
	@Test
	public void successLinkWithEnvironment() {

		new MockUp<Directory>() {
			@Mock
			String translate(String path) {
				return "/home/insertUsername/exampleApp";
			}
		};
		
		String[] args= new String[]{"23", "13"};
		ExecuteFileService service = new ExecuteFileService(validToken, "linkWith$", args );
		service.execute();
		
		new Verifications(){{
			Hello.sum(args);
		}};
	}

	@Test(expected = FileNotFoundException.class)
	public void failureLinkFileDoesNotExist() {

		new MockUp<Directory>() {
			@Mock
			String translate(String path) {
				return "/home/john/exampleApp";
			}
		};

		String[] args= new String[]{"23", "13"};
		ExecuteFileService service = new ExecuteFileService(validToken, "linkWith$FailFile", args );
		service.execute();

	}

	@Test (expected = UndefinedVariableException.class)
	public void failureLinkEnvDoesNotExist() {

		new MockUp<Directory>() {
			@Mock
			String translate(String path) throws UndefinedVariableException{
				throw new UndefinedVariableException("$JAKE");
			};	
			
		};

		String[] args= new String[]{"23", "13"};
		ExecuteFileService service = new ExecuteFileService(validToken, "linkWith$FailEnv", args );
		service.execute();

	}

}
package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.presentation.FileViewer;
import pt.tecnico.myDrive.presentation.Hello;

import mockit.Mock;
import mockit.MockUp;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.TextFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.AccessDeniedException;
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
	private App helloExecuteApp;
	private	User newUser;
	private static final String pdfFile = "appWithoutPermissions.pdf";
	private static final String linkFile = "link to useless app";
	private static final String exampleAppPath = "/home/insertUsername/exampleApp";
	private static final String helloExecuteAppPath = "/home/insertUsername/helloExecuteApp";
	private static final String helloClass = "pt.tecnico.myDrive.presentation.Hello";
	private static final String helloExecute = helloClass + ".execute";
	private static final String helloSum = helloClass + ".sum";
	
	private static final String sumArguments = " 42 80085";
	private static final String executeArguments = " rabbit pig chipmunk";
	
	private static final String validTextFile = exampleAppPath + sumArguments + "\n"
												+ exampleAppPath + sumArguments + "\n"
												+ helloExecuteAppPath + executeArguments + "\n";
	
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
		exampleApp.setPermissions("rwxdrwxd");
 		helloExecuteApp = new App(mD, currentDir, newUser, "helloExecuteApp", helloExecute);
 		helloExecuteApp.setPermissions("rwxdrwxd");
 		
 		new Link(mD, currentDir, newUser, "linkWith$", "/home/$NEWUSER/exampleApp");
		new Link(mD, currentDir, newUser, "linkWith$FailFile", "/home/$JOHN/exampleApp");
		new Link(mD, currentDir, newUser, "linkWith$FailEnv", "/home/$JAKE/exampleApp");
		
		new User(mD, "john", "windything", "john", "rwxdrwxd", null);
	}
	
	private void executeApp(String method, String[] arguments) {
 		exampleApp.setContent(method);
		ExecuteFileService service = new ExecuteFileService(validToken, "exampleApp", arguments);
		service.execute();
	}	
	
	@Test
	public void successApp() {
		String[] arguments = new String[]{"42", "80085"};
		executeApp(helloSum, arguments);
		
		new Verifications() {{
			Hello.sum(arguments);
		}};
	}
	
	@Test
	public void successAppMain() {
		String[] arguments = new String[]{};
		executeApp(helloClass, arguments);
		
		new Verifications() {{
			Hello.main(arguments);
		}};
	}

	@Test(expected=ExecuteFileException.class)
	public void failAppArguments() {
		String[] arguments = new String[]{"four", "two"};
		executeApp(helloSum, arguments);
		
		new Verifications() {{
			Hello.sum(arguments);
		}};
	}
	
	@Test(expected=ExecuteFileException.class)
	public void failMethod() {
		String[] arguments = new String[]{};
		executeApp(helloClass + ".fail", arguments);
		
		new Verifications() {{
			Hello.main(arguments);
		}};
	}
	
	
	private ArrayList<String[]> executeTextFile(String content, String targetPermissions) {
 		Directory currentDir = mD.getLoginByToken(validToken).getCurrentDir();
 		
 		TextFile file = new TextFile(mD, currentDir, newUser, "file", content);
 		file.setPermissions(targetPermissions);
		ArrayList<String[]> arguments = new ArrayList<String[]>();
		arguments.addAll(getTextFileArguments(content));

		ExecuteFileService service = new ExecuteFileService(validToken, "file");
		service.execute();
		
		return arguments;
	}
	
	private ArrayList<String[]> executeTextFile(String content) {
		return executeTextFile(content, "rwxdrwxd");
	}
	
	private ArrayList<String[]> getTextFileArguments(String content) {
		ArrayList<String[]> list = new ArrayList<String[]>();
		String[] lines = content.split("\\n");
		
		for (String line : lines) {
			list.add(getLineArguments(content, line));
		}
		
		return list;
	}
	
	private String[] getLineArguments(String content, String line) {
		return line.substring(line.indexOf(" ") + 1).split(" ");
	}	
	
	@Test
	public void successTextFile() {
		ArrayList<String[]> arguments = executeTextFile(validTextFile);
		new Verifications() {{
			Hello.sum(arguments.get(0));
			Hello.sum(arguments.get(1));
			Hello.execute(arguments.get(2));
	}};
	}
	
	@Test(expected=ExecuteFileException.class)
	public void failTextFileArguments() {
		String content = exampleAppPath + sumArguments + "\n"
						+ exampleAppPath + executeArguments + "\n";
		
		ArrayList<String[]> arguments = executeTextFile(content);
		new Verifications() {{
			Hello.sum(arguments.get(0));
			Hello.sum(arguments.get(1));
	}};
	}
	
	@Test(expected=AccessDeniedException.class)
	public void failTextFileAccessDeniedTarget() {
		exampleApp.setPermissions("rw-drw-d");
		
		ArrayList<String[]> arguments = executeTextFile(validTextFile);
		new Verifications() {{
			Hello.sum(arguments.get(0));
			Hello.sum(arguments.get(1));
	}};
	}
	
	@Test(expected=AccessDeniedException.class)
	public void failTextFileAccessDenied() {		
		ArrayList<String[]> arguments = executeTextFile(validTextFile, "rw-drw-d");
		new Verifications() {{
			Hello.sum(arguments.get(0));
			Hello.sum(arguments.get(1));
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
				return exampleAppPath;
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
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
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidLoginException;
import pt.tecnico.myDrive.exception.UndefinedVariableException;

@RunWith(JMockit.class)
public class ExecuteFileTest extends TokenReceivingTest {

	MyDriveFS mD;
	ExecuteFileService service;
	private App appNP;
	private App appHello;
	private static final String pdfFile = "appWithoutPermissions.pdf";
	private static final String linkFile = "link to useless app";
	private static final String helloFile = "helloFile";
	private static final String helloContent = "pt.tecnico.myDrive.presentation.Hello.sum";
	
	@Override
	protected void populate() {
		
		mD = MyDriveFS.getInstance();
		Login login;
		
		User newUser = new User(mD, "insertUsername", "insertPassword", "insert Name", "rwxdrwxd", null);
		super.populate("insertUsername", "insertPassword");
		login = mD.getLoginByToken(validToken);
		
 		Directory currentDir = login.getCurrentDir();
 		
 		appNP = new App(mD, currentDir, newUser, pdfFile);
 		appNP.setPermissions("rw-d----");
 		
 		appHello = new App(mD, currentDir, newUser, helloFile, helloContent);
 		appHello.setPermissions("rwxdrwxd"); 		
 		
 		new Link(mD, currentDir ,newUser, "link to useless app" ,"./appWithoutPermissions.pdf");
 		
 		new App(mD, currentDir, newUser, "exampleApp", "pt.tecnico.myDrive.presentation.Hello.sum");
 		
 		new Link(mD, currentDir, newUser, "linkWith$", "/home/$NEWUSER/exampleApp");
		new Link(mD, currentDir, newUser, "linkWith$FailFile", "/home/$JOHN/exampleApp");
		new Link(mD, currentDir, newUser, "linkWith$FailEnv", "/home/$JAKE/exampleApp");
		
		User john = new User(mD, "john", "windything", "john", "rwxdrwxd", null);
	}
	
	@Test
	public void successExecuteApplication() {
		successTest();
	}
	
	private void successTest() {
		String[] arguments = new String[]{"42", "80085"}; 
		
		ExecuteFileService service = new ExecuteFileService(validToken, helloFile, arguments);
		service.execute();

		new Verifications() {{
				Hello.sum(arguments);
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
		successTest();
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
package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.presentation.FileViewer;

import mockit.Mock;
import mockit.MockUp;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.AssociationDoesNotExistExceotion;
import pt.tecnico.myDrive.exception.InvalidLoginException;

@RunWith(JMockit.class)
public class ExecuteFileTest extends TokenReceivingTest {

	MyDriveFS mD;
	ExecuteFileService service;
	private static final String pdfFile = "appWithoutPermissions.pdf";
	private static final String linkFile = "link to useless app";
	
	@Override
	protected void populate() {
		
		mD = MyDriveFS.getInstance();
		Login login;
		
		User newUser = new User(mD, "insertUsername", "insertPassword", "insert Name", "rwxdrwxd", null);
		super.populate("insertUsername", "insertPassword");
		login = mD.getLoginByToken(validToken);
		
 		Directory currentDir = login.getCurrentDir();
 		
 		App appNP = new App(mD, currentDir, newUser, pdfFile);
 		appNP.setPermissions("rw-d----");
 		
 		new Link(mD, currentDir ,newUser, "link to useless app" ,"./appWithoutPermissions.pdf");
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
	
	@Test(expected=AssociationDoesNotExistExceotion.class)
	public void noAssociationFound() {
		final String nonExisting = "random.org";

		new MockUp<ExecuteFileService>() {
			@Mock
			void dispatch() { 
				throw new AssociationDoesNotExistExceotion(nonExisting);
			}
		};
		
		new ExecuteFileService(validToken, nonExisting, null).execute();;
	}
	
	@Test(expected = InvalidLoginException.class)
	public void expiredSessionTest2h05minAgo() throws InvalidLoginException {
		// TODO Auto-generated method stub
		
	}

	@Test
	public void sessionStillValidTest1h55min() {
		// TODO Auto-generated method stub
		
	}

	@Test(expected = InvalidLoginException.class)
	public void nonExistentTokenTest() throws InvalidLoginException {
		// TODO Auto-generated method stub
		
	}
}
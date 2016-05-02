package pt.tecnico.myDrive.service;

import static org.junit.Assert.*;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Test;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.TextFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.InvalidLoginException;

public class ExecuteFileTest extends TokenReceivingTest {

	MyDriveFS mD;
	ExecuteFileService service;
	
	@Override
	protected void populate() {
		
		mD = MyDriveFS.getInstance();
		Login login;
		
		User newUser = new User(mD, "insertUsername", "insertPassword", "insert Name", "rwxdrwxd", null);
		super.populate("insertUsername", "insertPassword");
		login = mD.getLoginByToken(validToken);
		
 		Directory currentDir = login.getCurrentDir();
 		
 		// This App and TextFile will be changed to future tests
 		App appNP = new App(mD, currentDir, newUser, "appWithoutPermissions");
 		appNP.setPermissions("rw-d----");
 		
 		new TextFile(mD, currentDir, newUser, "simpleTextFile");
	}

	@Test
	public void success() {
		// TODO 
	}
	
	@Test
	public void successExecuteAssociation() {
		//FIXME this mockup will need to be changed. Maybe call a method and use verifications?
		new MockUp<ExecuteFileService>() {
			@Mock
			void dispatch() { return; }
		};
		service = new ExecuteFileService(validToken, "appWithoutPermissions", null);		
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
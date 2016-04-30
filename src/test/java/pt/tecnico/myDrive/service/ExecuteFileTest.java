package pt.tecnico.myDrive.service;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.InvalidLoginException;

public class ExecuteFileTest extends TokenReceivingTest {

	MyDriveFS mD;
	
	@Override
	protected void populate() {
		
		mD = MyDriveFS.getInstance();
		Login login;
		
		User newUser = new User(mD, "insertUsername", "insertPassword", "insert Name", "rwxdrwxd", null);
		super.populate("Wololo", "password");
		login = new Login(mD, "insertUsername", "insertPassword");
		
//		Directory currentDir = login.getCurrentDir();
//		new Directory(mD, currentDir, newUser, "dirName");
	}

	@Test
	public void success() {
		// TODO 
	}

	@Override
	public void expiredSessionTest2h05minAgo() throws InvalidLoginException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionStillValidTest1h55min() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nonExistentTokenTest() throws InvalidLoginException {
		// TODO Auto-generated method stub
		
	}
}
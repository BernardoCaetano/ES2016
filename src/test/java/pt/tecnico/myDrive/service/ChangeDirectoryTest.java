package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.tecnico.myDrive.domain.*;
import pt.tecnico.myDrive.exception.InvalidAppContentException;
import pt.tecnico.myDrive.exception.InvalidDirectoryContentException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.InvalidLinkContentException;
import pt.tecnico.myDrive.exception.InvalidLoginException;
import pt.tecnico.myDrive.exception.InvalidTextFileContentException;
import pt.tecnico.myDrive.exception.InvalidTypeOfFileException;
import pt.tecnico.myDrive.exception.NameAlreadyExistsException;
import pt.tecnico.myDrive.exception.PathMaximumLengthException;
import pt.tecnico.myDrive.exception.CreateDeniedException;;

public class ChangeDirectoryTest extends TokenReceivingTest {

	MyDriveFS mD;
	RootDirectory rootDir;
	Login rootLogin;
	long rootToken;
	
	@Override
	protected void populate() {
		super.populate("root", "***");
		mD = MyDriveFS.getInstance();
		rootDir = mD.getRootDirectory();
		
		User rootUser = mD.getUserByUsername("root");
		User otherUser = new User(mD, "other", "smallerthanthree", "Other Woman", "rwxdrwxd");
		
		rootLogin = new Login(mD, "root", "***");
		rootToken = rootLogin.getToken();

		rootLogin = new Login(mD, "root", "***");
		rootToken = rootLogin.getToken();
		
		rootLogin.setCurrentDir(rootDir);
		
		Directory testDir = new Directory(mD, rootDir, rootUser, "testDir");
		Directory subDir = new Directory(mD, testDir, rootUser, "subDir");
		Directory stuff = new Directory(mD, testDir, rootUser, "stuff");
		Directory manny = new Directory(mD, subDir, rootUser, "manny");
		Directory calavera = new Directory(mD, manny, rootUser, "calavera");
		
		TextFile textFile = new TextFile(mD, testDir, rootUser, "textFile", "Lorem ipsum dolor sit amet");
		
		Directory notMine = new Directory(mD, subDir, otherUser, "notMine");
		
		testDir.setPermissions("rwxdrwxd");
		subDir.setPermissions("rwxdrwxd");
		stuff.setPermissions("rwxdrwxd");
		manny.setPermissions("rwxdrwxd");
		calavera.setPermissions("rwxdrwxd");
		textFile.setPermissions("rwxdrwxd");
		notMine.setPermissions("rwxdrwxd");
	}
	
	@Test
	public void emptyPath() {
		Directory oldCurrentDir = rootLogin.getCurrentDir();
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "");
		service.execute();
		
		assertEquals(rootLogin.getCurrentDir(), oldCurrentDir);
	}


	@Test(expected = InvalidLoginException.class)
	public void expiredSessionTest2h05minAgo() throws InvalidLoginException {
		super.setLastActivity2h05minAgo();
		ChangeDirectoryService service = new ChangeDirectoryService(validToken, ".");
		service.execute();	
		
	}
	
	@Test
	public void sessionStillValidTest1h55min() {
		super.setLastActivity2h05minAgo();
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "testDir");
		service.execute();
		
		assertEquals(rootLogin.getCurrentDir(), mD.getDirectoryByPath(rootDir, "testDir"));		
	}

	@Test(expected = InvalidLoginException.class)
	public void nonExistentTokenTest() throws InvalidLoginException {
		ChangeDirectoryService service = new ChangeDirectoryService(invalidToken, ".");
		service.execute();
	}
}

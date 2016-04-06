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
import pt.tecnico.myDrive.exception.CreateDeniedException;
import pt.tecnico.myDrive.exception.FileNotFoundException;;

public class ChangeDirectoryTest extends TokenReceivingTest {

	MyDriveFS mD;
	RootDirectory rootDir;
	Login rootLogin;
	long rootToken;
	Login otherLogin;
	long otherToken;
	
	Directory testDir;
	Directory subDir;
	Directory mannyDir;
	TextFile textFile;
	Directory notMineDir;
	
	@Override
	protected void populate() {
		super.populate("root", "***");
		mD = MyDriveFS.getInstance();
		rootDir = mD.getRootDirectory();
		
		User rootUser = mD.getUserByUsername("root");
		User otherUser = new User(mD, "other", "smallerthanthree", "Other Woman", "rwxdrwxd");
		
		otherLogin = new Login(mD, "other", "smallerthanthree");
		otherToken = rootLogin.getToken();
		rootLogin = new Login(mD, "root", "***");
		rootToken = rootLogin.getToken();
		
		rootLogin.setCurrentDir(rootDir);
		
		testDir = new Directory(mD, rootDir, rootUser, "testDir");
		subDir = new Directory(mD, testDir, rootUser, "subDir");
		mannyDir = new Directory(mD, subDir, rootUser, "manny");	
		textFile = new TextFile(mD, testDir, rootUser, "textFile", "Lorem ipsum dolor sit amet");
		notMineDir = new Directory(mD, subDir, otherUser, "notMine");
		
		testDir.setPermissions("rwxdrwxd");
		subDir.setPermissions("rwxdrwxd");
		mannyDir.setPermissions("rwxdrwxd");
		textFile.setPermissions("rwxdrwxd");
		notMineDir.setPermissions("rwxdrwxd");
	}
	
	@Test
	public void stayEmptyPath() {
		Directory oldCurrentDir = rootLogin.getCurrentDir();
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "");
		service.execute();
		
		assertEquals(rootLogin.getCurrentDir(), oldCurrentDir);
	}

	@Test
	public void goTospace() {
		Directory spaceDir = new Directory(mD, rootDir, rootLogin.getUser(), " ");
		spaceDir.setPermissions("rwxdrwxd");
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, " ");
		service.execute();
		
		assertEquals(rootLogin.getCurrentDir(), spaceDir);
	}
	
	@Test
	public void goToRoot() {
		rootLogin.setCurrentDir(mannyDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "/");
		service.execute();
		
		assertEquals(rootLogin.getCurrentDir(), rootDir);
	}
	
	@Test
	public void stayAbsolute() {
		rootLogin.setCurrentDir(mannyDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "/testDir/subDir/mannyDir");
		service.execute();
		
		assertEquals(rootLogin.getCurrentDir(), mannyDir);
	}
	
	@Test
	public void goToAbsolutePath() {
		Directory alternativeDir = new Directory(mD, testDir, rootLogin.getUser(), "alternativeDir");
		alternativeDir.setPermissions("rwxdrwxd");
		rootLogin.setCurrentDir(alternativeDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "/testDir/subDir/mannyDir");
		service.execute();
		
		assertEquals(rootLogin.getCurrentDir(), mannyDir);
	}
	
	@Test
	public void goTo() {
		rootLogin.setCurrentDir(subDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "mannyDir");
		service.execute();
		
		assertEquals(rootLogin.getCurrentDir(), mannyDir);
	}
	
	@Test
	public void goToRelativePath() {
		rootLogin.setCurrentDir(testDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "subDir/mannyDir");
		service.execute();
		
		assertEquals(rootLogin.getCurrentDir(), mannyDir);
	}
	
	
	@Test
	public void stay() {
		rootLogin.setCurrentDir(mannyDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, ".");
		service.execute();
		
		assertEquals(rootLogin.getCurrentDir(), mannyDir);
	}
	
	@Test
	public void goToParent() {
		rootLogin.setCurrentDir(mannyDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "..");
		service.execute();
		
		assertEquals(rootLogin.getCurrentDir(), mannyDir.getParent());
	}
	
	@Test
	public void goToGreatGrandparent() {
		Directory greatGrandchildDir = new Directory(mD, mannyDir, rootLogin.getUser(), "greatGrandchildDir");
		greatGrandchildDir.setPermissions("rwxdrwxd");
		rootLogin.setCurrentDir(greatGrandchildDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "../.././..");
		service.execute();
		
		assertEquals(rootLogin.getCurrentDir(), testDir);
	}
	
	@Test
	public void goToRootParent() {
		rootLogin.setCurrentDir(rootDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "..");
		service.execute();
		
		assertEquals(rootLogin.getCurrentDir(), rootDir);
	}
	
	@Test
	public void stayOddPath() {
		rootLogin.setCurrentDir(testDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "subDir/./..");
		service.execute();
		
		assertEquals(rootLogin.getCurrentDir(), testDir);
	}
	
	
	@Test(expected = FileNotFoundException.class)
	public void doubleSlashPath() throws FileNotFoundException{		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "testDir//");
		service.execute();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void doubleSlash() throws FileNotFoundException{		
		rootLogin.setCurrentDir(mannyDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "//");
		service.execute();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void fourOhFour() throws FileNotFoundException{
		rootLogin.setCurrentDir(mannyDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "fourOhFour");
		service.execute();
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
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "aOne");
		service.execute();
		
		assertEquals(rootLogin.getCurrentDir(), mD.getDirectoryByPath(rootDir, "testDir"));		
	}

	@Test(expected = InvalidLoginException.class)
	public void nonExistentTokenTest() throws InvalidLoginException {
		ChangeDirectoryService service = new ChangeDirectoryService(invalidToken, ".");
		service.execute();
	}
}
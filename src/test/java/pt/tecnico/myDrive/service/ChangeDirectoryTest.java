package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;

import pt.tecnico.myDrive.exception.AccessDeniedException;

import org.junit.Test;

import pt.tecnico.myDrive.domain.*;
import pt.tecnico.myDrive.exception.InvalidLoginException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.NotDirectoryException;

public class ChangeDirectoryTest extends TokenReceivingTest {

	private	MyDriveFS mD;
	private	RootDirectory rootDir;
	private	User rootUser;
	private	User otherUser;
	private	Login rootLogin;
	private	long rootToken;
	private	Login otherLogin;
	private	long otherToken;
		
	private	Directory testDir;
	private	Directory subDir;
	private	Directory otherDir;
	private	Directory mannyDir;
	
	
	protected void populate() {
		super.populate("root", "***");
		mD = MyDriveFS.getInstance();
		rootDir = mD.getRootDirectory();

		rootUser = mD.getUserByUsername("root");
		otherUser = new User(mD, "other", "smallerthanthree", "Other Woman", "rwxdrwxd", null);
		rootUser.setUmask("rwxdrwxd");
		
		rootLogin = new Login(mD, "root", "***");
		rootToken = rootLogin.getToken();
		otherLogin = new Login(mD, "other", "smallerthanthree");
		otherToken = otherLogin.getToken();
		
		rootLogin.setCurrentDir(rootDir);
		otherLogin.setCurrentDir(rootDir);
		
		testDir = new Directory(mD, rootDir, rootUser, "testDir");
		subDir = new Directory(mD, testDir, rootUser, "subDir");
		mannyDir = new Directory(mD, subDir, rootUser, "mannyDir");
		otherDir = new Directory(mD, rootDir, otherUser, "otherDir");
		
		testDir.setPermissions("rwxdrwxd");
		subDir.setPermissions("rwxdrwxd");
		mannyDir.setPermissions("rwxdrwxd");
		otherDir.setPermissions("rwxdrwxd");
	}
	
	@Test
	public void goTospace() {
		Directory spaceDir = new Directory(mD, rootDir, rootUser, " ");
		spaceDir.setPermissions("rwxdrwxd");
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, " ");
		service.execute();
		
		Directory properDir = spaceDir;
		assertEquals("Did not change to '" + properDir.getPath() + "'", properDir, rootLogin.getCurrentDir());
	}
	
	@Test
	public void goToRoot() {
		rootLogin.setCurrentDir(mannyDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "/");
		service.execute();
		
		Directory properDir = rootDir;
		assertEquals("Did not change to '" + properDir.getPath() + "'",  properDir, rootLogin.getCurrentDir());
	}
	
	@Test
	public void stayAbsolute() {
		rootLogin.setCurrentDir(mannyDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "/testDir/subDir/mannyDir");
		service.execute();
		
		assertEquals("Did not stay in same directory", mannyDir, rootLogin.getCurrentDir());
	}
	
	@Test
	public void goToAbsolutePath() {
		Directory alternativeDir = new Directory(mD, testDir, rootUser, "alternativeDir");
		alternativeDir.setPermissions("rwxdrwxd");
		rootLogin.setCurrentDir(alternativeDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "/testDir/subDir/mannyDir");
		service.execute();
		
		Directory properDir = mannyDir;
		assertEquals("Did not change to '" + properDir.getPath() + "'", properDir, rootLogin.getCurrentDir());
	}
	
	@Test
	public void goTo() {
		rootLogin.setCurrentDir(subDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "mannyDir");
		service.execute();
		
		Directory properDir = mannyDir;
		assertEquals("Did not change to '" + properDir.getPath() + "'", properDir, rootLogin.getCurrentDir());
	}
	
	@Test
	public void goToRelativePath() {
		rootLogin.setCurrentDir(testDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "subDir/mannyDir");
		service.execute();
		
		Directory properDir = mannyDir;
		assertEquals("Did not change to '" + properDir.getPath() + "'", properDir, rootLogin.getCurrentDir());
	}
	
	@Test
	public void stay() {
		rootLogin.setCurrentDir(mannyDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, ".");
		service.execute();
		
		assertEquals("Did not stay in same directory", mannyDir, rootLogin.getCurrentDir());
	}
	
	@Test
	public void goToParent() {
		rootLogin.setCurrentDir(mannyDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "..");
		service.execute();
		
		Directory properDir = mannyDir.getParent();
		assertEquals("Did not change to '" + properDir.getPath() + "'", properDir, rootLogin.getCurrentDir());
	}
	
	@Test
	public void goToGreatGrandparent() {
		Directory greatGrandchildDir = new Directory(mD, mannyDir, rootUser, "greatGrandchildDir");
		greatGrandchildDir.setPermissions("rwxdrwxd");
		rootLogin.setCurrentDir(greatGrandchildDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "../.././..");
		service.execute();
		
		Directory properDir = testDir;
		assertEquals("Did not change to '" + properDir.getPath() + "'", properDir, rootLogin.getCurrentDir());
	}
	
	@Test
	public void goToRootParent() {
		rootLogin.setCurrentDir(rootDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "..");
		service.execute();
		
		Directory properDir = rootDir;
		assertEquals("Did not change to '" + properDir.getPath() + "'", properDir, rootLogin.getCurrentDir());
	}
	
	@Test
	public void stayOddPath() {
		rootLogin.setCurrentDir(testDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "subDir/./..");
		service.execute();
		
		assertEquals("Did not stay in same directory", testDir, rootLogin.getCurrentDir());
	}
	
	@Test(expected = FileNotFoundException.class)
	public void fourOhFour() throws FileNotFoundException{
		rootLogin.setCurrentDir(mannyDir);
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "fourOhFour");
		service.execute();
	}
	
	@Test(expected = NotDirectoryException.class)
	public void textFile() throws NotDirectoryException, InvalidFileNameException{
		rootLogin.setCurrentDir(testDir);
		TextFile textFile = new TextFile(mD, testDir, rootUser, "textFile", "Lorem ipsum dolor sit amet");
		textFile.setPermissions("rwxdrwxd");
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "textFile");
		service.execute();
	}
	
	@Test
	public void linkFile() throws NotDirectoryException{
		rootLogin.setCurrentDir(testDir);
		TextFile textFile = new Link(mD, testDir, rootUser, "linkFile", "/testDir/subDir/mannyDir");
		textFile.setPermissions("rwxdrwxd");
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "linkFile");
		service.execute();

		Directory properDir = mannyDir;
		assertEquals("Did not change to '" + properDir.getPath() + "'", properDir, rootLogin.getCurrentDir());
	}
	
	
	@Test(expected = AccessDeniedException.class)
	public void otherWithoutPermission() throws AccessDeniedException{
		testDir.setPermissions("rwxd----");
		
		ChangeDirectoryService service = new ChangeDirectoryService(otherToken, "testDir");
		service.execute();
	}
	
	@Test
	public void otherWithPermission() {
		testDir.setPermissions("rwxd--x-");
		
		ChangeDirectoryService service = new ChangeDirectoryService(otherToken, "testDir");
		service.execute();
		
		Directory properDir = testDir;
		assertEquals("Did not change to '" + properDir.getPath() + "'", properDir, otherLogin.getCurrentDir());
	}
	
	@Test(expected = AccessDeniedException.class)
	public void mineWithoutPermission() throws AccessDeniedException{
		otherDir.setPermissions("rw-drwxd");
		
		ChangeDirectoryService service = new ChangeDirectoryService(otherToken, "otherDir");
		service.execute();
	}
	
	@Test
	public void mineWithPermission() {
		otherDir.setPermissions("--x-----");
		
		ChangeDirectoryService service = new ChangeDirectoryService(otherToken, "otherDir");
		service.execute();
		
		Directory properDir = otherDir;
		assertEquals("Did not change to '" + properDir.getPath() + "'", properDir, otherLogin.getCurrentDir());
	}
	
	@Test(expected = AccessDeniedException.class)
	public void permissionPath() throws AccessDeniedException{
		testDir.setPermissions("--------");
		subDir.setPermissions("--x---x-");
		
		ChangeDirectoryService service = new ChangeDirectoryService(otherToken, "testDir/subDir");
		service.execute();
	}
	
	@Test(expected = AccessDeniedException.class)
	public void otherWithoutMask() throws AccessDeniedException{
		otherUser.setUmask("--------");
		
		ChangeDirectoryService service = new ChangeDirectoryService(otherToken, "testDir");
		service.execute();
	}
	
	@Test
	public void rootWithoutMask() {
		rootUser.setUmask("--------");
		
		ChangeDirectoryService service = new ChangeDirectoryService(rootToken, "otherDir");
		service.execute();
		
		Directory properDir = otherDir;
		assertEquals("Did not change to '" + properDir.getPath() + "'", properDir, rootLogin.getCurrentDir());
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

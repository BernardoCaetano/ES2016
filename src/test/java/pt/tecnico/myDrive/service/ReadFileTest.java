package pt.tecnico.myDrive.service;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.TextFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.exception.AccessDeniedException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidLoginException;
import pt.tecnico.myDrive.exception.CyclicLinkException;
import pt.tecnico.myDrive.exception.NotTextFileException;

public class ReadFileTest extends TokenReceivingTest {

	@Override
	protected void populate() {
		MyDriveFS md = MyDriveFS.getInstance();
		User john = new User(md, "john", "1234", "Johnny", "rwxd-w--", null);
		User mary = new User(md, "mary", "5678", "Mary", "rwxdr-x-", null);
		Directory maryHome = mary.getHomeDirectory();
		new TextFile(md, maryHome, mary, "exampleTxt", "/home/mary/exampleApp 1 2");
		(new Directory(md, maryHome, mary, "exampleDir")).setPermissions("rwxdrw--");
		(new App(md, maryHome, john, "exampleApp", "pt.tecnico.myDrive.Main.main")).setPermissions("rwxd----");
		
		new Link(md, maryHome, mary, "absoluteLink", "/home/mary/exampleTxt");
		new Link(md, maryHome, mary, "relativeLink", "./exampleTxt");
		new Link(md, maryHome, mary, "Link To Nowhere", "./exampleDir/nothing");
		
		new Link(md, maryHome, mary, "loopLink1", "/home/mary/loopLink2");
		new Link(md, maryHome, mary, "loopLink2", "/home/mary/loopLink1");
		
		super.populate("mary", "5678");
	}

	@Test
	public void successTextFile() {
		ReadFileService service = new ReadFileService(validToken, "exampleTxt");
		service.execute();
		String res = service.result();

		assertEquals("Content is not the same -", "/home/mary/exampleApp 1 2", res);
	}
	
	@Test
	public void successAbsolutePathLink() {
		ReadFileService service = new ReadFileService(validToken, "absoluteLink");
		service.execute();
		String res = service.result();
		
		assertEquals("Content is not the same -", "/home/mary/exampleApp 1 2", res);
	}
	
	@Test
	public void successRelativePathLink() {
		ReadFileService service = new ReadFileService(validToken, "relativeLink");
		service.execute();
		String res = service.result();
		
		assertEquals("Content is not the same -", "/home/mary/exampleApp 1 2", res);
	}
	
	@Test(expected = FileNotFoundException.class)
	public void nonExistentLinkTarget() {
		ReadFileService service = new ReadFileService(validToken, "Link To Nowhere");
		service.execute();
	}
	
	@Test(expected = CyclicLinkException.class)
	public void linkLoopfail() {
		ReadFileService service = new ReadFileService(validToken, "loopLink1");
		service.execute();
	}


	@Test(expected = NotTextFileException.class)
	public void notTextFileTest() {
		ReadFileService service = new ReadFileService(validToken, "exampleDir");
		service.execute();
	}

	@Test(expected = FileNotFoundException.class)
	public void nonExistentFileTest() {
		ReadFileService service = new ReadFileService(validToken, "void");
		service.execute();
	}

	@Test(expected = AccessDeniedException.class)
	public void permissionDeniedTest() {
		ReadFileService service = new ReadFileService(validToken, "exampleApp");
		service.execute();
	}

	@Test(expected = InvalidLoginException.class)
	public void expiredSessionTest2h05minAgo() {
		super.setLastActivity2h05minAgo();
		ReadFileService service = new ReadFileService(validToken, "someTxt");
		service.execute();
	}

	@Test
	public void sessionStillValidTest1h55min() {
		super.setLastActivity1h55minAgo();
		ReadFileService service = new ReadFileService(validToken, "exampleTxt");
		service.execute();
		String res = service.result();

		assertEquals("Content is not the same -", "/home/mary/exampleApp 1 2", res);

	}

	@Test(expected = InvalidLoginException.class)
	public void nonExistentTokenTest() {
		ReadFileService service = new ReadFileService(invalidToken, "exampleTxt");
		service.execute();
	}
}

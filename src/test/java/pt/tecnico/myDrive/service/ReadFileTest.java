package pt.tecnico.myDrive.service;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.TextFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.exception.AccessDeniedException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidLoginException;
import pt.tecnico.myDrive.exception.NotTextFileException;

public class ReadFileTest extends TokenReceivingTest {

	@Override
	protected void populate() {
		MyDriveFS md = MyDriveFS.getInstance();
		User john = new User(md, "john", "1234", "Johnny", "rwxd-w--");
		User mary = new User(md, "mary", "5678", "Mary", "rwxdr-x-");
		new TextFile(md, mary.getHomeDirectory(), mary, "exampleTxt", "/home/mary/exampleApp 1 2");
		(new Directory(md, mary.getHomeDirectory(), mary, "exampleDir")).setPermissions("rwxdrw--");
		(new App(md, mary.getHomeDirectory(), john, "exampleApp", "pt.tecnico.myDrive.Main.main")).setPermissions("rwxd----");

		super.populate("mary", "5678");
	}

	@Test
	public void success() {
		ReadFileService service = new ReadFileService(validToken, "exampleTxt");
		service.execute();
		String res = service.result();

		assertEquals("Content is not the same -", "/home/mary/exampleApp 1 2", res);
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

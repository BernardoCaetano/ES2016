package pt.tecnico.myDrive.service;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.tecnico.myDrive.domain.AbstractFile;
import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.TextFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.AccessDeniedException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.ImmutableLinkContentException;
import pt.tecnico.myDrive.exception.InvalidAppContentException;
import pt.tecnico.myDrive.exception.InvalidLoginException;
import pt.tecnico.myDrive.exception.InvalidTextFileContentException;
import pt.tecnico.myDrive.exception.NotTextFileException;

public class WriteFileTest extends TokenReceivingTest {

	long rootToken;
	Directory currentDir;

	@Override
	protected void populate() {
		MyDriveFS md = MyDriveFS.getInstance();
		User john = new User(md, "john", "1234", "Johnny", "rwxdrwx-");
		User mary = new User(md, "mary", "5678", "Mary", "rwxdrwx-");
		currentDir = mary.getHomeDirectory();
		new Directory(md, currentDir, mary, "exampleDir");
		new TextFile(md, currentDir, mary, "marysTxt", "/home/mary/exampleApp 1 2");
		(new TextFile(md, currentDir, john, "johnsTxt", "/home/mary/exampleApp 20 9")).setPermissions("rwxdr-x-");
		new App(md, currentDir, mary, "exampleApp", "pt.tecnico.myDrive.Main.main");
		new Link(md, currentDir, mary, "exampleLink", "/home/john");

		populate("mary", "5678");

		Login rootLg = new Login(md, "root", "***");
		rootLg.setCurrentDir(currentDir);
		rootToken = rootLg.getToken();
	}

	private AbstractFile getFile(String name) {
		return currentDir.getFileByName(name);
	}

	@Test
	public void successRootTest() {
		WriteFileService service = new WriteFileService(rootToken, "johnsTxt", "/home/mary/exampleApp 23 11");
		service.execute();

		TextFile t = (TextFile) getFile("johnsTxt");
		assertEquals("Content is not the same", "/home/mary/exampleApp 23 11", t.getContent());
	}

	@Test
	public void successTextFileOwnerTest() {
		WriteFileService service = new WriteFileService(validToken, "marysTxt", "/home/mary/exampleApp 23 11");
		service.execute();

		TextFile t = (TextFile) getFile("marysTxt");
		assertEquals("Content is not the same", "/home/mary/exampleApp 23 11", t.getContent());
	}

	@Test(expected = AccessDeniedException.class)
	public void failTextFileOwnerTest() {
		TextFile t = (TextFile) getFile("marysTxt");
		t.setPermissions("r-xdr-x-");

		WriteFileService service = new WriteFileService(validToken, "marysTxt", "/home/mary/exampleApp 23 11");
		service.execute();
	}

	@Test
	public void successNotTextFileOwnerTest() {
		TextFile t = (TextFile) getFile("johnsTxt");
		t.setPermissions("rwxdrwx-");

		WriteFileService service = new WriteFileService(validToken, "johnsTxt", "/home/mary/exampleApp 23 11");
		service.execute();
		assertEquals("Content is not the same", "/home/mary/exampleApp 23 11", t.getContent());

	}

	@Test(expected = AccessDeniedException.class)
	public void failNotTextFileOwnerTest() {
		WriteFileService service = new WriteFileService(validToken, "johnsTxt", "/home/mary/exampleApp 23 11");
		service.execute();
	}
	
	@Test(expected = InvalidTextFileContentException.class)
	public void failTextFileInvalidContentTest() {
		WriteFileService service = new WriteFileService(validToken, "marysTxt", "this content is invalid");
		service.execute();
	}

	@Test(expected = ImmutableLinkContentException.class)
	public void failLinkTest() {
		WriteFileService service = new WriteFileService(validToken, "exampleLink", "/home/mary/exampleDir");
		service.execute();
	}

	@Test
	public void sucessAppTest() {
		WriteFileService service = new WriteFileService(validToken, "exampleApp", "java.lang.String.length");
		service.execute();

		App a = (App) getFile("exampleApp");
		assertEquals("Content is not the same", "java.lang.String.length", a.getContent());
	}

	@Test(expected = InvalidAppContentException.class)
	public void failAppInvalidContentTest() {
		WriteFileService service = new WriteFileService(validToken, "exampleApp", "invalid");
		service.execute();
	}

	@Test(expected = NotTextFileException.class)
	public void failDirectoryTest() {
		WriteFileService service = new WriteFileService(validToken, "exampleDir", "some text");
		service.execute();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void failFileNotFoundTest() {
		WriteFileService service = new WriteFileService(validToken, "someTxt", "some text");
		service.execute();
	}

	@Test(expected = InvalidLoginException.class)
	public void expiredSessionTest2h05minAgo() {
		super.setLastActivity2h05minAgo();
		WriteFileService service = new WriteFileService(validToken, "marysTxt", "/home/mary/exampleApp 3 4");
		service.execute();
	}

	@Test
	public void sessionStillValidTest1h55min() {
		super.setLastActivity1h55minAgo();
		WriteFileService service = new WriteFileService(validToken, "marysTxt", "/home/mary/exampleApp 3 4");
		service.execute();

		TextFile t = (TextFile) getFile("marysTxt");
		assertEquals("Content is not the same", "/home/mary/exampleApp 3 4", t.getContent());
	}

	@Test(expected = InvalidLoginException.class)
	public void nonExistentTokenTest() {
		WriteFileService service = new WriteFileService(invalidToken, "someTxt", "some text");
		service.execute();
	}

}

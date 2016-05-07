package pt.tecnico.myDrive.service;

import static org.junit.Assert.*;

import org.junit.Test;

import mockit.Mock;
import mockit.MockUp;
import pt.tecnico.myDrive.domain.AbstractFile;
import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.TextFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.AccessDeniedException;
import pt.tecnico.myDrive.exception.CyclicLinkException;
import pt.tecnico.myDrive.exception.EnvironmentVariableDoesNotExistException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidAppContentException;
import pt.tecnico.myDrive.exception.InvalidLoginException;
import pt.tecnico.myDrive.exception.InvalidPathException;
import pt.tecnico.myDrive.exception.NotTextFileException;

public class WriteFileTest extends TokenReceivingTest {

	long rootToken;
	Directory currentDir;

	@Override
	protected void populate() {
		MyDriveFS md = MyDriveFS.getInstance();
		User john = new User(md, "john", "1234", "Johnny", "rwxdrwx-", null);
		User mary = new User(md, "mary", "5678", "Mary", "rwxdrwx-", null);
		currentDir = mary.getHomeDirectory();
		new Directory(md, currentDir, mary, "exampleDir");
		new TextFile(md, currentDir, mary, "marysTxt", "/home/mary/exampleApp 1 2");
		(new TextFile(md, currentDir, john, "johnsTxt", "/home/mary/exampleApp 20 9")).setPermissions("rwxdr-x-");
		new App(md, currentDir, mary, "exampleApp", "pt.tecnico.myDrive.Main.main");
		new Link(md, currentDir, mary, "exampleLink", "/home/john");

		new Link(md, currentDir, mary, "absoluteLink", "/home/mary/marysTxt");
		new Link(md, currentDir, mary, "relativeLink", "./marysTxt");
		new Link(md, currentDir, mary, "Link To Nowhere", "./exampleDir/nothing");
		new Link(md, currentDir, mary, "linkToDir", "/home/mary/exampleDir");

		new Link(md, currentDir, mary, "loopLink1", "/home/mary/loopLink2");
		new Link(md, currentDir, mary, "loopLink2", "/home/mary/loopLink1");

		new Link(md, currentDir, mary, "linkWith$", "/home/$MARY/marysTxt");
		new Link(md, currentDir, mary, "linkWith$FailFile", "/home/$JOHN/marysTxt");
		new Link(md, currentDir, mary, "linkWith$FailEnv", "/home/$JAKE/maryysTxt");

		populate("mary", "5678");

		Login rootLg = new Login(md, "root", "***");
		rootLg.setCurrentDir(currentDir);
		rootToken = rootLg.getToken();
	}

	private AbstractFile getFile(String path) {
		MyDriveFS md = MyDriveFS.getInstance();
		return md.getFileByPath(currentDir, path);
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

	@Test
	public void successAbsolutePathLink() {
		WriteFileService service = new WriteFileService(validToken, "absoluteLink", "/home/mary/exampleApp 23 11");
		service.execute();

		TextFile t = (TextFile) getFile("marysTxt");

		assertEquals("The content of the text file is not as expected", t.getContent(), "/home/mary/exampleApp 23 11");
	}

	@Test
	public void successRelativePathLink() {
		WriteFileService service = new WriteFileService(validToken, "relativeLink", "/home/mary/exampleApp 23 11");
		service.execute();

		TextFile t = (TextFile) getFile("marysTxt");

		assertEquals("The content of the text file is not as expected", t.getContent(), "/home/mary/exampleApp 23 11");
	}

	@Test(expected = FileNotFoundException.class)
	public void nonExistentLinkTarget() {
		WriteFileService service = new WriteFileService(validToken, "Link To Nowhere", "some text");
		service.execute();
	}

	@Test(expected = CyclicLinkException.class)
	public void failLinkLoop() {
		WriteFileService service = new WriteFileService(validToken, "loopLink1", "some text");
		service.execute();
	}

	@Test(expected = NotTextFileException.class)
	public void failLinkToDirectory() {
		WriteFileService service = new WriteFileService(validToken, "linkToDir", "some text");
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
		WriteFileService service = new WriteFileService(validToken, "exampleApp", "this content is invalid");
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

	// NEW TESTS

	@Test(expected = InvalidPathException.class)
	public void pathIsInvalidTest() {
		WriteFileService service = new WriteFileService(validToken, "//", "some text");
		service.execute();
	}

	@Test(expected = FileNotFoundException.class)
	public void fileInPathNotFoundTest() {
		WriteFileService service = new WriteFileService(validToken, "/batata", "some text");
		service.execute();
	}

	@Test
	public void successWriteTextFileWithPathTest() {
		WriteFileService service = new WriteFileService(validToken, "/home/mary/marysTxt", "some texttt");
		service.execute();

		TextFile t = (TextFile) getFile("/home/mary/marysTxt");
		assertEquals("Content is not the same", "some texttt", t.getContent());
	}

	/// ENVIRONMENT TEST ///

	@Test
	public void successLinkWithEnvironment() {

		new MockUp<Directory>() {
			@Mock
			String translate(String path) {
				return "/home/mary/marysTxt";
			}
		};

		WriteFileService service = new WriteFileService(validToken, "linkWith$", "after Mock");
		service.execute();

		TextFile tf = (TextFile) getFile("/home/mary/marysTxt");
		assertEquals("Content not the same", "after Mock", tf.getContent());
	}

	@Test(expected = FileNotFoundException.class)
	public void failureLinkFileDoesNotExist() {

		new MockUp<Directory>() {
			@Mock
			String translate(String path) {
				return "/home/JOHN/marysTxt";
			}
		};

		WriteFileService service = new WriteFileService(validToken, "linkWith$FailFile$", "after Mock");
		service.execute();

	}

	@Test (expected= EnvironmentVariableDoesNotExistException.class)
	public void failureLinkEnvDoesNotExist() {

		new MockUp<Directory>() {
			@Mock
			String translate(String path) throws EnvironmentVariableDoesNotExistException{
				throw new EnvironmentVariableDoesNotExistException("$JAKE");
			};	
			
		};

		WriteFileService service = new WriteFileService(validToken, "linkWith$FailEnv", "after Mock");
		service.execute();

	}

}

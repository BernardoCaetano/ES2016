package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.tecnico.myDrive.domain.*;
import pt.tecnico.myDrive.exception.InvalidAppContentException;
import pt.tecnico.myDrive.exception.InvalidDirectoryContentException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.InvalidLinkContentException;
import pt.tecnico.myDrive.exception.InvalidLoginException;
import pt.tecnico.myDrive.exception.InvalidTypeOfFileException;
import pt.tecnico.myDrive.exception.NameAlreadyExistsException;
import pt.tecnico.myDrive.exception.PathMaximumLengthException;
import pt.tecnico.myDrive.exception.CreateDeniedException;
import pt.tecnico.myDrive.exception.FileNotFoundException;;

public class CreateFileTest extends TokenReceivingTest {

	MyDriveFS mD;
	long rootToken;
	String auxFileName = "o7NqcaiGU9HhSPgnE9m8kwpsT9DPgaNtC5hSFTxWzqPMcqCKwjUn3lAjhy1TlMmuctvmWwpj8O"
			+ "NKmxCfPsMhLJsj8C79zHmDkDMnGvZbDmBj8RD6CssCnxSlRAli8qCoPxgKE1f1Pxal0DI7clW1egZTEnl5ok1lYybs26Hg1"
			+ "jT1GnXgSqcZO5WElNFGSrtJ88nOJkJ000i5bcJ3gFiPwULpZGT1OhNSEY4p9Bob0jAC4COO5sbqNHhI4qVDs6gXE8GMSiy6"
			+ "O4Lt2RAGYaJRxsgSjcSIM7NTxkXgqMTpMNgJZ4FMnUNefRA1SCrG2JBGkNu9lvII6HuKCiNA2qtgXCLQlRHy7VzLRoLoSN7"
			+ "Nazle7lWy7BmYLN6g53A3lt8DUq9zkCVkCKWpjZaHGTCEXHw3HXnrFU62EV6AeH0CWrT1IW6sR2SnLtAYGR0BB7q12nG9cr"
			+ "qht3Q29UwYfYIb5f8JTGv7GYErHj9Eaz1tgqeOSY5kpN22OaMRSIkVyoCA4DuQXL1BmuqsSsvH5DOTkZ8zSgfxXleelt0kU"
			+ "mTCjY8GJqDS4JBZZjVkuOchix1flQDuBvQKvj6M07UIPuVaeAYu3rPFzAhAV3LI4jWivcaaQePsQMKruiFXxp4WetUVRm7r"
			+ "M9rDHD5CaPH2xD0Ggacue08HTP0AUMgWSZNERHzgptqls84McvLAK1ttmA93JDBpTPAgpWDzP8onv6Wuvig50minRKvv78i"
			+ "M9BqA2KShRF7vJ4F47gWmtiGlSObvO4D2G8sV4ZUQJlw03uuOaqTrpCTMcRS74ZJpSlIIy0jEzBz1JmRbR4oFfGhhXDQlCK"
			+ "h2oJMhP33VnQJQgsGV3cPKFNwWqrIHNOALzXcKfcVMosmc4bwD6LUgwstTWjI8knfM9tMEF3VbxVkg04Z7crBPzjbEWWw6f"
			+ "WVOnCtS4yjYyhL682e5acDFP41RjUIP4eZeam8IcOngUOrbcYGU0up9m0YgBMMZ6bgFSoYfAkuLgRV96M3";

	protected void populate() {
		mD = MyDriveFS.getInstance();
		Login login;

		User newUser = new User(mD, "Wololo", "password", "April Fool", "rwxdrwxd", null);
		super.populate("Wololo", "password");
		login = mD.getLoginByToken(validToken);
		
		Directory currentDir = login.getCurrentDir();
		new Directory(mD, currentDir, newUser, "existingDir");
		
		login = new Login(mD, "root", "***");
		rootToken = login.getToken();
	}

	private AbstractFile getAbstractFile(long token, String fileName) {
		try {
			AbstractFile file = mD.getLoginByToken(token).getCurrentDir().getFileByNameNoFollow(fileName);
			return file;
		} catch (FileNotFoundException e) {
			return null;
		}
		
	}

	private void BasicFileTest(AbstractFile f, String fileName, String typeOfFile, long token) {
		Login login = mD.getLoginByToken(token);
		
		assertNotNull(typeOfFile + " created without an ID", f.getId());
		assertNotNull(typeOfFile + " created without a Date", f.getLastModified());
		assertEquals(typeOfFile + " created with a wrong Owner", f.getOwner(), login.getUser());
		assertEquals(typeOfFile + " created with wrong permissions", f.getPermissions(), login.getUser().getUmask());
		assertEquals(typeOfFile + " created in a wrong location", f.getParent(), login.getCurrentDir());
		assertEquals(typeOfFile + " created with a wrong name", fileName, f.getName());
	}

	@Test
	public void successCreateDirectory() {
		CreateFileService service = new CreateFileService(validToken, "newDir", "Directory");
		service.execute();

		AbstractFile f = getAbstractFile(validToken, "newDir");
		assertNotNull("Directory wasn't created", f);
		assertTrue("File created is not a Directory", (f instanceof Directory));
		BasicFileTest(f, "newDir", "Directory", validToken);
	}

	@Test
	public void successCreateAppWithContent() {
		CreateFileService service = new CreateFileService(validToken, "newApp", "App",
				"pt.tecnico.myDrive.domain.MyDriveFS.getInstance");
		service.execute();

		AbstractFile f = getAbstractFile(validToken, "newApp");
		assertNotNull("App wasn't created", f);
		assertTrue("File created is not a App", (f instanceof App));
		BasicFileTest(f, "newApp", "App", validToken);
		App app = (App) f;
		assertEquals("App has invalid Content", "pt.tecnico.myDrive.domain.MyDriveFS.getInstance", app.getContent());
	}

	@Test
	public void successCreateAppWithoutContent() {
		CreateFileService service = new CreateFileService(validToken, "newApp", "App");
		service.execute();

		AbstractFile f = getAbstractFile(validToken, "newApp");
		assertNotNull("App wasn't created", f);
		assertTrue("File created is not a App", (f instanceof App));
		BasicFileTest(f, "newApp", "App", validToken);
	}

	@Test
	public void successCreateLink() {
		CreateFileService service = new CreateFileService(validToken, "newLink", "Link", "/home/root");
		service.execute();

		AbstractFile f = getAbstractFile(validToken, "newLink");
		assertNotNull("Link wasn't created", f);
		assertTrue("File created is not a Link", (f instanceof Link));
		BasicFileTest(f, "newLink", "Link", validToken);
		Link link = (Link) f;
		assertEquals("Link has invalid Content", "/home/root", link.getContent());
	}

	@Test
	public void successCreateTextFileWthContent() {
		CreateFileService service = new CreateFileService(validToken, "newTextFile", "TextFile",
				"/home/Wololo/existingApp");
		service.execute();

		AbstractFile f = getAbstractFile(validToken, "newTextFile");
		assertNotNull("TextFile wasn't created", f);
		assertTrue("File created is not a TextFile", (f instanceof TextFile));
		BasicFileTest(f, "newTextFile", "TextFie", validToken);
		TextFile textFile = (TextFile) f;
		assertEquals("TextFile has invalid Content", "/home/Wololo/existingApp", textFile.getContent());
	}

	@Test
	public void successCreateTextFileWthoutContent() {
		CreateFileService service = new CreateFileService(validToken, "newTextFile", "TextFile");
		service.execute();

		AbstractFile f = getAbstractFile(validToken, "newTextFile");
		assertNotNull("TextFile wasn't created", f);
		assertTrue("File created is not a TextFile", (f instanceof TextFile));
		BasicFileTest(f, "newTextFile", "TextFie", validToken);
	}

	@Test(expected = NameAlreadyExistsException.class)
	public void invalidFileCreationWithDuplicateName() {
		CreateFileService service = new CreateFileService(validToken, "existingDir", "Directory");
		service.execute();
	}

	@Test(expected = InvalidTypeOfFileException.class)
	public void invalidFileCreationWithInvalidType() {
		CreateFileService service = new CreateFileService(validToken, "newSomething", "Something");
		service.execute();
	}

	@Test(expected = InvalidLinkContentException.class)
	public void invalidLinkCreationWithInvalidContent() {
		CreateFileService service = new CreateFileService(validToken, "newLink", "Link", "invalidPath/\0/");
		service.execute();
	}

	@Test(expected = InvalidLinkContentException.class)
	public void invalidLinkCreationWithoutContent() {

		CreateFileService service = new CreateFileService(validToken, "newLink", "Link");
		service.execute();
	}

	@Test(expected = InvalidAppContentException.class)
	public void invalidAppCreationWithInvalidContent() {
		CreateFileService service = new CreateFileService(validToken, "newApp", "App", "invalid method");
		service.execute();
	}

	@Test(expected = InvalidDirectoryContentException.class)
	public void invalidDirCreationWithContent() {
		CreateFileService service = new CreateFileService(validToken, "newDir", "Directory", "Some content");
		service.execute();
	}

	@Test(expected = InvalidFileNameException.class)
	public void InvalidFileCreationWithSlash() {
		CreateFileService service = new CreateFileService(validToken, "new/Dir", "Directory");
		service.execute();
	}

	@Test(expected = InvalidFileNameException.class)
	public void InvalidFileCreationWithNullChar() {
		CreateFileService service = new CreateFileService(validToken, "new\0Dir", "Directory");
		service.execute();
	}
	
	@Test(expected = InvalidFileNameException.class)
	public void InvalidFileCreationWithDot() {
		CreateFileService service = new CreateFileService(validToken, ".", "Directory");
		service.execute();
	}
	
	@Test(expected = InvalidFileNameException.class)
	public void InvalidFileCreationWithDotDot() {
		CreateFileService service = new CreateFileService(validToken, "..", "Directory");
		service.execute();
	}

	@Test
	public void FileCreationWithAbsoutePathEqualTo1024Characters() {
		CreateFileService service = new CreateFileService(validToken, auxFileName, "TextFile");
		service.execute();
		AbstractFile f = getAbstractFile(validToken, auxFileName);

		assertNotNull("TextFile wasn't created", f);
		assertTrue("File created is not a TextFile", (f instanceof TextFile));
		BasicFileTest(f, auxFileName, "TextFile", validToken);
	}

	@Test(expected = PathMaximumLengthException.class)
	public void invalidFileCreationWithAbsoutePathEqualTo1025Characters() {

		CreateFileService service = new CreateFileService(validToken, auxFileName + "1", "TextFile");
		service.execute();
	}

	@Test(expected = CreateDeniedException.class)
	public void permissionDeniedTest() {
		mD.getLoginByToken(validToken).getCurrentDir().setPermissions("r-xd----");

		CreateFileService service = new CreateFileService(validToken, "Teste", "Directory");
		service.execute();
	}

	@Test
	public void successRootCreateFile() {
		mD.getLoginByToken(rootToken).getCurrentDir().setPermissions("--------");
		CreateFileService service = new CreateFileService(rootToken, "newDir", "Directory");
		service.execute();

		AbstractFile f = getAbstractFile(rootToken, "newDir");
		assertNotNull("Directory wasn't created", f);
		assertTrue("File created is not a Directory", (f instanceof Directory));
		BasicFileTest(f, "newDir", "Directory", rootToken);
	}

	@Test(expected = InvalidLoginException.class)
	public void expiredSessionTest2h05minAgo() throws InvalidLoginException {
		super.setLastActivity2h05minAgo();
		CreateFileService service = new CreateFileService(validToken, "newDir", "Directory");
		service.execute();
	}

	@Test
	public void sessionStillValidTest1h55min() {
		super.setLastActivity1h55minAgo();
		CreateFileService service = new CreateFileService(validToken, "newDir", "Directory");
		service.execute();

		AbstractFile f = getAbstractFile(validToken, "newDir");
		assertNotNull("Directory wasn't created", f);
		assertTrue("File created is not a Directory", (f instanceof Directory));
		BasicFileTest(f, "newDir", "Directory", validToken);
	}

	@Test(expected = InvalidLoginException.class)
	public void nonExistentTokenTest() {
		CreateFileService service = new CreateFileService(invalidToken, "newDir", "Directory");
		service.execute();
	}
}

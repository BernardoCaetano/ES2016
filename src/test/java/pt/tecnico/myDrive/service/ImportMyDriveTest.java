package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.TextFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.domain.App;

public class ImportMyDriveTest extends AbstractServiceTest {

	ImportMyDriveService service;

	@Override
	protected void populate() {
		MyDriveFS.getInstance();

	}

	@Test
	public void success() throws JDOMException, IOException {

		Document doc = new SAXBuilder().build(new File("src/test/resources/test.xml"));
		service = new ImportMyDriveService(doc);
		service.execute();

		MyDriveFS mD = MyDriveFS.getInstance();

		assertTrue("User was not created", mD.hasUser("BOMDIAaLÉGRIA"));
		User createdUser = mD.getUserByUsername("BOMDIAaLÉGRIA");
		assertEquals("Username is not correct", createdUser.getUsername(), "BOMDIAaLÉGRIA");
		assertTrue("Incorrect Password", createdUser.checkPassword("BOMDIAaLÉGRIA"));
		assertEquals("Umask is not correct", createdUser.getUmask(), "rwxdrwxd");
		assertEquals("Incorrect Home Directory", mD.getDirectoryByPath(null, "/home/jUAN"),
				createdUser.getHomeDirectory());

		assertTrue("User was not created", mD.hasUser("justCreateMeWITHDEFAULT"));
		User createdUser2 = mD.getUserByUsername("justCreateMeWITHDEFAULT");
		assertEquals("Username is not correct", createdUser2.getUsername(), "justCreateMeWITHDEFAULT");
		assertTrue("Incorrect Password", createdUser2.checkPassword("justCreateMeWITHDEFAULT"));
		assertEquals("Umask is not correct", createdUser2.getUmask(), "rwxd----");
		assertEquals("Incorrect Home Directory", mD.getDirectoryByPath(null, "/home/justCreateMeWITHDEFAULT"),
				createdUser2.getHomeDirectory());

		assertTrue("Directory was not created", mD.hasFile("/home/jUAN/Movies"));
		Directory createdDirectory = mD.getDirectoryByPath(null, "/home/jUAN/Movies");
		assertEquals("Directory was not created for the correct Owner", createdDirectory.getOwner(),
				mD.getUserByUsername("BOMDIAaLÉGRIA"));
		assertEquals("Directory permissions are not correct", createdDirectory.getPermissions(), "rwxdrwxd");
		assertEquals("Last Modified Date is not correct", createdDirectory.getLastModified().toString(),
				"2016-05-08T00:12:53.000+01:00");

		assertTrue("TextFile was not created", mD.hasFile("/home/jUAN/Movies/#2"));
		TextFile createdTextFile = mD.getTextFileByPath(null, "/home/jUAN/Movies/#2");
		assertEquals("TextFile was not created for the correct Owner", createdTextFile.getOwner(),
				mD.getUserByUsername("BOMDIAaLÉGRIA"));
		assertEquals("TextFile permissions are not correct", createdTextFile.getPermissions(), "rwxdrwxd");
		assertEquals("Content is not correct", createdTextFile.getContent(), "The Perks of Being a Wallflower");
		assertEquals("Last Modified Date is not correct", createdTextFile.getLastModified().toString(),
				"2016-05-08T00:12:53.000+01:00");

		assertTrue("App was not created", mD.hasFile("/home/jUAN/Movies/play"));
		App createdApp = (App) mD.getFileByPath(null, "/home/jUAN/Movies/play");
		assertEquals("App was not created for the correct Owner", createdApp.getOwner(),
				mD.getUserByUsername("BOMDIAaLÉGRIA"));
		assertEquals("App permissions are not correct", createdApp.getPermissions(), "rwxdrwxd");
		assertEquals("Content is not correct", createdApp.getContent(), "Video.VLC.Play");
		assertEquals("Last Modified Date is not correct", createdApp.getLastModified().toString(),
				"2016-05-08T00:12:53.000+01:00");
		
		assertTrue("Link was not created", mD.hasFile("/home/jUAN/Movies/linkToPlay"));
		Link createdLink = (Link) mD.getFileByPathNoFollow(null, "/home/jUAN/Movies/linkToPlay");
		assertEquals("Link was not created for the correct Owner", createdLink.getOwner(),
				mD.getUserByUsername("BOMDIAaLÉGRIA"));
		assertEquals("Link permissions are not correct", createdLink.getPermissions(), "rwxdrwxd");
		assertEquals("Content is not correct", createdLink.getContent(), "/home/jUAN/Movies/play");
		assertEquals("Last Modified Date is not correct", createdLink.getLastModified().toString(),
				"2016-05-08T00:12:53.000+01:00");
	}
	
	@Test(expected=ImportDocumentException.class)
	public void failRepeatedUsername() throws JDOMException, IOException {
		Document doc = new SAXBuilder().build(new File("src/test/resources/failRepeatedUsername.xml"));
		service = new ImportMyDriveService(doc);
		service.execute();
	}

	@Test(expected=ImportDocumentException.class)
	public void failNoUsernameSpecified() throws JDOMException, IOException {
		Document doc = new SAXBuilder().build(new File("src/test/resources/failNoUsername.xml"));
		service = new ImportMyDriveService(doc);
		service.execute();
	}
	
	@Test(expected=ImportDocumentException.class)
	public void failWrongUserPermissions() throws JDOMException, IOException {
		Document doc = new SAXBuilder().build(new File("src/test/resources/failWrongUserPermissions.xml"));
		service = new ImportMyDriveService(doc);
		service.execute();
	}
	
	@Test(expected=ImportDocumentException.class)
	public void failInvalidAppContent() throws JDOMException, IOException {
		Document doc = new SAXBuilder().build(new File("src/test/resources/failInvalidAppContent.xml"));
		service = new ImportMyDriveService(doc);
		service.execute();
	}

	@Test(expected=ImportDocumentException.class)
	public void failInvalidLinkContent() throws JDOMException, IOException {
		Document doc = new SAXBuilder().build(new File("src/test/resources/failInvalidLinkContent.xml"));
		service = new ImportMyDriveService(doc);
		service.execute();
	}
	
	@Test(expected=ImportDocumentException.class)
	public void failPathNotSpecified() throws JDOMException, IOException {
		Document doc = new SAXBuilder().build(new File("src/test/resources/failPathNotSpecified.xml"));
		service = new ImportMyDriveService(doc);
		service.execute();
	}
	
	@Test(expected=ImportDocumentException.class)
	public void failPathIsNotAbsolute() throws JDOMException, IOException {
		Document doc = new SAXBuilder().build(new File("src/test/resources/failPathIsNotAbsolute.xml"));
		service = new ImportMyDriveService(doc);
		service.execute();
	}

	@Test(expected=ImportDocumentException.class)
	public void failNoFileName() throws JDOMException, IOException {
		Document doc = new SAXBuilder().build(new File("src/test/resources/failNoFileName.xml"));
		service = new ImportMyDriveService(doc);
		service.execute();
	}
	
	@Test(expected=ImportDocumentException.class)
	public void failRepeatedFile() throws JDOMException, IOException {
		Document doc = new SAXBuilder().build(new File("src/test/resources/failRepeatedFile.xml"));
		service = new ImportMyDriveService(doc);
		service.execute();
	}
	
	@Test(expected=ImportDocumentException.class)
	public void failWrongFilePermissions() throws JDOMException, IOException {
		Document doc = new SAXBuilder().build(new File("src/test/resources/failWrongFilePermissions.xml"));
		service = new ImportMyDriveService(doc);
		service.execute();
	}
	
}

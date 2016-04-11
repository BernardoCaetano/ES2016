package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.service.dto.AbstractFileDTO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.joda.time.DateTime;

import pt.tecnico.myDrive.domain.*;

import pt.tecnico.myDrive.exception.InvalidLoginException;

public class ListDirectoryTest extends TokenReceivingTest {
	
	MyDriveFS mD;
	long rootToken;
	
	User root;
	User manel;
	
	Login rootLogin;
	Login manelLogin;
	
	Directory rootDir;
	Directory manelHome;
	Directory rootHomeDir;
	Directory emptyDirectory;
	Directory rootDirectory;
	
	App MyApp;
	TextFile MyTextFile;
	Link MyLink;
		
	@Override
	public void populate() {
		
		mD = MyDriveFS.getInstance();

		manel = new User(mD, "manel", "password", "Malandro", "rwxd----", null);
		
		super.populate("manel", "password");

		rootLogin = new Login(mD, "root", "***");
		
		rootToken = rootLogin.getToken();
		
		rootDirectory = mD.getRootDirectory();
		
		rootLogin.setCurrentDir(rootDirectory);
		
		manelHome = manel.getHomeDirectory();
		manelLogin.setCurrentDir(manelHome);
		
		rootHomeDir = root.getHomeDirectory();
		
		MyApp = new App(mD, manelHome, manel, "MyApp","pt.tecnico.myDrive.Main.main");
		MyLink = new Link(mD, manelHome, manel, "MyLink","/home/manel");
		MyTextFile = new TextFile(mD, manelHome, manel, "MyTextFile", "Something");
		emptyDirectory = new Directory(mD, manelHome, manel, "emptyDirectory");
	}
	
	@Test
	public void success() {
		rootLogin.setCurrentDir(rootDir);
		
		ListDirectoryService service = new ListDirectoryService(rootToken);
        service.execute();
		
		List<AbstractFileDTO> lds = service.result();

		assertEquals("Right number of Files in RootDirectory", 3, lds.size());
		
		assertEquals("Type of . is correct", "Directory", lds.get(0).getType());
		assertEquals("Permissions of . are correct", "rwxdr-x-", lds.get(0).getPermissions());
		assertEquals(" of . is correct", 0x8, lds.get(0).getSize());
		assertEquals("Username of Owner is correct", "root", lds.get(0).getOwner());
		assertEquals("Id of . is correct", 8, lds.get(0).getId());
		assertEquals("Last Modified date of . is correct", 8, lds.get(0).getLastModified());
		assertEquals("Name of . is correct", ".", lds.get(0).getName());
		
		assertEquals("Type of .. is correct", "Directory", lds.get(1).getType());
		assertEquals("Permissions of .. are correct", "rwxdr-x-", lds.get(1).getPermissions());
		assertEquals("Size of .. is correct", 0x8, lds.get(1).getSize());
		assertEquals("Username of Owner is correct", "root", lds.get(1).getOwner());
		assertEquals("Id of .. is correct", 8, lds.get(1).getId());
		assertEquals("Last Modified date of .. is correct", 8, lds.get(1).getLastModified());
		assertEquals("Name of .. is correct", "..", lds.get(1).getName());
		
		assertEquals("Type of home is Correct", "Directory", lds.get(2).getType());
		assertEquals("Permissions of home are correct", "rwxdr-x-", lds.get(2).getPermissions());
		assertEquals("Size of home is correct", 0x8, lds.get(2).getSize());
		assertEquals("Username of Owner is correct", "root", lds.get(2).getOwner());
		assertEquals("Id of home is correct", 8, lds.get(2).getId());
		assertEquals("Last Modified date of home is correct", new DateTime(), lds.get(2).getLastModified());
		assertEquals("Name of home is correct", "home", lds.get(2).getName());
	}
	
	@Test
	public void emptyHomeDirectory() {
		rootLogin.setCurrentDir(rootHomeDir);
		ListDirectoryService service = new ListDirectoryService(rootToken);
        service.execute();
        
        List<AbstractFileDTO> lds = service.result();

		assertEquals("Right number of Files in Root Home Directory", 2, lds.size());
		
		assertEquals("Type of . is correct", "Directory", lds.get(0).getType());
		assertEquals("Permissions of . are correct", "rwxdr-x-", lds.get(0).getPermissions());
		assertEquals("Dimension of . is correct", 0x8, lds.get(0).getSize());
		assertEquals("Username of Owner is correct", "root", lds.get(0).getOwner());
		assertEquals("Id of . is correct", 8, lds.get(0).getId());
		assertEquals("Last Modified date of . is correct", 8, lds.get(0).getLastModified());
		assertEquals("Name of . is correct", ".", lds.get(0).getName());
		
		assertEquals("Type of .. is correct", "Directory", lds.get(1).getType());
		assertEquals("Permissions of .. are correct","rwxdr-x-", lds.get(1).getPermissions());
		assertEquals("Size of .. is correct", 0x8, lds.get(1).getSize());
		assertEquals("Username of Owner is correct", "root", lds.get(1).getOwner());
		assertEquals("Id of .. is correct", 8, lds.get(1).getId());
		assertEquals("Last Modified date of .. is correct", new DateTime(), lds.get(1).getLastModified());
		assertEquals("Name of .. is correct", "..", lds.get(1).getName());
	}
	
	@Test(expected = InvalidLoginException.class)
	public void expiredSessionTest2h05minAgo() {
		super.setLastActivity2h05minAgo();
		ListDirectoryService service = new ListDirectoryService(validToken);
		service.execute();
	}
	
	@Test
	public void sessionStillValidTest1h55min() {
		super.setLastActivity1h55minAgo();
		
		rootLogin.setCurrentDir(rootDir);
		
		ListDirectoryService service = new ListDirectoryService(rootToken);
		service.execute();
		
		List<AbstractFileDTO> lds = service.result();
		
		assertEquals("Right number of Files in RootDirectory", 3, lds.size());
		
		assertEquals("Type of . is correct", "Directory", lds.get(0).getType());
		assertEquals("Permissions of . are correct", "rwxdr-x-", lds.get(0).getPermissions());
		assertEquals("Size of . is correct", 0x8, lds.get(0).getSize());
		assertEquals("Username of Owner is correct", "root", lds.get(0).getOwner());
		assertEquals("Id of . is correct", 8, lds.get(0).getId());
		assertEquals("Last Modified date of . is correct", 8, lds.get(0).getLastModified());
		assertEquals("Name of . is correct", ".", lds.get(0).getName());
		
		assertEquals("Type of .. is correct", "Directory", lds.get(1).getType());
		assertEquals("Permissions of .. are correct", "rwxdr-x-", lds.get(1).getPermissions());
		assertEquals("Size of .. is correct", 0x8, lds.get(1).getSize());
		assertEquals("Username of Owner is correct", "root", lds.get(1).getOwner());
		assertEquals("Id of .. is correct", 8, lds.get(1).getId());
		assertEquals("Last Modified date of .. is correct", 8, lds.get(1).getLastModified());
		assertEquals("Name of .. is correct", "..", lds.get(1).getName());
		
		assertEquals("Type of home is Correct", "Directory", lds.get(2).getType());
		assertEquals("Permissions of home are correct", "rwxdr-x-", lds.get(2).getPermissions());
		assertEquals("Size of home is correct", 2, lds.get(2).getSize());
		assertEquals("Username of Owner is correct", "root", lds.get(2).getOwner());
		assertEquals("Id of home is correct",8, lds.get(2).getId());
		assertEquals("Last Modified date of home is correct", new DateTime(), lds.get(2).getLastModified());
		assertEquals("Name of home is correct","home", lds.get(2).getName());
		
	}
	
	@Test(expected = InvalidLoginException.class)
	public void nonExistentTokenTest() {
		ListDirectoryService service = new ListDirectoryService(invalidToken);
		service.execute();
	}
}

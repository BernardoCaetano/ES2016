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
	
	Directory rootDirectory;
	Directory home;
	Directory manelHome;
	Directory rootHomeDir;
	Directory emptyDirectory;
	
	
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
		
		manelLogin = mD.getLoginByToken(validToken);
		
		rootDirectory = mD.getRootDirectory();
		home = (Directory)rootDirectory.getFileByName("home");
		
		rootLogin.setCurrentDir(rootDirectory);
		
		manelHome = manel.getHomeDirectory();
		
		root = mD.getUserByUsername("root");
		
		rootHomeDir = root.getHomeDirectory();
		
		MyApp = new App(mD, manelHome, manel, "MyApp","pt.tecnico.myDrive.Main.main");
		MyLink = new Link(mD, manelHome, manel, "MyLink","/home/manel");
		MyTextFile = new TextFile(mD, manelHome, manel, "MyTextFile", "Something");
		emptyDirectory = new Directory(mD, manelHome, manel, "emptyDirectory");
	}
	
	@Test
	public void success() {
		rootLogin.setCurrentDir(rootDirectory);
		
		ListDirectoryService service = new ListDirectoryService(rootToken);
        service.execute();
		
		List<AbstractFileDTO> lds = service.result();

		assertEquals("Wrong number of Files in RootDirectory", 3, lds.size());
		
		assertEquals("Type of . is incorrect", "directory", lds.get(0).getType());
		assertEquals("Permissions of . are incorrect", "rwxdr-x-", lds.get(0).getPermissions());
		assertEquals("Dimension of . is incorrect", rootDirectory.dimension(), lds.get(0).getDimension());
		assertEquals("Username of Owner is incorrect", "root", lds.get(0).getOwner());
		assertEquals("Id of . is incorrect", rootDirectory.getId(), lds.get(0).getId());
		assertEquals("Last Modified date of . is incorrect", rootDirectory.getLastModified(), lds.get(0).getLastModified());
		assertEquals("Name of . is incorrect", ".", lds.get(0).getName());
		
		assertEquals("Type of .. is incorrect", "directory", lds.get(1).getType());
		assertEquals("Permissions of .. are incorrect", "rwxdr-x-", lds.get(1).getPermissions());
		assertEquals("Dimension of .. is incorrect", rootDirectory.dimension(), lds.get(1).getDimension());
		assertEquals("Username of Owner is incorrect", "root", lds.get(1).getOwner());
		assertEquals("Id of .. is incorrect", rootDirectory.getId(), lds.get(1).getId());
		assertEquals("Last Modified date of .. is incorrect", rootDirectory.getLastModified(), lds.get(1).getLastModified());
		assertEquals("Name of .. is incorrect", "..", lds.get(1).getName());
		
		assertEquals("Type of home is incorrect", "directory", lds.get(2).getType());
		assertEquals("Permissions of home are incorrect", "rwxdr-x-", lds.get(2).getPermissions());
		assertEquals("Dimension of home is incorrect", home.dimension(), lds.get(2).getDimension());
		assertEquals("Username of Owner is incorrect", "root", lds.get(2).getOwner());
		assertEquals("Id of home is incorrect", home.getId(), lds.get(2).getId());
		assertEquals("Last Modified date of home is incorrect", home.getLastModified(), lds.get(2).getLastModified());
		assertEquals("Name of home is incorrect", "home", lds.get(2).getName());
	}
	
	@Test
	public void emptyHomeDirectory() {
		rootLogin.setCurrentDir(rootHomeDir);
		ListDirectoryService service = new ListDirectoryService(rootToken);
        service.execute();
        
        List<AbstractFileDTO> lds = service.result();

		assertEquals("Wrong number of Files in Root Home Directory", 2, lds.size());
		
		assertEquals("Type of . is incorrect", "directory", lds.get(0).getType());
		assertEquals("Permissions of . are incorrect", "rwxdr-x-", lds.get(0).getPermissions());
		assertEquals("Dimension of . is incorrect", rootHomeDir.dimension(), lds.get(0).getDimension());
		assertEquals("Username of Owner is incorrect", "root", lds.get(0).getOwner());
		assertEquals("Id of . is incorrect", rootHomeDir.getId(), lds.get(0).getId());
		assertEquals("Last Modified date of . is incorrect", rootHomeDir.getLastModified(), lds.get(0).getLastModified());
		assertEquals("Name of . is incorrect", ".", lds.get(0).getName());
		
		assertEquals("Type of .. is incorrect", "directory", lds.get(1).getType());
		assertEquals("Permissions of .. are incorrect", "rwxdr-x-", lds.get(1).getPermissions());
		assertEquals("Dimension of .. is incorrect", home.dimension(), lds.get(1).getDimension());
		assertEquals("Username of Owner is incorrect", "root", lds.get(1).getOwner());
		assertEquals("Id of .. is incorrect", home.getId(), lds.get(1).getId());
		assertEquals("Last Modified date of .. is incorrect", home.getLastModified(), lds.get(1).getLastModified());
		assertEquals("Name of .. is incorrect", "..", lds.get(1).getName());
	}
	
	@Test
	public void filledDirectory() {
		manelLogin.setCurrentDir(manelHome);
		ListDirectoryService service = new ListDirectoryService(validToken);
        service.execute();
        
        List<AbstractFileDTO> lds = service.result();

		assertEquals("Wrong number of Files in manel Home Directory", 6, lds.size());
		
		assertEquals("Type of . is incorrect", "directory", lds.get(0).getType());
		assertEquals("Permissions of . are incorrect", "rwxd----", lds.get(0).getPermissions());
		assertEquals("Dimension of . is correct", manelHome.dimension(), lds.get(0).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(0).getOwner());
		assertEquals("Id of . is incorrect", manelHome.getId(), lds.get(0).getId());
		assertEquals("Last Modified date of . is incorrect", manelHome.getLastModified(), lds.get(0).getLastModified());
		assertEquals("Name of . is incorrect", ".", lds.get(0).getName());
		
		assertEquals("Type of .. is incorrect", "directory", lds.get(1).getType());
		assertEquals("Permissions of .. are incorrect","rwxdr-x-", lds.get(1).getPermissions());
		assertEquals("Dimension of .. is incorrect", home.dimension(), lds.get(1).getDimension());
		assertEquals("Username of Owner is incorrect", "root", lds.get(1).getOwner());
		assertEquals("Id of .. is incorrect", home.getId(), lds.get(1).getId());
		assertEquals("Last Modified date of .. is incorrect", home.getLastModified(), lds.get(1).getLastModified());
		assertEquals("Name of .. is incorrect", "..", lds.get(1).getName());
		
		assertEquals("Type of emptyDirectory is incorrect", "directory", lds.get(2).getType());
		assertEquals("Permissions of emptyDirectory are incorrect", "rwxd----", lds.get(2).getPermissions());
		assertEquals("Dimension of emptyDirectory is incorrect", emptyDirectory.dimension(), lds.get(2).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(2).getOwner());
		assertEquals("Id of emptyDirectory is incorrect", emptyDirectory.getId(), lds.get(2).getId());
		assertEquals("Last Modified date of emptyDirectory is incorrect", emptyDirectory.getLastModified(), lds.get(2).getLastModified());
		assertEquals("Name of emptyDirectory is incorrect", "emptyDirectory", lds.get(2).getName());
		
		assertEquals("Type of MyApp is incorrect", "app", lds.get(3).getType());
		assertEquals("Permissions of MyApp are incorrect","rwxd----", lds.get(3).getPermissions());
		assertEquals("Dimension of MyApp is incorrect", MyApp.dimension(), lds.get(3).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(3).getOwner());
		assertEquals("Id of MyApp is incorrect", MyApp.getId(), lds.get(3).getId());
		assertEquals("Last Modified date of MyApp is incorrect", MyApp.getLastModified(), lds.get(3).getLastModified());
		assertEquals("Name of MyApp is incorrect", "MyApp", lds.get(3).getName());
		
		assertEquals("Type of MyLink is incorrect", "link", lds.get(4).getType());
		assertEquals("Permissions of MyLink are incorrect", "rwxd----", lds.get(4).getPermissions());
		assertEquals("Dimension of MyLink is incorrect", MyLink.dimension(), lds.get(4).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(4).getOwner());
		assertEquals("Id of MyLink is incorrect", MyLink.getId(), lds.get(4).getId());
		assertEquals("Last Modified date of MyLink is incorrect", MyLink.getLastModified(), lds.get(4).getLastModified());
		assertEquals("Name of MyLink is incorrect", "MyLink", lds.get(4).getName());
		
		assertEquals("Type of MyTextFile is incorrect", "textFile", lds.get(5).getType());
		assertEquals("Permissions of MyTextFile are incorrect","rwxd----", lds.get(5).getPermissions());
		assertEquals("Dimension of MyTextFile is incorrect", MyTextFile.dimension(), lds.get(5).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(5).getOwner());
		assertEquals("Id of MyTextFile is incorrect", MyTextFile.getId(), lds.get(5).getId());
		assertEquals("Last Modified date of MyTextFile is incorrect", MyTextFile.getLastModified(), lds.get(5).getLastModified());
		assertEquals("Name of MyTextFile is incorrect", "MyTextFile", lds.get(5).getName());
	}
	
	@Test
	public void emptyDirectory(){
		manelLogin.setCurrentDir(emptyDirectory);
		
		ListDirectoryService service = new ListDirectoryService(validToken);
        service.execute();
        
        List<AbstractFileDTO> lds = service.result();
        
        assertEquals("Wrong number of files in Manel Empty Directory", 2, lds.size());
		
        assertEquals("Type of . is incorrect", "directory", lds.get(0).getType());
		assertEquals("Permissions of . are incorrect", "rwxd----", lds.get(0).getPermissions());
		assertEquals("Dimension of . is correct", emptyDirectory.dimension(), lds.get(0).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(0).getOwner());
		assertEquals("Id of . is incorrect", emptyDirectory.getId(), lds.get(0).getId());
		assertEquals("Last Modified date of . is incorrect", emptyDirectory.getLastModified(), lds.get(0).getLastModified());
		assertEquals("Name of . is incorrect", ".", lds.get(0).getName());
		
		assertEquals("Type of .. is incorrect", "directory", lds.get(1).getType());
		assertEquals("Permissions of .. are incorrect","rwxd----", lds.get(1).getPermissions());
		assertEquals("Dimension of .. is incorrect", manelHome.dimension(), lds.get(1).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(1).getOwner());
		assertEquals("Id of .. is incorrect", manelHome.getId(), lds.get(1).getId());
		assertEquals("Last Modified date of .. is incorrect", manelHome.getLastModified(), lds.get(1).getLastModified());
		assertEquals("Name of .. is incorrect", "..", lds.get(1).getName());
		
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
		
		rootLogin.setCurrentDir(rootDirectory);
		
		ListDirectoryService service = new ListDirectoryService(rootToken);
		service.execute();
		
		List<AbstractFileDTO> lds = service.result();
		
		assertEquals("Wrong number of Files in RootDirectory", 3, lds.size());
		
		assertEquals("Type of . is incorrect", "directory", lds.get(0).getType());
		assertEquals("Permissions of . are incorrect", "rwxdr-x-", lds.get(0).getPermissions());
		assertEquals("Dimension of . is incorrect", rootDirectory.dimension(), lds.get(0).getDimension());
		assertEquals("Username of Owner is incorrect", "root", lds.get(0).getOwner());
		assertEquals("Id of . is incorrect", rootDirectory.getId(), lds.get(0).getId());
		assertEquals("Last Modified date of . is incorrect", rootDirectory.getLastModified(), lds.get(0).getLastModified());
		assertEquals("Name of . is incorrect", ".", lds.get(0).getName());
		
		assertEquals("Type of .. is incorrect", "directory", lds.get(1).getType());
		assertEquals("Permissions of .. are incorrect", "rwxdr-x-", lds.get(1).getPermissions());
		assertEquals("Dimension of .. is incorrect", rootDirectory.dimension(), lds.get(1).getDimension());
		assertEquals("Username of Owner is incorrect", "root", lds.get(1).getOwner());
		assertEquals("Id of .. is incorrect", rootDirectory.getId(), lds.get(1).getId());
		assertEquals("Last Modified date of .. is incorrect", rootDirectory.getLastModified(), lds.get(1).getLastModified());
		assertEquals("Name of .. is incorrect", "..", lds.get(1).getName());
		
		assertEquals("Type of home is incorrect", "directory", lds.get(2).getType());
		assertEquals("Permissions of home are incorrect", "rwxdr-x-", lds.get(2).getPermissions());
		assertEquals("Dimension of home is incorrect", home.dimension(), lds.get(2).getDimension());
		assertEquals("Username of Owner is incorrect", "root", lds.get(2).getOwner());
		assertEquals("Id of home is incorrect", home.getId(), lds.get(2).getId());
		assertEquals("Last Modified date of home is incorrect", home.getLastModified(), lds.get(2).getLastModified());
		assertEquals("Name of home is incorrect", "home", lds.get(2).getName());
		
	}
	
	@Test(expected = InvalidLoginException.class)
	public void nonExistentTokenTest() {
		ListDirectoryService service = new ListDirectoryService(invalidToken);
		service.execute();
	}
}

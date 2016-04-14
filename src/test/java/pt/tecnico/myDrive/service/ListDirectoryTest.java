package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.service.dto.AbstractFileDTO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.List;
import pt.tecnico.myDrive.domain.*;

import pt.tecnico.myDrive.exception.InvalidLoginException;
import pt.tecnico.myDrive.exception.AccessDeniedException;

public class ListDirectoryTest extends TokenReceivingTest {
	
	MyDriveFS mD;
	long rootToken;
	long otherUserToken;
	
	User root;
	User manel;
	User otherUser;
	
	Login rootLogin;
	Login manelLogin;
	Login otherUserLogin;
	
	Directory rootDirectory;
	Directory home;
	Directory manelHome;
	Directory rootHomeDir;
	Directory emptyDirectory;
	Directory filesPermDir;
	Directory otherCanNotList;
	Directory ownerCanNotList;
	
	
	App MyApp;
	TextFile MyTextFile;
	Link MyLink;
	TextFile otherCannotRead;
	TextFile ownerCannotRead;
	
	@Override
	public void populate() {
		
		mD = MyDriveFS.getInstance();

		root = mD.getUserByUsername("root");
		manel = new User(mD, "manel", "password", "Malandro", "rwxdrwxd", null);
		otherUser = new User(mD, "other", "123456", "TheOther", "rwxd----", null);
		
		super.populate("manel", "password");

		manelLogin = mD.getLoginByToken(validToken);
		rootLogin = new Login(mD, "root", "***");
		otherUserLogin = new Login(mD, "other", "123456");

		rootToken = rootLogin.getToken();
		otherUserToken = otherUserLogin.getToken();
		
		rootDirectory = mD.getRootDirectory();
		home = (Directory)rootDirectory.getFileByName("home");			
		manelHome = manel.getHomeDirectory();		
		rootHomeDir = root.getHomeDirectory();
		
		MyApp = new App(mD, manelHome, manel, "MyApp","pt.tecnico.myDrive.Main.main");
		MyLink = new Link(mD, manelHome, manel, "MyLink","/home/manel");
		MyTextFile = new TextFile(mD, manelHome, manel, "MyTextFile", "Something");
		emptyDirectory = new Directory(mD, manelHome, manel, "emptyDirectory");
		filesPermDir = new Directory(mD, manelHome, manel, "filesPermDir");
		
		otherCanNotList = new Directory(mD, manelHome, manel, "otherCanNotList");
		otherCanNotList.setPermissions("rwxd-wxd");
		
		ownerCanNotList = new Directory(mD, manelHome, manel, "ownerCanNotList");
		ownerCanNotList.setPermissions("-wxdrwxd");
		
		ownerCannotRead = new TextFile(mD, filesPermDir, manel, "ownerCannotRead", "Owner Knows Nothing" ); 
		ownerCannotRead.setPermissions("-wxdrwxd");
		
		otherCannotRead = new TextFile(mD, filesPermDir, manel, "otherCannotRead", "Other Knows Nothing" );
		otherCannotRead.setPermissions("rwxd-wxd");
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

		assertEquals("Wrong number of Files in manel Home Directory", 9, lds.size());
		
		assertEquals("Type of . is incorrect", "directory", lds.get(0).getType());
		assertEquals("Permissions of . are incorrect", "rwxdrwxd", lds.get(0).getPermissions());
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
		assertEquals("Permissions of emptyDirectory are incorrect", "rwxdrwxd", lds.get(2).getPermissions());
		assertEquals("Dimension of emptyDirectory is incorrect", emptyDirectory.dimension(), lds.get(2).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(2).getOwner());
		assertEquals("Id of emptyDirectory is incorrect", emptyDirectory.getId(), lds.get(2).getId());
		assertEquals("Last Modified date of emptyDirectory is incorrect", emptyDirectory.getLastModified(), lds.get(2).getLastModified());
		assertEquals("Name of emptyDirectory is incorrect", "emptyDirectory", lds.get(2).getName());
		
		assertEquals("Type of filesPermDir is incorrect", "directory", lds.get(3).getType());
		assertEquals("Permissions of filesPermDir are incorrect","rwxdrwxd", lds.get(3).getPermissions());
		assertEquals("Dimension of filesPermDir is incorrect", filesPermDir.dimension(), lds.get(3).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(3).getOwner());
		assertEquals("Id of filesPermDir is incorrect", filesPermDir.getId(), lds.get(3).getId());
		assertEquals("Last Modified date of filesPermDir is incorrect", filesPermDir.getLastModified(), lds.get(3).getLastModified());
		assertEquals("Name of filesPermDir is incorrect", "filesPermDir", lds.get(3).getName());
		
		assertEquals("Type of MyApp is incorrect", "app", lds.get(4).getType());
		assertEquals("Permissions of MyApp are incorrect","rwxdrwxd", lds.get(4).getPermissions());
		assertEquals("Dimension of MyApp is incorrect", MyApp.dimension(), lds.get(4).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(4).getOwner());
		assertEquals("Id of MyApp is incorrect", MyApp.getId(), lds.get(4).getId());
		assertEquals("Last Modified date of MyApp is incorrect", MyApp.getLastModified(), lds.get(4).getLastModified());
		assertEquals("Name of MyApp is incorrect", "MyApp", lds.get(4).getName());
		
		assertEquals("Type of MyLink is incorrect", "link", lds.get(5).getType());
		assertEquals("Permissions of MyLink are incorrect", "rwxdrwxd", lds.get(5).getPermissions());
		assertEquals("Dimension of MyLink is incorrect", MyLink.dimension(), lds.get(5).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(5).getOwner());
		assertEquals("Id of MyLink is incorrect", MyLink.getId(), lds.get(5).getId());
		assertEquals("Last Modified date of MyLink is incorrect", MyLink.getLastModified(), lds.get(5).getLastModified());
		assertEquals("Name of MyLink is incorrect", "MyLink", lds.get(5).getName());
		
		assertEquals("Type of MyTextFile is incorrect", "textFile", lds.get(6).getType());
		assertEquals("Permissions of MyTextFile are incorrect","rwxdrwxd", lds.get(6).getPermissions());
		assertEquals("Dimension of MyTextFile is incorrect", MyTextFile.dimension(), lds.get(6).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(6).getOwner());
		assertEquals("Id of MyTextFile is incorrect", MyTextFile.getId(), lds.get(6).getId());
		assertEquals("Last Modified date of MyTextFile is incorrect", MyTextFile.getLastModified(), lds.get(6).getLastModified());
		assertEquals("Name of MyTextFile is incorrect", "MyTextFile", lds.get(6).getName());
		
		assertEquals("Type of otherCanNotList is incorrect", "directory", lds.get(7).getType());
		assertEquals("Permissions of otherCanNotList are incorrect","rwxd-wxd", lds.get(7).getPermissions());
		assertEquals("Dimension of otherCanNotList is incorrect", otherCanNotList.dimension(), lds.get(7).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(7).getOwner());
		assertEquals("Id of otherCanNotList is incorrect", otherCanNotList.getId(), lds.get(7).getId());
		assertEquals("Last Modified date of otherCanNotList is incorrect", otherCanNotList.getLastModified(), lds.get(7).getLastModified());
		assertEquals("Name of otherCanNotList is incorrect", "otherCanNotList", lds.get(7).getName());
		
		assertEquals("Type of ownerCanNotList is incorrect", "directory", lds.get(8).getType());
		assertEquals("Permissions of ownerCanNotList are incorrect","-wxdrwxd", lds.get(8).getPermissions());
		assertEquals("Dimension of ownerCanNotList is incorrect", ownerCanNotList.dimension(), lds.get(8).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(8).getOwner());
		assertEquals("Id of ownerCanNotList is incorrect", ownerCanNotList.getId(), lds.get(8).getId());
		assertEquals("Last Modified date of ownerCanNotList is incorrect", ownerCanNotList.getLastModified(), lds.get(8).getLastModified());
		assertEquals("Name of ownerCanNotList is incorrect", "ownerCanNotList", lds.get(8).getName());
	}
	
	@Test
	public void emptyDirectory(){
		manelLogin.setCurrentDir(emptyDirectory);
		
		ListDirectoryService service = new ListDirectoryService(validToken);
        service.execute();
        
        List<AbstractFileDTO> lds = service.result();
        
        assertEquals("Wrong number of files in Manel Empty Directory", 2, lds.size());
		
        assertEquals("Type of . is incorrect", "directory", lds.get(0).getType());
		assertEquals("Permissions of . are incorrect", "rwxdrwxd", lds.get(0).getPermissions());
		assertEquals("Dimension of . is correct", emptyDirectory.dimension(), lds.get(0).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(0).getOwner());
		assertEquals("Id of . is incorrect", emptyDirectory.getId(), lds.get(0).getId());
		assertEquals("Last Modified date of . is incorrect", emptyDirectory.getLastModified(), lds.get(0).getLastModified());
		assertEquals("Name of . is incorrect", ".", lds.get(0).getName());
		
		assertEquals("Type of .. is incorrect", "directory", lds.get(1).getType());
		assertEquals("Permissions of .. are incorrect","rwxdrwxd", lds.get(1).getPermissions());
		assertEquals("Dimension of .. is incorrect", manelHome.dimension(), lds.get(1).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(1).getOwner());
		assertEquals("Id of .. is incorrect", manelHome.getId(), lds.get(1).getId());
		assertEquals("Last Modified date of .. is incorrect", manelHome.getLastModified(), lds.get(1).getLastModified());
		assertEquals("Name of .. is incorrect", "..", lds.get(1).getName());
		
	}
	
	@Test
	public void rootAllowedToListFiles(){
		rootLogin.setCurrentDir(filesPermDir);
		
		ListDirectoryService service = new ListDirectoryService(rootToken);
        service.execute();
        
        List<AbstractFileDTO> lds = service.result();
        
		assertEquals("Wrong number of Files in filesPermDir (root)", 4, lds.size());
		
		assertEquals("Type of . is incorrect", "directory", lds.get(0).getType());
		assertEquals("Permissions of . are incorrect", "rwxdrwxd", lds.get(0).getPermissions());
		assertEquals("Dimension of . is correct", filesPermDir.dimension(), lds.get(0).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(0).getOwner());
		assertEquals("Id of . is incorrect", filesPermDir.getId(), lds.get(0).getId());
		assertEquals("Last Modified date of . is incorrect", filesPermDir.getLastModified(), lds.get(0).getLastModified());
		assertEquals("Name of . is incorrect", ".", lds.get(0).getName());
		
		assertEquals("Type of .. is incorrect", "directory", lds.get(1).getType());
		assertEquals("Permissions of .. are incorrect","rwxdrwxd", lds.get(1).getPermissions());
		assertEquals("Dimension of .. is incorrect", manelHome.dimension(), lds.get(1).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(1).getOwner());
		assertEquals("Id of .. is incorrect", manelHome.getId(), lds.get(1).getId());
		assertEquals("Last Modified date of .. is incorrect", manelHome.getLastModified(), lds.get(1).getLastModified());
		assertEquals("Name of .. is incorrect", "..", lds.get(1).getName());
		
		assertEquals("Type of otherCannotRead is incorrect", "textFile", lds.get(2).getType());
		assertEquals("Permissions of otherCannotRead are incorrect", "rwxd-wxd", lds.get(2).getPermissions());
		assertEquals("Dimension of otherCannotRead is incorrect", otherCannotRead.dimension(), lds.get(2).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(2).getOwner());
		assertEquals("Id of otherCannotRead is incorrect", otherCannotRead.getId(), lds.get(2).getId());
		assertEquals("Last Modified date of otherCannotRead is incorrect", otherCannotRead.getLastModified(), lds.get(2).getLastModified());
		assertEquals("Name of otherCannotRead is incorrect", "otherCannotRead", lds.get(2).getName());
		
		assertEquals("Type of ownerCannotRead is incorrect", "textFile", lds.get(3).getType());
		assertEquals("Permissions of ownerCannotRead are incorrect","-wxdrwxd", lds.get(3).getPermissions());
		assertEquals("Dimension of ownerCannotRead is incorrect", ownerCannotRead.dimension(), lds.get(3).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(3).getOwner());
		assertEquals("Id of ownerCannotRead is incorrect", ownerCannotRead.getId(), lds.get(3).getId());
		assertEquals("Last Modified date of ownerCannotRead is incorrect", ownerCannotRead.getLastModified(), lds.get(3).getLastModified());
		assertEquals("Name of ownerCannotRead is incorrect", "ownerCannotRead", lds.get(3).getName());
		
	}
	
	@Test
	public void ownerCanNotReadFile(){
		manelLogin.setCurrentDir(filesPermDir);
		
		ListDirectoryService service = new ListDirectoryService(validToken);
        service.execute();
        
        List<AbstractFileDTO> lds = service.result();
        
		assertEquals("Wrong number of Files in filesPermDir (Owner)", 3, lds.size());
		
		assertEquals("Type of . is incorrect", "directory", lds.get(0).getType());
		assertEquals("Permissions of . are incorrect", "rwxdrwxd", lds.get(0).getPermissions());
		assertEquals("Dimension of . is correct", filesPermDir.dimension(), lds.get(0).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(0).getOwner());
		assertEquals("Id of . is incorrect", filesPermDir.getId(), lds.get(0).getId());
		assertEquals("Last Modified date of . is incorrect", filesPermDir.getLastModified(), lds.get(0).getLastModified());
		assertEquals("Name of . is incorrect", ".", lds.get(0).getName());
		
		assertEquals("Type of .. is incorrect", "directory", lds.get(1).getType());
		assertEquals("Permissions of .. are incorrect","rwxdrwxd", lds.get(1).getPermissions());
		assertEquals("Dimension of .. is incorrect", manelHome.dimension(), lds.get(1).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(1).getOwner());
		assertEquals("Id of .. is incorrect", manelHome.getId(), lds.get(1).getId());
		assertEquals("Last Modified date of .. is incorrect", manelHome.getLastModified(), lds.get(1).getLastModified());
		assertEquals("Name of .. is incorrect", "..", lds.get(1).getName());
		
		assertEquals("Type of otherCannotRead is incorrect", "textFile", lds.get(2).getType());
		assertEquals("Permissions of otherCannotRead are incorrect", "rwxd-wxd", lds.get(2).getPermissions());
		assertEquals("Dimension of otherCannotRead is incorrect", otherCannotRead.dimension(), lds.get(2).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(2).getOwner());
		assertEquals("Id of otherCannotRead is incorrect", otherCannotRead.getId(), lds.get(2).getId());
		assertEquals("Last Modified date of otherCannotRead is incorrect", otherCannotRead.getLastModified(), lds.get(2).getLastModified());
		assertEquals("Name of otherCannotRead is incorrect", "otherCannotRead", lds.get(2).getName());
	}
	
	@Test 
	public void otherCanNotReadFile(){
		otherUserLogin.setCurrentDir(filesPermDir);
		
		ListDirectoryService service = new ListDirectoryService(otherUserToken);
        service.execute();
        
        List<AbstractFileDTO> lds = service.result();
        
		assertEquals("Wrong number of Files in filesPermDir (Other)", 3, lds.size());
		
		assertEquals("Type of . is incorrect", "directory", lds.get(0).getType());
		assertEquals("Permissions of . are incorrect", "rwxdrwxd", lds.get(0).getPermissions());
		assertEquals("Dimension of . is correct", filesPermDir.dimension(), lds.get(0).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(0).getOwner());
		assertEquals("Id of . is incorrect", filesPermDir.getId(), lds.get(0).getId());
		assertEquals("Last Modified date of . is incorrect", filesPermDir.getLastModified(), lds.get(0).getLastModified());
		assertEquals("Name of . is incorrect", ".", lds.get(0).getName());
		
		assertEquals("Type of .. is incorrect", "directory", lds.get(1).getType());
		assertEquals("Permissions of .. are incorrect","rwxdrwxd", lds.get(1).getPermissions());
		assertEquals("Dimension of .. is incorrect", manelHome.dimension(), lds.get(1).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(1).getOwner());
		assertEquals("Id of .. is incorrect", manelHome.getId(), lds.get(1).getId());
		assertEquals("Last Modified date of .. is incorrect", manelHome.getLastModified(), lds.get(1).getLastModified());
		assertEquals("Name of .. is incorrect", "..", lds.get(1).getName());
		
		assertEquals("Type of ownerCannotRead is incorrect", "textFile", lds.get(2).getType());
		assertEquals("Permissions of ownerCannotRead are incorrect", "-wxdrwxd", lds.get(2).getPermissions());
		assertEquals("Dimension of ownerCannotRead is incorrect", ownerCannotRead.dimension(), lds.get(2).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(2).getOwner());
		assertEquals("Id of ownerCannotRead is incorrect", ownerCannotRead.getId(), lds.get(2).getId());
		assertEquals("Last Modified date of ownerCannotRead is incorrect", ownerCannotRead.getLastModified(), lds.get(2).getLastModified());
		assertEquals("Name of ownerCannotRead is incorrect", "ownerCannotRead", lds.get(2).getName());
	}

	@Test(expected = AccessDeniedException.class) 
	public void ownerCanNotList(){
		manelLogin.setCurrentDir(ownerCanNotList);
		ListDirectoryService service = new ListDirectoryService(validToken);
        service.execute();
	}
	
	@Test(expected = AccessDeniedException.class)
	public void otherCanNotList(){
		otherUserLogin.setCurrentDir(otherCanNotList);
		ListDirectoryService service = new ListDirectoryService(otherUserToken);
        service.execute();    
    }
	
	@Test
	public void rootCanList() {
		rootLogin.setCurrentDir(otherCanNotList);
		ListDirectoryService service = new ListDirectoryService(rootToken);
        service.execute();
		
        List<AbstractFileDTO> lds = service.result();
        
        assertEquals("Wrong number of files in otherCanNotListDirectory", 2, lds.size());
        
		assertEquals("Type of . is incorrect", "directory", lds.get(0).getType());
		assertEquals("Permissions of . are incorrect", "rwxd-wxd", lds.get(0).getPermissions());
		assertEquals("Dimension of . is correct", otherCanNotList.dimension(), lds.get(0).getDimension());
		assertEquals("Username of Owner is incorrect", "manel", lds.get(0).getOwner());
		assertEquals("Id of . is incorrect", otherCanNotList.getId(), lds.get(0).getId());
		assertEquals("Last Modified date of . is incorrect", otherCanNotList.getLastModified(), lds.get(0).getLastModified());
		assertEquals("Name of . is incorrect", ".", lds.get(0).getName());
		
		assertEquals("Type of .. is incorrect", "directory", lds.get(1).getType());
		assertEquals("Permissions of .. are incorrect","rwxdrwxd", lds.get(1).getPermissions());
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
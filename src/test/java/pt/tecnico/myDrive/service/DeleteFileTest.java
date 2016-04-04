package pt.tecnico.myDrive.service;


import static org.junit.Assert.assertFalse;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.TextFile;
import pt.tecnico.myDrive.domain.Login;

import pt.tecnico.myDrive.exception.IsHomeDirectoryException;
import pt.tecnico.myDrive.exception.IsCurrentDirectoryException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidLoginException;
import pt.tecnico.myDrive.exception.AccessDeniedException;


public class DeleteFileTest extends TokenReceivingTest {
	
	private long rootToken;
	private Login rootLogin;
	MyDriveFS md ;
	User u1 ;
    Directory u1HomeDir;
    
	 
	
    @Override
    protected void populate() {
    	
    	md = MyDriveFS.getInstance();
    	u1 = new User(md, "u1", "pass1", "User1", "rwxd--x-");
    	User u2 = new User(md, "u2", "pass2", "User2", "rwxd--x-");
    	u1HomeDir= u1.getHomeDirectory();
    	
		User u3 =new User(md, "u1", "pass1", "User1", "rwxd--x-");
		Directory dirHomeDirU3 = new Directory(md, u1HomeDir, u3, "U3");
		u3.setHomeDirectory(dirHomeDirU3);
        
        new TextFile(md, u1.getHomeDirectory(), u1, "tf1", "blah erase me");
        TextFile tf2= new TextFile(md, u1HomeDir, u1, "tf2", "blah cant erase me");
        tf2.setPermissions("rw-d----");
        new TextFile(md, u1HomeDir, u2, "tf3", "blah erase me");
        TextFile tf4 =new TextFile(md, u1HomeDir, u2, "tf4", "blah cant erase me");
        tf4.setPermissions("rwxd----");
        
        Directory dirNotEmptyI = new Directory(md, u1HomeDir, u1, "NotEmptyI");
        Directory dirNotEmptyII = new Directory(md, dirNotEmptyI, u1, "NotEmptyII");
        new TextFile(md, dirNotEmptyII, u1, "tf", "blah delete me");
        
        Directory dirContainsCurrentDir = new Directory(md, u1HomeDir, u1, "dirContainsCurrentDir");
        Directory dirCurrentDirU2 = new Directory(md, dirContainsCurrentDir, u1, "dccd");
        Login u2Login= new Login(md, "u2", "pass2");
        u2Login.setCurrentDir(dirCurrentDirU2); 
        
        
        Directory dirContainsHomeDir = new Directory(md, u1HomeDir, u1, "dirContainsHomeDir");
        Directory dirHomeDir = new Directory(md, dirContainsHomeDir, u1, "dccd"); //can homeDir be from another user??
        u2.setHomeDirectory(dirHomeDir); 
        
        
        Directory dirContainsFileWithoutPermission = new Directory(md, u1HomeDir, u1, "dcfwp");       
        Directory dir = new Directory(md, dirContainsFileWithoutPermission, u1, "dccd");
        TextFile tf1=new TextFile(md, dir, u2, "tf1", "blah ble black cannot delete me");
        tf1.setPermissions("rwxd----");
        
        
        rootLogin = new Login(md, "root", "***");
        rootToken= rootLogin.getToken();
            
        super.populate("u1", "pass1");
         
    }
    
    
    @Test 
    public void sucessOwnTextFile(){
    	
    	DeleteFileService service = new DeleteFileService(validToken, "tf1");
    	service.execute();
    	
    	assertFalse("File was not deleted", (u1HomeDir.hasFile("tf1")));	
    }
   
    
    @Test 
    public void sucessTextFileOfAnother(){
    	
    	DeleteFileService service = new DeleteFileService(validToken, "tf3");
    	service.execute();
    	
    	assertFalse("File was not deleted", (u1HomeDir.hasFile("tf3")));
    }
   
    
    @Test(expected=AccessDeniedException.class)
    public void failOwnTextFile(){
    	
    	DeleteFileService service = new DeleteFileService(validToken, "tf2");
    	service.execute();
    }
   
    
    @Test (expected=AccessDeniedException.class)
    public void failTextFileOfAnother(){
    	
    	DeleteFileService service = new DeleteFileService(validToken, "tf4");
    	service.execute();
    }   
   
    
    @Test 
    public void sucessRootDeleteAnyFile(){
    	
    	rootLogin.setCurrentDir( u1.getHomeDirectory() );
    	
    	DeleteFileService service = new DeleteFileService(rootToken, "tf4");
    	service.execute();
    	
    	assertFalse("File was not deleted", (u1HomeDir.hasFile("tf4")));
    }
    
    
    @Test(expected= FileNotFoundException.class)
    public void deleteNonExistingFile(){
    	
    	DeleteFileService service = new DeleteFileService(validToken, "none" );
    	service.execute();	    	
    }
    
    
    @Test(expected = IsHomeDirectoryException.class)
    public void failHomeDirectory(){
    	 
    	DeleteFileService service = new DeleteFileService(validToken, "U3");
    	service.execute();
    }
 

    @Test(expected = IsCurrentDirectoryException.class )
    public void failParentDirectory(){
    	    	
    	DeleteFileService service = new DeleteFileService(validToken, "..");
    	service.execute();	   	
    }
    
    
    @Test(expected= IsCurrentDirectoryException.class )
    public void failCurrentDir(){
    	
    	DeleteFileService service = new DeleteFileService(validToken, ".");
    	service.execute();		
    }
   
    
    @Test
    public void sucessNotEmptyDirectory(){
   
    	DeleteFileService service = new DeleteFileService(validToken, "NotEmptyI");
    	service.execute(); 	
    	
    	assertFalse("File was not deleted", (u1HomeDir.hasFile("NotEmptyI")));
    }
   
    
    @Test(expected= AccessDeniedException.class)
    public void failDirContainsNoPermissionFiles(){
    	DeleteFileService service = new DeleteFileService(validToken, "dirContainsFileWithoutPermission");
    	service.execute();
    }
    
    @Test(expected= IsCurrentDirectoryException.class)
    public void failDirContainsCurrentDir(){
    	DeleteFileService service = new DeleteFileService(validToken, "dirContainsCurrentDir");
    	service.execute();
    }
 
    
    @Test(expected= IsHomeDirectoryException.class)
    public void failDirContainsHomeDir(){	
    	DeleteFileService service = new DeleteFileService(validToken, "dirContainsHomeDir");
    	service.execute();
    }


	@Override
	@Test(expected = InvalidLoginException.class)
	public void expiredSessionTest2h05minAgo() throws InvalidLoginException {
		super.setLastActivity2h05minAgo();
		ReadFileService service = new ReadFileService(validToken, "tf1");
		service.execute();	
	}


	@Override
	@Test
	public void sessionStillValidTest1h55min() {
		super.setLastActivity2h05minAgo();
		ReadFileService service = new ReadFileService(validToken, "tf1");
		service.execute();
		
		assertFalse("File was not deleted", (u1HomeDir.hasFile("tf1")));	
	}


	@Override
	@Test(expected= InvalidLoginException.class)
	public void nonExistentTokenTest() throws InvalidLoginException {
		ReadFileService service = new ReadFileService(invalidToken, "tf1");
		service.execute();
		
	}
    
   
	

}
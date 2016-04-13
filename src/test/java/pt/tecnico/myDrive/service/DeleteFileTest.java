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
	private Login jadeLogin;
	MyDriveFS md ;
	User jade ;
	User roxy;
    Directory jadeHomeDir;

    @Override
    protected void populate() {
    	
    	md = MyDriveFS.getInstance();
    	jade = new User(md, "jade", "passjade", "jade", "rwxd--xd", null);
    	roxy = new User(md, "roxy", "passroxy", "roxy", "rwxd--xd", null);
    	jadeHomeDir= jade.getHomeDirectory();
    	new TextFile(md, jadeHomeDir, jade, "tf1", "blah delete me");
		
        rootLogin = new Login(md, "root", "***");
        rootToken= rootLogin.getToken();
            
        super.populate("jade", "passjade"); 
        jadeLogin = md.getLoginByToken(validToken);
    }
    
    
    @Test 
    public void sucessOwnTextFile(){
    	
    	DeleteFileService service = new DeleteFileService(validToken, "tf1");
    	service.execute();
    	
    	assertFalse("File was not deleted", (jadeHomeDir.hasFile("tf1")));	
    }
   
    
    @Test 
    public void sucessTextFileOfAnother(){
    	
    	new TextFile(md, jadeHomeDir, roxy, "tf3", "blah erase me");
    	 
    	DeleteFileService service = new DeleteFileService(validToken, "tf3");
    	service.execute();
    	
    	assertFalse("File was not deleted", (jadeHomeDir.hasFile("tf3")));
    }
   
    
    @Test(expected=AccessDeniedException.class)
    public void failOwnTextFile(){
    	
    	TextFile tf2= new TextFile(md, jadeHomeDir, jade, "tf2", "blah cant delete me");
        tf2.setPermissions("rwx-----");
        
    	DeleteFileService service = new DeleteFileService(validToken, "tf2");
    	service.execute();
    }
   
    
    @Test (expected=AccessDeniedException.class)
    public void failTextFileOfAnother(){
    	
    	TextFile tf4 =new TextFile(md, jadeHomeDir, roxy, "tf4", "blah cant delete me");
        tf4.setPermissions("rwxd----");
         
    	DeleteFileService service = new DeleteFileService(validToken, "tf4");
    	service.execute();
    }   
   
    
    @Test 
    public void sucessRootDeleteAnyFile(){
    	
    	rootLogin.setCurrentDir(jadeHomeDir);
        
    	DeleteFileService service = new DeleteFileService(rootToken, "tf1");
    	service.execute();
    	
    	assertFalse("File was not deleted", (jadeHomeDir.hasFile("tf1")));
    }
    
    
    @Test(expected= FileNotFoundException.class)
    public void deleteNonExistingFile(){
    	
    	DeleteFileService service = new DeleteFileService(validToken, "none" );
    	service.execute();	    	
    }
    
    
    @Test(expected = IsHomeDirectoryException.class)
    public void failHomeDirectory(){
    	
    	Directory newRoxyHomeDir = new Directory(md, jadeHomeDir, roxy, "newRoxyHomeDir"); 
        roxy.setHomeDirectory(newRoxyHomeDir); 
    	
    	DeleteFileService service = new DeleteFileService(validToken, "newRoxyHomeDir");
    	service.execute();
    }
 

    @Test(expected = IsCurrentDirectoryException.class )
    public void failParentDirectory(){
    	
    	Directory newDir = new Directory(md, jadeHomeDir, jade, "newDir");
    	Directory newDirI = new Directory(md, newDir, jade, "newDirI");
    	jadeLogin.setCurrentDir(newDirI);
    	
    	DeleteFileService service = new DeleteFileService(validToken, "..");
    	service.execute();	   	
    }
    
    
    @Test(expected= IsCurrentDirectoryException.class )
    public void failCurrentDir(){
    	
    	Directory newDir = new Directory(md, jadeHomeDir, jade, "newDir");
    	jadeLogin.setCurrentDir(newDir);
    	
    	DeleteFileService service = new DeleteFileService(validToken, ".");
    	service.execute();		
    }
   
    
    @Test
    public void sucessNotEmptyDirectory(){
    	
    	Directory dirNotEmptyI = new Directory(md, jadeHomeDir, jade, "NotEmptyI");
        Directory dirNotEmptyII = new Directory(md, dirNotEmptyI, jade, "NotEmptyII");
        new TextFile(md, dirNotEmptyII, jade, "tf", "blah delete me");
        
    	DeleteFileService service = new DeleteFileService(validToken, "NotEmptyI");
    	service.execute(); 	
    	
    	assertFalse("File was not deleted", (jadeHomeDir.hasFile("NotEmptyI")));
    }
   
    
    @Test(expected= AccessDeniedException.class)
    public void failDirContainsNoPermissionFiles(){
    	
    	Directory dirContainsFileWithoutPermission = new Directory(md, jadeHomeDir, jade, "dirContainsFileWithoutPermission");       
        Directory dir = new Directory(md, dirContainsFileWithoutPermission, jade, "dirAux");          
        TextFile tf1=new TextFile(md, dir, roxy, "tf1", "blah ble black cannot delete me");
        tf1.setPermissions("rwxd----");
        
    	DeleteFileService service = new DeleteFileService(validToken, "dirContainsFileWithoutPermission");
    	service.execute();
    }
    
    @Test(expected= IsCurrentDirectoryException.class)
    public void failDirContainsCurrentDir(){
    	
    	Directory dirContainsCurrentDir = new Directory(md, jadeHomeDir, jade, "dirContainsCurrentDir");
        Directory dirCurrentDirRoxy = new Directory(md, dirContainsCurrentDir, jade, "dirAux");      
        Login roxyLogin= new Login(md, "roxy", "passroxy");
        roxyLogin.setCurrentDir(dirCurrentDirRoxy); 
        
    	DeleteFileService service = new DeleteFileService(validToken, "dirContainsCurrentDir");
    	service.execute();
    }
 
    
    @Test(expected= IsHomeDirectoryException.class)
    public void failDirContainsHomeDir(){	
    	
    	Directory dirContainsHomeDir = new Directory(md, jadeHomeDir, jade, "dirContainsHomeDir");
        Directory dirHomeDir = new Directory(md, dirContainsHomeDir, roxy, "newRoxyHomeDir"); 
        roxy.setHomeDirectory(dirHomeDir); 
         
    	DeleteFileService service = new DeleteFileService(validToken, "dirContainsHomeDir");
    	service.execute();
    }
    
    @Test(expected= AccessDeniedException.class )
    public void failDirWithoutPermissionContainsFile(){
    	
    	Directory dir = new Directory(md, jadeHomeDir, jade, "NotEmptyI");
        new TextFile(md, dir, jade, "tf", "blah try to delete me");
        dir.setPermissions("r-x-----");
        
    	jadeLogin.setCurrentDir(dir);
    	
    	DeleteFileService service = new DeleteFileService(validToken, "tf");
    	service.execute(); 	
    	
    	assertFalse("File was deleted", (dir.hasFile("tf")));
    }
   


	@Override
	@Test(expected = InvalidLoginException.class)
	public void expiredSessionTest2h05minAgo() throws InvalidLoginException {
		super.setLastActivity2h05minAgo();
		DeleteFileService service = new DeleteFileService(validToken, "tf1");
		service.execute();	
	}


	@Override
	@Test
	public void sessionStillValidTest1h55min() {
		super.setLastActivity1h55minAgo();
		DeleteFileService service = new DeleteFileService(validToken, "tf1");
		service.execute();
		
		assertFalse("File was not deleted", (jadeHomeDir.hasFile("tf1")));	
	}


	@Override
	@Test(expected= InvalidLoginException.class)
	public void nonExistentTokenTest() throws InvalidLoginException {
		DeleteFileService service = new DeleteFileService(invalidToken, "tf1");
		service.execute();
		
	}
    
}
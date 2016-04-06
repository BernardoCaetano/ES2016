package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.tecnico.myDrive.domain.*;

public class ListDirectoryTest extends TokenReceivingTest {
	
	String rootList = "directory rwxdr-x- size Super User id Date home";
	
	@Override
	public void populate() {
		md = MyDriveFS.getInstance();
		
		new User(mD, "Manel", "password", "Malandro", "rwxd----");
		super.populate("Manel", "password");
		
		User manel = md.getUserByUsername("Manel");
		Directory ManelHome = manel.getHomeDirectory();
	}
	
	@Test
	public void success() {
		ListDirectoryService service = new ListDirectoryService(validToken);
        service.execute();
		
		String dto = service.result();

        assertEquals("Root Directory Listed successfully", rootList, dto);
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
		ListDirectoryService service = new ListDirectoryService(validToken);
		service.execute();
		
		String dto = service.result();
		assertEquals("The Root Directory was listed succesfully",rootList,dto);
	}
	
	@Test(expected = InvalidLoginException.class)
	public void nonExistentTokenTest() {
		ListDirectoryService service = new ListDirectoryService(invalidToken);
		service.execute();
}

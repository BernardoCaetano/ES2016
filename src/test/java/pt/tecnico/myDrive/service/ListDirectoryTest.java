package pt.tecnico.myDrive.service;

import java.util.List;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.myDrive.domain.*;

import pt.tecnico.myDrive.exception.InvalidLoginException;

public class ListDirectoryTest extends TokenReceivingTest {
	
	@Override
	public void populate() {
		
	}
	
	@Test
	public void success() {
	
	}
	

	
	@Test(expected = InvalidLoginException.class)
	public void expiredSessionTest2h05minAgo() {
		super.setLastActivity2h05minAgo();
		ListDirectoryService service = new ListDirectoryService(validToken);
		service.execute();
	}
	
	@Test
	public void sessionStillValidTest1h55min() {

		
	}
	
	@Test(expected = InvalidLoginException.class)
	public void nonExistentTokenTest() {
		ListDirectoryService service = new ListDirectoryService(invalidToken);
		service.execute();
	}
}

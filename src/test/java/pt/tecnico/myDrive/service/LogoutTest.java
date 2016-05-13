package pt.tecnico.myDrive.service;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.InvalidLoginException;

public class LogoutTest extends TokenReceivingTest {

	MyDriveFS md;
	
	@Override
	protected void populate() {
		md = MyDriveFS.getInstance();
		new User(md, "dave", "clockwork", "dave", "rwxdrwxd", null);
		super.populate("dave", "clockwork");
	}
	
	
	
	@Override
	@Test(expected = InvalidLoginException.class)
	public void expiredSessionTest2h05minAgo() throws InvalidLoginException {
		super.setLastActivity2h05minAgo();
		LogoutService service = new LogoutService(validToken);
		service.execute();
	}

	@Override
	@Test(expected = InvalidLoginException.class)
	public void sessionStillValidTest1h55min() {
		super.setLastActivity1h55minAgo();
		LogoutService service = new LogoutService(validToken);
		service.execute();

		md.getLoginByToken(validToken);
	}

	@Override
	@Test(expected = InvalidLoginException.class)
	public void nonExistentTokenTest() throws InvalidLoginException {
		LogoutService service = new LogoutService(invalidToken);
		service.execute();

	}

}

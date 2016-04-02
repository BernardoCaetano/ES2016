package pt.tecnico.myDrive.service;

import java.math.BigInteger;
import java.sql.Date;
import java.util.Random;

import org.joda.time.DateTime;
import org.junit.Test;

import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.exception.InvalidLoginException;

public abstract class TokenReceivingTest extends AbstractServiceTest{
	
	protected void sessionExpired3hAgo(long token) throws InvalidLoginException{
		Login lg = MyDriveFS.getInstance().getLoginByToken(token);
		lg.setLastActivity(lg.getLastActivity().minusHours(3));
		
		ReadFileService service = new ReadFileService(token, "someTxt");
	}
	
	protected void sessionExpired2h01minAgo(long token) throws InvalidLoginException{
		Login lg = MyDriveFS.getInstance().getLoginByToken(token);
		DateTime time = lg.getLastActivity().minusHours(2);
		time = time.minusMinutes(1);
		lg.setLastActivity(time);
		
		ReadFileService service = new ReadFileService(token, "someTxt");
	}
	
	protected void setLastActivity1h59minAgo(long token) {
		Login lg = MyDriveFS.getInstance().getLoginByToken(token);
		DateTime time = lg.getLastActivity().minusHours(1);
		time = time.minusMinutes(59);
		lg.setLastActivity(time);
	}
	
	//TODO catarina: test for 2h (after asking professor if it is needed, and mentioning the milisecond problem)
	
	@Test(expected = InvalidLoginException.class)
	public void nonExistentTokenTest() {
		long fakeToken = new BigInteger(64, new Random()).longValue();
		ReadFileService service =  new ReadFileService(fakeToken, "exampleTxt");
	}
	
}

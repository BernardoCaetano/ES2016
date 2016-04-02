package pt.tecnico.myDrive.service;

import java.math.BigInteger;
import java.sql.Date;
import java.util.Random;

import org.joda.time.DateTime;
import org.junit.Test;

import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.exception.InvalidLoginException;

public abstract class TokenReceivingTest extends AbstractServiceTest implements TokenReceivingInterface {

	protected long validToken;
	protected long invalidToken;

	protected void populate(String username, String password) {

		LoginService svc = new LoginService(username, password);
		svc.execute();
		validToken = svc.result();
		invalidToken = new BigInteger(64, new Random()).longValue();

	}

	protected void setLastActivity2h05minAgo() {
		Login lg = MyDriveFS.getInstance().getLoginByToken(validToken);
		DateTime time = lg.getLastActivity().minusHours(2).minusMinutes(5);
		lg.setLastActivity(time);
	}

	protected void setLastActivity1h55minAgo() {
		Login lg = MyDriveFS.getInstance().getLoginByToken(validToken);
		DateTime time = lg.getLastActivity().minusHours(1).minusMinutes(55);
		lg.setLastActivity(time);
	}

}

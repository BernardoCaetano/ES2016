package pt.tecnico.myDrive.service;

import java.math.BigInteger;
import java.util.Random;

import org.joda.time.DateTime;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.exception.InvalidLoginException;

public abstract class TokenReceivingTest extends AbstractServiceTest implements TokenReceivingInterface {

	protected long validToken;
	protected long invalidToken;

	protected void populate(String username, String password) {
		validToken = (new Login(MyDriveFS.getInstance(), username, password)).getToken();
		
		while (true) {
			invalidToken = new BigInteger(64, new Random()).longValue();
			try {
				MyDriveFS.getInstance().getLoginByToken(invalidToken);
			} catch (InvalidLoginException e) {
				break;
			}
		}
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

package pt.tecnico.myDrive.service;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.Variable;
import pt.tecnico.myDrive.exception.InvalidLoginException;
import pt.tecnico.myDrive.exception.UndefinedVariableException;
import pt.tecnico.myDrive.service.dto.VariableDTO;

public class ShowVariableTest extends TokenReceivingTest {

	private MyDriveFS md;

	@Override
	protected void populate() {
		md = MyDriveFS.getInstance();
		new User(md, "username", "mypassword", "User", "rwxdrwxd", null);
		super.populate("username", "mypassword");
	}

	@Override
	@Test(expected = InvalidLoginException.class)
	public void expiredSessionTest2h05minAgo() throws InvalidLoginException {
		super.setLastActivity2h05minAgo();
		ShowVariableService service = new ShowVariableService(validToken, "myvar");
		service.execute();
	}

	@Override
	@Test
	public void sessionStillValidTest1h55min() {
		super.setLastActivity1h55minAgo();

		success();
	}

	@Override
	@Test(expected = InvalidLoginException.class)
	public void nonExistentTokenTest() throws InvalidLoginException {
		ShowVariableService service = new ShowVariableService(invalidToken, "myvar");
		service.execute();
	}

	@Test
	public void success() {
		Login login = md.getLoginByToken(validToken);
		login.addVariable("myvar", "myvalue");

		ShowVariableService service = new ShowVariableService(validToken, "myvar");
		service.execute();

		VariableDTO v = service.result();
		assertEquals("Variable name differs", "myvar", v.getName());
		assertEquals("Variable value differs", "myvalue", v.getValue());
	}

	@Test(expected = UndefinedVariableException.class)
	public void unknownVariableTest() {
		final String name = "idontexistforsureiguessblablabla";

		Login login = md.getLoginByToken(validToken);
		try {
			Variable v = login.getVariableByName(name);
			login.removeVariable(v);
		} catch (UndefinedVariableException e) {
			// expected
		}

		ShowVariableService service = new ShowVariableService(validToken, name);
		service.execute();
	}

}

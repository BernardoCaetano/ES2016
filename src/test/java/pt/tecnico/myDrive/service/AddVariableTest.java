package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.service.AddVariableService;
import pt.tecnico.myDrive.service.dto.VariableDTO;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.User;

import pt.tecnico.myDrive.exception.InvalidLoginException;

public class AddVariableTest extends TokenReceivingTest {

	MyDriveFS mD;
	
	User user;
	User other;
	
	Login login;
	Login otherLogin;
	
	Long otherToken;
	
	@Override
	protected void populate() {
		mD = MyDriveFS.getInstance();
		user = new User(mD, "username", "myPassword", "User", "rwxdrwxd", null);
		super.populate("username", "myPassword");
		
		other = new User(mD, "other", "otherPass", "Other", "rwxdrwxd", null);
		
		login = mD.getLoginByToken(validToken);
		otherLogin = new Login(mD, "other", "otherPass");
		
		otherToken = otherLogin.getToken();
		
		login.addVariable("First", "First Value");
		
		otherLogin.addVariable("Third", "Third Value");
	}
	
	@Test
	public void success() {
		AddVariableService service = new AddVariableService(validToken, "Second", "Second Value");
		service.execute();
		List<VariableDTO> variableList = service.result();
		
		assertNotNull(variableList);
		assertEquals("Wrong Number of Variables", 2, variableList.size());
		
		assertEquals("Wrong Name of First Variable","First", variableList.get(0).getName());
		assertEquals("Wrong Value of First Variable","First Value", variableList.get(0).getValue());
		
		assertEquals("Wrong Name of Second Variable","Second", variableList.get(1).getName());
		assertEquals("Wrong Value of Second Variable","Second Value", variableList.get(1).getValue());
	}
	
	@Test
	public void canRedefineVariable(){
		AddVariableService service = new AddVariableService(validToken, "First", "Redefined Value");
		service.execute();
		List<VariableDTO> variableList = service.result();
		
		assertNotNull(variableList);
		assertEquals("Wrong Number of Variables", 2, variableList.size());
		
		assertEquals("Wrong Name of First Variable","First", variableList.get(0).getName());
		assertEquals("Wrong Value of First Variable", "Redefined Value", variableList.get(0).getValue());
	}
	
	@Test
	public void differentLoginsDoesntCount() {
		Login newLogin = new Login(mD, "username", "myPassword");
		long newToken = newLogin.getToken();
		
		AddVariableService service = new AddVariableService(newToken, "Third", "Third Value");
		service.execute();
		List<VariableDTO> variableList = service.result();
		
		assertNotNull(variableList);
		assertEquals("Wrong Number of Variables", 2, variableList.size());
		
		assertEquals("Wrong Name of First Variable","First", variableList.get(0).getName());
		assertEquals("Wrong Value of First Variable", "Redefined Value", variableList.get(0).getValue());
		
		assertEquals("Wrong Name of Second Variable","Second", variableList.get(1).getName());
		assertEquals("Wrong Value of Second Variable","Second Value", variableList.get(1).getValue());
	}
	
	@Override
	public void expiredSessionTest2h05minAgo() throws InvalidLoginException {
		super.setLastActivity2h05minAgo();
		AddVariableService service = new AddVariableService(validToken, "myVar", "myValue");
		service.execute();
	}

	@Override
	public void sessionStillValidTest1h55min() {
		super.setLastActivity1h55minAgo();
		success();
	}

	@Override
	public void nonExistentTokenTest() throws InvalidLoginException {
		AddVariableService service = new AddVariableService(invalidToken, "myVar", "myValue");
		service.execute();
	}
}

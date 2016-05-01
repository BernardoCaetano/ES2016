package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.InvalidLoginException;
import pt.tecnico.myDrive.service.dto.VariableDTO;

public class ListVariablesTest extends TokenReceivingTest {

	MyDriveFS mD;
	Login login;
	
	@Override
	protected void populate() {
		mD = MyDriveFS.getInstance();
		
		new User(mD, "username", "password", "Name", "rwxdrwxd", null);
		super.populate("username", "password");
		login = mD.getLoginByToken(validToken);
		
		login.addVariable("noIdea", "I have no idea what to write here, lol");
		login.addVariable("iTry", "I really do");
	}
	
	@Test
    public void success() {
		ListVariablesService service = new ListVariablesService(validToken);
        service.execute();
        List<VariableDTO> variableList = service.result();
        
        assertNotNull("Returned list is null", variableList);
        assertEquals("List hasn't 2 Variables", 2, variableList.size());
        assertEquals("Variable name does not match", "noIdea", variableList.get(0).getName());
        assertEquals("Variable value does not match", "I have no idea what to write here, lol", variableList.get(0).getValue());
        
        assertEquals("Variable name does not match", "iTry", variableList.get(1).getName());
        assertEquals("Variable value does not match", "I really do", variableList.get(1).getValue());
    }
	
	@Test
    public void successWithChangeValue() {
		login.addVariable("iTry", "The Value MUST be changed!");
		
		ListVariablesService service = new ListVariablesService(validToken);
        service.execute();
        List<VariableDTO> variableList = service.result();
        
        assertNotNull("Returned list is null", variableList);
        assertEquals("List hasn't 2 Variables", 2, variableList.size());
        assertEquals("Variable name does not match", "noIdea", variableList.get(0).getName());
        assertEquals("Variable value does not match", "I have no idea what to write here, lol", variableList.get(0).getValue());
        
        assertEquals("Variable name does not match", "iTry", variableList.get(1).getName());
        assertEquals("Variable value does not match", "The Value MUST be changed!", variableList.get(1).getValue());
	}
	
	@Test(expected = InvalidLoginException.class)
	public void expiredSessionTest2h05minAgo() throws InvalidLoginException {
		super.setLastActivity2h05minAgo();
		ListVariablesService service = new ListVariablesService(validToken);
        service.execute();
	}
	
	@Test
	public void sessionStillValidTest1h55min() {
		super.setLastActivity1h55minAgo();
		ListVariablesService service = new ListVariablesService(validToken);
		service.execute();
		List<VariableDTO> variableList = service.result();
	    
		assertNotNull("Returned list is null", variableList);
	    assertEquals("List hasn't 2 Variables", 2, variableList.size());
	    assertEquals("Variable name does not match", "noIdea", variableList.get(0).getName());
	    assertEquals("Variable value does not match", "I have no idea what to write here, lol", variableList.get(0).getValue());
	        
	    assertEquals("Variable name does not match", "iTry", variableList.get(1).getName());
	    assertEquals("Variable value does not match", "I really do", variableList.get(1).getValue());
	}

	@Test(expected = InvalidLoginException.class)
	public void nonExistentTokenTest() throws InvalidLoginException {
		ListVariablesService service = new ListVariablesService(invalidToken);
		service.execute();
	}
}

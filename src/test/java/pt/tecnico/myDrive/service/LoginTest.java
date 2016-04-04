package pt.tecnico.myDrive.service;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exception.UserNotFoundException;
import pt.tecnico.myDrive.exception.WrongPasswordException;

public class LoginTest extends AbstractServiceTest {

	@Override
	protected void populate() {
		MyDriveFS md = MyDriveFS.getInstance();

		new User(md, "jane", "sp1der", "Mary Jane", null);
		new User(md, "camoes", "lus1adas", "Luís de Camões", null);
	}

	@Test
	public void success() {
		MyDriveFS md = MyDriveFS.getInstance();
		LoginService login = new LoginService("jane", "sp1der");
		login.execute();
		long token = login.result();

		assertNotNull("Login failed.", md.getLoginByToken(token));
	}

	@Test(expected = UserNotFoundException.class)
	public void userNotFound() {
		LoginService login = new LoginService("idontexistbla", "pass");
		login.execute();
	}

	@Test(expected = WrongPasswordException.class)
	public void wrongPassword() {
		LoginService login = new LoginService("jane", "wrongpw");
		login.execute();
	}
	
	@Test
	public void differentToken(){
		LoginService login1 = new LoginService("jane", "sp1der");
		LoginService login2 = new LoginService("camoes", "lus1adas");
		login1.execute();
		login2.execute();
		assertNotEquals("Tokens are the same.", login1.result(), login2.result());
	}
	
	@Test
	public void differentTokenSameUser(){
		LoginService login1 = new LoginService("camoes", "lus1adas");
		LoginService login2 = new LoginService("camoes", "lus1adas");
		login1.execute();
		login2.execute();
		assertNotEquals("Tokens are the same.", login1.result(), login2.result());
	}

}

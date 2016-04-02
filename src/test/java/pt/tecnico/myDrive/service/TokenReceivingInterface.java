package pt.tecnico.myDrive.service;

import pt.tecnico.myDrive.exception.InvalidLoginException;

public interface TokenReceivingInterface {
	public void expiredSessionTest2h05minAgo() throws InvalidLoginException;
	public void sessionStillValidTest1h55min();
	public void nonExistentTokenTest() throws InvalidLoginException;
}

package pt.tecnico.myDrive.service;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Test;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.domain.TextFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.exception.AccessDeniedException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidLoginException;
import pt.tecnico.myDrive.exception.NotTextFileException;

public class ReadFileTest extends TokenReceivingTest {
	
	private long maryToken;

	@Override
	protected void populate() {
		MyDriveFS md = MyDriveFS.getInstance();
		User john = new User(md, "john", "1234", "Johnny", "rwxd----");
		User mary = new User(md, "mary", "5678", "Mary", "rwxdr-x-");
		new TextFile(md, mary.getHomeDirectory(), mary, "exampleTxt", "/home/mary/exampleApp 1 2");
		new Directory(md, mary.getHomeDirectory(), mary, "exampleDir");
		new App(md, mary.getHomeDirectory(), john, "exampleApp", "pt.tecnico.myDrive.Main.main");
		
		LoginService svc = new LoginService("mary", "5678");
		svc.execute();
		maryToken = svc.result();
	}
	
	@Test
	public void success() {
		ReadFileService service = new ReadFileService(maryToken, "exampleTxt");
		service.execute();
		String res = service.result();
		
		assertEquals("Content not is the same", "/home/mary/exampleApp 1 2", res);
	}
	
	@Test(expected = NotTextFileException.class)
	public void notTextFileTest() {
		ReadFileService service = new ReadFileService(maryToken, "exampleDir");
		service.execute();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void nonExistentFileTest() {
		ReadFileService service = new ReadFileService(maryToken, "void");
		service.execute();
	}
	
	@Test(expected = AccessDeniedException.class)
	public void permissionDeniedTest() {
		ReadFileService service = new ReadFileService(maryToken, "exampleApp");
		service.execute();
	}
	
	@Test(expected = InvalidLoginException.class)
	public void expiredSessionTest3h() {
		super.sessionExpired3hAgo(maryToken);
	}
	
	@Test(expected = InvalidLoginException.class)
	public void expiredSessionTest2h01minAgo() {
		super.sessionExpired2h01minAgo(maryToken);
	}
	
	@Test
	public void sessionStillValidTest1h59min() {
		super.setLastActivity1h59minAgo(maryToken);
		ReadFileService service = new ReadFileService(maryToken, "exampleTxt");
		service.execute();
		String res = service.result();
		
		assertEquals("Content not is the same", "/home/mary/exampleApp 1 2", res);

	}
	
}

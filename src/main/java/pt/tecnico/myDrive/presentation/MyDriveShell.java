package pt.tecnico.myDrive.presentation;

import java.util.HashMap;
import java.util.Map;

import pt.tecnico.myDrive.service.LoginService;
import pt.tecnico.myDrive.service.LogoutService;

public class MyDriveShell extends Shell {

	private Map<String, Long> tokens = new HashMap<String, Long>();
	
	private String currentUsername;
	private long currentToken;
	private boolean asGuest;

	Long switchToToken(String username) {
		Long token = tokens.get(username);
		if (token != null) {
			currentToken = token;
			currentUsername = username;
		}
		return token;
	}

	void switchToNewToken(String username, long token) {
		logoutGuest();
		tokens.put(username, token);
		currentToken = token;
		currentUsername = username;
	}
	
	void logoutGuest(){
		if(asGuest){
			LogoutService logout = new LogoutService(currentToken);
			logout.execute();
			asGuest = false;
		}
	}

	String getCurrentUsername() {
		return currentUsername;
	}

	long getCurrentToken() {
		return currentToken;
	}
	
	private void login(String username, String password){
		LoginService login = new LoginService(username, password);
		login.execute();
		currentToken = login.result();
		currentUsername = username;
	}

	public MyDriveShell() {
		super("MyDrive");
		login("nobody", "");
		asGuest = true;

		// Add MyDrive commands here
		new Environment(this);
		new Execute(this);
		new Key(this);
		new Write(this);
		new Login(this);
	}

	@Override
	protected void quit() {
		logoutGuest();
		super.quit();
	}

}

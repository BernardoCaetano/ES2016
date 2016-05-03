package pt.tecnico.myDrive.presentation;

import java.util.HashMap;
import java.util.Map;

public class MyDriveShell extends Shell {

	private Map<String, Long> tokens = new HashMap<String, Long>();
	private String currentUsername;
	private long currentToken;

	long getToken(String username) {
		return tokens.get(username);
	}

	void addToken(String username, long token) {
		tokens.put(username, token);
		currentToken = token;
		currentUsername = username;
	}

	String getCurrentUsername() {
		return currentUsername;
	}

	long getCurrentToken() {
		return currentToken;
	}
	

	public MyDriveShell() {
		super("MyDrive");

		// Add MyDrive commands here
		new Environment(this);
		new Execute(this);
	}

}

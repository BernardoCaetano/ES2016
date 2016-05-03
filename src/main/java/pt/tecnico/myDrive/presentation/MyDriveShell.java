package pt.tecnico.myDrive.presentation;

import java.util.HashMap;
import java.util.Map;

public class MyDriveShell extends Shell {

	private Map<String, Long> tokens = new HashMap<String, Long>();
	
	// TODO: Log nobody in
	private String currentUsername;
	private long currentToken;

	Long switchToToken(String username) {
		Long token = tokens.get(username);
		if (token != null) {
			currentToken = token;
			currentUsername = username;
		}
		return token;
	}

	void switchToNewToken(String username, long token) {
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
		new Key(this);
	}

}

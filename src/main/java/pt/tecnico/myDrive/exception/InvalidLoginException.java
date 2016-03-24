package pt.tecnico.myDrive.exception;

public class InvalidLoginException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	private long token;

	public InvalidLoginException(long token) {
		this.token = token;
	}
	
	public long getToken() { return token; }
	
	@Override
	public String getMessage() {
		return "The token " + token + " is not valid. Please login again.";
	}
	

}

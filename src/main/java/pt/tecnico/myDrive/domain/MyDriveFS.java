package pt.tecnico.myDrive.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidLoginException;
import pt.tecnico.myDrive.exception.InvalidOperationException;
import pt.tecnico.myDrive.exception.InvalidPathException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;
import pt.tecnico.myDrive.exception.NameAlreadyExistsException;
import pt.tecnico.myDrive.exception.UserNotFoundException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.NotTextFileException;
import pt.tecnico.myDrive.exception.CannotRemoveUserException;

import org.jdom2.Element;
import org.jdom2.Document;

public class MyDriveFS extends MyDriveFS_Base {

	public static MyDriveFS getInstance() throws InvalidUsernameException, InvalidPathException{
		MyDriveFS myDrive = FenixFramework.getDomainRoot().getMyDrive();
		if (myDrive != null)
			return myDrive;

		MyDriveFS newMyDrive = new MyDriveFS();
		RootDirectory rootDir = RootDirectory.getInstance(newMyDrive);
		newMyDrive.setRootDirectory(rootDir);
		User root = new SuperUser(newMyDrive, "root", "***", "Super User", "rwxdr-x-", "/home/root");
		new Guest(newMyDrive);
		rootDir.setOwner(root);
		return newMyDrive;
	}

	private MyDriveFS() {
		setRoot(FenixFramework.getDomainRoot());
	}

	@Override
	public void addUser(User userToBeAdded) throws NameAlreadyExistsException {
		if (hasUser(userToBeAdded.getUsername())) {
			throw new NameAlreadyExistsException(userToBeAdded.getUsername());
		}
		super.addUser(userToBeAdded);
	}

	protected boolean hasUser(String username) {
		try {
			return getUserByUsername(username) != null;
		} catch(UserNotFoundException e){
			return false;
		}
	}

	protected User getUserByUsername(String username) throws UserNotFoundException{
		for (User user : getUsers()) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		throw new UserNotFoundException(username);
	}
	
	@Override
	public Set<User> getUserSet() {
		throw new InvalidOperationException("Obtaining application users");
	}
	
	protected Set<User> getUsers() {
		return super.getUserSet();
	}

	protected void incrementLastFileID() {
		super.setLastFileID(getLastFileID() + 1);
	}

	private AbstractFile getFileByPathAux(Directory currentDir, String path, boolean follow)
			throws FileNotFoundException, InvalidPathException {
		if (!isValidPath(path)){
			throw new InvalidPathException(path);
		}
		if (path.equals("/")) {
			return getRootDirectory();
		} else if (path.startsWith("/")) {
			currentDir = getRootDirectory();
			path = path.substring(1);
		}

		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}

		String[] parts = path.split("/", 2);

		if (parts.length == 0) {
			throw new InvalidPathException(path);
		}

		AbstractFile f = follow	? currentDir.getFileByName(parts[0])
								: currentDir.getFileByNameNoFollow(parts[0]);

		if (parts.length < 2) {
			return f;
		}

		try {
			return getFileByPathAux((Directory) f, parts[1], follow);
		} catch (InvalidPathException e) {
			throw new InvalidPathException(path);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException(path);
		}
	}
	
	public AbstractFile getFileByPath(Directory currentDir, String path){
		return getFileByPathAux(currentDir, path, true);
	}
	
	public AbstractFile getFileByPathNoFollow(Directory currentDir, String path){
		return getFileByPathAux(currentDir, path, false);
	}

	public Directory getDirectoryByPath(Directory currentDir, String path) 
			throws NotDirectoryException, FileNotFoundException, InvalidPathException {
		AbstractFile af = getFileByPath(currentDir, path);
		if (!(af instanceof Directory)){
			throw new NotDirectoryException(af.getName());
		}
		return (Directory) af;
	}
	
	public TextFile getTextFileByPath(Directory currentDir, String path) 
			throws NotDirectoryException, FileNotFoundException, InvalidPathException {
		AbstractFile af = getFileByPath(currentDir, path);
		if (!(af instanceof TextFile)){
			throw new NotTextFileException(af.getName());
		}
		return (TextFile) af;
	}

	@Override
	public Set<Login> getLoginSet() {
		throw new InvalidOperationException("Obtaining application login history");
	}
	
	private Set<Login> getLogins() {
		return super.getLoginSet();
	}
	
	public Login getLoginByToken(long token) {
		for (Login l : this.getLogins()) {
			if (token == l.getToken() && l.isValid()) {
				l.updateLastActivity();
				return l;
			}
		}
		throw new InvalidLoginException(token);
	}
	
	protected void deleteInvalidLogins() {
		for (Login l : this.getLogins()) {
			if (!l.isValid()) {
				l.cleanup();
			}
		}
	}

	public Document xmlExport() {
		Element element = new Element("myDrive");
		Document doc = new Document(element);
		
		Set<User> userSet = getUsers();

		for (User u : userSet) {
			if (!u.getUsername().equals("root") && !u.getUsername().equals("nobody"))
				element.addContent(u.xmlExport());
		}
		
		ArrayList<AbstractFile> allFiles = getRootDirectory().getFilesRecursive();
		Collections.sort(allFiles);
		
		for (AbstractFile f: allFiles){
			if (!f.getPath().equals("/") && !f.getPath().equals("/home/") && !f.isHomeDirectory())
				element.addContent(f.xmlExport());
		}		

		return doc;
	}

	private boolean elementExistsInMyDriveFS(Element xml) {
		RootDirectory rootDir = RootDirectory.getInstance(this);
		Directory parentDirectory = rootDir.getParentFromPath(xml.getAttribute("path").getValue());
		String nameOfFile = rootDir.getNameOfFileFromPath(xml.getAttribute("path").getValue());
		if (!parentDirectory.hasFile(nameOfFile)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void xmlImport(Element myDriveElement) {
		
		RootDirectory rootDir = getRootDirectory();
		
		for (Element userElement : myDriveElement.getChildren("user")) {
			String username = userElement.getAttributeValue("username");
			try {
				User u = getUserByUsername(username);
				u.xmlImport(this, userElement);
			} catch (UserNotFoundException e) {
				new User(this, userElement);
			}
		}

		for (Element dirElement : myDriveElement.getChildren("directory")) {
			try {
				Directory d = getDirectoryByPath(rootDir, dirElement.getChildText("path"));
				d.xmlImport(this, dirElement);
			} catch (NotDirectoryException | FileNotFoundException e ) {
				new Directory(this, dirElement);
			}
		}

		for (Element textFileElement : myDriveElement.getChildren("textFile")) {
			if (elementExistsInMyDriveFS(textFileElement)) {
				new TextFile(this, textFileElement);
			} else {
				getFileByPath(rootDir, textFileElement.getAttribute("path").getValue()).xmlImport(this,
						textFileElement);
			}
		}

		for (Element appElement : myDriveElement.getChildren("app")) {
			if (elementExistsInMyDriveFS(appElement)) {
				new App(this, appElement);
			} else {
				getFileByPath(rootDir, appElement.getAttribute("path").getValue()).xmlImport(this, appElement);
			}
		}

		for (Element linkElement : myDriveElement.getChildren("link")) {
			if (elementExistsInMyDriveFS(linkElement)) {
				new Link(this, linkElement);
			} else {
				getFileByPath(rootDir, linkElement.getAttribute("path").getValue()).xmlImport(this, linkElement);
			}
		}

		for (Element userElement : myDriveElement.getChildren("user")) {
			if (hasUser(userElement.getAttribute("username").getValue())) {
				getUserByUsername(userElement.getAttribute("username").getValue()).xmlImport(this, userElement);
			} else {
				new User(this, userElement);
			}
		}
	}
	
	protected static boolean isValidPath(String path) {
    	if(path == null || path.length() < 1){
    		return false;
    	}
    	return !path.contains("\0") && !path.contains("//");
    }
	
	public void cleanup() {
		for (User u : getUsers()) u.cleanup();
		this.getRootDirectory().cleanup();
	}
	
	@Override
	public void removeUser(User u) throws CannotRemoveUserException {
		if(u instanceof Guest || u instanceof SuperUser)
			throw new CannotRemoveUserException(u);
		super.removeUser(u);
	}
}

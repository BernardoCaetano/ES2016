package pt.tecnico.myDrive.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidLoginException;
import pt.tecnico.myDrive.exception.InvalidPathException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;
import pt.tecnico.myDrive.exception.NameAlreadyExistsException;
import pt.tecnico.myDrive.exception.UserNotFoundException;
import pt.tecnico.myDrive.exception.NotDirectoryException;

import org.jdom2.Element;
import org.jdom2.Document;

public class MyDriveFS extends MyDriveFS_Base {

	public static MyDriveFS getInstance() throws InvalidUsernameException{
		MyDriveFS myDrive = FenixFramework.getDomainRoot().getMyDrive();
		if (myDrive != null)
			return myDrive;

		MyDriveFS newMyDrive = new MyDriveFS();
		RootDirectory rootDir = RootDirectory.getInstance(newMyDrive);
		newMyDrive.setRootDirectory(rootDir);
		User root = new SuperUser(newMyDrive, "root", "***", "Super User", "rwxdr-x-");
		rootDir.setOwner(root);
		return newMyDrive;
	}

	private MyDriveFS() {
		setRoot(FenixFramework.getDomainRoot());
	}

	@Override
	public void addUsers(User userToBeAdded) throws NameAlreadyExistsException {
		if (hasUser(userToBeAdded.getUsername())) {
			throw new NameAlreadyExistsException(userToBeAdded.getUsername());
		}
		super.addUsers(userToBeAdded);
	}

	public boolean hasUser(String username) {
		try {
			return getUserByUsername(username) != null;
		} catch(UserNotFoundException e){
			return false;
		}
	}

	public User getUserByUsername(String username) throws UserNotFoundException{
		for (User user : getUsersSet()) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		throw new UserNotFoundException(username);
	}

	public void incrementLastFileID() {
		super.setLastFileID(getLastFileID() + 1);
	}

	public AbstractFile getFileByPath(Directory currentDir, String path)
			throws FileNotFoundException, InvalidPathException {
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

		AbstractFile f = currentDir.getFileByName(parts[0]);

		if (parts.length < 2) {
			return f;
		}

		try {
			return getFileByPath((Directory) f, parts[1]);
		} catch (InvalidPathException e) {
			throw new InvalidPathException(path);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException(path);
		}
	}

	public Directory getDirectoryByPath(Directory currentDir, String path) 
			throws NotDirectoryException, FileNotFoundException, InvalidPathException {
		AbstractFile af = getFileByPath(currentDir, path);
		if (!(af instanceof Directory)){
			throw new NotDirectoryException(af.getName());
		}
		return (Directory) af;
	}
	
	public Login getLoginByToken(long token) {
		for (Login l : this.getLoginSet()) {
			if (token == l.getToken() && l.isValid()) {
				return l;
			}
		}
		throw new InvalidLoginException(token);
	}
	
	public void deleteInvalidLogins() {
		for (Login l : this.getLoginSet()) {
			if (!l.isValid()) {
				l.remove();
			}
		}
	}

	public Document xmlExport() {
		Element element = new Element("myDrive");
		Document doc = new Document(element);
		
		ArrayList<AbstractFile> allFiles = getRootDirectory().getFilesRecursive();
		Collections.sort(allFiles);
		
		for (AbstractFile f: allFiles){
			if (!f.getPath().equals("/") && !f.getPath().equals("/home/") && !f.isHomeDirectory())
				element.addContent(f.xmlExport());
		}
		
		
		Set<User> userSet = getUsersSet();

		for (User u : userSet) {
			if (!u.getUsername().equals("root"))
				element.addContent(u.xmlExport());
		}

		return doc;
	}

	public boolean elementExistsInMyDriveFS(Element xml) {
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
		RootDirectory rootDir = RootDirectory.getInstance(this);

		for (Element directoryElement : myDriveElement.getChildren("directory")) {
			if (elementExistsInMyDriveFS(directoryElement)) {
				new Directory(this, directoryElement);
			} else {
				getFileByPath(rootDir, directoryElement.getAttribute("path").getValue()).xmlImport(this,
						directoryElement);
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
}

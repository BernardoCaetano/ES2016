package pt.tecnico.myDrive.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidPathException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;
import pt.tecnico.myDrive.exception.NameAlreadyExistsException;
import pt.tecnico.myDrive.exception.NotTextFileException;
import pt.tecnico.myDrive.exception.UserNotFoundException;
import pt.tecnico.myDrive.exception.NotDirectoryException;

import org.jdom2.Element;
import org.jdom2.Document;

public class MyDriveFS extends MyDriveFS_Base {

	public static MyDriveFS getInstance() throws InvalidUsernameException{
		MyDriveFS mydrive = FenixFramework.getDomainRoot().getMyDrive();
		if (mydrive != null)
			return mydrive;

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

	public Document xmlExport() {
		Element element = new Element("myDrive");
		Document doc = new Document(element);
		
		ArrayList<AbstractFile> allFiles = getRootDirectory().getFilesRecursive();
		Collections.sort(allFiles);
		
		for (AbstractFile f: allFiles){
			element.addContent(f.xmlExport());
		}
		
		
		Set<User> userSet = getUsersSet();

		for (User u : userSet) {
			element.addContent(u.xmlExport());
		}

		return doc;
	}

	public ArrayList<String> listDirectorySorted(Directory currentDir, String path) 
			throws InvalidPathException, FileNotFoundException {

		AbstractFile dir = getFileByPath(currentDir, path);
		if (!(dir instanceof Directory)) {
			throw new NotDirectoryException(path);
		}

		ArrayList<AbstractFile> files = ((Directory) dir).getFilesSimpleSorted();
		ArrayList<String> filenames = new ArrayList<String>();
		filenames.add(".");
		filenames.add("..");

		for (AbstractFile f : files) {
			filenames.add(f.getName());
		}

		return filenames;

	}

	public Directory createIntermediatePath(Directory currentDir, String path) {
		
		if (!path.contains("/")) {
			return currentDir;
		}

		String[] parts = path.split("/", 2);
		if (path.startsWith("/")) {
			parts[0] = "/" + parts[0];
		}
		
		
		Directory d;
		try {
			d = getDirectoryByPath(currentDir, parts[0]);
		} catch (FileNotFoundException e) {
			d = new Directory(this, currentDir, this.getUserByUsername("root"),
					(parts[0].startsWith("/") ? parts[0].substring(1) : parts[0]));
		}

		if (!parts[1].contains("/")) {
			return d;
		}

		return createIntermediatePath(d, parts[1]);
	}

	public Directory createDirectoryFromPath(User owner, Directory currentDir, String path) {
		
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		
		Directory d = createIntermediatePath(currentDir, path);
		Directory newDir = new Directory(this, d, owner, path.substring(path.lastIndexOf("/") + 1));
		return newDir;
	}

	public TextFile createTextFileFromPath(User owner, Directory currentDir, String path, String content) {
		Directory d = createIntermediatePath(currentDir, path);
		TextFile t = new TextFile(this, d, owner, path.substring(path.lastIndexOf("/") + 1), content);
		return t;
	}

	public Link createLinkFromPath(User owner, Directory currentDir, String path, String content) {
		Directory d = createIntermediatePath(currentDir, path);
		Link l = new Link(this, d, owner, path.substring(path.lastIndexOf("/") + 1), content);
		return l;
	}

	public App createAppFromPath(User owner, Directory currentDir, String path, String content) {
		Directory d = createIntermediatePath(currentDir, path);
		App a = new App(this, d, owner, path.substring(path.lastIndexOf("/") + 1), content);
		return a;
	}

	public void removeFileGivenPath(Directory currentDir, String path) throws FileNotFoundException, InvalidPathException{

		AbstractFile af = getFileByPath(currentDir, path);
		af.removeFile();
	}

	public String readTextFile(Directory currentDir, String path) throws NotTextFileException, FileNotFoundException, InvalidPathException{
		AbstractFile af= getFileByPath(currentDir, path);
		if (!(af instanceof TextFile)){
			throw new NotTextFileException(af.getName());
		}
		return ((TextFile)af).getContent();
	}
	
	public void xmlImport(Element myDriveElement) {
		Directory parentDirectory;
		String nameOfFile;
		RootDirectory rootDir = RootDirectory.getInstance(this);
			
		rootDir.xmlImport(this, myDriveElement.getChild("rootDirectory"));
				
		for (Element directoryElement: myDriveElement.getChildren("directory")){
			parentDirectory = rootDir.getParentFromPath(directoryElement.getAttribute("path").getValue());
			nameOfFile = rootDir.getNameOfFileFromPath(directoryElement.getAttribute("path").getValue());
			
			if( !parentDirectory.hasFile(nameOfFile) ){
				new Directory(this, directoryElement);
			}
			else {
				getFileByPath(rootDir, directoryElement.getAttribute("path").getValue()).xmlImport(this, directoryElement);
			}
		}
		
		for (Element textFileElement: myDriveElement.getChildren("textFile")){
			parentDirectory = rootDir.getParentFromPath(textFileElement.getAttribute("path").getValue());
			nameOfFile = rootDir.getNameOfFileFromPath(textFileElement.getAttribute("path").getValue());
			
			if( !parentDirectory.hasFile(nameOfFile) ){
				new TextFile(this, textFileElement);
			}
			else {
				getFileByPath(rootDir, textFileElement.getAttribute("path").getValue()).xmlImport(this, textFileElement);
			}
		}
		
		for (Element appElement: myDriveElement.getChildren("app")){
			parentDirectory = rootDir.getParentFromPath(appElement.getAttribute("path").getValue());
			nameOfFile = rootDir.getNameOfFileFromPath(appElement.getAttribute("path").getValue());
			
			if( !parentDirectory.hasFile(nameOfFile) ){
				new App(this, appElement);
			}
			else {
				getFileByPath(rootDir, appElement.getAttribute("path").getValue()).xmlImport(this, appElement);
			}
		}
			
		for (Element linkElement: myDriveElement.getChildren("link")){
			parentDirectory = rootDir.getParentFromPath(linkElement.getAttribute("path").getValue());
			nameOfFile = rootDir.getNameOfFileFromPath(linkElement.getAttribute("path").getValue());
			
			if( !parentDirectory.hasFile(nameOfFile) ){
				new Link(this, linkElement);
			}
			else {
				getFileByPath(rootDir, linkElement.getAttribute("path").getValue()).xmlImport(this, linkElement);
			}
		}
			
		for (Element userElement: myDriveElement.getChildren("user")){
			if (hasUser(userElement.getAttribute("username").getValue())){
				getUserByUsername(userElement.getAttribute("username").getValue()).xmlImport(this, userElement);
			} 
			else {
				new User(this, userElement);
			}
			
		}
    }	
}

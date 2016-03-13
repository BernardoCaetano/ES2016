package pt.tecnico.myDrive.domain;

import java.util.ArrayList;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidPathException;
import pt.tecnico.myDrive.exception.NameAlreadyExistsException;
import pt.tecnico.myDrive.exception.NotTextFileException;
import pt.tecnico.myDrive.exception.UserNotFoundException;
import pt.tecnico.myDrive.exception.NotDirectoryException;

import org.jdom2.Element;
import org.jdom2.Document;

import java.util.Set;

public class MyDriveFS extends MyDriveFS_Base {

	public static MyDriveFS getInstance() {
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

	public Directory getDirectoryByPath(Directory currentDir, String path) throws NotDirectoryException{
		AbstractFile af = getFileByPath(currentDir, path);
		if (!(af instanceof Directory)){
			throw new NotDirectoryException(af.getName());
		}
		return (Directory) af;
	}

	public Document xmlExport() {
		Element element = new Element("myDrive");
		element.setAttribute("lastFileID", "" + getLastFileID());
		Document doc = new Document(element);

		element.addContent(getRootDirectory().xmlExport());

		Set<User> userSet = getUsersSet();

		for (User u : userSet) {
			element.addContent(u.xmlExport());
		}

		return doc;
	}

	public ArrayList<String> listDirectorySorted(Directory currentDir, String path) throws InvalidPathException {

		AbstractFile dir = getFileByPath(currentDir, path);
		if (!(dir instanceof Directory)) {
			throw new InvalidPathException(path);
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

		String[] parts = path.split("/", 2);
		if (path.startsWith("/")) {
			parts[0] = "/" + parts[0];
		}

		Directory d = (Directory) this.getFileByPath(currentDir, parts[0]);
		if (d == null) {
			d = new Directory(this, currentDir, this.getUserByUsername("root"),
					(parts[0].startsWith("/") ? parts[0].substring(1) : parts[0]));
		}

		if (!parts[1].contains("/")) {
			return d;
		}

		return createIntermediatePath(d, parts[1]);
	}

	public Directory createDirectoryFromPath(User owner, Directory currentDir, String path) {

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

	public void removeFileGivenPath(Directory currentDir, String path) {

		AbstractFile af = getFileByPath(currentDir, path);
		af.removeFile();
	}

	public String readTextFile(Directory currentDir, String path) throws NotTextFileException{
		AbstractFile af= getFileByPath(currentDir, path);
		if (!(af instanceof TextFile)){
			throw new NotTextFileException(af.getName());
		}
		return ((TextFile)af).getContent();
	}
}

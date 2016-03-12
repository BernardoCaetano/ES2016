package pt.tecnico.myDrive.domain;

import java.util.ArrayList;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidPathException;
import pt.tecnico.myDrive.exception.NameAlreadyExistsException;

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
		return getUserByUsername(username) != null;
	}

	public User getUserByUsername(String username) {
		for (User user : getUsersSet()) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		return null;
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

}

package pt.tecnico.myDrive.domain;

import pt.ist.fenixframework.FenixFramework;

import pt.tecnico.myDrive.exception.NameAlreadyExistsException;

public class MyDriveFS extends MyDriveFS_Base {
    
	public static MyDriveFS getInstance() {
        MyDriveFS mydrive = FenixFramework.getDomainRoot().getMyDrive();
        if (mydrive != null)
	    return mydrive;

        MyDriveFS newMyDrive = new MyDriveFS();
        new SuperUser(newMyDrive, "root", "***", "Super User", "rwxdr-x-");
        newMyDrive.setRootDirectory(RootDirectory.getInstance());
        return newMyDrive;
    }

    private MyDriveFS() {
        setRoot(FenixFramework.getDomainRoot());
    }

    @Override
    public void addUsers(User userToBeAdded) throws NameAlreadyExistsException {
        if (hasUser(userToBeAdded.getUsername())){
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
    
	public AbstractFile getFileByPath(Directory currentDir, String path) {
		if (path.equals("/")) {
			return getRootDirectory();
		} else if (path.startsWith("/")) {
			currentDir = getRootDirectory();
			path = path.substring(1);
		}

		String[] parts = path.split("/", 2);
		AbstractFile f = currentDir.getFileByName(parts[0]);

		if (parts.length < 2) {
			return f;
		}

		return getFileByPath((Directory) f, parts[1]);
	}

}

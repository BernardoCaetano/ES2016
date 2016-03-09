package pt.tecnico.myDrive.domain;

import pt.ist.fenixframework.FenixFramework;

import pt.tecnico.myDrive.exception.NameAlreadyExistsException;

public class MyDriveFS extends MyDriveFS_Base {
    
    //Singleton 
	public static MyDriveFS getInstance() {
        MyDriveFS mydrive = FenixFramework.getDomainRoot().getMyDrive();
        if (mydrive != null)
	    return mydrive;

        return new MyDriveFS();
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

}

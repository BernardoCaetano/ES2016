package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;

public class RootDirectory extends RootDirectory_Base {
    
	public static RootDirectory getInstance(MyDriveFS mydrive) {
        RootDirectory rootDirectory = mydrive.getRootDirectory();
        if (rootDirectory != null)
	    return rootDirectory;

		User root = mydrive.getUserByUsername("root");
        RootDirectory newRootDirectory = new RootDirectory(mydrive);
        newRootDirectory.setParent(newRootDirectory);
        return newRootDirectory;
    }

    private RootDirectory(MyDriveFS mydrive) {
        super();
        setMyDrive(mydrive);
        setId(mydrive);
    	setName("");
    	setLastModified(new DateTime());
    	setPermissions("rwxdr-x-");
    	setOwner(mydrive.getUserByUsername("root"));
    }
    
    @Override
    public String getPath(){
    	return "/";
    }
}



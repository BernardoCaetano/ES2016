package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;

import pt.ist.fenixframework.DomainRoot;
import pt.ist.fenixframework.FenixFramework;

public class RootDirectory extends RootDirectory_Base {
    
	public static RootDirectory getInstance() {
        RootDirectory rootDirectory = FenixFramework.getDomainRoot().getMyDrive().getRootDirectory();
        if (rootDirectory != null)
	    return rootDirectory;

		User root = FenixFramework.getDomainRoot().getMyDrive().getUserByUsername("root");
        RootDirectory newRootDirectory = new RootDirectory();
        newRootDirectory.setParent(newRootDirectory);
        return newRootDirectory;
    }

    private RootDirectory() {
        super();
        setMyDrive(FenixFramework.getDomainRoot().getMyDrive());
        setId();
    	setName("");
    	setLastModified(new DateTime());
    	setOwner(FenixFramework.getDomainRoot().getMyDrive().getUserByUsername("root"));
    }
    
    @Override
    public String getPath(){
    	return "/";
    }
}



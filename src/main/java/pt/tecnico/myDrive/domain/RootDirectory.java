package pt.tecnico.myDrive.domain;

import pt.ist.fenixframework.DomainRoot;
import pt.ist.fenixframework.FenixFramework;

public class RootDirectory extends RootDirectory_Base {
    
	//Singleton 
	public static RootDirectory getInstance() {
        RootDirectory rootDirectory = FenixFramework.getDomainRoot().getMyDrive().getRootDirectory();
        if (rootDirectory != null)
	    return rootDirectory;

		User root = FenixFramework.getDomainRoot().getMyDrive().getUserByUsername("root");
        RootDirectory newRootDirectory = new RootDirectory();
        newRootDirectory.initAbstractFile(newRootDirectory, root,"/");
        return newRootDirectory;
    }

    private RootDirectory() {
        super();
        setMyDrive(FenixFramework.getDomainRoot().getMyDrive());
    }
    
    @Override
    public String getPath(){
    	return getName();
    }
}



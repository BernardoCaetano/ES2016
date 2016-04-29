package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.InvalidOperationException;

import org.jdom2.Element;

public class RootDirectory extends RootDirectory_Base {
    
	public static RootDirectory getInstance(MyDriveFS mydrive) {
        RootDirectory rootDirectory = mydrive.getRootDirectory();
        if (rootDirectory != null)
	    return rootDirectory;

        RootDirectory newRootDirectory = new RootDirectory(mydrive);
        return newRootDirectory;
    }

    private RootDirectory(MyDriveFS mydrive) {
        super();
        setMyDrive(mydrive);
        setId(mydrive);
    	setName("");
    	setLastModified(new DateTime());
    	setPermissions("rwxdr-x-");
    }
    
    @Override
    public void setOwner(User owner) {
    	if (!(owner instanceof SuperUser)) 
    		throw new InvalidOperationException("Cannot set owner of / to any user other than root");
    	super.setOwner(owner);
    }
     
    @Override
    public Directory getParent() {
    	return this;
    }

    @Override
    public String getPath(){
    	return "/";
    }
    
    public void xmlImport(Element rootElement){
		setPermissions(rootElement.getAttribute("permissions").getValue());
		setLastModified(DateTime.parse(rootElement.getAttribute("lastModified").getValue()));
    } 
    
    @Override
    protected void cleanup() {
		for (AbstractFile f : this.getFilesSet()) f.cleanup();
	}
}

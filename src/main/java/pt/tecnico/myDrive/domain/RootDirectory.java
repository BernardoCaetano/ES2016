package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;
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
    public String xmlTag() {
		return "rootDirectory";
	}    
}

package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidPathException;

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
    public Directory createDirectoryByPath(MyDriveFS myDrive, String path) throws InvalidPathException{
				
		if (path.startsWith("/")) {
			path = path.substring(1);
		} else {
			throw new InvalidPathException(path);
		}
		
		if (path.equals("")){
			return this;
		}
		
		String dirName = path.split("/")[0];
		AbstractFile dir;
		
		try {
			dir = getFileByName(dirName);  
		} catch (FileNotFoundException e) {
			dir = new Directory(myDrive, this, null, dirName);
		}
		
		if (!(dir instanceof Directory)){
			throw new InvalidPathException(path);
		}
		
		String newPath;
		if (path.indexOf("/") == -1) {
			newPath = path.substring(path.indexOf(dirName)+ dirName.length());
		} else {
			newPath = path.substring(path.indexOf("/") + 1);
		}
		
		return ((Directory) dir).createDirectoryByPath(myDrive, newPath);
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
}

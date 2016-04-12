package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;

import org.jdom2.Element;

import java.util.ArrayList;

import pt.tecnico.myDrive.exception.AccessDeniedException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.NameAlreadyExistsException;
import pt.tecnico.myDrive.exception.PathMaximumLengthException;;


public abstract class AbstractFile extends AbstractFile_Base implements Comparable<AbstractFile> {
    
   public void initAbstractFile(MyDriveFS mydrive, Directory parentDir, User owner, String name) 
    		throws InvalidFileNameException, NameAlreadyExistsException, PathMaximumLengthException {
	   
	    setName(name);
    	setId(mydrive);
    	setLastModified(new DateTime());
    	setParent(parentDir);
   		setOwner(mydrive, owner);
    	setPermissions(getOwner().getUmask());
    	checkPathLength();
    }

    @Override
    public void setName(String name) 
    		throws InvalidFileNameException {
    	if (name.equals(".") || name.equals("..")) {
    		throw new InvalidFileNameException(name);
    	}else if (name.contains("/") || name.contains("\0")) {
    		throw new InvalidFileNameException(name);
    	} else {
    		super.setName(name);
    	}
    }
    
    @Override
    public void setParent(Directory parentDir) 
    		throws NameAlreadyExistsException {
        if (parentDir == null)
            super.setParent(null); //FIXME better to throw exception? 
        else
            parentDir.addFiles(this);
    }

    public void setOwner(MyDriveFS mydrive, User owner) {
        if (owner == null)
            mydrive.getUserByUsername("root").addFiles(this);
        else
            owner.addFiles(this);
    }

    public void setId(MyDriveFS mydrive) {
    	mydrive.incrementLastFileID();
    	super.setId(mydrive.getLastFileID());
    }
    
    public String getPath() {
    	return getParent().getPath() + getName();
    }
    
    public void checkPathLength(){
    	if ((getPath().length() > 1024)){
    		throw new PathMaximumLengthException();
    	}
    }
	
	public String getNameOfFileFromPath(String path) {
		String[] parts = path.split("/");
		
		if (parts.length > 0)
			return parts[parts.length-1];
		else
			return "";
	}
	
	public Directory getParentFromPath(String path) {
		if (path.equals("/"))
			return RootDirectory.getInstance(MyDriveFS.getInstance());
		else {
			String[] parts = path.split("/");
			String name = parts[parts.length-1];
			
			int lengthWithoutName = path.length() - name.length() - 1;
			String pathWithoutName = path.substring(0, lengthWithoutName);
			
			return MyDriveFS.getInstance().getDirectoryByPath(RootDirectory.getInstance(MyDriveFS.getInstance()), pathWithoutName);
		}
	}

    public abstract Element xmlExport();
    
    public void xmlImport(MyDriveFS myDrive, Element element) {
		Directory parentDirectory = getParentFromPath(element.getAttribute("path").getValue());
		String nameOfFile = getNameOfFileFromPath(element.getAttribute("path").getValue());
		
		if( !parentDirectory.hasFile(nameOfFile) ){
			setName(nameOfFile);
			setParent(parentDirectory);
			setId(myDrive);
		}
		
		setPermissions(element.getAttribute("permissions").getValue());
		setLastModified(DateTime.parse(element.getAttribute("lastModified").getValue()));
	}
    
    public Element xmlAddFile() {
		Element element = new Element(xmlTag());
		element.setAttribute("path", getPath());
		element.setAttribute("permissions", getPermissions());
		element.setAttribute("lastModified", getLastModified().toString());

		return element;
    }
    
    public abstract String xmlTag();

    public ArrayList<AbstractFile> getFilesRecursive() {
    	ArrayList<AbstractFile> children = new ArrayList<AbstractFile>();
        children.add(this);

        return children;
    }    

    public void remove(User user) throws AccessDeniedException {
    	if(user.canWrite(getParent()) || user.canDelete(this) ){
    		setParent(null);
            setOwner(null);
            deleteDomainObject();
		} else {
			throw new AccessDeniedException(user.getUsername(), this.getName());
		}
    };
    
    public int compareTo(AbstractFile f) {
    	String thisPath = this.getPath().replaceAll("/", "");
    	String otherPath = f.getPath().replaceAll("/", "");
    	
    	return thisPath.compareToIgnoreCase(otherPath);
    }
    
    public boolean isHomeDirectory() {
    	return false;
    }
    
    public abstract int dimension();
}

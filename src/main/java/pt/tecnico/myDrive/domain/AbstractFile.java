package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;

import org.jdom2.Element;

import java.util.Set;
import java.util.ArrayList;

import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.UserNotFoundException;

public abstract class AbstractFile extends AbstractFile_Base implements Comparable<AbstractFile> {
    
    public AbstractFile() {
        super();
    }

    public AbstractFile(MyDriveFS mydrive, Directory parentDir, User owner, String name) {
    	super();
    	initAbstractFile(mydrive, parentDir, owner, name);
    }

    public void initAbstractFile(MyDriveFS mydrive, Directory parentDir, User owner, String name) {

    	setId(mydrive);
    	setName(name);
    	setLastModified(new DateTime());
        setPermissions("rwxdr-x-");
    	setParent(parentDir);
   		setOwner(mydrive, owner);
    	getOwner().addFiles(this);
    }

    @Override
    public void setParent(Directory parentDir) {
        if (parentDir == null)
            super.setParent(null);
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

    public abstract Element xmlExport();
    
    public void xmlImportFile(Element element, Directory par) throws ImportDocumentException{
		try {
            setName(element.getAttribute("name").getValue());
            setPermissions(element.getAttribute("permissions").getValue());
            setLastModified(DateTime.parse(element.getAttribute("lastModified").getValue()));
			setParent(par);
						
		}
		catch (ImportDocumentException e) {
            throw new ImportDocumentException();
		} 		
		
	}
    
    public Element xmlAddFile() {
		Element element = new Element(xmlTag());
		element.setAttribute("name", getName());
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

    public void removeFile(){};
    
    public int compareTo(AbstractFile f) {
    	String thisPath = this.getPath().replaceAll("/", "");
    	String otherPath = f.getPath().replaceAll("/", "");
    	
    	return thisPath.compareToIgnoreCase(otherPath);
    }
    
    
}

package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;

import org.jdom2.Element;

import java.util.ArrayList;


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
        setPermissions(owner.getUmask());
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

    public void removeFile(){};
    
    public int compareTo(AbstractFile f) {
    	String thisPath = this.getPath().replaceAll("/", "");
    	String otherPath = f.getPath().replaceAll("/", "");
    	
    	return thisPath.compareToIgnoreCase(otherPath);
    }
}

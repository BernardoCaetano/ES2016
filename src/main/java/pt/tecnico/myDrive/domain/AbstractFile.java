package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;

import org.jdom2.Element;

public abstract class AbstractFile extends AbstractFile_Base {
    
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
    
    public Element xmlAddFile() {
		Element element = new Element(xmlTag());
		element.setAttribute("id", ""+getId());
		element.setAttribute("name", getName());
		element.setAttribute("permissions", "Permissions not initialized. Alert Bernardo!");
		element.setAttribute("lastModified", getLastModified().toString());
		element.setAttribute("owner", getOwner().getName());

		return element;
    }
    
    public abstract String xmlTag();

    public void removeFile(){};
}

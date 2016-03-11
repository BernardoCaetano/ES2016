package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;

import pt.ist.fenixframework.DomainRoot;
import pt.ist.fenixframework.FenixFramework;

public class AbstractFile extends AbstractFile_Base {
    
    public AbstractFile() {
        super();
    }

    public AbstractFile(Directory parentDir, User owner, String name) {
    	super();
    	initAbstractFile(parentDir, owner, name);
    }

    public void initAbstractFile(Directory parentDir, User owner, String name) {

    	setId();
    	setName(name);
    	setLastModified(new DateTime());
    	setParent(parentDir);
   		setOwner(owner);
    	getOwner().addFiles(this);
    }

    @Override
    public void setParent(Directory parentDir) {
        if (parentDir == null)
            super.setParent(null);
        else
            parentDir.addFiles(this);
    }

    @Override
    public void setOwner(User owner) {
        if (owner == null)
            FenixFramework.getDomainRoot().getMyDrive().getUserByUsername("root").addFiles(this);
        else
            owner.addFiles(this);
    }

    public void setId() {
    	MyDriveFS mydrive = FenixFramework.getDomainRoot().getMyDrive();
    	mydrive.incrementLastFileID();
    	super.setId(mydrive.getLastFileID());
    }
    
    public String getPath() {
    	return getParent().getPath() + getName();
    }
}

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
    	//setPermissions("rwxd----");
    	if(owner == null){
    		setOwner(FenixFramework.getDomainRoot().getMyDrive().getUserByUsername("root"));
    	}
    	else{
    		setOwner(owner);
    	}
    	getOwner().addFiles(this);
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

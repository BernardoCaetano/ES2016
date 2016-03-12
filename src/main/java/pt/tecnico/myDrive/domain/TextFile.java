package pt.tecnico.myDrive.domain;

public class TextFile extends TextFile_Base {
    
    public TextFile() {
        super();
    }

    public TextFile(MyDriveFS mydrive, Directory parentDir, User owner, String name, String content) {
    	super();
    	initTextFile(mydrive, parentDir, owner, name, content);
    }

    public void initTextFile(MyDriveFS mydrive, Directory parentDir, User owner, String name, String content) {
    	initAbstractFile(mydrive, parentDir, owner, name);
    	setContent(content);
    }

    @Override
    public void removeFile(){
        setParent(null);
        setOwner(null);
        deleteDomainObject();
    }
    
}

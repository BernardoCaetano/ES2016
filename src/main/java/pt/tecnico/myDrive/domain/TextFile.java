package pt.tecnico.myDrive.domain;

public class TextFile extends TextFile_Base {
    
    public TextFile() {
        super();
    }

    @Override
    public void removeFile(){
        setParent(null);
        setOwner(null);
        deleteDomainObject();
    }
    
}

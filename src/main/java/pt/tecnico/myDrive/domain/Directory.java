package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.NameAlreadyExistsException;

public class Directory extends Directory_Base {
    
    public Directory() {
        super();
    }

    public Directory(MyDriveFS mydrive, Directory parentDir, User owner, String name) {
        super();
        initAbstractFile(mydrive, parentDir, owner, name);
    }
    
    @Override
    public void addFiles(AbstractFile file) throws NameAlreadyExistsException{
        if (hasFile(file.getName())){
            throw new NameAlreadyExistsException(file.getName());
        }
        super.addFiles(file);
    }

    public boolean hasFile(String filename) {
        return getFileByName(filename) != null;
    }

    public AbstractFile getFileByName(String name) throws FileNotFoundException {
        if( name.equals(".") ){
            return this;
        }else if( name.equals("..") ){
            return this.getParent();
        }
        for( AbstractFile f : getFilesSet() ) {
            if( f.getName().equals(name) ){
                return f;
            }
        }
        throw new FileNotFoundException(getPath() + "/" + name);
    }

    @Override
    public String getPath(){
    	return super.getPath() + "/";
    }

    
    public String readTextFile(String path) {

        MyDriveFS mydrive= this.getOwner().getMyDrive();
        TextFile tf = (TextFile) mydrive.getFileByPath(this, path);     
        return tf.getContent();
    }

    public void deleteFile(String path) {

        MyDriveFS mydrive= this.getOwner().getMyDrive();
        AbstractFile f = mydrive.getFileByPath(null , path);
        f.removeFile();
    }


    @Override
    public void removeFile(){
        if (getFilesCount() == 0){
            setOwner(null);
            deleteDomainObject();
        }
    }
}

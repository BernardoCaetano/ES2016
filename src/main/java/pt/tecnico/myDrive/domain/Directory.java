package pt.tecnico.myDrive.domain;

public class Directory extends Directory_Base {
    
    public Directory() {
        super();
    }
    
    
    public AbstractFile getChild(String name){
    	if( name.equals(".") ){
    		return this;
    	}else if( name.equals("..") ){
    		return this.getParent();
    	}
    	for( AbstractFile f : getFilesSet() ){
    		if( f.getName().equals(name) ){
    			return f;
    		}
    	}
    	return null;
    }
    
}

package pt.tecnico.myDrive.domain;

public class RootDirectory extends RootDirectory_Base {
    
    public RootDirectory() {
        super();
    }
    
    @Override
    public String getPath(){
    	return "/";
    }
}

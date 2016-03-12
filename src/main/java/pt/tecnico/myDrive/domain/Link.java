package pt.tecnico.myDrive.domain;

public class Link extends Link_Base {
    
    public Link() {
        super();
    }

    public Link(MyDriveFS mydrive, Directory parentDir, User owner, String name, String content) {
    	super();
    	initTextFile(mydrive, parentDir, owner, name, content);
    }
    
    public String xmlTag() {
   	 return "app";
    }
    
}

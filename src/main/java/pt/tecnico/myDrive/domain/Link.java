package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

public class Link extends Link_Base {
    
    public Link() {
        super();
    }

    public Link(MyDriveFS mydrive, Directory parentDir, User owner, String name, String content) {
    	super();
    	initTextFile(mydrive, parentDir, owner, name, content);
    }
    
    public Link(MyDriveFS myDrive, Element linkElement){
		super();
		xmlImport(myDrive, linkElement);
	}
	
    
    public String xmlTag() {
   	 return "link";
    }
    
}

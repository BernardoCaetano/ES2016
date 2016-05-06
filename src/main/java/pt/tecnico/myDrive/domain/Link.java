package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.ImmutableLinkContentException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.InvalidLinkContentException;

public class Link extends Link_Base {
    
    public Link() {
        super();
    }

	public Link(MyDriveFS mydrive, Directory parentDir, User owner, String name, String content) 
    		throws InvalidFileNameException {
    	super();
    	
    	if(!mydrive.isValidPath(content)){
    		throw new InvalidLinkContentException();
    	}
    	
    	initTextFile(mydrive, parentDir, owner, name, content);
    }
    
    @Override
    public void setContent(String content) {
    	throw new ImmutableLinkContentException();
    }
    
    public Link(MyDriveFS myDrive, Element linkElement){
		super();
		xmlImport(myDrive, linkElement);
	}
	
    @Override
    public String xmlTag() {
   	 return "link";
    }
    
}

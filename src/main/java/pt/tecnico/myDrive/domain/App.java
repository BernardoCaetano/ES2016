package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.InvalidFileNameException;

public class App extends App_Base {
    
    public App() {
        super();
    }
    
     public App(MyDriveFS mydrive, Directory parentDir, User owner, String name, String content) 
    		 throws InvalidFileNameException {
    	super();
    	initTextFile(mydrive, parentDir, owner, name, content);
    }

	public App(MyDriveFS myDrive, Element appElement){
		super();
		xmlImport(myDrive, appElement);
	}
	
    public boolean hasValidContent() {
    	String packageParts[] = getContent().split("\\.");
    	
    	if (packageParts.length == 0)
    		return false;
    	
    	for (String part : packageParts){
    		char firstChar = part.charAt(0); 
    		
    		if (!Character.isJavaIdentifierStart(firstChar))
    			return false;
    		
    		for (int i=0; i<part.length(); i++) {
    			char nextChar = part.charAt(i);
    			
    			if (!Character.isJavaIdentifierPart(nextChar))
    				return false;
    		}
    	}
    	
    	return true;
    }

    @Override
     public String xmlTag() {
    	 return "app";
     }
     
}

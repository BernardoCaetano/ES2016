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

    @Override
     public String xmlTag() {
    	 return "app";
     }
     
}

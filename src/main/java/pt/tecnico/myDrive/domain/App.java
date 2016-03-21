package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

public class App extends App_Base {
    
    public App() {
        super();
    }
    
     public App(MyDriveFS mydrive, Directory parentDir, User owner, String name, String content) {
    	super();
    	initTextFile(mydrive, parentDir, owner, name, content);
    }

	public App(MyDriveFS myDrive, Element appElement){
		super();
		xmlImport(myDrive, appElement);
	}
	
     public String xmlTag() {
    	 return "app";
     }
     
}

package pt.tecnico.myDrive.domain;

public class App extends App_Base {
    
    public App() {
        super();
    }
    
     public App(MyDriveFS mydrive, Directory parentDir, User owner, String name, String content) {
    	super();
    	initTextFile(mydrive, parentDir, owner, name, content);
    }

     public String xmlTag() {
    	 return "app";
     }
     
}

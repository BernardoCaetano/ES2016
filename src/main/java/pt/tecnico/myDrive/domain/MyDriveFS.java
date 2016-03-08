package pt.tecnico.myDrive.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.ist.fenixframework.FenixFramework;

public class MyDriveFS extends MyDriveFS_Base {
    
	public static MyDriveFS getInstance() {
        MyDriveFS fs = FenixFramework.getDomainRoot().getMyDriveFS();
        if (fs != null)
	    return fs;

		log.trace("new MyDriveFS");
        return new MyDriveFS();
    }

    public MyDriveFS() {
        setRoot(FenixFramework.getDomainRoot());
    }
    
}

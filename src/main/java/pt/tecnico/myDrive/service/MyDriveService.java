package pt.tecnico.myDrive.service;

import pt.ist.fenixframework.Atomic;
import pt.tecnico.myDrive.domain.MyDriveFS;
import pt.tecnico.myDrive.exception.MyDriveException;

public abstract class MyDriveService {

    @Atomic
    public final void execute() throws MyDriveException {
        dispatch();
    }

    static MyDriveFS getMyDrive() {
        return MyDriveFS.getInstance();
    }

    protected abstract void dispatch() throws MyDriveException;
}

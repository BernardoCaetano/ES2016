package pt.tecnico.myDrive;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainRoot;
import pt.ist.fenixframework.FenixFramework;

import pt.tecnico.myDrive.domain.*;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.tecnico.myDrive.exception.*;

public class Main {
    static final Logger log = LogManager.getRootLogger();

    public static void main(String [] args) {
        try {
            setup();
        } finally {
            // ensure an orderly shutdown
            FenixFramework.shutdown();
        }
    }

    @Atomic
    public static void setup() { // phonebook with debug data
        try {
        log.trace("Setup: " + FenixFramework.getDomainRoot());
        MyDriveFS mydrive = MyDriveFS.getInstance();
        User root = mydrive.getUserByUsername("root");
        new Directory(mydrive, RootDirectory.getInstance(mydrive), null, "oi");
        new User(mydrive, "Bernardo", null, null, null);
        new App(mydrive, RootDirectory.getInstance(mydrive), null, "AppExample", "This is my content");
        } catch(MyDriveException e){
            System.out.println(e.getMessage());
        }
    }
}

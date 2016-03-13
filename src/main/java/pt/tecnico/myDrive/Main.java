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

import java.io.PrintStream;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Main {
    static final Logger log = LogManager.getRootLogger();

    public static void main(String [] args) {
        try {
            setup();
        } finally {
            
            FenixFramework.shutdown();
        }
    }

    @Atomic
    public static void setup() { 
        try {
        log.trace("Setup: " + FenixFramework.getDomainRoot());
        MyDriveFS mydrive = MyDriveFS.getInstance();
        User root = mydrive.getUserByUsername("root");
        new Directory(mydrive, RootDirectory.getInstance(mydrive), null, "oi");
        new User(mydrive, "Bernardo", null, null, null);
        new App(mydrive, RootDirectory.getInstance(mydrive), null, "AppExample", "This is my content");
        xmlPrint();
        } catch(MyDriveException e){
            System.out.println(e.getMessage());
        }
    }

    @Atomic
    public static void xmlPrint() {
        log.trace("xmlPrint: " + FenixFramework.getDomainRoot());
        Document doc = MyDriveFS.getInstance().xmlExport();
        XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
        try { xmlOutput.output(doc, new PrintStream(System.out));
        } catch (IOException e) { System.out.println(e); }
    }
}

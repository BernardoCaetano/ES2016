package pt.tecnico.myDrive;

import java.io.IOException;
import java.io.PrintStream;
import java.io.File;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import pt.tecnico.myDrive.domain.*;
import pt.tecnico.myDrive.exception.*;

import java.util.Collection;

public class Main {

	public static void main(String[] args) throws MyDriveException {
		try {
			for (String s : args)
				xmlScan(new File(s));
			xmlPrint();
			
		} finally {
			FenixFramework.shutdown();
		}
	}
	
	@Atomic
    public static void cleanup() {
		MyDriveFS.getInstance().cleanup();
    }

    @Atomic
    public static void xmlPrint() {
        Document doc = MyDriveFS.getInstance().xmlExport();
        XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
        try { xmlOutput.output(doc, new PrintStream(System.out));
        } catch (IOException e) { System.out.println(e); }
    }
    
    public static void printCollection(Collection<String> stringCollection) {
        for(String s : stringCollection){
            System.out.println(s);
        }
    }

    @Atomic
    public static void xmlScan(File file) {
		MyDriveFS md = MyDriveFS.getInstance();		
		SAXBuilder builder = new SAXBuilder();
		try {
			Document document = (Document)builder.build(file);	
			md.xmlImport(document.getRootElement());
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
			
		}
	}
}

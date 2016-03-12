package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

public class TextFile extends TextFile_Base {
    
    public TextFile() {
        super();
    }

    public TextFile(MyDriveFS mydrive, Directory parentDir, User owner, String name, String content) {
    	super();
    	initTextFile(mydrive, parentDir, owner, name, content);
    }

    public void initTextFile(MyDriveFS mydrive, Directory parentDir, User owner, String name, String content) {
    	initAbstractFile(mydrive, parentDir, owner, name);
    	setContent(content);
    }

    @Override
    public void removeFile(){
        setParent(null);
        setOwner(null);
        deleteDomainObject();
    }
    
    public Element xmlExport() {
		Element element = new Element("textFile");
		element.setAttribute("id", ""+getId());
		element.setAttribute("name", getName());
		element.setAttribute("permissions", "Permissions not initialized. Alert Bernardo!");
		element.setAttribute("lastModified", getLastModified().toString());
		element.setAttribute("owner", getOwner().getName());
		element.setAttribute("content", getContent());

		return element;
    }    
}

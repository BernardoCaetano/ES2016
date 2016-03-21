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

	public TextFile(MyDriveFS myDrive, Element textFileElement){
		super();
		xmlImport(myDrive, textFileElement);
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
    
    @Override
    public void xmlImport(MyDriveFS myDrive, Element element){
		super.xmlImport(myDrive, element);
		
		setContent(element.getAttribute("content").getValue());
	}
    
    public Element xmlExport() {
		Element element = xmlAddFile();
		element.setAttribute("content", getContent());

		return element;
    }
    
    public String xmlTag() {
    	return "textFile";
    }
}

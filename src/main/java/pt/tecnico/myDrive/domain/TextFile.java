package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.ImportDocumentException;

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
    
    public void xmlImport(Element element, Directory par) throws ImportDocumentException {
		super.xmlImportFile(element, par);
		try{
			setContent(element.getAttribute("content").getValue());
		}
		catch(Exception e){
			throw new ImportDocumentException();
		}
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

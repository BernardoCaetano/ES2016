package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.InvalidFileNameException;

public class TextFile extends TextFile_Base {
    
    public TextFile() {
        super();
    }

    public TextFile(MyDriveFS mydrive, Directory parentDir, User owner, String name, String content) 
    		throws InvalidFileNameException {
    	super();
    	initTextFile(mydrive, parentDir, owner, name, content);
    }

	public TextFile(MyDriveFS myDrive, Element textFileElement){
		super();
		xmlImport(myDrive, textFileElement);
	}

    public void initTextFile(MyDriveFS mydrive, Directory parentDir, User owner, String name, String content) 
    		throws InvalidFileNameException {
    	initAbstractFile(mydrive, parentDir, owner, name);
    	super.setContent(content);
    }
 
    @Override
    public void xmlImport(MyDriveFS myDrive, Element element){
		super.xmlImport(myDrive, element);
		
		super.setContent(element.getAttribute("content").getValue());
	}

    @Override
    public Element xmlExport() {
		Element element = xmlAddFile();
		element.setAttribute("content", getContent());

		return element;
    }
    
    @Override
    public String xmlTag() {
    	return "textFile";
    }
    
    @Override
    public int dimension(){
    	int result;
    	
    	if(this.getContent() != null){
			result = this.getContent().length();
    	} 
    	else {
    		result = 0;
    	}
    	return result;
    }
}

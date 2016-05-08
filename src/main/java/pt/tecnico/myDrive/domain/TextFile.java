package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.AccessDeniedException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;

public class TextFile extends TextFile_Base {
    
    public TextFile() {
        super();
    }

    public TextFile(MyDriveFS mydrive, Directory parentDir, User owner, String name, String content) 
    		throws InvalidFileNameException{
    	super();
    	initTextFile(mydrive, parentDir, owner, name, content);
    }

    public TextFile(MyDriveFS mydrive, Directory parentDir, User owner, String name) 
    		throws InvalidFileNameException {
    	super();
		initTextFile(mydrive, parentDir, owner, name, "");
    }

    public TextFile(MyDriveFS myDrive, Element textFileElement){
		super();
		xmlImport(myDrive, textFileElement);
	}

    protected void initTextFile(MyDriveFS mydrive, Directory parentDir, User owner, String name, String content) 
    		throws InvalidFileNameException {
    	initAbstractFile(mydrive, parentDir, owner, name);
    	super.setContent(content);
    }
 
    @Override
    public void xmlImport(MyDriveFS myDrive, Element element){
		super.xmlImport(myDrive, element);
		String content = element.getChildText("content");
    	super.setContent(content != null ? content : "");
		
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
    
    
	public void setContent(String content, User user) throws AccessDeniedException {
		if (user.canWrite(this) &&  user.canWrite(this.getParent())){
			setContent(content);
		}else{
			throw new AccessDeniedException(user.getUsername(), getName());
		}
		
	}
	
	public String getContent(User user) {
		if (user.canRead(this) && user.canExecute(this.getParent())) {
			return getContent();
		} else {
			throw new AccessDeniedException(user.getUsername(), getName());
		}
	}

}

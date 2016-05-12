package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.ExecuteFileException;
import pt.tecnico.myDrive.exception.ImmutableLinkContentException;
import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.InvalidLinkContentException;
import pt.tecnico.myDrive.exception.NotTextFileException;
import pt.tecnico.myDrive.service.dto.AbstractFileDTO;
import pt.tecnico.myDrive.service.dto.LinkDTO;

public class Link extends Link_Base {
    
    public Link() {
        super();
    }

	public Link(MyDriveFS mydrive, Directory parentDir, User owner, String name, String content) 
    		throws InvalidFileNameException {
    	super();
    	
    	if(!isValidPath(content)){
    		throw new InvalidLinkContentException();
    	}
    	
    	initTextFile(mydrive, parentDir, owner, name, content);
    }
    
    @Override
    public void setContent(String content) {
    	throw new ImmutableLinkContentException();
    }
    
    @Override
    public void execute(User u, String[] args) {
    	try {
    		TextFile t = u.getMyDrive().getTextFileByPath(getParent(), super.getContent());
    		t.execute(u, args);
    	} catch (NotTextFileException e) {
    		throw new ExecuteFileException("The link does not reference neither a TextFile nor an App");
    	}
    }
    
    public Link(MyDriveFS myDrive, Element linkElement){
		super();
		String content = linkElement.getChildText("content");
		content = (content != null ? content : "");
		if (!isValidPath(content))
			throw new ImportDocumentException("Invalid content '" + content + "' for App file");
		
		xmlImport(myDrive, linkElement);
	}
    
    protected static boolean isValidPath(String content) {
    	return MyDriveFS.isValidPath(content);
    }
	
    @Override
    public String xmlTag() {
   	 return "link";
    }
    
    @Override
	public AbstractFileDTO convertToDTO() {
		return new LinkDTO(this);
	}
    
}

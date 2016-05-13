package pt.tecnico.myDrive.domain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.AccessDeniedException;
import pt.tecnico.myDrive.exception.ExecuteFileException;
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
		
		Element contentElement = new Element("content");
		contentElement.addContent(getContent());
        element.addContent(contentElement);

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
    
	public void execute(User u, String[] args) {
		if (!u.canExecute(this))
			throw new AccessDeniedException(u.getUsername(), getName());

		String[] lines = getContent().split("\\n");
		for (String line : lines) {
			String appPath = line.split(" ")[0];
			String[] params = line.substring(line.indexOf(" ") + 1).split(" ");
			try {
				App app = (App) u.getMyDrive().getFileByPath(getParent(), appPath);
				app.execute(u, params);
			} catch (ClassCastException e) {
				throw new ExecuteFileException("Wrong type of file found. The path on each line of "
						+ "a text file must refer to an App or a Link to an App");
			}
		}
	}
    
	static void executeReflection(String name, String[] args) {
		
		Class<?> c;
		Method meth;
		try {
			try { // name is a class: call main()
				c = Class.forName(name);
				meth = c.getMethod("main", String[].class);
			} catch (ClassNotFoundException cnfe) { // name is a method
				int pos;
				if ((pos = name.lastIndexOf('.')) < 0) 
					throw new ExecuteFileException("Class '" + name + "' not found");
				c = Class.forName(name.substring(0, pos));
				meth = c.getMethod(name.substring(pos + 1), String[].class);
			} 
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			throw new ExecuteFileException("Method '" + name + "' not found");
		}
		try {
			meth.invoke(null, (Object) args); // static method (ignore return)
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new ExecuteFileException("Failed to execute method '" + name + "'");
		} 
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

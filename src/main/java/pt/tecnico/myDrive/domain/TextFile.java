package pt.tecnico.myDrive.domain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
    
	public void execute(User u, Object[] args) {
		if (!u.canExecute(this))
			throw new AccessDeniedException(u.getUsername(), getName());

		String[] lines = getContent().split("\\n");
		for (String line : lines) {
			String appPath = line.split(" ")[0];
			Object[] params = line.substring(line.indexOf(" ") + 1).split(" ");
			try {
				App app = (App) u.getMyDrive().getFileByPath(getParent(), appPath);
				app.execute(u, params);
			} catch (ClassCastException e) {
				throw new RuntimeException("Wrong type of file found. The path on each line of "
						+ "a text file must refer to an App or a Link to an App"); // FIXME
			}
		}
	}
    
	static void executeReflection(String fqnMethod, Object[] args) {
		String className = fqnMethod.substring(0, fqnMethod.lastIndexOf("."));
		String methodName = fqnMethod.substring(fqnMethod.lastIndexOf(".") + 1);
		Class<?> c = null;

		boolean loaded;
		try {
			c = Class.forName(className);
			loaded = true;
		} catch (ClassNotFoundException e) {
			loaded = false;
		}
		if (!loaded) {
			try {
				c = Class.forName(fqnMethod);
				loaded = true;
				className = fqnMethod;
				methodName = "main";
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Failed to load class (either " + className + " or " + fqnMethod + ")"); // FIXME
			}
		}

		try {
			Method method = c.getMethod(methodName, new Class[] { String[].class });
			method.invoke(null, new Object[] { args });
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Method not found (either " + fqnMethod + " or " + className + ".main)"); // FIXME
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException("Failed to execute method '" + methodName + "'"); // FIXME
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

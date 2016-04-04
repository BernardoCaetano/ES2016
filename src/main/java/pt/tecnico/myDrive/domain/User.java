package pt.tecnico.myDrive.domain;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.InvalidUsernameException;


public class User extends User_Base {
    
    public User() {
        super();
    }

	public User(MyDriveFS mydrive, String username, String password, String name, String umask)
			throws InvalidUsernameException {
		super();
		init(mydrive, username, (password == null) ? username : password, (name == null) ? username : name,
				(umask == null) ? "rwxd----" : umask);
	}

	public User(MyDriveFS myDrive, Element userElement){
		super();	
		xmlImport(myDrive, userElement);
	}

    public void init(MyDriveFS mydrive, String username, String password, String name, String umask) 
            throws InvalidUsernameException {
    	
        setUsername(username);
        setPassword(password);
        setName(name);
        setUmask(umask);
        setMyDrive(mydrive);
        setHomeDirectory(mydrive);
    }

    @Override
    public void setMyDrive(MyDriveFS mydrive) {
        if (mydrive == null) {
            super.setMyDrive(null);
        } else {
            mydrive.addUsers(this);
        }
    }

    @Override
	public void setUsername(String username) throws InvalidUsernameException {
		if (username.equals("") || !StringUtils.isAlphanumeric(username) || (username == null) || (username.length() < 3)) {
			throw new InvalidUsernameException(username);
		}
		super.setUsername(username);
	}

    public void setHomeDirectory(MyDriveFS mydrive) {
        Directory rootDir = (Directory) mydrive.getRootDirectory();
        Directory home;

        if (!rootDir.hasFile("home")) {
            home = new Directory(mydrive, rootDir, null, "home");
        } else {
            home = (Directory) rootDir.getFileByName("home");
        }
        Directory userHomeDir;
        if (!home.hasFile(this.getUsername())) {
            userHomeDir = new Directory(mydrive, home, this, this.getUsername());
            setHomeDirectory(userHomeDir);
        } else {
            userHomeDir = (Directory) home.getFileByName(this.getUsername());
            setHomeDirectory(userHomeDir);
        }
    }
    
    @Override
    public String getPassword() {
    	return null; //FIXME: define behaviour (throw exception/ log.warn() i don't know)
    }
    
    public boolean checkPassword(String password) {
    	return password.equals(super.getPassword());
    }

	public void xmlImport(MyDriveFS myDrive, Element userElement) {
		if(!myDrive.hasUser(userElement.getAttribute("username").getValue())){
			
			setUsername(userElement.getAttribute("username").getValue());
			setMyDrive(myDrive);
		}
		
		setPassword(userElement.getAttribute("password").getValue());
		setName(userElement.getAttribute("name").getValue());
		setUmask(userElement.getAttribute("umask").getValue());
		
		Directory currentDirectory = myDrive.getRootDirectory();
		String homeDirectoryPath = userElement.getAttribute("homeDirectory").getValue();
		
		Directory homeDirectory = myDrive.getDirectoryByPath(
			currentDirectory, homeDirectoryPath);
		
		setHomeDirectory(homeDirectory);

		for (Element fileElement : userElement.getChildren("file")){ 
			String path = fileElement.getAttribute("path").getValue();
			Directory currentDir = myDrive.getRootDirectory();
			myDrive.getFileByPath(currentDir, path).setOwner(MyDriveFS.getInstance(), this);
		} 
	}



    public Element xmlExport() {
        Element element = new Element("user");
        
        element.setAttribute("username", getUsername());
        element.setAttribute("password", super.getPassword());
        element.setAttribute("name", getName());
        element.setAttribute("umask", getUmask());
        element.setAttribute("homeDirectory", getHomeDirectory().getPath());
        
		ArrayList<AbstractFile> ownedFiles = new ArrayList<AbstractFile>();
		ownedFiles.addAll(getFilesSet());
		Collections.sort(ownedFiles);

        for (AbstractFile f: ownedFiles) {
            Element fileElement = new Element("file");
            fileElement.setAttribute("path", f.getPath());
            element.addContent(fileElement);
        }

        return element;
    }
}

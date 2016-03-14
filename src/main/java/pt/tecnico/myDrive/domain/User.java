package pt.tecnico.myDrive.domain;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;

import java.util.Set;
import java.util.List;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.InvalidUsernameException;
import pt.tecnico.myDrive.exception.ImportDocumentException;


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

    public void init(MyDriveFS mydrive, String username, String password, String name, String umask) 
            throws InvalidUsernameException {
        if(username == null) {
            throw new InvalidUsernameException(null);
        }
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
    public void setUsername(String username) throws InvalidUsernameException{
        if(username.equals("") || !StringUtils.isAlphanumeric(username)) {
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

	public void xmlImport(Element userElement) throws ImportDocumentException {
		try {
            setUsername(userElement.getAttribute("username").getValue());
            setPassword(userElement.getAttribute("password").getValue());
            setName(userElement.getAttribute("name").getValue());
            setUmask(userElement.getAttribute("umask").getValue());
            Directory homeDirectory = getMyDrive().createDirectoryFromPath(this, getMyDrive().getRootDirectory().getInstance(getMyDrive()),userElement.getAttribute("homeDirectory").getValue());
			setHomeDirectory(homeDirectory);
		}
		catch (Exception e) {
            throw new ImportDocumentException();
		}
							
		List<Element> filesList = userElement.getChildren("file");

		for (Element fileElement : filesList){ 
			String path =  fileElement.getAttribute("path").getValue();
			Directory currentDir = getMyDrive().getRootDirectory().getInstance(getMyDrive());
			
			getMyDrive().getFileByPath( currentDir, path).setOwner(this);
		} 
	}



    public Element xmlExport() {
        Element element = new Element("user");
        
        element.setAttribute("username", getUsername());
        element.setAttribute("password", getPassword());
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

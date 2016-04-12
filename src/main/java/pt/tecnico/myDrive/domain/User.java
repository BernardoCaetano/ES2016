package pt.tecnico.myDrive.domain;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.InvalidPathException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;


public class User extends User_Base {
    
    public User() {
        super();
    }

	public User(MyDriveFS mydrive, String username, String password, String name, String umask, String homeDirPath)
			throws InvalidUsernameException, InvalidPathException {
		super();
		init(mydrive, username, (password == null) ? username : password, (name == null) ? username : name,
				(umask == null) ? "rwxd----" : umask, (homeDirPath == null) ? ("/home/" + username) : homeDirPath);
	}

	public User(MyDriveFS myDrive, Element userElement){
		super();	
		xmlImport(myDrive, userElement);
	}

    public void init(MyDriveFS mydrive, String username, String password, String name, String umask, String homeDirPath) 
            throws InvalidUsernameException, InvalidPathException {
    	if (homeDirPath.contains("\0") || !homeDirPath.startsWith("/")){
    		throw new InvalidPathException(homeDirPath);
    	}
        setUsername(username);
        setPassword(password);
        setName(name);
        setUmask(umask);
        setMyDrive(mydrive);
        setHomeDirectory(mydrive, homeDirPath);
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
		if (username.equals("") || !StringUtils.isAlphanumeric(username) || (username == null)
				|| (username.length() < 3)) {
			throw new InvalidUsernameException(username);
		}
		super.setUsername(username);
	}

	public void setHomeDirectory(MyDriveFS myDrive, String homeDirPath) throws InvalidPathException {
		try {
			homeDirPath = homeDirPath.substring(1);
			Directory rootDir = (Directory) myDrive.getRootDirectory();
			String homeDirName = homeDirPath.substring(homeDirPath.lastIndexOf("/") + 1);
			Directory parentDir = rootDir.createDirectoryByPath(myDrive, homeDirPath.substring(0, homeDirPath.lastIndexOf("/")));
			AbstractFile homeDir;
			
			try {
				homeDir = parentDir.getFileByName(homeDirName);  
			} catch (FileNotFoundException e) {
				homeDir = new Directory(myDrive, parentDir, this, homeDirName);
			}
			
			if (!(homeDir instanceof Directory)){
				throw new InvalidPathException(homeDirPath);
			}
			setHomeDirectory((Directory) homeDir);
			
		} catch (InvalidPathException e) {
			throw new InvalidPathException(homeDirPath);
		}
	}
    
    @Override
    public String getPassword() {
    	return null; //FIXME: define behaviour (throw exception/ log.warn() i don't know)
    }
    
    public boolean checkPassword(String password) {
    	return password.equals(super.getPassword());
    }
    
    protected boolean canAccess(AbstractFile file, char type) {
    	int pos;
    	
    	switch (type) {
    		case 'r' : pos = 0; break;
    		case 'w' : pos = 1; break;
    		case 'x' : pos = 2; break;
    		case 'd' : pos = 3; break;
    		default  : return false;
    	}
    	
    	if (file.getOwner() != this)
    		 pos += 4;
    	
    	return getUmask().charAt(pos) == type && file.getPermissions().charAt(pos) == type;
    }
    
    public boolean canRead(AbstractFile file) {
    	return canAccess(file, 'r');
    }
    
    public boolean canWrite(AbstractFile file) {
    	return canAccess(file, 'w');
    }
    
    public boolean canExecute(AbstractFile file) {
    	return canAccess(file, 'x');
    }
    
    public boolean canDelete(AbstractFile file) {
    	return canAccess(file, 'd');
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
		
		
		setHomeDirectory(myDrive, homeDirectoryPath);

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

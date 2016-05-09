package pt.tecnico.myDrive.domain;

import org.apache.commons.lang.StringUtils;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.InvalidOperationException;
import pt.tecnico.myDrive.exception.InvalidPathException;
import pt.tecnico.myDrive.exception.InvalidPermissionStringException;
import pt.tecnico.myDrive.exception.InvalidUsernameException;
import pt.tecnico.myDrive.exception.PasswordTooShortException;


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
            mydrive.addUser(this);
        }
    }
    
    @Override
    public void setPassword(String password) {
    	setValidPassword(password, true);
    }
    
    void setValidPassword(String password, boolean atLeast8char) {
    	if (atLeast8char) {
    		if (!(password.length() >= 8)) 
        		throw new PasswordTooShortException(password);
    	}
    	super.setPassword(password);
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
			if(homeDirPath.endsWith("/") || !homeDirPath.startsWith("/")){
				throw new InvalidPathException(homeDirPath);
			}
			homeDirPath = homeDirPath.substring(1);
			Directory rootDir = (Directory) myDrive.getRootDirectory();
			String homeDirName = homeDirPath.substring(homeDirPath.lastIndexOf("/") + 1);
			Directory parentDir = rootDir.createDirectoryByPath(myDrive, myDrive.getUserByUsername("root"), homeDirPath.substring(0, homeDirPath.lastIndexOf("/")));
			AbstractFile homeDir;
			
			try {
				homeDir = parentDir.getFileByName(homeDirName);  
			} catch (FileNotFoundException e) {
				homeDir = new Directory(myDrive, parentDir, myDrive.getUserByUsername("root"), homeDirName);
				homeDir.setOwner(this);
				homeDir.setPermissions(this.getUmask());
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
    	throw new InvalidOperationException("Accessing user password");
    }
    
    public boolean checkPassword(String password) {
    	return password.equals(super.getPassword());
    }
    
    public boolean canLogin() {
    	return (super.getPassword().length() >= 8);
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
    	
    	if (file.getOwner().equals(this) == false)
    		 pos += 4;
    	
    	return file.getPermissions().charAt(pos) == type;
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
		String username = userElement.getAttributeValue("username");
		if (username == null)
			throw new ImportDocumentException("User must have a username");

		if (myDrive.hasUser(username))
			throw new ImportDocumentException("Trying to import a user that already exists '" + username + "'");

		setUsername(username);
		setMyDrive(myDrive);

		String s = userElement.getChildText("password");
		setPassword(s != null ? s : username);
		s = userElement.getChildText("name");
		setName(s != null ? s : username);
		try {
			s = userElement.getChildText("umask");
			setUmask(s != null ? s : "rwxd----");
		} catch (InvalidPermissionStringException e) {
			throw new ImportDocumentException(e.getMessage());
		}
		s = userElement.getChildText("home");
		setHomeDirectory(myDrive, s != null ? s : "/home/" + username);
	}

    public Element xmlExport() {
        Element element = new Element("user");
        
        element.setAttribute("username", getUsername());
        
        Element nameElement = new Element("name");
        nameElement.addContent(getName());
        element.addContent(nameElement);
        
        Element passwordElement = new Element("password");
        passwordElement.addContent(super.getPassword());
        element.addContent(passwordElement);
        
        Element umaskElement = new Element("umask");
        umaskElement.addContent(getUmask());
        element.addContent(umaskElement);
        
        //FIXME Very, very dirty hack: Paths need to be changed not to accept '/' as last char
        String path = getHomeDirectory().getPath();
		if ((path != "/") && (path.endsWith("/"))) {
			path = path.substring(0, path.lastIndexOf("/"));
		}
        
        Element homeElement = new Element("home");
        homeElement.addContent(path);
        element.addContent(homeElement);

        return element;
    }
    
	public boolean sameAs(Object o) {
		if (o instanceof User) {
			return ((User) o).getUsername().equals(this.getUsername());
		}
		return false;
	}
	
	protected void cleanup() {
		for (Association a : this.getAssociationsSet()) a.cleanup();	
		for (Login l : this.getLoginSet()) l.cleanup();	
		for (AbstractFile f : this.getFilesSet()) f.setOwner(null);
		super.setHomeDirectory(null);
		super.setMyDrive(null);
		deleteDomainObject();
	}
	
	public void addAssociation(String fileExtension, TextFile applicationFile) {
    	Association a = getAssociationByFileExtension(fileExtension);
		if (a != null) 
    		a.setApplicationFile(applicationFile);
		else 
			addAssociations(new Association(fileExtension, applicationFile));
    	
    }
       
    public Association getAssociationByFileExtension(String fileExtension) {
    	for (Association a : getAssociationsSet()) {
    		if (a.getFileExtension().equals(fileExtension)) 
    			return a;
    	}
    	return null;
    }
    
    public boolean isLoginValid(DateTime lastActivity) {
    	return (DateTime.now().getMillis() - lastActivity.plusHours(2).getMillis()) < 0;
    }

	public static boolean isValidPermissionString(String test) {
		return AbstractFile.isValidPermissionString(test);
	}

	@Override
	public void setUmask(String umask) {
		if (isValidPermissionString(umask)) {
			super.setUmask(umask);
		} else {
			throw new InvalidPermissionStringException(umask);
		}
	}
}

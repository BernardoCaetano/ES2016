package pt.tecnico.myDrive.domain;

import org.apache.commons.lang.StringUtils;

import java.util.Set;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.InvalidUsernameException;

public class User extends User_Base {
    
    public User() {
        super();
    }

    public User(MyDriveFS mydrive, String username, String password, String name, String umask) {
        super();
        try {
            init(mydrive, username, (password == null) ? username : password, 
                                (name == null) ? username : name, 
                                (umask == null) ? "rwxd----" : umask);
        } catch(InvalidUsernameException e){
            System.out.println(e.getMessage());
        }
    }

    public void init(MyDriveFS mydrive, String username, String password, String name, String umask) {
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
            // check if home is a directory and thorw exception if not 
            // "There is a file in /home/ with new User username"
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


    public Element xmlExport() {
        Element element = new Element("user");
        
        element.setAttribute("username", getUsername());
        element.setAttribute("password", getPassword());
        element.setAttribute("name", getName());
        element.setAttribute("umask", getUmask());
        element.setAttribute("homeDirectory", getHomeDirectory().getPath());

        Set<AbstractFile> ownedFiles = getFilesSet();

        for (AbstractFile f: ownedFiles) {
            Element fileElement = f.xmlAddFile();
            fileElement.setAttribute("path", f.getPath());
            element.addContent(fileElement);
        }

        return element;
    }
}

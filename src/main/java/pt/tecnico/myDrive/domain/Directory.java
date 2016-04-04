package pt.tecnico.myDrive.domain;

import java.util.ArrayList;
import java.util.Collections;

import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.NameAlreadyExistsException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.IsCurrentDirectoryException;
import pt.tecnico.myDrive.exception.IsHomeDirectoryException;
import org.jdom2.Element;

public class Directory extends Directory_Base {
	
	public Directory() {
		super();
	}

	public Directory(MyDriveFS mydrive, Directory parentDir, User owner, String name) 
			throws InvalidFileNameException, NameAlreadyExistsException {
		super();
		initAbstractFile(mydrive, parentDir, owner, name);
	}
	
	public Directory(MyDriveFS myDrive, Element directoryElement){
		super();
		xmlImport(myDrive, directoryElement);
	}

	@Override
	public void addFiles(AbstractFile file) throws NameAlreadyExistsException {
		if (hasFile(file.getName())) {
			throw new NameAlreadyExistsException(file.getName());
		}
		super.addFiles(file);
	}

	public boolean hasFile(String filename) {
		try {
			return getFileByName(filename) != null;
		} catch (FileNotFoundException e) {
			return false;
		}
	}

	public AbstractFile getFileByName(String name) throws FileNotFoundException {
		if (name.equals(".")) {
			return this;
		} else if (name.equals("..")) {
			return this.getParent();
		}
		for (AbstractFile f : getFilesSet()) {
			if (f.getName().equals(name)) {
				return f;
			}
		}
		throw new FileNotFoundException(getPath() + "/" + name);
	}

	@Override
	public String getPath() {
		return super.getPath() + "/";
	}

	
	@Override
	public void remove() 
			throws IsHomeDirectoryException, IsCurrentDirectoryException {
        if (getHostUserSet().size()!=0){
            throw new IsHomeDirectoryException(this.getName());
        }

        if (getLoginSet().size()!=0) {
        	throw new IsCurrentDirectoryException(this.getName());
        }
        
        if (getFilesSet().size()!=0) {
            for (AbstractFile child : this.getFilesSet()) {
            	child.remove();
            }
         }

        setParent(null);
        setOwner(null);
        deleteDomainObject();		
       
	}

	public ArrayList<AbstractFile> getFilesSimpleSorted() {

		ArrayList<AbstractFile> children = new ArrayList<AbstractFile>();
		children.addAll(getFilesSet());
		Collections.sort(children);

		return children;

	}

	public ArrayList<AbstractFile> getFilesRecursive() {
		ArrayList<AbstractFile> children = new ArrayList<AbstractFile>();
		
		for (AbstractFile f: getFilesSet()){
			if (f!=this && f!= getParent())
				children.addAll(f.getFilesRecursive());
		}

		children.add(this);

		return children;
	}

    @Override
    public boolean isHomeDirectory() {
    	return !getHostUserSet().isEmpty();
    }

    @Override
    public Element xmlExport() {
		return xmlAddFile();
    }

    @Override
	public String xmlTag() {
		return "directory";
	}
}

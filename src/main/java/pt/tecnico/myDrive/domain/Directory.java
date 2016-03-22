package pt.tecnico.myDrive.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.NameAlreadyExistsException;
import pt.tecnico.myDrive.exception.NotDirectoryException;
import pt.tecnico.myDrive.exception.HomeDirectoryException;
import pt.tecnico.myDrive.exception.DirectoryNotEmptyException;
import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.InvalidPathException;

import org.jdom2.Element;

public class Directory extends Directory_Base {

	public Directory() {
		super();
	}

	public Directory(MyDriveFS mydrive, Directory parentDir, User owner, String name) {
		super();
		initAbstractFile(mydrive, parentDir, owner, name);
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
	public void removeFile() throws HomeDirectoryException, DirectoryNotEmptyException {
        if (getHostUserSet().size()!=0){
            throw new HomeDirectoryException(this.getName());
        }

        if (getFilesSet().size()!=0) {
           throw new DirectoryNotEmptyException(this.getName()); 
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
	
	public Directory getRootDirectory() {		
		if (this==this.getParent())
			return this;
		else
			return this.getParent().getRootDirectory();
	}
	
	public void xmlImport(Element element, Directory par) throws ImportDocumentException{
		try {
            xmlImportFile(element, par);
		}
		catch (Exception e) {
            throw new ImportDocumentException();
		}
							
	}

	
    public Element xmlExport() {
		return xmlAddFile();
    }
	
	public String xmlTag() {
		return "directory";
	}
}

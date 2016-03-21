package pt.tecnico.myDrive.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.NameAlreadyExistsException;
import pt.tecnico.myDrive.exception.HomeDirectoryException;
import pt.tecnico.myDrive.exception.DirectoryNotEmptyException;
import pt.tecnico.myDrive.exception.ImportDocumentException;

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
        //return null;
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
	
	public void xmlImport(Element element, Directory par) throws ImportDocumentException{
		try {
            xmlImportFile(element, par);
		}
		catch (Exception e) {
            throw new ImportDocumentException();
		}
							
	}
	
	
	public Element xmlExport() {
		Element element = xmlAddFile();
		
		ArrayList<AbstractFile> children = new ArrayList<AbstractFile>();
		children.addAll(getFilesRecursive());
		Collections.sort(children);

		for(AbstractFile c: children){
			if (c!=this && c!= getParent())
				element.addContent(c.xmlExport());
		}
		return element;
	}
	
	public String xmlTag() {
		return "directory";
	}
}

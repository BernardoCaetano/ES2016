package pt.tecnico.myDrive.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.NameAlreadyExistsException;
import pt.tecnico.myDrive.exception.HomeDirectoryException;
import pt.tecnico.myDrive.exception.DirectoryNotEmptyException;


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
		return getFileByName(filename) != null;
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
        return null;
		//throw new FileNotFoundException(getPath() + "/" + name);
	}

	@Override
	public String getPath() {
		return super.getPath() + "/";
	}

	public void deleteFile(String path) {

		MyDriveFS mydrive = this.getOwner().getMyDrive();
		AbstractFile f = mydrive.getFileByPath(null, path);
		f.removeFile();
	}

	 @Override
	public void removeFile() throws HomeDirectoryException, DirectoryNotEmptyException {
        if (getHostUserSet().size()!=0){
            throw new HomeDirectoryException(this.getName());
        }

        if (getFilesSet().size()!=0) {
           throw new DirectoryNotEmptyException(this.getName()); 
        }

        setOwner(null);
        deleteDomainObject();		
	}

	public ArrayList<AbstractFile> getFilesSimpleSorted() {

		Set<AbstractFile> fileSet = getFilesSet();
		ArrayList<AbstractFile> fileList = new ArrayList<AbstractFile>();
		for (AbstractFile f : fileSet) {
			fileList.add(f);
		}

		Collections.sort(fileList);

		return fileList;

	}
	
	public Element xmlExport() {
		Element element = xmlAddFile();
		
		Set<AbstractFile> children = getFilesSet();

		for(AbstractFile c: children){
			if (c != this && c != getParent())
				element.addContent(c.xmlExport());
		}

		return element;
	}
	
	public String xmlTag() {
		return "directory";
	}
}

package pt.tecnico.myDrive.domain;

import org.joda.time.DateTime;

import org.jdom2.Element;

import java.util.ArrayList;

import pt.tecnico.myDrive.exception.AccessDeniedException;
import pt.tecnico.myDrive.exception.CreateDeniedException;
import pt.tecnico.myDrive.exception.FileNotFoundException;
import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;
import pt.tecnico.myDrive.exception.InvalidOperationException;
import pt.tecnico.myDrive.exception.InvalidPermissionStringException;
import pt.tecnico.myDrive.exception.NameAlreadyExistsException;
import pt.tecnico.myDrive.exception.PathMaximumLengthException;
import pt.tecnico.myDrive.service.dto.AbstractFileDTO;;

public abstract class AbstractFile extends AbstractFile_Base implements Comparable<AbstractFile> {

	public void initAbstractFile(MyDriveFS mydrive, Directory parentDir, User owner, String name)
			throws InvalidFileNameException, NameAlreadyExistsException, PathMaximumLengthException {
		if (!owner.canWrite(parentDir)) {
			throw new CreateDeniedException(owner.getUsername());
		}
		setName(name);
		setId(mydrive);
		setLastModified(new DateTime());
		setParent(parentDir);
		setOwner(mydrive, owner);
		setPermissions(getOwner().getUmask());
		checkPathLength();
	}

	@Override
	public void setName(String name) {
		setValidName(name, true);
	}

	public void setValidName(String name, boolean notRootDir) throws InvalidFileNameException {

		if (notRootDir) {
			if (name.equals(".") || name.equals("..") || name.trim().isEmpty()) {
				throw new InvalidFileNameException(name);
			} else if (name.contains("/") || name.contains("\0")) {
				throw new InvalidFileNameException(name);
			} else {
				super.setName(name);
			}
		}else{
			super.setName(name);
		}
	}

	@Override
	public void setParent(Directory parentDir) throws NameAlreadyExistsException {
		if (parentDir == null)
			throw new InvalidOperationException("Setting host directory of a file to null");
		else
			parentDir.addFiles(this);
	}

	public void setOwner(MyDriveFS mydrive, User owner) {
		if (owner == null)
			mydrive.getUserByUsername("root").addFiles(this);
		else
			owner.addFiles(this);
	}

	protected void setId(MyDriveFS mydrive) {
		mydrive.incrementLastFileID();
		super.setId(mydrive.getLastFileID());
	}

	public String getPath() {
		return getParent().getPath() + getName();
	}

	protected void checkPathLength() {
		if ((getPath().length() > 1024)) {
			throw new PathMaximumLengthException();
		}
	}

	protected String getNameOfFileFromPath(String path) {
		String[] parts = path.split("/");

		if (parts.length > 0)
			return parts[parts.length - 1];
		else
			return "";
	}

	protected Directory getParentFromPath(String path) {
		if (path.equals("/"))
			return MyDriveFS.getInstance().getRootDirectory();
		else {
			String[] parts = path.split("/");
			String name = parts[parts.length - 1];

			int lengthWithoutName = path.length() - name.length() - 1;
			String pathWithoutName = path.substring(0, lengthWithoutName);

			return MyDriveFS.getInstance().getDirectoryByPath(RootDirectory.getInstance(MyDriveFS.getInstance()),
					pathWithoutName);
		}
	}

	public void xmlImport(MyDriveFS myDrive, Element element) {

		String parentPath = element.getChildText("path");
		if (parentPath == null)
			throw new ImportDocumentException("A file must reside inside a directory");

		if (!parentPath.startsWith("/"))
			throw new ImportDocumentException("All paths specified must begin at root '/'");

		Directory parent;
		try {
			parent = myDrive.getDirectoryByPath(myDrive.getRootDirectory(), parentPath);
		} catch (FileNotFoundException e) {
			Directory root = myDrive.getRootDirectory();
			User superuser = myDrive.getUserByUsername("root");
			parent = root.createDirectoryByPath(myDrive, superuser, parentPath.substring(1));
		}

		String name = element.getChildText("name");
		if (name == null) {
			throw new ImportDocumentException("A file must have a name");
		}

		if (parent.hasFile(name))
			throw new ImportDocumentException(
					"Trying to import a " + element.getName() + " that already exists '" + name + "'");

		setName(name);
		setParent(parent);
		setId(myDrive);

		String owner = element.getChildText("owner");
		setOwner(myDrive.getUserByUsername(owner != null ? owner : "root"));

		try {
			setPermissions(element.getChildText("permissions"));
		} catch (InvalidPermissionStringException e) {
			throw new ImportDocumentException(e.getMessage());
		}

		String time = element.getChildText("lastModified");
		setLastModified(time != null ? DateTime.parse(time) : DateTime.now());
	}

	public abstract Element xmlExport();

	public Element xmlAddFile() {
		Element element = new Element(xmlTag());

		element.setAttribute("id", "" + getId());
		
		String path = getParent().getPath();
		if (path != "/" && path.endsWith("/")) {
			path = path.substring(0, path.lastIndexOf("/"));
		}

		Element pathElement = new Element("path");
		pathElement.addContent(path);
		element.addContent(pathElement);

		Element nameElement = new Element("name");
		nameElement.addContent(getName());
		element.addContent(nameElement);

		Element ownerElement = new Element("owner");
		ownerElement.addContent(getOwner().getUsername());
		element.addContent(ownerElement);

		Element permissionsElement = new Element("permissions");
		permissionsElement.addContent(getPermissions());
		element.addContent(permissionsElement);

		Element lastModifiedElement = new Element("lastModified");
		lastModifiedElement.addContent(getLastModified().toString());
		element.addContent(lastModifiedElement);

		return element;
	}

	public abstract String xmlTag();

	public ArrayList<AbstractFile> getFilesRecursive() {
		ArrayList<AbstractFile> children = new ArrayList<AbstractFile>();
		children.add(this);

		return children;
	}

	public void remove(User user) throws AccessDeniedException {
		if (!user.canWrite(getParent()) || !user.canDelete(this)) {
			throw new AccessDeniedException(user.getUsername(), this.getName());
		} else {
			super.setParent(null);
			super.setOwner(null);
			deleteDomainObject();
		}
	};

	protected void cleanup() {
		super.setParent(null);
		super.deleteDomainObject();
	}

	public int compareTo(AbstractFile f) {
		String thisPath = this.getPath().replaceAll("/", "");
		String otherPath = f.getPath().replaceAll("/", "");

		return thisPath.compareToIgnoreCase(otherPath);
	}

	public boolean isHomeDirectory() {
		return false;
	}

	public abstract int dimension();

	public static boolean isValidPermissionString(String test) {
		return test.matches("([r-][w-][x-][d-]){2}");
	}

	@Override
	public void setPermissions(String permissions) {
		if (permissions == null) {
			super.setPermissions(getOwner().getUmask());
			return;
		}
		if (isValidPermissionString(permissions)) {
			super.setPermissions(permissions);
		} else {
			throw new InvalidPermissionStringException(permissions);
		}
	}

	public AbstractFileDTO convertToDTO() {
		return new AbstractFileDTO(this);
	}
}

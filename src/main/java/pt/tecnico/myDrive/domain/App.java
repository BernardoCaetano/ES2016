package pt.tecnico.myDrive.domain;

import org.jdom2.Element;

import pt.tecnico.myDrive.exception.AccessDeniedException;
import pt.tecnico.myDrive.exception.ImportDocumentException;
import pt.tecnico.myDrive.exception.InvalidAppContentException;
import pt.tecnico.myDrive.exception.InvalidFileNameException;

public class App extends App_Base {

	public App() {
		super();
	}

	public App(MyDriveFS mydrive, Directory parentDir, User owner, String name, String content)
			throws InvalidFileNameException {
		super();

		if (!isValidPackageName(content)) {
			throw new InvalidAppContentException(content);
		}

		initTextFile(mydrive, parentDir, owner, name, content);
	}

	public App(MyDriveFS mydrive, Directory parentDir, User owner, String name) throws InvalidFileNameException {
		super();

		initTextFile(mydrive, parentDir, owner, name, "");
	}

	public App(MyDriveFS myDrive, Element appElement) {
		super();
		String content = appElement.getChildText("content");
		content = (content != null ? content : "");
		if (!isValidPackageName(content))
			throw new ImportDocumentException("Invalid content '" + content + "' for App file");
		
		xmlImport(myDrive, appElement);
	}

	@Override
	public void setContent(String content) {
		if (!isValidPackageName(content)) {
			throw new InvalidAppContentException(content);
		}
		super.setContent(content);
	}
	
	@Override
	public void execute(User u, Object[] args) {
		if (!u.canExecute(this))
			throw new AccessDeniedException(u.getUsername(), getName());
		executeReflection(getContent(), args);
	}

	protected boolean isValidPackageName(String packageName) {

		if (packageName == "")
			return true;

		String packageParts[] = packageName.split("\\.");

		if (packageParts.length == 0)
			return false;

		for (String part : packageParts) {
			char firstChar = part.charAt(0);

			if (!Character.isJavaIdentifierStart(firstChar))
				return false;

			for (int i = 0; i < part.length(); i++) {
				char nextChar = part.charAt(i);

				if (!Character.isJavaIdentifierPart(nextChar))
					return false;
			}
		}

		return true;
	}

	@Override
	public String xmlTag() {
		return "app";
	}

}

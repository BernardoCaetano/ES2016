package pt.tecnico.myDrive.presentation;

import java.io.File;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import pt.tecnico.myDrive.service.ImportMyDriveService;

/* Based on Phonebook-V3 import command */
public class Import extends MyDriveCommand {

	public Import(MyDriveShell shell) {
		super(shell, "import", "import file system from XML file (use './localFile', '/localFile' or 'resourceFile')");
	}

	@Override
	public void execute(String[] args) {
		if (args.length != 1) {
			throw new RuntimeException("USAGE: '" + getName() + " <xml file name>'");
		} else {

			try {
				SAXBuilder builder = new SAXBuilder();
				File file;
				if (args[0].startsWith(".") || args[0].startsWith("/")) {
					file = new File(args[0]);
				} else {
					file = resourceFile(args[0]);
				}
				Document doc = (Document) builder.build(file);

				ImportMyDriveService service = new ImportMyDriveService(doc);
				service.execute();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	public File resourceFile(String filename) {
		log.trace("Resource: " + filename);
		ClassLoader classLoader = getClass().getClassLoader();
		if (classLoader.getResource(filename) == null) {
			throw new RuntimeException("unkown resource");
		}
		return new java.io.File(classLoader.getResource(filename).getFile());
	}

}
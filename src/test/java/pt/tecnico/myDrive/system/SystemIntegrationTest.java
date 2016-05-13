package pt.tecnico.myDrive.system;

import static org.junit.Assert.*;

import java.io.File;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import mockit.integration.junit4.JMockit;

import mockit.Mock;
import mockit.MockUp;
import mockit.Verifications;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.presentation.FileViewer;
import pt.tecnico.myDrive.service.AbstractServiceTest;
import pt.tecnico.myDrive.service.AddVariableService;
import pt.tecnico.myDrive.service.ChangeDirectoryService;
import pt.tecnico.myDrive.service.CreateFileService;
import pt.tecnico.myDrive.service.DeleteFileService;
import pt.tecnico.myDrive.service.ExecuteFileService;
import pt.tecnico.myDrive.service.ImportMyDriveService;
import pt.tecnico.myDrive.service.ListDirectoryService;
import pt.tecnico.myDrive.service.ListVariablesService;
import pt.tecnico.myDrive.service.LoginService;
import pt.tecnico.myDrive.service.LogoutService;
import pt.tecnico.myDrive.service.ReadFileService;
import pt.tecnico.myDrive.service.ShowVariableService;
import pt.tecnico.myDrive.service.WriteFileService;

@RunWith(JMockit.class)
public class SystemIntegrationTest extends AbstractServiceTest {
	
	private static final String importFile = "execute.xml";

	@Override
	protected void populate() {
		
	}
	
	@Test
	public void success() throws Exception {
		
		// Import system
		ClassLoader loader = getClass().getClassLoader();
		File file = new File(loader.getResource(importFile).getFile());
		Document doc = (Document)new SAXBuilder().build(file);
		new ImportMyDriveService(doc).execute();
		
		// Login as jtb (currentDir will be /home/jtb)
		LoginService lgs = new LoginService("jtb", "Fernandes");
		lgs.execute();
		long jtbToken = lgs.result();
		
		// List /home/jtb/bin
		ListDirectoryService lds = new ListDirectoryService(jtbToken, "./bin");
		lds.execute();
		assertEquals("/home/jtb/bin size is not correct", 8, lds.result().size()); // ., .., greetings, the_answer, farewell, exec:death, the_beginning
		
		// Delete /home/jtb/sum
		DeleteFileService dfs = new DeleteFileService(jtbToken, "sum");
		dfs.execute();
		
		// List /home/jtb after deleting sum
		lds = new ListDirectoryService(jtbToken);
		lds.execute();
		assertEquals("/home/jtb size is not correct", 7, lds.result().size()); // profile, documents, doc, example
		
		ExecuteFileService efs = new ExecuteFileService(jtbToken, "example");
		efs.execute();
						
		// Change to /home/jtb/bin
		ChangeDirectoryService cds = new ChangeDirectoryService(jtbToken, "/home/jtb/bin");
		cds.execute();
		assertEquals("Did not change current directory", "/home/jtb/bin/", cds.result());
		
		
		AddVariableService avs = new AddVariableService(jtbToken, "HOMEDIR", "/home/jtb");
		avs.execute();
		assertEquals("Variable list does not contain one variable", 1, avs.result().size());
		assertEquals("HOMEDIR", avs.result().get(0).getName());
		assertEquals("/home/jtb", avs.result().get(0).getValue());
		
		
		new CreateFileService(jtbToken, "batata", "TextFile").execute();
		new WriteFileService(jtbToken, "./batata", "Just some text").execute();
		
		ReadFileService rfs = new ReadFileService(jtbToken, "batata");
		rfs.execute();
		assertEquals("Just some text", rfs.result());
		
		lds = new ListDirectoryService(jtbToken);
		lds.execute();
		assertEquals("/home/jtb/bin size is not correct", 9, lds.result().size());
		
		
		new MockUp<ExecuteFileService>() {
			@Mock 
			void dispatch() {
				FileViewer.executePDF("README.pdf");
			}
		};
		efs = new ExecuteFileService(jtbToken, "./README.pdf");
		efs.execute();
		
		new Verifications() {
			{
				FileViewer.executePDF("README.pdf");
			}
		};
		
		new AddVariableService(jtbToken, "UNIFOLDER", "/home/jtb/uni").execute();
		new AddVariableService(jtbToken, "GAMESFOLDER", "/home/jtb/games").execute();
		
		
		ShowVariableService svs = new ShowVariableService(jtbToken, "UNIFOLDER");
		svs.execute();
		assertEquals("Did not match variable name", "UNIFOLDER", svs.result().getName());
		assertEquals("Did not match variable value", "/home/jtb/uni", svs.result().getValue());
		
		ListVariablesService lvs = new ListVariablesService(jtbToken);
		lvs.execute();
		assertEquals("Variable list size is not correct", 3, lvs.result().size());
				
		new MockUp<Directory>() {
			@Mock
			String translate(String path) {
				return "/home/jtb/sum";
			}
		};
		
		efs = new ExecuteFileService(jtbToken, "$HOMEDIR/sum");
		efs.execute();

		new LogoutService(jtbToken).execute();
		
	}

}

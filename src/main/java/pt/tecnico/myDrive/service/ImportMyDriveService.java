package pt.tecnico.myDrive.service;

import org.jdom2.Document;

import pt.tecnico.myDrive.exception.ImportDocumentException;

public class ImportMyDriveService extends MyDriveService {
	
	private final Document doc; 
	
	public ImportMyDriveService(Document doc) {
		this.doc = doc;
	}

	@Override
	protected void dispatch() throws ImportDocumentException {
		getMyDrive().xmlImport(doc.getRootElement());
	}

}

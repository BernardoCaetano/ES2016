package pt.tecnico.myDrive.service.dto;

import pt.tecnico.myDrive.domain.Link;

public class LinkDTO extends AbstractFileDTO {

	private String _content;
	
	public LinkDTO(Link file) {
		super(file);
		setContent(file.getContent());
	}

	public void setContent(String c){
		_content = " -> "+ c;
	}
	
	@Override
	public final String getContent() {
		return _content;
	}
}

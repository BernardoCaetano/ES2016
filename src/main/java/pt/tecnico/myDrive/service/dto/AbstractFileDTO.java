package pt.tecnico.myDrive.service.dto;

import org.joda.time.DateTime;

import pt.tecnico.myDrive.domain.AbstractFile;

public class AbstractFileDTO {
	protected String _type;
	protected String _permissions;
	protected int _dimension;
	protected String _owner;
	protected Integer _id;
	protected DateTime _lastModified;
	protected String _name;
	
	public AbstractFileDTO(AbstractFile file, String name) {
		_type = file.xmlTag();
		_permissions = file.getPermissions();
		_dimension = file.dimension();
		_owner = file.getOwner().getUsername();
		_id = file.getId();
		_lastModified = file.getLastModified();
		_name = name;
	}
	
	public AbstractFileDTO(AbstractFile file) {		
		this(file, file.getName());
	}
	
	public final String getType() {
		return this._type;
	}
	
	public final String getPermissions() {
		return this._permissions;
	}
	
	public final int getDimension() {
		return this._dimension;
	}
	
	public final String getOwner() {
		return this._owner;
	}
	
	public final int getId() {
		return this._id;
	}
	
	public final DateTime getLastModified() {
		return this._lastModified;
	}
	
	public final String getName() {
		return this._name;
	}
	
	public String getContent() {
		return "";
	}
}

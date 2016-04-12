package pt.tecnico.myDrive.service.dto;

import org.joda.time.DateTime;

import pt.tecnico.myDrive.domain.AbstractFile;

public class AbstractFileDTO {
	private String _type;
	private String _permissions;
	private int _dimension;
	private String _owner;
	private Integer _id;
	private DateTime _lastModified;
	private String _name;
	
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
}

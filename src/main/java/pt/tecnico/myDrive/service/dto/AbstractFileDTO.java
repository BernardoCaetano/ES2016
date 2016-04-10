package pt.tecnico.myDrive.service.dto;

import org.joda.time.DateTime;

public class AbstractFileDTO {
	private String _type;
	private String _permissions;
	private byte _size;
	private String _owner;
	private Integer _id;
	private DateTime _lastModified;
	private String _name;
	
	public AbstractFileDTO(String type, String permissions, byte size, 
			String owner, int id, DateTime lastModified, String name) {
		this._type = type;
		this._permissions = permissions;
		this._size = size;
		this._owner = owner;
		this._id = id;
		this._lastModified = lastModified;
		this._name = name;
	}
	
	public final String getType() {
		return this._type;
	}
	
	public final String getPermissions() {
		return this._permissions;
	}
	
	public final byte getSize() {
		return this._size;
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

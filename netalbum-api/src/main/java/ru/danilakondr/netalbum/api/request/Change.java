package ru.danilakondr.netalbum.api.request;

import jakarta.json.bind.annotation.*;

@JsonbPropertyOrder({"oldName", "newName"})
public class Change {
	private String oldName;
	private String newName;
	
	public void setOldName(String name) {
		this.oldName = name;
	}
	
	@JsonbNillable
	public void setNewName(String name) {
		this.newName = name;
	}
	
	public String getOldName() {
		return oldName;
	}
	
	@JsonbNillable
	public String getNewName() {
		return newName;
	}
}

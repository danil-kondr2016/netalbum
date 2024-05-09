package ru.danilakondr.netalbum.api;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"oldName", "newName"})
public class Change {
	private String oldName;
	private String newName;
	
	public void setOldName(String name) {
		this.oldName = name;
	}
	
	public void setNewName(String name) {
		this.newName = name;
	}
	
	public String getOldName() {
		return oldName;
	}
	
	public String getNewName() {
		return newName;
	}
}

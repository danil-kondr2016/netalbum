package ru.danilakondr.netalbum.api.request;

import java.util.List;

public class Synchronize {
	private List<Change> changes;
	
	public void setChanges(List<Change> changes) {
		this.changes = changes;
	}
	
	public List<Change> getChanges() {
		return changes;
	}
}

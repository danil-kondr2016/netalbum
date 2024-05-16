package ru.danilakondr.netalbum.api.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Класс-держатель параметра изменения. Содержит два поля:
 * <ul>
 *     <li>{@code oldName}: старое имя</li>
 *     <li>{@code newName}: новое имя (значение null означает, что файл удалён)</li>
 * </ul>
 *
 * @author Данила А. Кондратенко
 */
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

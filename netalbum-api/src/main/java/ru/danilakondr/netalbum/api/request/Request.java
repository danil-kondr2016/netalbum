package ru.danilakondr.netalbum.api.request;

import jakarta.json.bind.annotation.JsonbPropertyOrder;

/**
 * Объект запроса, посылаемого в формате JSON.
 * <p>
 * Формат запроса:
 * <pre>
 * {"method": строка-название метода,
 *  "contents": объект с произвольным содержимым
 * }
 * </pre>
 * @param <T> тип содержимого
 */
@JsonbPropertyOrder({"method", "contents"})
public class Request<T> {
	private RequestType method;
	private T contents;

	public Request() {}
	
	public Request(RequestType method) {
		this.method = method;
	}
	
	public Request(RequestType method, T contents) {
		this.method = method;
		this.contents = contents;
	}
	
	public RequestType getMethod() {
		return method;
	}
	
	public void setMethod(RequestType method) {
		this.method = method;
	}
	
	public T getContents() {
		return contents;
	}
	
	public void setContents(T contents) {
		this.contents = contents;
	}
}

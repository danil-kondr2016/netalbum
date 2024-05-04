package ru.danilakondr.netalbum.api.request;

import ru.danilakondr.netalbum.api.DirectoryName;
import ru.danilakondr.netalbum.api.SessionId;

public class Requests {
	public static Request<DirectoryName> initSession(String directoryName) {
		Request<DirectoryName> request = new Request<>();
		request.setMethod("initSession");
		
		DirectoryName contents = new DirectoryName();
		contents.setDirectoryName(directoryName);

		request.setContents(contents);
		
		return request;
	}
	
	public static Request<SessionId> connectToSession(String sessionId) {
		Request<SessionId> request = new Request<>();
		request.setMethod("connectToSession");
		
		SessionId contents = new SessionId();
		contents.setSessionId(sessionId);
		
		request.setContents(contents);
		
		return request;
	}
	
	public static Request<Void> disconnectFromSession() {
		return new Request<Void>("disconnectFromSession");
	}
	
	public static Request<Void> closeSession() {
		return new Request<Void>("closeSession");
	}
}

package ru.danilakondr.netalbum.api.request;

import java.util.List;

import ru.danilakondr.netalbum.api.DirectoryName;
import ru.danilakondr.netalbum.api.ImageData;
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
	
	public static Request<AddImages> addImages(List<ImageData> images) {
		AddImages add = new AddImages();
		add.setImages(images);
		
		return new Request<AddImages>("addImages", add);
	}
	
	public static Request<AddImages> addSingleImage(ImageData image) {
		AddImages add = new AddImages();
		add.setImages(List.of(image));
		
		return new Request<AddImages>("addImages", add);
	}
	
	public static Request<Void> getDirectoryInfo() {
		return new Request<Void>("getDirectoryInfo");
	}
	
	public static Request<Void> downloadContents() {
		return new Request<Void>("downloadContents");
	}
	
	public static Request<Synchronize> synchronize(List<Change> changes) {
		Synchronize content = new Synchronize();
		content.setChanges(changes);
		
		return new Request<>("synchronize", content);
	}
}

package ru.danilakondr.netalbum.api.request;

import ru.danilakondr.netalbum.api.DirectoryName;

public class Requests {
	public static Request<DirectoryName> initSession(String directoryName) {
		Request<DirectoryName> request = new Request<>();
		request.setMethod("initSession");
		
		DirectoryName contents = new DirectoryName();
		contents.setDirectoryName(directoryName);

		request.setContents(contents);
		
		return request;
	}
}

package ru.danilakondr.netalbum.api.response;

import java.util.List;

import ru.danilakondr.netalbum.api.ImageData;

public class Responses {
	private static Response<Void> responseWithStatus(Status st) {
		Response<Void> response = new Response<>();
		response.setStatus(st);
		
		return response;
	}
	
	public static Response<Void> success() {
		return responseWithStatus(Status.success());
	}
	
	public static Response<Void> invalidMethod(String method) {
		return responseWithStatus(Status.invalidMethod(method));
	}
	
	public static Response<Void> invalidArgument(String reason) {
		return responseWithStatus(Status.invalidArgument(reason));
	}
	
	public static Response<Void> sqlError(String reason) {
		return responseWithStatus(Status.sqlError(reason));
	}
	
	public static Response<DirectoryInfo> directoryInfo(String name, long size) {
		DirectoryInfo info = new DirectoryInfo();
		info.setDirectoryName(name);
		info.setDirectorySize(size);
		
		Response<DirectoryInfo> resp = new Response<>();
		resp.setStatus(Status.success());
		resp.setContents(info);
		
		return resp;
	}
	
	public static Response<DirectoryContents> directoryContents(List<ImageData> contents) {
		DirectoryContents dirContents = new DirectoryContents();
		dirContents.setDirectoryContents(contents);
		
		Response<DirectoryContents> resp = new Response<DirectoryContents>();
		resp.setStatus(Status.success());
		resp.setContents(dirContents);
		
		return resp;
	}
}
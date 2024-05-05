package ru.danilakondr.netalbum.api.response;

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
}

package ru.danilakondr.netalbum.api.request;

public enum RequestType {
	  INIT_SESSION
	, CONNECT_TO_SESSION
	, DISCONNECT_FROM_SESSION
	, CLOSE_SESSION
	, ADD_FILES
	, GET_DIRECTORY_INFO
	, ADD_IMAGES
	, DOWNLOAD_CONTENTS
	, SYNCHRONIZE
	;
}

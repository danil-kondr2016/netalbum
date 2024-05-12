package ru.danilakondr.netalbum.api.response;

public enum Status {
	  SUCCESS
	, INVALID_REQUEST
	, INVALID_METHOD
	, INVALID_ARGUMENT
	, FILE_NOT_FOUND
	, FILE_ALREADY_EXISTS
	, NON_EXISTENT_SESSION
	, NOT_AN_INITIATOR
	, NOT_A_VIEWER
	, CLIENT_NOT_CONNECTED
	, CLIENT_ALREADY_CONNECTED
	, ERROR
	, EXCEPTION
	, SESSION_EXITS
	/* Возврат данных */
	, SESSION_CREATED
	, SYNCHRONIZING
	, DIRECTORY_INFO
	, THUMBNAILS_ARCHIVE
	;
}

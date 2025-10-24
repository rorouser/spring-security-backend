package com.security.artifact.common.exception;

public class NewUserWithDifferentPasswordException extends RuntimeException{
	
	public NewUserWithDifferentPasswordException() {
		super("Las contraseñas no coinciden");
	}
}

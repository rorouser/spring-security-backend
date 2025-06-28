package com.security.artifact.exceptions;

public class NewUserWithDifferentPasswordException extends RuntimeException{
	
	public NewUserWithDifferentPasswordException() {
		super("Las contraseñas no coinciden");
	}
}
